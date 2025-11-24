# Web Bluetooth Implementation for Polar SDK - Seeking Contributors

## Summary

I've implemented **Web Bluetooth support** for Polar devices in a Capacitor plugin, enabling Polar 360 connectivity directly in web browsers. While basic features (HR streaming, PPI, battery) work well, I'm facing challenges with **PFTP (Polar File Transfer Protocol)** and **Protocol Buffers** that need community expertise.

**Repo**: https://github.com/abbjetmus/capacitor-polar-sdk

## What Works ✅

- Device scanning/connection via Web Bluetooth
- Heart rate streaming
- PPI streaming
- Battery monitoring
- Offline recording start/stop
- Basic FTU configuration

## The Challenge: PFTP + Protocol Buffers on Web

### PFTP Protocol Complexity

PFTP uses **RFC-76 framing** requiring:

- Sequence number management (4-bit, wraps at 15)
- Status bits for error/response/continuation
- MTU-based packet fragmentation (20-512 bytes)
- Asynchronous response reassembly from notifications

**Problem**: Web Bluetooth notifications arrive asynchronously. Reassembling multi-packet responses with correct sequence tracking, handling errors, and managing MTU limitations is complex.

### Protocol Buffers on Web

Native SDK uses Protocol Buffers, but on web we must:

- Manually define schemas (no access to native `.proto` files)
- Match native encoding byte-for-byte
- Handle complex nested structures (dates, times, user data)

**Problem**: Schema definitions must exactly match native SDK. Field IDs, types, and encoding must be precise. Currently reverse-engineered from native behavior.

### Web Bluetooth Limitations

- Limited MTU control (default 20 bytes, up to ~512)
- All data via async notifications (state management complexity)
- No background operation
- Browser-specific quirks

## What Needs Help 🔧

1. **Complete PFTP GET operations** for file retrieval
2. **Protocol Buffer schema verification** against native SDK
3. **Error recovery** for dropped packets and timeouts
4. **Large file transfer** optimization
5. **Cross-browser testing** and edge case handling

## Seeking Contributors

Looking for developers with experience in:

- BLE protocols and RFC-76 framing
- Protocol Buffers encoding/decoding
- Web Bluetooth API limitations
- Binary protocol implementation
- Polar SDK native implementations (for reference)

## Technical Details

Key files:

- `src/pftp-client.ts` - PFTP implementation with RFC-76 framing
- `src/polar-protobuf.ts` - Protocol Buffer schemas
- `src/web.ts` - Web Bluetooth integration

Current implementation handles basic PUT operations but struggles with:

- GET operations for file retrieval
- Large file transfers
- Protocol edge cases
- Error recovery

## Questions for Polar Team

1. Are official `.proto` files available for PFTP operations?
2. Is there detailed PFTP documentation beyond RFC-76?
3. Any plans for official Web Bluetooth support?
4. Could we get test devices or protocol specs for validation?

## Why This Matters

Web Bluetooth support opens up Polar device integration to web applications, which offers significant advantages over native mobile app development:

**Easier Development Experience:**

- **Faster iteration**: No app store review process, instant deployment, and hot reload capabilities
- **Single codebase**: Write once, run on any device with a compatible browser (desktop, mobile, tablet)
- **Familiar tools**: Use standard web technologies (HTML, CSS, JavaScript/TypeScript) instead of platform-specific languages
- **Better debugging**: Browser DevTools provide excellent debugging capabilities compared to native development environments
- **Simpler deployment**: Just deploy to a web server - no need to build separate iOS/Android apps or manage certificates

**More Pleasant Development:**

- **Lower barrier to entry**: Web developers can contribute without learning Swift/Kotlin
- **Better tooling**: Rich ecosystem of web development tools, frameworks, and libraries
- **Easier testing**: Test in browser without device simulators or physical hardware setup
- **Rapid prototyping**: Quickly build and test features without compilation steps
- **Cross-platform by default**: One implementation works across all platforms with Web Bluetooth support

**Real-World Benefits:**

- **Faster time-to-market**: Develop and deploy features quickly
- **Lower maintenance**: One codebase instead of maintaining iOS and Android separately
- **Easier updates**: Push updates instantly without app store approval
- **Broader reach**: Works on any device with a modern browser, not just mobile

However, PFTP/Protocol Buffer challenges currently block full feature parity (offline recording retrieval, complete FTU, file operations). Solving these challenges will unlock the full potential of web-based Polar device integration.

**Contributions welcome!** Please check the repo, test with devices, and help improve the implementation.

---

**Repository**: https://github.com/abbjetmus/capacitor-polar-sdk  
**Issues/PRs**: Open for bugs, questions, and contributions
