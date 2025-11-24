# capacitor-polar-sdk

Ionic Capacitor plugin to connect Polar 360 sensors.

> **Note**: This is an independent fork with completely different functionality from the original project. This project is actively maintained and accepts contributions.

## Install

```bash
npm install capacitor-polar-sdk
npx cap sync
```

## API

<docgen-index>

- [`initialize()`](#initialize)
- [`connectToDevice(...)`](#connecttodevice)
- [`disconnectFromDevice(...)`](#disconnectfromdevice)
- [`searchForDevice()`](#searchfordevice)
- [`getAvailableOnlineStreamDataTypes(...)`](#getavailableonlinestreamdatatypes)
- [`requestStreamSettings(...)`](#requeststreamsettings)
- [`startHrStreaming(...)`](#starthrstreaming)
- [`startEcgStreaming(...)`](#startecgstreaming)
- [`startAccStreaming(...)`](#startaccstreaming)
- [`startGyroStreaming(...)`](#startgyrostreaming)
- [`startMagnetometerStreaming(...)`](#startmagnetometerstreaming)
- [`startPpgStreaming(...)`](#startppgstreaming)
- [`startPpiStreaming(...)`](#startppistreaming)
- [`startTemperatureStreaming(...)`](#starttemperaturestreaming)
- [`startPressureStreaming(...)`](#startpressurestreaming)
- [`stopStreaming(...)`](#stopstreaming)
- [`startRecording(...)`](#startrecording)
- [`stopRecording(...)`](#stoprecording)
- [`requestRecordingStatus(...)`](#requestrecordingstatus)
- [`listExercises(...)`](#listexercises)
- [`fetchExercise(...)`](#fetchexercise)
- [`removeExercise(...)`](#removeexercise)
- [`setLedConfig(...)`](#setledconfig)
- [`doFactoryReset(...)`](#dofactoryreset)
- [`enableSdkMode(...)`](#enablesdkmode)
- [`disableSdkMode(...)`](#disablesdkmode)
- [`isSdkModeEnabled(...)`](#issdkmodeenabled)
- [`getAvailableOfflineRecordingDataTypes(...)`](#getavailableofflinerecordingdatatypes)
- [`requestOfflineRecordingSettings(...)`](#requestofflinerecordingsettings)
- [`startOfflineRecording(...)`](#startofflinerecording)
- [`stopOfflineRecording(...)`](#stopofflinerecording)
- [`getOfflineRecordingStatus(...)`](#getofflinerecordingstatus)
- [`listOfflineRecordings(...)`](#listofflinerecordings)
- [`getOfflineRecord(...)`](#getofflinerecord)
- [`removeOfflineRecord(...)`](#removeofflinerecord)
- [`getDiskSpace(...)`](#getdiskspace)
- [`getLocalTime(...)`](#getlocaltime)
- [`setLocalTime(...)`](#setlocaltime)
- [`doFirstTimeUse(...)`](#dofirsttimeuse)
- [`getConnectionStatus(...)`](#getconnectionstatus)
- [`isFtuDone(...)`](#isftudone)
- [`deleteStoredDeviceData(...)`](#deletestoreddevicedata)
- [`deleteDeviceDateFolders(...)`](#deletedevicedatefolders)
- [`getSteps(...)`](#getsteps)
- [`getDistance(...)`](#getdistance)
- [`getActiveTime(...)`](#getactivetime)
- [`getActivitySampleData(...)`](#getactivitysampledata)
- [`sendInitializationAndStartSyncNotifications(...)`](#sendinitializationandstartsyncnotifications)
- [`sendTerminateAndStopSyncNotifications(...)`](#sendterminateandstopsyncnotifications)
- [`addListener('hrData', ...)`](#addlistenerhrdata-)
- [`addListener('ecgData', ...)`](#addlistenerecgdata-)
- [`addListener('accData', ...)`](#addlisteneraccdata-)
- [`addListener('gyroData', ...)`](#addlistenergyrodata-)
- [`addListener('magnetometerData', ...)`](#addlistenermagnetometerdata-)
- [`addListener('ppgData', ...)`](#addlistenerppgdata-)
- [`addListener('ppiData', ...)`](#addlistenerppidata-)
- [`addListener('temperatureData', ...)`](#addlistenertemperaturedata-)
- [`addListener('pressureData', ...)`](#addlistenerpressuredata-)
- [`addListener('deviceConnected', ...)`](#addlistenerdeviceconnected-)
- [`addListener('deviceConnecting', ...)`](#addlistenerdeviceconnecting-)
- [`addListener('deviceDisconnected', ...)`](#addlistenerdevicedisconnected-)
- [`addListener('deviceFound', ...)`](#addlistenerdevicefound-)
- [`addListener('blePowerStateChanged', ...)`](#addlistenerblepowerstatechanged-)
- [`addListener('batteryLevelReceived', ...)`](#addlistenerbatterylevelreceived-)
- [`addListener('disInformationReceived', ...)`](#addlistenerdisinformationreceived-)
- [`addListener('sdkFeatureReady', ...)`](#addlistenersdkfeatureready-)
- [`removeAllListeners()`](#removealllisteners)
- [Interfaces](#interfaces)
- [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize()

```typescript
initialize() => Promise<void>
```

---

### connectToDevice(...)

```typescript
connectToDevice(options: { identifier: string; }) => Promise<void>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

---

### disconnectFromDevice(...)

```typescript
disconnectFromDevice(options: { identifier: string; }) => Promise<void>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

---

### searchForDevice()

```typescript
searchForDevice() => Promise<{ value: boolean; }>
```

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### getAvailableOnlineStreamDataTypes(...)

```typescript
getAvailableOnlineStreamDataTypes(options: { identifier: string; }) => Promise<{ types: PolarDataType[]; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ types: PolarDataType[]; }&gt;</code>

---

### requestStreamSettings(...)

```typescript
requestStreamSettings(options: { identifier: string; feature: PolarDataType; }) => Promise<PolarSensorSetting>
```

| Param         | Type                                                                                      |
| ------------- | ----------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; feature: <a href="#polardatatype">PolarDataType</a>; }</code> |

**Returns:** <code>Promise&lt;<a href="#polarsensorsetting">PolarSensorSetting</a>&gt;</code>

---

### startHrStreaming(...)

```typescript
startHrStreaming(options: { identifier: string; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### startEcgStreaming(...)

```typescript
startEcgStreaming(options: { identifier: string; settings?: PolarSensorSetting; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                                                                                  |
| ------------- | ----------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; settings?: <a href="#polarsensorsetting">PolarSensorSetting</a>; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### startAccStreaming(...)

```typescript
startAccStreaming(options: { identifier: string; settings?: PolarSensorSetting; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                                                                                  |
| ------------- | ----------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; settings?: <a href="#polarsensorsetting">PolarSensorSetting</a>; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### startGyroStreaming(...)

```typescript
startGyroStreaming(options: { identifier: string; settings?: PolarSensorSetting; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                                                                                  |
| ------------- | ----------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; settings?: <a href="#polarsensorsetting">PolarSensorSetting</a>; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### startMagnetometerStreaming(...)

```typescript
startMagnetometerStreaming(options: { identifier: string; settings?: PolarSensorSetting; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                                                                                  |
| ------------- | ----------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; settings?: <a href="#polarsensorsetting">PolarSensorSetting</a>; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### startPpgStreaming(...)

```typescript
startPpgStreaming(options: { identifier: string; settings?: PolarSensorSetting; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                                                                                  |
| ------------- | ----------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; settings?: <a href="#polarsensorsetting">PolarSensorSetting</a>; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### startPpiStreaming(...)

```typescript
startPpiStreaming(options: { identifier: string; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### startTemperatureStreaming(...)

```typescript
startTemperatureStreaming(options: { identifier: string; settings?: PolarSensorSetting; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                                                                                  |
| ------------- | ----------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; settings?: <a href="#polarsensorsetting">PolarSensorSetting</a>; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### startPressureStreaming(...)

```typescript
startPressureStreaming(options: { identifier: string; settings?: PolarSensorSetting; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                                                                                  |
| ------------- | ----------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; settings?: <a href="#polarsensorsetting">PolarSensorSetting</a>; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### stopStreaming(...)

```typescript
stopStreaming(options: { identifier: string; feature: PolarDataType; }) => Promise<{ value: boolean; }>
```

| Param         | Type                                                                                      |
| ------------- | ----------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; feature: <a href="#polardatatype">PolarDataType</a>; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

---

### startRecording(...)

```typescript
startRecording(options: { identifier: string; exerciseId: string; interval: RecordingInterval; sampleType: SampleType; }) => Promise<void>
```

| Param         | Type                                                                                                                                                                     |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`options`** | <code>{ identifier: string; exerciseId: string; interval: <a href="#recordinginterval">RecordingInterval</a>; sampleType: <a href="#sampletype">SampleType</a>; }</code> |

---

### stopRecording(...)

```typescript
stopRecording(options: { identifier: string; }) => Promise<void>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

---

### requestRecordingStatus(...)

```typescript
requestRecordingStatus(options: { identifier: string; }) => Promise<{ ongoing: boolean; entryId: string; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ ongoing: boolean; entryId: string; }&gt;</code>

---

### listExercises(...)

```typescript
listExercises(options: { identifier: string; }) => Promise<{ entries: PolarExerciseEntry[]; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ entries: PolarExerciseEntry[]; }&gt;</code>

---

### fetchExercise(...)

```typescript
fetchExercise(options: { identifier: string; entry: PolarExerciseEntry; }) => Promise<PolarExerciseData>
```

| Param         | Type                                                                                              |
| ------------- | ------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; entry: <a href="#polarexerciseentry">PolarExerciseEntry</a>; }</code> |

**Returns:** <code>Promise&lt;<a href="#polarexercisedata">PolarExerciseData</a>&gt;</code>

---

### removeExercise(...)

```typescript
removeExercise(options: { identifier: string; entry: PolarExerciseEntry; }) => Promise<void>
```

| Param         | Type                                                                                              |
| ------------- | ------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; entry: <a href="#polarexerciseentry">PolarExerciseEntry</a>; }</code> |

---

### setLedConfig(...)

```typescript
setLedConfig(options: { identifier: string; config: LedConfig; }) => Promise<void>
```

| Param         | Type                                                                             |
| ------------- | -------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; config: <a href="#ledconfig">LedConfig</a>; }</code> |

---

### doFactoryReset(...)

```typescript
doFactoryReset(options: { identifier: string; preservePairingInformation: boolean; }) => Promise<void>
```

| Param         | Type                                                                      |
| ------------- | ------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; preservePairingInformation: boolean; }</code> |

---

### enableSdkMode(...)

```typescript
enableSdkMode(options: { identifier: string; }) => Promise<void>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

---

### disableSdkMode(...)

```typescript
disableSdkMode(options: { identifier: string; }) => Promise<void>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

---

### isSdkModeEnabled(...)

```typescript
isSdkModeEnabled(options: { identifier: string; }) => Promise<{ enabled: boolean; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ enabled: boolean; }&gt;</code>

---

### getAvailableOfflineRecordingDataTypes(...)

```typescript
getAvailableOfflineRecordingDataTypes(options: { identifier: string; }) => Promise<{ types: PolarDataType[]; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ types: PolarDataType[]; }&gt;</code>

---

### requestOfflineRecordingSettings(...)

```typescript
requestOfflineRecordingSettings(options: { identifier: string; feature: PolarDataType; }) => Promise<PolarSensorSetting | null>
```

| Param         | Type                                                                                      |
| ------------- | ----------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; feature: <a href="#polardatatype">PolarDataType</a>; }</code> |

**Returns:** <code>Promise&lt;<a href="#polarsensorsetting">PolarSensorSetting</a> | null&gt;</code>

---

### startOfflineRecording(...)

```typescript
startOfflineRecording(options: { identifier: string; feature: PolarDataType; settings?: PolarSensorSetting; }) => Promise<void>
```

| Param         | Type                                                                                                                                                       |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; feature: <a href="#polardatatype">PolarDataType</a>; settings?: <a href="#polarsensorsetting">PolarSensorSetting</a>; }</code> |

---

### stopOfflineRecording(...)

```typescript
stopOfflineRecording(options: { identifier: string; feature: PolarDataType; }) => Promise<void>
```

| Param         | Type                                                                                      |
| ------------- | ----------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; feature: <a href="#polardatatype">PolarDataType</a>; }</code> |

---

### getOfflineRecordingStatus(...)

```typescript
getOfflineRecordingStatus(options: { identifier: string; }) => Promise<{ features: PolarDataType[]; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ features: PolarDataType[]; }&gt;</code>

---

### listOfflineRecordings(...)

```typescript
listOfflineRecordings(options: { identifier: string; }) => Promise<{ entries: PolarOfflineRecordingEntry[]; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ entries: PolarOfflineRecordingEntry[]; }&gt;</code>

---

### getOfflineRecord(...)

```typescript
getOfflineRecord(options: { identifier: string; entry: PolarOfflineRecordingEntry; }) => Promise<PolarOfflineRecordingData>
```

| Param         | Type                                                                                                              |
| ------------- | ----------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; entry: <a href="#polarofflinerecordingentry">PolarOfflineRecordingEntry</a>; }</code> |

**Returns:** <code>Promise&lt;<a href="#polarofflinerecordingdata">PolarOfflineRecordingData</a>&gt;</code>

---

### removeOfflineRecord(...)

```typescript
removeOfflineRecord(options: { identifier: string; entry: PolarOfflineRecordingEntry; }) => Promise<void>
```

| Param         | Type                                                                                                              |
| ------------- | ----------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; entry: <a href="#polarofflinerecordingentry">PolarOfflineRecordingEntry</a>; }</code> |

---

### getDiskSpace(...)

```typescript
getDiskSpace(options: { identifier: string; }) => Promise<{ freeSpace: number; totalSpace: number; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ freeSpace: number; totalSpace: number; }&gt;</code>

---

### getLocalTime(...)

```typescript
getLocalTime(options: { identifier: string; }) => Promise<{ time: string; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ time: string; }&gt;</code>

---

### setLocalTime(...)

```typescript
setLocalTime(options: { identifier: string; time: Date; }) => Promise<void>
```

| Param         | Type                                                                 |
| ------------- | -------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; time: <a href="#date">Date</a>; }</code> |

---

### doFirstTimeUse(...)

```typescript
doFirstTimeUse(options: { identifier: string; config: PolarFirstTimeUseConfig; }) => Promise<void>
```

| Param         | Type                                                                                                         |
| ------------- | ------------------------------------------------------------------------------------------------------------ |
| **`options`** | <code>{ identifier: string; config: <a href="#polarfirsttimeuseconfig">PolarFirstTimeUseConfig</a>; }</code> |

---

### getConnectionStatus(...)

```typescript
getConnectionStatus(options: { identifier: string; }) => Promise<{ connected: boolean; gattConnected: boolean; deviceName: string; services: string[]; browserPaired: boolean; details: string; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ connected: boolean; gattConnected: boolean; deviceName: string; services: string[]; browserPaired: boolean; details: string; }&gt;</code>

---

### isFtuDone(...)

```typescript
isFtuDone(options: { identifier: string; }) => Promise<{ done: boolean; message?: string; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ done: boolean; message?: string; }&gt;</code>

---

### deleteStoredDeviceData(...)

```typescript
deleteStoredDeviceData(options: { identifier: string; dataType: number; until: string; }) => Promise<void>
```

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; dataType: number; until: string; }</code> |

---

### deleteDeviceDateFolders(...)

```typescript
deleteDeviceDateFolders(options: { identifier: string; fromDate: string; toDate: string; }) => Promise<void>
```

| Param         | Type                                                                   |
| ------------- | ---------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; fromDate: string; toDate: string; }</code> |

---

### getSteps(...)

```typescript
getSteps(options: { identifier: string; fromDate: string; toDate: string; }) => Promise<{ data: PolarStepsData[]; }>
```

| Param         | Type                                                                   |
| ------------- | ---------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; fromDate: string; toDate: string; }</code> |

**Returns:** <code>Promise&lt;{ data: PolarStepsData[]; }&gt;</code>

---

### getDistance(...)

```typescript
getDistance(options: { identifier: string; fromDate: string; toDate: string; }) => Promise<{ data: PolarDistanceData[]; }>
```

| Param         | Type                                                                   |
| ------------- | ---------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; fromDate: string; toDate: string; }</code> |

**Returns:** <code>Promise&lt;{ data: PolarDistanceData[]; }&gt;</code>

---

### getActiveTime(...)

```typescript
getActiveTime(options: { identifier: string; fromDate: string; toDate: string; }) => Promise<{ data: PolarActiveTimeData[]; }>
```

| Param         | Type                                                                   |
| ------------- | ---------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; fromDate: string; toDate: string; }</code> |

**Returns:** <code>Promise&lt;{ data: PolarActiveTimeData[]; }&gt;</code>

---

### getActivitySampleData(...)

```typescript
getActivitySampleData(options: { identifier: string; fromDate: string; toDate: string; }) => Promise<{ data: PolarActivitySampleData[]; }>
```

| Param         | Type                                                                   |
| ------------- | ---------------------------------------------------------------------- |
| **`options`** | <code>{ identifier: string; fromDate: string; toDate: string; }</code> |

**Returns:** <code>Promise&lt;{ data: PolarActivitySampleData[]; }&gt;</code>

---

### sendInitializationAndStartSyncNotifications(...)

```typescript
sendInitializationAndStartSyncNotifications(options: { identifier: string; }) => Promise<{ success: boolean; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

---

### sendTerminateAndStopSyncNotifications(...)

```typescript
sendTerminateAndStopSyncNotifications(options: { identifier: string; }) => Promise<void>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ identifier: string; }</code> |

---

### addListener('hrData', ...)

```typescript
addListener(eventName: 'hrData', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'hrData'</code>               |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('ecgData', ...)

```typescript
addListener(eventName: 'ecgData', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'ecgData'</code>              |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('accData', ...)

```typescript
addListener(eventName: 'accData', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'accData'</code>              |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('gyroData', ...)

```typescript
addListener(eventName: 'gyroData', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'gyroData'</code>             |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('magnetometerData', ...)

```typescript
addListener(eventName: 'magnetometerData', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'magnetometerData'</code>     |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('ppgData', ...)

```typescript
addListener(eventName: 'ppgData', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'ppgData'</code>              |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('ppiData', ...)

```typescript
addListener(eventName: 'ppiData', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'ppiData'</code>              |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('temperatureData', ...)

```typescript
addListener(eventName: 'temperatureData', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'temperatureData'</code>      |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('pressureData', ...)

```typescript
addListener(eventName: 'pressureData', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'pressureData'</code>         |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('deviceConnected', ...)

```typescript
addListener(eventName: 'deviceConnected', listenerFunc: (data: PolarDeviceInfo) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                           |
| ------------------ | ------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'deviceConnected'</code>                                                 |
| **`listenerFunc`** | <code>(data: <a href="#polardeviceinfo">PolarDeviceInfo</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('deviceConnecting', ...)

```typescript
addListener(eventName: 'deviceConnecting', listenerFunc: (data: PolarDeviceInfo) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                           |
| ------------------ | ------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'deviceConnecting'</code>                                                |
| **`listenerFunc`** | <code>(data: <a href="#polardeviceinfo">PolarDeviceInfo</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('deviceDisconnected', ...)

```typescript
addListener(eventName: 'deviceDisconnected', listenerFunc: (data: PolarDeviceInfo) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                           |
| ------------------ | ------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'deviceDisconnected'</code>                                              |
| **`listenerFunc`** | <code>(data: <a href="#polardeviceinfo">PolarDeviceInfo</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('deviceFound', ...)

```typescript
addListener(eventName: 'deviceFound', listenerFunc: (data: PolarDeviceInfo) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                           |
| ------------------ | ------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'deviceFound'</code>                                                     |
| **`listenerFunc`** | <code>(data: <a href="#polardeviceinfo">PolarDeviceInfo</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('blePowerStateChanged', ...)

```typescript
addListener(eventName: 'blePowerStateChanged', listenerFunc: (data: { enabled: boolean; }) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                  |
| ------------------ | ----------------------------------------------------- |
| **`eventName`**    | <code>'blePowerStateChanged'</code>                   |
| **`listenerFunc`** | <code>(data: { enabled: boolean; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('batteryLevelReceived', ...)

```typescript
addListener(eventName: 'batteryLevelReceived', listenerFunc: (data: { identifier: string; level: number; }) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                   |
| ------------------ | ---------------------------------------------------------------------- |
| **`eventName`**    | <code>'batteryLevelReceived'</code>                                    |
| **`listenerFunc`** | <code>(data: { identifier: string; level: number; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('disInformationReceived', ...)

```typescript
addListener(eventName: 'disInformationReceived', listenerFunc: (data: { identifier: string; uuid: string; value: string; }) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                                 |
| ------------------ | ------------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'disInformationReceived'</code>                                                |
| **`listenerFunc`** | <code>(data: { identifier: string; uuid: string; value: string; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### addListener('sdkFeatureReady', ...)

```typescript
addListener(eventName: 'sdkFeatureReady', listenerFunc: (data: { identifier: string; feature: number; }) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                     |
| ------------------ | ------------------------------------------------------------------------ |
| **`eventName`**    | <code>'sdkFeatureReady'</code>                                           |
| **`listenerFunc`** | <code>(data: { identifier: string; feature: number; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

---

### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

---

### Interfaces

#### PolarSensorSetting

| Prop           | Type                                      |
| -------------- | ----------------------------------------- |
| **`settings`** | <code>{ [key: string]: number[]; }</code> |

#### PolarExerciseEntry

| Prop          | Type                |
| ------------- | ------------------- |
| **`path`**    | <code>string</code> |
| **`date`**    | <code>string</code> |
| **`entryId`** | <code>string</code> |

#### PolarExerciseData

| Prop           | Type                  |
| -------------- | --------------------- |
| **`interval`** | <code>number</code>   |
| **`samples`**  | <code>number[]</code> |

#### LedConfig

| Prop             | Type                 |
| ---------------- | -------------------- |
| **`ledEnabled`** | <code>boolean</code> |

#### PolarOfflineRecordingEntry

| Prop       | Type                |
| ---------- | ------------------- |
| **`path`** | <code>string</code> |
| **`size`** | <code>number</code> |
| **`date`** | <code>string</code> |
| **`type`** | <code>string</code> |

#### PolarOfflineRecordingData

| Prop            | Type                                                              |
| --------------- | ----------------------------------------------------------------- |
| **`startTime`** | <code>string</code>                                               |
| **`settings`**  | <code><a href="#polarsensorsetting">PolarSensorSetting</a></code> |

#### Date

Enables basic storage and retrieval of dates and times.

| Method                 | Signature                                                                                                    | Description                                                                                                                             |
| ---------------------- | ------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------- |
| **toString**           | () =&gt; string                                                                                              | Returns a string representation of a date. The format of the string depends on the locale.                                              |
| **toDateString**       | () =&gt; string                                                                                              | Returns a date as a string value.                                                                                                       |
| **toTimeString**       | () =&gt; string                                                                                              | Returns a time as a string value.                                                                                                       |
| **toLocaleString**     | () =&gt; string                                                                                              | Returns a value as a string value appropriate to the host environment's current locale.                                                 |
| **toLocaleDateString** | () =&gt; string                                                                                              | Returns a date as a string value appropriate to the host environment's current locale.                                                  |
| **toLocaleTimeString** | () =&gt; string                                                                                              | Returns a time as a string value appropriate to the host environment's current locale.                                                  |
| **valueOf**            | () =&gt; number                                                                                              | Returns the stored time value in milliseconds since midnight, January 1, 1970 UTC.                                                      |
| **getTime**            | () =&gt; number                                                                                              | Gets the time value in milliseconds.                                                                                                    |
| **getFullYear**        | () =&gt; number                                                                                              | Gets the year, using local time.                                                                                                        |
| **getUTCFullYear**     | () =&gt; number                                                                                              | Gets the year using Universal Coordinated Time (UTC).                                                                                   |
| **getMonth**           | () =&gt; number                                                                                              | Gets the month, using local time.                                                                                                       |
| **getUTCMonth**        | () =&gt; number                                                                                              | Gets the month of a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                             |
| **getDate**            | () =&gt; number                                                                                              | Gets the day-of-the-month, using local time.                                                                                            |
| **getUTCDate**         | () =&gt; number                                                                                              | Gets the day-of-the-month, using Universal Coordinated Time (UTC).                                                                      |
| **getDay**             | () =&gt; number                                                                                              | Gets the day of the week, using local time.                                                                                             |
| **getUTCDay**          | () =&gt; number                                                                                              | Gets the day of the week using Universal Coordinated Time (UTC).                                                                        |
| **getHours**           | () =&gt; number                                                                                              | Gets the hours in a date, using local time.                                                                                             |
| **getUTCHours**        | () =&gt; number                                                                                              | Gets the hours value in a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                       |
| **getMinutes**         | () =&gt; number                                                                                              | Gets the minutes of a <a href="#date">Date</a> object, using local time.                                                                |
| **getUTCMinutes**      | () =&gt; number                                                                                              | Gets the minutes of a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                           |
| **getSeconds**         | () =&gt; number                                                                                              | Gets the seconds of a <a href="#date">Date</a> object, using local time.                                                                |
| **getUTCSeconds**      | () =&gt; number                                                                                              | Gets the seconds of a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                           |
| **getMilliseconds**    | () =&gt; number                                                                                              | Gets the milliseconds of a <a href="#date">Date</a>, using local time.                                                                  |
| **getUTCMilliseconds** | () =&gt; number                                                                                              | Gets the milliseconds of a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                      |
| **getTimezoneOffset**  | () =&gt; number                                                                                              | Gets the difference in minutes between the time on the local computer and Universal Coordinated Time (UTC).                             |
| **setTime**            | (time: number) =&gt; number                                                                                  | Sets the date and time value in the <a href="#date">Date</a> object.                                                                    |
| **setMilliseconds**    | (ms: number) =&gt; number                                                                                    | Sets the milliseconds value in the <a href="#date">Date</a> object using local time.                                                    |
| **setUTCMilliseconds** | (ms: number) =&gt; number                                                                                    | Sets the milliseconds value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                              |
| **setSeconds**         | (sec: number, ms?: number \| undefined) =&gt; number                                                         | Sets the seconds value in the <a href="#date">Date</a> object using local time.                                                         |
| **setUTCSeconds**      | (sec: number, ms?: number \| undefined) =&gt; number                                                         | Sets the seconds value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                   |
| **setMinutes**         | (min: number, sec?: number \| undefined, ms?: number \| undefined) =&gt; number                              | Sets the minutes value in the <a href="#date">Date</a> object using local time.                                                         |
| **setUTCMinutes**      | (min: number, sec?: number \| undefined, ms?: number \| undefined) =&gt; number                              | Sets the minutes value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                   |
| **setHours**           | (hours: number, min?: number \| undefined, sec?: number \| undefined, ms?: number \| undefined) =&gt; number | Sets the hour value in the <a href="#date">Date</a> object using local time.                                                            |
| **setUTCHours**        | (hours: number, min?: number \| undefined, sec?: number \| undefined, ms?: number \| undefined) =&gt; number | Sets the hours value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                     |
| **setDate**            | (date: number) =&gt; number                                                                                  | Sets the numeric day-of-the-month value of the <a href="#date">Date</a> object using local time.                                        |
| **setUTCDate**         | (date: number) =&gt; number                                                                                  | Sets the numeric day of the month in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                        |
| **setMonth**           | (month: number, date?: number \| undefined) =&gt; number                                                     | Sets the month value in the <a href="#date">Date</a> object using local time.                                                           |
| **setUTCMonth**        | (month: number, date?: number \| undefined) =&gt; number                                                     | Sets the month value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                     |
| **setFullYear**        | (year: number, month?: number \| undefined, date?: number \| undefined) =&gt; number                         | Sets the year of the <a href="#date">Date</a> object using local time.                                                                  |
| **setUTCFullYear**     | (year: number, month?: number \| undefined, date?: number \| undefined) =&gt; number                         | Sets the year value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                      |
| **toUTCString**        | () =&gt; string                                                                                              | Returns a date converted to a string using Universal Coordinated Time (UTC).                                                            |
| **toISOString**        | () =&gt; string                                                                                              | Returns a date as a string value in ISO format.                                                                                         |
| **toJSON**             | (key?: any) =&gt; string                                                                                     | Used by the JSON.stringify method to enable the transformation of an object's data for JavaScript Object Notation (JSON) serialization. |

#### PolarFirstTimeUseConfig

| Prop                     | Type                |
| ------------------------ | ------------------- |
| **`gender`**             | <code>string</code> |
| **`birthDate`**          | <code>string</code> |
| **`height`**             | <code>number</code> |
| **`weight`**             | <code>number</code> |
| **`maxHeartRate`**       | <code>number</code> |
| **`vo2Max`**             | <code>number</code> |
| **`restingHeartRate`**   | <code>number</code> |
| **`trainingBackground`** | <code>number</code> |
| **`deviceTime`**         | <code>string</code> |
| **`typicalDay`**         | <code>number</code> |
| **`sleepGoalMinutes`**   | <code>number</code> |

#### PolarStepsData

| Prop        | Type                |
| ----------- | ------------------- |
| **`date`**  | <code>string</code> |
| **`steps`** | <code>number</code> |

#### PolarDistanceData

| Prop           | Type                |
| -------------- | ------------------- |
| **`date`**     | <code>string</code> |
| **`distance`** | <code>number</code> |

#### PolarActiveTimeData

| Prop                    | Type                |
| ----------------------- | ------------------- |
| **`date`**              | <code>string</code> |
| **`activeTimeSeconds`** | <code>number</code> |

#### PolarActivitySampleData

| Prop                  | Type                |
| --------------------- | ------------------- |
| **`date`**            | <code>string</code> |
| **`samplesDataList`** | <code>any[]</code>  |

#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

#### PolarDeviceInfo

| Prop                | Type                 |
| ------------------- | -------------------- |
| **`deviceId`**      | <code>string</code>  |
| **`address`**       | <code>string</code>  |
| **`rssi`**          | <code>number</code>  |
| **`name`**          | <code>string</code>  |
| **`isConnectable`** | <code>boolean</code> |

### Enums

#### PolarDataType

| Members            | Value          |
| ------------------ | -------------- |
| **`HR`**           | <code>0</code> |
| **`ECG`**          | <code>1</code> |
| **`ACC`**          | <code>2</code> |
| **`PPG`**          | <code>3</code> |
| **`PPI`**          | <code>4</code> |
| **`GYRO`**         | <code>5</code> |
| **`MAGNETOMETER`** | <code>6</code> |
| **`TEMPERATURE`**  | <code>7</code> |
| **`PRESSURE`**     | <code>8</code> |

#### RecordingInterval

| Members           | Value          |
| ----------------- | -------------- |
| **`INTERVAL_1S`** | <code>0</code> |
| **`INTERVAL_5S`** | <code>1</code> |

#### SampleType

| Members  | Value          |
| -------- | -------------- |
| **`HR`** | <code>0</code> |
| **`RR`** | <code>1</code> |

</docgen-api>
