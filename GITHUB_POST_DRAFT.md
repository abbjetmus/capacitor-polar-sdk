# Web Bluetooth Implementation for Polar SDK - Seeking Contributors

## Introduction

I've successfully implemented a **Web Bluetooth** version of the Polar SDK for Capacitor, enabling Polar device connectivity directly in web browsers. This implementation supports Polar 360 devices and provides heart rate monitoring, PPI streaming, offline recording, and First Time Use (FTU) configuration.

**Repository**: [https://github.com/abbjetmus/capacitor-polar-sdk](https://github.com/abbjetmus/capacitor-polar-sdk)

## What's Working ✅

- Device scanning and connection via Web Bluetooth API
- Heart rate (HR) streaming
- PPI (Pulse-to-Pulse Interval) streaming
- Battery level monitoring
- Offline recording start/stop for PPI, ACC, and PPG
- Basic First Time Use (FTU) configuration
- Device disconnect handling

## The Challenge: PFTP and Protocol Buffers on Web

While the basic streaming features work well, there's a **significant technical challenge** with implementing the **Polar File Transfer Protocol (PFTP)** and **Protocol Buffers** for file operations over Web Bluetooth. This is where I need help from the community.

### Understanding the Problem

#### 1. PFTP Protocol Complexity

PFTP uses **RFC-76 framing** which requires:

- **Sequence Number Management**: Each packet has a 4-bit sequence number (0-15) that must wrap correctly
- **Status Bits**: 2-bit status indicating error/response (0), last packet (1), or continuation (2)
- **Next Bit**: Indicates if more packets follow
- **MTU-based Fragmentation**: Messages larger than MTU (typically 20-512 bytes) must be split into multiple packets
- **Response Reassembly**: Multi-packet responses arrive asynchronously via notifications and must be reassembled in order

**Current Implementation Status:**

```typescript
// RFC-76 Frame Format
// Byte 0: [sequence:4bits][status:2bits][reserved:1bit][next:1bit]
// Bytes 1+: Payload data

// Example from pftp-client.ts:
const header = (this.sequenceNumber << 4) | (status << 1) | next;
const packet = new Uint8Array(1 + chunk.length);
packet[0] = header;
packet.set(chunk, 1);
```

**The Challenge:**

- Web Bluetooth notifications arrive asynchronously and may be out of order
- Sequence number tracking must handle wraparound correctly
- Status bit interpretation varies by operation type
- Error responses have different formats than data responses
- MTU negotiation is limited in Web Bluetooth (no direct control)

#### 2. Protocol Buffers on Web

The native Polar SDK uses Protocol Buffers for structured data exchange, but on the web we must:

- **Manually Define Schemas**: No direct access to native `.proto` files
- **JavaScript/TypeScript Implementation**: Using `protobufjs` library
- **Exact Encoding Match**: Must match native SDK byte-for-byte
- **Complex Nested Structures**: Dates, times, user data with timezone offsets

**Current Implementation:**

```typescript
// Example: FTU Configuration Encoding
const ftuSchema = {
  nested: {
    PolarUserData: {
      nested: {
        PbUserPhysData: {
          fields: {
            gender: { type: 'PbGender', id: 1 },
            birthDate: { type: 'PbDate', id: 2 },
            weight: { type: 'float', id: 3 },
            // ... many more fields
          },
        },
      },
    },
  },
};
```

**The Challenge:**

- Schema definitions must exactly match native SDK
- Field IDs, types, and nesting must be precise
- Encoding/decoding must produce identical byte sequences
- Timezone offset handling is complex
- No official protobuf definitions from Polar (reverse-engineered)

#### 3. Web Bluetooth Limitations

Unlike native implementations, Web Bluetooth has constraints:

- **Limited MTU Control**: Default 20 bytes, can negotiate up to ~512 bytes, but negotiation is browser-dependent
- **Asynchronous Notifications**: All data arrives via event listeners, requiring careful state management
- **No Background Operation**: Browser tab must be active
- **Browser Quirks**: Different behavior across Chrome, Edge, Opera
- **User Gesture Required**: Connection must be initiated by user action

**The Challenge:**

- Large file transfers (like offline recordings) require many packets
- Response reassembly must handle timing issues
- No direct control over Bluetooth stack behavior
- Error recovery is more complex

### What Needs Work 🔧

1. **Complete PFTP Implementation**
   - Full GET operation for file retrieval
   - Proper error handling and recovery
   - Session management (if required by device)
   - Large file transfer optimization

2. **Protocol Buffer Schema Accuracy**
   - Verification against native SDK output
   - Complete schema definitions for all operations
   - Proper handling of optional fields
   - Timezone and date/time encoding verification

3. **Robustness Improvements**
   - Better error recovery for dropped packets
   - Sequence number synchronization
   - MTU negotiation handling
   - Timeout and retry logic

4. **Testing and Validation**
   - Cross-browser testing
   - Device compatibility testing
   - Edge case handling
   - Performance optimization

## Technical Details

### PFTP Implementation Structure

The current implementation includes:

- **`PftpClient` class**: Handles RFC-76 framing, packet fragmentation, and response reassembly
- **`polar-protobuf.ts`**: Protocol Buffer schema definitions and encoding functions
- **`web.ts`**: Web Bluetooth integration and device management

### Key Files

- `src/pftp-client.ts` - PFTP protocol implementation
- `src/polar-protobuf.ts` - Protocol Buffer definitions
- `src/web.ts` - Web Bluetooth integration
- `WEB_BLUETOOTH_README.md` - Detailed documentation

### Example: Current FTU Implementation

```typescript
// Encoding FTU config to Protocol Buffers
const ftuData = encodeFtuConfig({
  gender: 'MALE',
  birthDate: '1990-01-15',
  height: 175,
  weight: 70,
  // ... more fields
});

// Writing via PFTP
const pftpClient = new PftpClient(mtuChar, d2hChar, h2dChar);
await pftpClient.initialize();
const operation = createPftpOperation('PUT', '/U/0/S/PHYSDATA.BPB');
await pftpClient.writeFile('/U/0/S/PHYSDATA.BPB', ftuData, operation);
```

**What works**: Basic PUT operations for small files
**What's tricky**: GET operations, large files, error handling, protocol edge cases

## Call for Contributors

I'm looking for help from developers who have experience with:

- **Bluetooth Low Energy (BLE)** protocols
- **Protocol Buffers** encoding/decoding
- **Web Bluetooth API** limitations and workarounds
- **Binary protocol** implementation
- **Polar SDK** native implementations (for reference)

### How to Contribute

1. **Review the Implementation**: Check out the code in `src/pftp-client.ts` and `src/polar-protobuf.ts`
2. **Test with Devices**: If you have a Polar 360 or other supported device, test the current implementation
3. **Identify Issues**: Document edge cases, errors, or protocol mismatches
4. **Propose Solutions**: Suggest improvements or submit pull requests
5. **Share Knowledge**: If you have experience with Polar's native SDK, help verify protocol buffer schemas

### Areas Needing Expertise

- **PFTP Protocol**: Deep understanding of RFC-76 framing and Polar's file transfer protocol
- **Protocol Buffers**: Experience with protobuf encoding/decoding, especially for embedded systems
- **Web Bluetooth**: Knowledge of Web Bluetooth API limitations and best practices
- **Binary Protocols**: Experience implementing complex binary protocols in JavaScript/TypeScript

## Why This Matters

Web Bluetooth support opens up Polar device integration to web applications, which offers significant advantages over native mobile app development:

### Easier Development Experience

- **Faster iteration**: No app store review process, instant deployment, and hot reload capabilities mean you can see changes immediately
- **Single codebase**: Write once, run on any device with a compatible browser (desktop, mobile, tablet) - no need to maintain separate iOS and Android codebases
- **Familiar tools**: Use standard web technologies (HTML, CSS, JavaScript/TypeScript) instead of learning platform-specific languages like Swift or Kotlin
- **Better debugging**: Browser DevTools provide excellent debugging capabilities with breakpoints, network inspection, and performance profiling - much more accessible than native development environments
- **Simpler deployment**: Just deploy to a web server - no need to build separate iOS/Android apps, manage certificates, or navigate app store requirements

### More Pleasant Development

- **Lower barrier to entry**: Web developers can contribute without learning Swift/Kotlin or setting up Xcode/Android Studio
- **Better tooling**: Rich ecosystem of web development tools, frameworks (React, Vue, Angular), and libraries that developers are already familiar with
- **Easier testing**: Test in browser without device simulators or physical hardware setup - just open DevTools and debug
- **Rapid prototyping**: Quickly build and test features without compilation steps or waiting for builds to complete
- **Cross-platform by default**: One implementation works across all platforms with Web Bluetooth support - no platform-specific code needed

### Real-World Benefits

- **Faster time-to-market**: Develop and deploy features quickly without app store approval delays
- **Lower maintenance**: One codebase instead of maintaining iOS and Android separately reduces bugs and development time
- **Easier updates**: Push updates instantly without app store approval - fix bugs and add features on the fly
- **Broader reach**: Works on any device with a modern browser, not just mobile - desktop, laptop, tablet, and mobile all supported

However, the PFTP and Protocol Buffer challenges are blocking full feature parity with native implementations, particularly for:

- Retrieving offline recordings
- Complete FTU configuration
- File system operations
- Large data transfers

Solving these challenges will unlock the full potential of web-based Polar device integration, making it easier and more accessible for developers to build applications with Polar devices.

## Questions for the Polar Team

If anyone from Polar is reading this:

1. **Protocol Buffer Schemas**: Are there official `.proto` files available for the PFTP operations?
2. **PFTP Documentation**: Is there detailed documentation for the PFTP protocol beyond the basic RFC-76 framing?
3. **Web Bluetooth Support**: Is Polar planning official Web Bluetooth support, or can we collaborate on this implementation?
4. **Testing**: Would it be possible to get test devices or access to protocol specifications for validation?

## Conclusion

This implementation demonstrates that Web Bluetooth support for Polar devices is **technically feasible**, but completing the PFTP and Protocol Buffer implementation requires expertise in low-level Bluetooth protocols and binary data encoding.

**I'm actively seeking contributors** to help overcome these challenges and make full Web Bluetooth support a reality for the Polar SDK community.

---

**Repository**: [https://github.com/abbjetmus/capacitor-polar-sdk](https://github.com/abbjetmus/capacitor-polar-sdk)  
**Issues**: Please open issues for bugs, questions, or feature requests  
**Pull Requests**: Contributions are welcome!
