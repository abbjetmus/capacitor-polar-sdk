/**
 * Polar File Transfer Protocol (PFTP) Client for Web Bluetooth
 * Implements RFC-76 framing and file operations
 */
export class PftpClient {
    constructor(mtuChar, d2hChar, _h2dChar) {
        this.sequenceNumber = 0;
        this.resetSequenceAfterWrite = true;
        this.responseBuffer = [];
        this.responsePromise = null;
        this.mtu = 512; // Default MTU
        this.mtuCharacteristic = mtuChar;
        this.d2hCharacteristic = d2hChar;
    }
    async initialize() {
        console.log('Setting up MTU notification listener...');
        this.mtuCharacteristic.addEventListener('characteristicvaluechanged', (event) => {
            console.log('📨 MTU characteristicvaluechanged event fired');
            this.handleMTUNotification(event.target.value);
        });
        console.log('Starting MTU notifications...');
        await this.mtuCharacteristic.startNotifications();
        console.log('Setting up D2H notification listener...');
        this.d2hCharacteristic.addEventListener('characteristicvaluechanged', (event) => {
            console.log('📨 D2H characteristicvaluechanged event fired');
            this.handleD2HNotification(event.target.value);
        });
        console.log('Starting D2H notifications...');
        await this.d2hCharacteristic.startNotifications();
        console.log('✅ PFTP client initialized - MTU and D2H notifications active');
    }
    /**
     * Send PFTP query via MTU characteristic
     * Used for queries like SET_LOCAL_TIME, GET_LOCAL_TIME, etc.
     */
    async sendQuery(queryId, params) {
        console.log(`PFTP: Sending query ID ${queryId}`);
        // RFC-60 query format: [query_id_low][query_id_high|0x80][params...]
        const header = new Uint8Array([
            queryId & 0xFF,
            ((queryId >> 8) & 0x7F) | 0x80
        ]);
        let message;
        if (params) {
            message = new Uint8Array(header.length + params.length);
            message.set(header, 0);
            message.set(params, header.length);
        }
        else {
            message = header;
        }
        console.log('PFTP: Query message:', Array.from(message).map(b => '0x' + b.toString(16).padStart(2, '0')).join(' '));
        const queryPromise = this.waitForResponse();
        await this.sendRequest(message);
        console.log('PFTP: Query sent, waiting for response...');
        const response = await queryPromise;
        console.log(`PFTP: Query response: ${response.length} bytes`);
        return response;
    }
    /**
     * Write a file to the device using PFTP PUT operation
     */
    async writeFile(path, data, operation) {
        console.log(`PFTP: Writing file to ${path} (${data.length} bytes)`);
        console.log(`PFTP: Operation packet: ${operation.length} bytes`);
        console.log('PFTP: Skipping session initialization for Polar 360 (not supported)');
        await new Promise(resolve => setTimeout(resolve, 100));
        // Combine operation header and data into single message with RFC-60 framing
        // RFC-60 format: [header_size_low][header_size_high][operation_bytes][data_bytes]
        const headerSize = operation.length;
        const completeMessage = new Uint8Array(2 + headerSize + data.length);
        completeMessage[0] = headerSize & 0xFF;
        completeMessage[1] = (headerSize >> 8) & 0x7F;
        completeMessage.set(operation, 2);
        completeMessage.set(data, 2 + headerSize);
        console.log(`PFTP: Sending complete message (${completeMessage.length} bytes)...`);
        console.log(`PFTP: RFC-60 header: size=${headerSize} [0x${completeMessage[0].toString(16).padStart(2, '0')} 0x${completeMessage[1].toString(16).padStart(2, '0')}]`);
        const writePromise = this.waitForResponse();
        await this.sendRequest(completeMessage);
        console.log('PFTP: Complete message sent via MTU, waiting for response...');
        const response = await writePromise;
        console.log(`PFTP: Write response: ${response.length} bytes`);
        this.checkResponse(response);
        console.log('✅ PFTP: File written successfully!');
        console.log('PFTP: Skipping session termination for Polar 360 (not supported)');
        // Reset sequence number after successful write
        if (this.resetSequenceAfterWrite) {
            console.log('PFTP: Resetting sequence number to 0');
            this.sequenceNumber = 0;
        }
    }
    /**
     * Send PFTP request via MTU characteristic with RFC-76 framing
     * Used for GET/PUT operations
     */
    async sendRequest(data) {
        const maxPayload = this.mtu - 1;
        if (maxPayload <= 0) {
            throw new Error(`Invalid MTU: ${this.mtu}`);
        }
        const totalPackets = Math.ceil(data.length / maxPayload);
        console.log(`PFTP MTU: Sending ${data.length} bytes in ${totalPackets} packets (MTU: ${this.mtu}, maxPayload: ${maxPayload})`);
        console.log('PFTP: First 32 bytes:', Array.from(data.slice(0, 32)).map(b => '0x' + b.toString(16).padStart(2, '0')).join(' '));
        for (let i = 0; i < totalPackets; i++) {
            const start = i * maxPayload;
            const end = Math.min(start + maxPayload, data.length);
            const chunk = data.slice(start, end);
            const isLast = i === totalPackets - 1;
            const next = (i === 0) ? 0 : 1;
            const status = isLast ? 1 : 2;
            const header = (this.sequenceNumber << 4) | (status << 1) | next;
            const packet = new Uint8Array(1 + chunk.length);
            packet[0] = header;
            packet.set(chunk, 1);
            console.log(`PFTP MTU: Packet ${i + 1}/${totalPackets} [seq:${this.sequenceNumber}, status:${status}, next:${next}] - ${packet.length} bytes`);
            await this.mtuCharacteristic.writeValue(packet);
            if (this.sequenceNumber < 15) {
                this.sequenceNumber++;
            }
            else {
                this.sequenceNumber = 0;
            }
            if (!isLast) {
                await new Promise(resolve => setTimeout(resolve, 10));
            }
        }
        console.log('PFTP: All packets sent via MTU');
    }
    /**
     * Handle MTU notifications (for query/request responses)
     * RFC-76 frame format:
     * Byte 0: [sequence:4bits][status:2bits][next:1bit]
     */
    handleMTUNotification(value) {
        const data = new Uint8Array(value.buffer);
        console.log('🔵 PFTP MTU:', data.length, 'bytes:', Array.from(data).map(b => '0x' + b.toString(16).padStart(2, '0')).join(' '));
        this.processRFC76Response(data);
    }
    /**
     * Handle D2H notifications (for device notifications)
     * RFC-76 frame format:
     * Byte 0: [sequence:4bits][status:2bits][next:1bit]
     */
    handleD2HNotification(value) {
        const data = new Uint8Array(value.buffer);
        console.log('🔵 PFTP D2H:', data.length, 'bytes:', Array.from(data).map(b => '0x' + b.toString(16).padStart(2, '0')).join(' '));
        this.processRFC76Response(data);
    }
    /**
     * Process RFC-76 response (common for MTU and D2H)
     * Error response: [header][error_low][error_high] (3 bytes)
     * Data response: [header][payload...]
     */
    processRFC76Response(data) {
        if (data.length === 0) {
            console.log('PFTP: Empty response (success)');
            const promise = this.responsePromise;
            this.responsePromise = null;
            this.responseBuffer = [];
            if (promise) {
                promise.resolve(new Uint8Array(0));
            }
            return;
        }
        const header = data[0];
        const next = header & 0x01;
        const status = (header & 0x06) >> 1;
        const sequence = header >> 4;
        console.log(`RFC76: seq=${sequence}, status=${status}, next=${next}`);
        const RFC76_STATUS_ERROR_OR_RESPONSE = 0;
        const RFC76_STATUS_LAST = 1;
        if (status === RFC76_STATUS_ERROR_OR_RESPONSE) {
            if (data.length >= 3) {
                const errorCode = data[1] | (data[2] << 8);
                if (errorCode === 0) {
                    console.log('✅ PFTP: Success response (error code 0)');
                    const promise = this.responsePromise;
                    this.responsePromise = null;
                    this.responseBuffer = [];
                    if (promise) {
                        promise.resolve(new Uint8Array(0));
                    }
                }
                else {
                    console.error(`❌ PFTP ERROR: code=${errorCode} (0x${errorCode.toString(16)})`);
                    const promise = this.responsePromise;
                    this.responsePromise = null;
                    this.responseBuffer = [];
                    if (promise) {
                        promise.reject(new Error(`PFTP error code: ${errorCode}`));
                    }
                }
            }
            else if (data.length === 1) {
                console.log('✅ PFTP: Success response (header only)');
                const promise = this.responsePromise;
                this.responsePromise = null;
                this.responseBuffer = [];
                if (promise) {
                    promise.resolve(new Uint8Array(0));
                }
            }
            else {
                console.warn('PFTP: Unexpected error response length:', data.length);
                const promise = this.responsePromise;
                this.responsePromise = null;
                this.responseBuffer = [];
                if (promise) {
                    promise.resolve(data);
                }
            }
            return;
        }
        if (data.length > 1) {
            const payload = data.slice(1);
            this.responseBuffer.push(payload);
        }
        if (status === RFC76_STATUS_LAST || next === 0) {
            const totalLength = this.responseBuffer.reduce((sum, chunk) => sum + chunk.length, 0);
            const combined = new Uint8Array(totalLength);
            let offset = 0;
            for (const chunk of this.responseBuffer) {
                combined.set(chunk, offset);
                offset += chunk.length;
            }
            console.log(`PFTP: Complete response: ${combined.length} bytes`);
            const promise = this.responsePromise;
            this.responsePromise = null;
            this.responseBuffer = [];
            if (promise) {
                promise.resolve(combined);
            }
            else {
                console.warn('PFTP: Response received but no promise waiting');
            }
        }
    }
    /**
     * Wait for response from device
     */
    waitForResponse(timeout = 10000) {
        return new Promise((resolve, reject) => {
            this.responsePromise = { resolve, reject };
            setTimeout(() => {
                if (this.responsePromise) {
                    this.responsePromise.reject(new Error('PFTP operation timeout'));
                    this.responsePromise = null;
                    this.responseBuffer = [];
                }
            }, timeout);
        });
    }
    /**
     * Check PFTP response for errors
     */
    checkResponse(response) {
        if (response.length === 0) {
            return; // Empty response = success
        }
        // Short responses like 0x02 0x07 are errors
        if (response.length === 2) {
            const errorType = response[0];
            const errorCode = response[1];
            console.error(`PFTP error: type=${errorType}, code=${errorCode}`);
            throw new Error(`PFTP error: type=${errorType}, code=${errorCode}`);
        }
        // Check for longer error response
        if (response.length >= 4) {
            const view = new DataView(response.buffer);
            const errorCode = view.getUint32(0, true);
            if (errorCode !== 0) {
                throw new Error(`PFTP error code: ${errorCode}`);
            }
        }
    }
    /**
     * Clean up resources
     */
    async cleanup() {
        try {
            await this.d2hCharacteristic.stopNotifications();
        }
        catch (error) {
            console.warn('Failed to stop PFTP notifications:', error);
        }
    }
}
//# sourceMappingURL=pftp-client.js.map