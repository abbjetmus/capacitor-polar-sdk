# Web Bluetooth Support for Capacitor Polar SDK

## Overview

This implementation adds Web Bluetooth API support to the Capacitor Polar SDK, enabling heart rate monitoring and battery level tracking from Polar 360 devices directly in web browsers.

## Browser Compatibility

Web Bluetooth is supported in:

- Chrome 56+ (desktop & Android)
- Edge 79+
- Opera 43+
- Samsung Internet 6.0+

**Note:** Safari and Firefox do not currently support Web Bluetooth API.

## Supported Features on Web

### ✅ Implemented

- Device scanning and connection
- Heart rate (HR) streaming
- **PPI (Pulse-to-Pulse Interval) streaming** ⭐ NEW
- **Offline recording for PPI, ACC, and PPG** ⭐ NEW
- **First Time Use (FTU) configuration** ⭐ NEW
- Battery level monitoring
- Device disconnect handling

### ⚠️ Experimental/Limited Support

- PPI streaming (requires Polar 360, data parsing may need refinement)
- Offline recording data retrieval (PFTP protocol partially implemented)
- **First Time Use (FTU) - Simplified implementation** (stores status locally, full device configuration requires Protocol Buffers)

### ❌ Not Supported on Web

- ECG streaming
- Live Accelerometer streaming
- Live Gyroscope data
- Live Magnetometer data
- Live PPG streaming
- Temperature streaming
- Pressure streaming
- Exercise data retrieval
- Device configuration (LED, factory reset, etc.)

## Usage

### 1. Initialize the SDK

```typescript
import { PolarSdk } from 'capacitor-polar-sdk';

await PolarSdk.initialize();
```

### 2. Search for Devices

```typescript
const result = await PolarSdk.searchForDevice();
// User will see a browser dialog to select a Polar 360 device
```

### 3. Connect to Device

```typescript
const deviceId = 'E49E872C'; // The ID from the device name (e.g., "Polar 360 E49E872C")
await PolarSdk.connectToDevice({ identifier: deviceId });

// Listen for connection events
PolarSdk.addListener('deviceConnected', (info) => {
  console.log('Device connected:', info);
});

PolarSdk.addListener('deviceDisconnected', (info) => {
  console.log('Device disconnected:', info);
});
```

### 4. Monitor Battery Level

```typescript
PolarSdk.addListener('batteryLevelReceived', (data) => {
  console.log(`Battery level: ${data.level}%`);
});
```

### 5. Start Heart Rate Streaming

```typescript
// Listen for HR data
PolarSdk.addListener('hrData', (data) => {
  console.log(`Heart rate: ${data.data.hr} bpm`);
});

// Start streaming
await PolarSdk.startHrStreaming({ identifier: deviceId });
```

### 6. Stop Streaming

```typescript
import { PolarDataType } from 'capacitor-polar-sdk';

await PolarSdk.stopStreaming({
  identifier: deviceId,
  feature: PolarDataType.HR,
});
```

### 7. PPI Streaming (NEW)

```typescript
// Listen for PPI data
PolarSdk.addListener('ppiData', (data) => {
  console.log('PPI Data:', data.data);
  data.data.samples.forEach((sample: any) => {
    console.log(`PP Interval: ${sample.ppInterval}ms`);
    console.log(`Blocker: ${sample.blocker}`);
    console.log(`Skin Contact: ${sample.skinContact}`);
  });
});

// Start PPI streaming
await PolarSdk.startPpiStreaming({ identifier: deviceId });

// Stop PPI streaming
await PolarSdk.stopStreaming({
  identifier: deviceId,
  feature: PolarDataType.PPI,
});
```

**Important Notes about PPI:**

- PPI should only be used at complete rest (no movement)
- Samples with `blocker: true` or `skinContact: false` should be discarded
- More sensitive to noise than ECG-based RR intervals

### 8. Offline Recording (NEW)

