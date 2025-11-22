# Polar SDK Capacitor Wrapper - Extended Features

## Overview

The Capacitor wrapper has been extended to match all the functionality available in the Flutter wrapper. This document outlines the new features and how to use them.

## New Features Added

### 1. Device Management
- **`connectToDevice(options: { identifier: string })`** - Connect to a specific Polar device
- **`disconnectFromDevice(options: { identifier: string })`** - Disconnect from a device
- **`searchForDevice()`** - Search for available Polar devices

### 2. Stream Settings
- **`getAvailableOnlineStreamDataTypes(options: { identifier: string })`** - Get available streaming data types
- **`requestStreamSettings(options: { identifier: string; feature: PolarDataType })`** - Request stream settings for a specific data type

### 3. Additional Streaming Types
- **`startGyroStreaming()`** - Stream gyroscope data
- **`startMagnetometerStreaming()`** - Stream magnetometer data
- **`startPpgStreaming()`** - Stream PPG (photoplethysmography) data
- **`startPpiStreaming()`** - Stream PPI (pulse-to-pulse interval) data
- **`startTemperatureStreaming()`** - Stream temperature data (iOS only)
- **`startPressureStreaming()`** - Stream pressure data (iOS only)
- **`stopStreaming(options: { identifier: string; feature: PolarDataType })`** - Stop any streaming

### 4. H10 Recording Features
- **`startRecording()`** - Start recording exercise on H10
- **`stopRecording()`** - Stop recording
- **`requestRecordingStatus()`** - Get current recording status
- **`listExercises()`** - List stored exercises
- **`fetchExercise()`** - Fetch exercise data
- **`removeExercise()`** - Remove stored exercise

### 5. Offline Recording
- **`getAvailableOfflineRecordingDataTypes()`** - Get available offline recording types
- **`requestOfflineRecordingSettings()`** - Request offline recording settings
- **`startOfflineRecording()`** - Start offline recording
- **`stopOfflineRecording()`** - Stop offline recording
- **`getOfflineRecordingStatus()`** - Get offline recording status
- **`listOfflineRecordings()`** - List offline recordings
- **`getOfflineRecord()`** - Get offline recording data
- **`removeOfflineRecord()`** - Remove offline recording

### 6. Device Configuration
- **`setLedConfig()`** - Configure LED settings (for Verity Sense)
- **`doFactoryReset()`** - Perform factory reset
- **`enableSdkMode()`** - Enable SDK mode
- **`disableSdkMode()`** - Disable SDK mode
- **`isSdkModeEnabled()`** - Check if SDK mode is enabled

### 7. Time & Disk Management
- **`getDiskSpace()`** - Get device disk space information
- **`getLocalTime()`** - Get device local time
- **`setLocalTime()`** - Set device local time

### 8. First Time Use (FTU)
- **`doFirstTimeUse()`** - Configure device for first time use (Polar 360)
- **`isFtuDone()`** - Check if FTU is completed

### 9. Activity Data
- **`deleteStoredDeviceData()`** - Delete stored data by type
- **`deleteDeviceDateFolders()`** - Delete date folders
- **`getSteps()`** - Get steps data
- **`getDistance()`** - Get distance data
- **`getActiveTime()`** - Get active time data
- **`getActivitySampleData()`** - Get detailed activity samples

### 10. Sync Notifications
- **`sendInitializationAndStartSyncNotifications()`** - Start efficient data transfer mode
- **`sendTerminateAndStopSyncNotifications()`** - End efficient data transfer mode

### 11. Event Listeners

New event listeners have been added:
- `deviceConnected` - Fired when device connects
- `deviceConnecting` - Fired when connection attempt starts
- `deviceDisconnected` - Fired when device disconnects
- `deviceFound` - Fired when device is found during search
- `blePowerStateChanged` - Fired when Bluetooth state changes
- `batteryLevelReceived` - Fired when battery level is received
- `disInformationReceived` - Fired when DIS information is received
- `sdkFeatureReady` - Fired when SDK feature becomes ready
- `gyroData` - Fired when gyro data is received
- `magnetometerData` - Fired when magnetometer data is received
- `ppgData` - Fired when PPG data is received
- `ppiData` - Fired when PPI data is received
- `temperatureData` - Fired when temperature data is received
- `pressureData` - Fired when pressure data is received

## Data Types

### PolarDataType Enum
```typescript
enum PolarDataType {
  HR = 0,
  ECG = 1,
  ACC = 2,
  PPG = 3,
  PPI = 4,
  GYRO = 5,
  MAGNETOMETER = 6,
  TEMPERATURE = 7,
  PRESSURE = 8
}
```

### RecordingInterval Enum
```typescript
enum RecordingInterval {
  INTERVAL_1S = 0,
  INTERVAL_5S = 1
}
```

### SampleType Enum
```typescript
enum SampleType {
  HR = 0,
  RR = 1
}
```

## Usage Example

```typescript
import { PolarSdk, PolarDataType } from 'capacitor-polar-sdk';

// Search for devices
await PolarSdk.searchForDevice();

// Listen for found devices
PolarSdk.addListener('deviceFound', (device) => {
  console.log('Found device:', device.deviceId);
});

// Connect to a device
await PolarSdk.connectToDevice({ identifier: 'A1B2C3D4' });

// Get available stream types
const { types } = await PolarSdk.getAvailableOnlineStreamDataTypes({ 
  identifier: 'A1B2C3D4' 
});

// Start ECG streaming
await PolarSdk.startEcgStreaming({ identifier: 'A1B2C3D4' });

// Listen for ECG data
PolarSdk.addListener('ecgData', (data) => {
  console.log('ECG data:', data.yV, data.timestamp);
});

// Get device disk space
const { freeSpace, totalSpace } = await PolarSdk.getDiskSpace({ 
  identifier: 'A1B2C3D4' 
});

// Get steps data
const { data: stepsData } = await PolarSdk.getSteps({
  identifier: 'A1B2C3D4',
  fromDate: '2024-01-01',
  toDate: '2024-01-31'
});
```

## Breaking Changes

The following changes may affect existing code:

1. The old `connectPolar()` method has been replaced with `connectToDevice(options)` which requires a device identifier
2. `disconnectPolar()` is now `disconnectFromDevice(options)` and requires an identifier
3. Stream methods now require an identifier parameter
4. Stop methods have been unified into a single `stopStreaming()` method

## Platform-Specific Notes

### iOS
- All features are fully supported
- Temperature and pressure streaming are available

### Android
- Temperature and pressure streaming are not yet implemented
- Activity data methods require Android API level 26+ (Android 8.0 Oreo)
- Some methods use Java 8 time APIs which require API 26+

## Migration from Old API

Old API:
```typescript
await PolarSdk.connectPolar(); // Auto-connect
await PolarSdk.streamHR();
await PolarSdk.stopHR();
```

New API:
```typescript
// Search and connect to specific device
await PolarSdk.searchForDevice();
await PolarSdk.connectToDevice({ identifier: 'DEVICE_ID' });
await PolarSdk.startHrStreaming({ identifier: 'DEVICE_ID' });
await PolarSdk.stopStreaming({ identifier: 'DEVICE_ID', feature: PolarDataType.HR });
```

## Additional Resources

- [Polar BLE SDK Documentation](https://github.com/polarofficial/polar-ble-sdk)
- [Flutter Polar Plugin](https://pub.dev/packages/polar)

