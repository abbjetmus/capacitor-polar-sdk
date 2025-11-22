/**
 * Polar File Transfer Protocol (PFTP) Client for Web Bluetooth
 * Implements RFC-76 framing and file operations
 */
export declare class PftpClient {
    private mtuCharacteristic;
    private d2hCharacteristic;
    private sequenceNumber;
    private resetSequenceAfterWrite;
    private responseBuffer;
    private responsePromise;
    private mtu;
    constructor(mtuChar: any, d2hChar: any, _h2dChar: any);
    initialize(): Promise<void>;
    /**
     * Send PFTP query via MTU characteristic
     * Used for queries like SET_LOCAL_TIME, GET_LOCAL_TIME, etc.
     */
    sendQuery(queryId: number, params?: Uint8Array): Promise<Uint8Array>;
    /**
     * Write a file to the device using PFTP PUT operation
     */
    writeFile(path: string, data: Uint8Array, operation: Uint8Array): Promise<void>;
    /**
     * Send PFTP request via MTU characteristic with RFC-76 framing
     * Used for GET/PUT operations
     */
    private sendRequest;
    /**
     * Handle MTU notifications (for query/request responses)
     * RFC-76 frame format:
     * Byte 0: [sequence:4bits][status:2bits][next:1bit]
     */
    private handleMTUNotification;
    /**
     * Handle D2H notifications (for device notifications)
     * RFC-76 frame format:
     * Byte 0: [sequence:4bits][status:2bits][next:1bit]
     */
    private handleD2HNotification;
    /**
     * Process RFC-76 response (common for MTU and D2H)
     * Error response: [header][error_low][error_high] (3 bytes)
     * Data response: [header][payload...]
     */
    private processRFC76Response;
    /**
     * Wait for response from device
     */
    private waitForResponse;
    /**
     * Check PFTP response for errors
     */
    private checkResponse;
    /**
     * Clean up resources
     */
    cleanup(): Promise<void>;
}