```typescript
// Check available offline recording types
const availableTypes = await PolarSdk.getAvailableOfflineRecordingDataTypes({
  identifier: deviceId,
});
console.log('Available types:', availableTypes.types); // [PPI, ACC, PPG]

// Start offline recording
await PolarSdk.startOfflineRecording({
  identifier: deviceId,
  feature: PolarDataType.PPI,
});

// Check recording status
const status = await PolarSdk.getOfflineRecordingStatus({ identifier: deviceId });
console.log('Recording:', status.features); // [PPI]

// Stop offline recording
await PolarSdk.stopOfflineRecording({
  identifier: deviceId,
  feature: PolarDataType.PPI,
});

// List offline recordings (NEW)
const recordings = await PolarSdk.listOfflineRecordings({ identifier: deviceId });
console.log('Recordings:', recordings.entries);

// Retrieve a specific recording (NEW)
if (recordings.entries.length > 0) {
  const entry = recordings.entries[0];
  const data = await PolarSdk.getOfflineRecord({
    identifier: deviceId,
    entry: entry,
  });
  console.log('Recording data:', data);

  // Remove the recording after retrieval
  await PolarSdk.removeOfflineRecord({
    identifier: deviceId,
    entry: entry,
  });
}
```

**Important Notes about Offline Recording:**

- The PFTP (Polar File Transfer Protocol) is partially implemented
- `listOfflineRecordings()` returns entries based on active recording sessions
- `getOfflineRecord()` establishes PFTP service connection but full file retrieval requires complete PFTP implementation

### 9. First Time Use (FTU) Configuration (NEW)

**IMPORTANT for Polar 360:** The Polar 360 device requires proper user setup using `doFirstTimeUse()` before it can track wellness data (activity, sleep). Without FTU, the device shows a "waiting for setup" animation.

```typescript
// Check if FTU has been completed
const ftuStatus = await PolarSdk.isFtuDone({ identifier: deviceId });
console.log('FTU completed:', ftuStatus.done);

// Configure device with user data
const ftuConfig = {
  gender: 'MALE', // 'MALE' or 'FEMALE'
  birthDate: '1990-01-15', // ISO format
  height: 175, // cm (90-240)
  weight: 70, // kg (15-300)
  maxHeartRate: 190, // bpm (100-240) - can use formula: 220 - age
  vo2Max: 45, // ml/kg/min (10-95) - fitness level indicator
  restingHeartRate: 60, // bpm (20-120)
  trainingBackground: 20, // 10=occasional, 20=regular, 30=frequent, 40=heavy, 50=semi-pro, 60=pro
  deviceTime: new Date().toISOString(), // Current time
  typicalDay: 2, // 1=mostly sitting, 2=mostly standing, 3=mostly moving
  sleepGoalMinutes: 480, // 300-660 minutes (5-11 hours)
};

await PolarSdk.doFirstTimeUse({
  identifier: deviceId,
  config: ftuConfig,
});

console.log('FTU configuration completed!');
```

**Parameter Guidelines:**

- **gender**: Biological sex for accurate wellness calculations
- **maxHeartRate**: Use formula `220 - age` as starting point
- **vo2Max**: See [Polar VO2max guide](https://www.polar.com/blog/vo2max/) for age/fitness tables
- **restingHeartRate**: Measure when fully rested, default 60 bpm is reasonable
- **trainingBackground**:
  - 10: Occasional (< 1 hour/week)
  - 20: Regular (1-3 hours/week)
  - 30: Frequent (3-5 hours/week)
  - 40: Heavy (5-9 hours/week)
  - 50: Semi-professional (9-12 hours/week)
  - 60: Professional (> 12 hours/week)
- **typicalDay**: Daily activity level for setting activity goals
- **sleepGoalMinutes**: Personal nightly sleep target

**Web Implementation Limitations:**

The web implementation currently:

- ✅ Validates all FTU parameters
- ✅ Establishes PFTP service connection
- ✅ Stores FTU status locally
- ⚠️ Does NOT write full configuration to device (requires Protocol Buffers)
- ⚠️ For production Polar 360 wellness tracking, use native iOS/Android SDK

The simplified web version is useful for development/testing but full FTU requires the native implementation.

### 10. Disconnect

```typescript
await PolarSdk.disconnectFromDevice({ identifier: deviceId });
```

## Complete Example

```typescript
import { PolarSdk, PolarDataType } from 'capacitor-polar-sdk';

class PolarService {
  private deviceId: string | null = null;

  async init() {
    try {
      await PolarSdk.initialize();
      this.setupListeners();
    } catch (error) {
      console.error('Web Bluetooth not available:', error);
    }
  }

  setupListeners() {
    PolarSdk.addListener('deviceConnected', (info) => {
      console.log('Connected:', info);
    });

    PolarSdk.addListener('deviceDisconnected', (info) => {
      console.log('Disconnected:', info);
      this.deviceId = null;
    });

    PolarSdk.addListener('batteryLevelReceived', (data) => {
      console.log(`Battery: ${data.level}%`);
    });

    PolarSdk.addListener('hrData', (data) => {
      console.log(`HR: ${data.data.hr} bpm`);
    });
  }

  async connect() {
    try {
      const result = await PolarSdk.searchForDevice();
      if (result.value) {
        // Device ID will be extracted from the selected device
        this.deviceId = 'E49E872C'; // Replace with actual device ID
        await PolarSdk.connectToDevice({ identifier: this.deviceId });
      }
    } catch (error) {
      console.error('Connection failed:', error);
    }
  }

  async startHR() {
    if (!this.deviceId) {
      throw new Error('No device connected');
    }
    await PolarSdk.startHrStreaming({ identifier: this.deviceId });
  }

  async stopHR() {
    if (!this.deviceId) return;
    await PolarSdk.stopStreaming({
      identifier: this.deviceId,
      feature: PolarDataType.HR,
    });
  }

  async disconnect() {
    if (!this.deviceId) return;
    await this.stopHR();
    await PolarSdk.disconnectFromDevice({ identifier: this.deviceId });
  }
}
```

## Vue 3 / Quasar Example

```vue
<template>
  <q-page class="q-pa-md">
    <q-btn @click="connect" :disable="connected">Connect Polar 360</q-btn>
    <q-btn @click="startHR" :disable="!connected">Start HR</q-btn>
    <q-btn @click="stopHR" :disable="!connected">Stop HR</q-btn>

    <div v-if="connected">
      <p>Battery: {{ batteryLevel }}%</p>
      <p>Heart Rate: {{ heartRate }} bpm</p>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { PolarSdk, PolarDataType } from 'capacitor-polar-sdk';
import type { PluginListenerHandle } from '@capacitor/core';

const connected = ref(false);
const batteryLevel = ref(0);
const heartRate = ref(0);
const deviceId = ref<string | null>(null);

const listeners: PluginListenerHandle[] = [];

onMounted(async () => {
  try {
    await PolarSdk.initialize();

    listeners.push(
      await PolarSdk.addListener('deviceConnected', () => {
        connected.value = true;
      }),
      await PolarSdk.addListener('deviceDisconnected', () => {
        connected.value = false;
        deviceId.value = null;
      }),
      await PolarSdk.addListener('batteryLevelReceived', (data) => {
        batteryLevel.value = data.level;
      }),
      await PolarSdk.addListener('hrData', (data) => {
        heartRate.value = data.data.hr;
      }),
    );
  } catch (error) {
    console.error('Initialization failed:', error);
  }
});

onUnmounted(async () => {
  if (deviceId.value) {
    await PolarSdk.disconnectFromDevice({ identifier: deviceId.value });
  }
  listeners.forEach((listener) => listener.remove());
});

async function connect() {
  try {
    const result = await PolarSdk.searchForDevice();
    if (result.value) {
      deviceId.value = 'E49E872C'; // Get from device selection
      await PolarSdk.connectToDevice({ identifier: deviceId.value });
    }
  } catch (error) {
    console.error('Connection failed:', error);
  }
}

async function startHR() {
  if (!deviceId.value) return;
  try {
    await PolarSdk.startHrStreaming({ identifier: deviceId.value });
  } catch (error) {
    console.error('Failed to start HR streaming:', error);
  }
}

async function stopHR() {
  if (!deviceId.value) return;
  try {
    await PolarSdk.stopStreaming({
      identifier: deviceId.value,
      feature: PolarDataType.HR,
    });
  } catch (error) {
    console.error('Failed to stop HR streaming:', error);
  }
}
</script>
```

## Security Considerations

1. **HTTPS Required**: Web Bluetooth API only works on HTTPS sites (or localhost for testing)
2. **User Gesture**: Device connection must be initiated by a user action (button click)
3. **Pairing**: The browser will handle device pairing automatically

## Troubleshooting

### "Web Bluetooth API is not available"

- Ensure you're using a compatible browser
- Check that your site is served over HTTPS
- On Android, enable "Experimental Web Platform features" in chrome://flags

### Connection Fails

- Ensure the Polar 360 device is charged and nearby
- Check that Bluetooth is enabled on your computer/device
- Try resetting the device if it was previously paired with another device

### No Heart Rate Data

- Ensure the device is worn correctly on your wrist
- Check that HR streaming has been started
- Verify the device is properly connected (not just paired)

## Technical Details

### GATT Services Used

- **Heart Rate Service**: `0x180D` (Standard)
  - Heart Rate Measurement Characteristic: `0x2A37`
- **Battery Service**: `0x180F` (Standard)
  - Battery Level Characteristic: `0x2A19`
- **PMD (Polar Measurement Data) Service**: `FB005C80-02E7-F387-1CAD-8ACD2D8DF0C8` (Proprietary)
  - PMD Control Point: `FB005C81-02E7-F387-1CAD-8ACD2D8DF0C8`
  - PMD Data: `FB005C82-02E7-F387-1CAD-8ACD2D8DF0C8`
- **PSFTP (Polar File Transfer Protocol) Service**: `0000FEEE-0000-1000-8000-00805F9B34FB` (Proprietary)
  - MTU Characteristic: `FB005C51-02E7-F387-1CAD-8ACD2D8DF0C8`
  - Device-to-Host Notification: `FB005C52-02E7-F387-1CAD-8ACD2D8DF0C8`
  - Host-to-Device Notification: `FB005C53-02E7-F387-1CAD-8ACD2D8DF0C8`

### Device Detection

- Manufacturer ID: `0x006B` (Polar Electro OY)
- Name prefix: "Polar 360"

### Data Parsing

**Heart Rate:** Follows Bluetooth Heart Rate Service specification

- First byte contains flags
- Heart rate is encoded as either uint8 or uint16 depending on flags

**Battery Level:** Simple uint8 (0-100%)

**PPI Data:** Proprietary Polar format via PMD service

- Each sample contains:
  - `ppInterval` (uint16): Pulse-to-pulse interval in milliseconds
  - `errorEstimate` (uint16): Error estimate for the measurement
  - `blocker` (bool): Movement detected flag
  - `skinContactSupported` (bool): Whether skin contact detection is supported
  - `skinContact` (bool): Whether skin contact is detected

### PMD Control Commands

- `GET_MEASUREMENT_SETTINGS`: 0x01
- `REQUEST_MEASUREMENT_START`: 0x02
- `STOP_MEASUREMENT`: 0x03
- `GET_SDK_MODE_SETTINGS`: 0x04
- `GET_MEASUREMENT_STATUS`: 0x05

### PMD Measurement Types

- PPI: 0x03
- ACC: 0x02
- PPG: 0x01
- ECG: 0x00 (not implemented)

## Known Limitations

1. PFTP (Polar File Transfer Protocol) is partially implemented
   - Offline recording data retrieval connects to PFTP service but doesn't perform full file transfer
   - Complete PFTP implementation requires Protocol Buffers and complex frame handling
   - For production offline recording data retrieval, use native iOS/Android implementation
2. PPI data parsing may need refinement for edge cases
3. Browser must support Web Bluetooth API (Chrome, Edge, Opera only)
4. User must manually approve device connection each time
5. No background operation support
6. Offline recordings are tracked in browser memory, not persisted across sessions

## Platform Comparison

| Feature                        | iOS/Android Native | Web               |
| ------------------------------ | ------------------ | ----------------- |
| Heart Rate                     | ✅                 | ✅                |
| PPI Streaming                  | ✅                 | ✅ NEW            |
| ECG                            | ✅                 | ❌                |
| Accelerometer                  | ✅                 | ❌ (offline only) |
| PPG                            | ✅                 | ❌ (offline only) |
| Battery Level                  | ✅                 | ✅                |
| Offline Recording (Start/Stop) | ✅                 | ✅ NEW            |
| Offline Recording (Retrieval)  | ✅                 | ⚠️ (partial)      |
| First Time Use (FTU)           | ✅                 | ⚠️ (simplified)   |
| Background Operation           | ✅                 | ❌                |

## References

- [Web Bluetooth API Specification](https://webbluetoothcg.github.io/web-bluetooth/)
- [Polar BLE SDK Documentation](https://github.com/polarofficial/polar-ble-sdk)
- [Heart Rate Service Specification](https://www.bluetooth.com/specifications/specs/heart-rate-service-1-0/)
