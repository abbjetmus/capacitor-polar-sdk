import type { PluginListenerHandle } from '@capacitor/core';
export interface PolarDeviceInfo {
    deviceId: string;
    address: string;
    rssi: number;
    name: string;
    isConnectable: boolean;
}
export interface PolarSensorSetting {
    settings: {
        [key: string]: number[];
    };
}
export interface PolarExerciseEntry {
    path: string;
    date: string;
    entryId: string;
}
export interface PolarExerciseData {
    interval: number;
    samples: number[];
}
export interface PolarOfflineRecordingEntry {
    path: string;
    size: number;
    date: string;
    type: string;
}
export interface PolarOfflineRecordingData {
    startTime: string;
    settings?: PolarSensorSetting;
}
export interface LedConfig {
    ledEnabled: boolean;
}
export interface PolarFirstTimeUseConfig {
    gender: string;
    birthDate: string;
    height: number;
    weight: number;
    maxHeartRate: number;
    vo2Max: number;
    restingHeartRate: number;
    trainingBackground: number;
    deviceTime: string;
    typicalDay: number;
    sleepGoalMinutes: number;
}
export interface PolarStepsData {
    date: string;
    steps: number;
}
export interface PolarDistanceData {
    date: string;
    distance: number;
}
export interface PolarActiveTimeData {
    date: string;
    activeTimeSeconds: number;
}
export interface PolarActivitySampleData {
    date: string;
    samplesDataList: any[];
}
export declare enum PolarDataType {
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
export declare enum RecordingInterval {
    INTERVAL_1S = 0,
    INTERVAL_5S = 1
}
export declare enum SampleType {
    HR = 0,
    RR = 1
}
export interface PolarSdkPlugin {
    initialize(): Promise<void>;
    connectToDevice(options: {
        identifier: string;
    }): Promise<void>;
    disconnectFromDevice(options: {
        identifier: string;
    }): Promise<void>;
    searchForDevice(): Promise<{
        value: boolean;
    }>;
    getAvailableOnlineStreamDataTypes(options: {
        identifier: string;
    }): Promise<{
        types: PolarDataType[];
    }>;
    requestStreamSettings(options: {
        identifier: string;
        feature: PolarDataType;
    }): Promise<PolarSensorSetting>;
    startHrStreaming(options: {
        identifier: string;
    }): Promise<{
        value: boolean;
    }>;
    startEcgStreaming(options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startAccStreaming(options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startGyroStreaming(options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startMagnetometerStreaming(options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startPpgStreaming(options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startPpiStreaming(options: {
        identifier: string;
    }): Promise<{
        value: boolean;
    }>;
    startTemperatureStreaming(options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startPressureStreaming(options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    stopStreaming(options: {
        identifier: string;
        feature: PolarDataType;
    }): Promise<{
        value: boolean;
    }>;
    startRecording(options: {
        identifier: string;
        exerciseId: string;
        interval: RecordingInterval;
        sampleType: SampleType;
    }): Promise<void>;
    stopRecording(options: {
        identifier: string;
    }): Promise<void>;
    requestRecordingStatus(options: {
        identifier: string;
    }): Promise<{
        ongoing: boolean;
        entryId: string;
    }>;
    listExercises(options: {
        identifier: string;
    }): Promise<{
        entries: PolarExerciseEntry[];
    }>;
    fetchExercise(options: {
        identifier: string;
        entry: PolarExerciseEntry;
    }): Promise<PolarExerciseData>;
    removeExercise(options: {
        identifier: string;
        entry: PolarExerciseEntry;
    }): Promise<void>;
    setLedConfig(options: {
        identifier: string;
        config: LedConfig;
    }): Promise<void>;
    doFactoryReset(options: {
        identifier: string;
        preservePairingInformation: boolean;
    }): Promise<void>;
    enableSdkMode(options: {
        identifier: string;
    }): Promise<void>;
    disableSdkMode(options: {
        identifier: string;
    }): Promise<void>;
    isSdkModeEnabled(options: {
        identifier: string;
    }): Promise<{
        enabled: boolean;
    }>;
    getAvailableOfflineRecordingDataTypes(options: {
        identifier: string;
    }): Promise<{
        types: PolarDataType[];
    }>;
    requestOfflineRecordingSettings(options: {
        identifier: string;
        feature: PolarDataType;
    }): Promise<PolarSensorSetting | null>;
    startOfflineRecording(options: {
        identifier: string;
        feature: PolarDataType;
        settings?: PolarSensorSetting;
    }): Promise<void>;
    stopOfflineRecording(options: {
        identifier: string;
        feature: PolarDataType;
    }): Promise<void>;
    getOfflineRecordingStatus(options: {
        identifier: string;
    }): Promise<{
        features: PolarDataType[];
    }>;
    listOfflineRecordings(options: {
        identifier: string;
    }): Promise<{
        entries: PolarOfflineRecordingEntry[];
    }>;
    getOfflineRecord(options: {
        identifier: string;
        entry: PolarOfflineRecordingEntry;
    }): Promise<PolarOfflineRecordingData>;
    removeOfflineRecord(options: {
        identifier: string;
        entry: PolarOfflineRecordingEntry;
    }): Promise<void>;
    getDiskSpace(options: {
        identifier: string;
    }): Promise<{
        freeSpace: number;
        totalSpace: number;
    }>;
    getLocalTime(options: {
        identifier: string;
    }): Promise<{
        time: string;
    }>;
    setLocalTime(options: {
        identifier: string;
        time: Date;
    }): Promise<void>;
    doFirstTimeUse(options: {
        identifier: string;
        config: PolarFirstTimeUseConfig;
    }): Promise<void>;
    getConnectionStatus(options: {
        identifier: string;
    }): Promise<{
        connected: boolean;
        gattConnected: boolean;
        deviceName: string;
        services: string[];
        browserPaired: boolean;
        details: string;
    }>;
    isFtuDone(options: {
        identifier: string;
    }): Promise<{
        done: boolean;
        message?: string;
    }>;
    deleteStoredDeviceData(options: {
        identifier: string;
        dataType: number;
        until: string;
    }): Promise<void>;
    deleteDeviceDateFolders(options: {
        identifier: string;
        fromDate: string;
        toDate: string;
    }): Promise<void>;
    getSteps(options: {
        identifier: string;
        fromDate: string;
        toDate: string;
    }): Promise<{
        data: PolarStepsData[];
    }>;
    getDistance(options: {
        identifier: string;
        fromDate: string;
        toDate: string;
    }): Promise<{
        data: PolarDistanceData[];
    }>;
    getActiveTime(options: {
        identifier: string;
        fromDate: string;
        toDate: string;
    }): Promise<{
        data: PolarActiveTimeData[];
    }>;
    getActivitySampleData(options: {
        identifier: string;
        fromDate: string;
        toDate: string;
    }): Promise<{
        data: PolarActivitySampleData[];
    }>;
    sendInitializationAndStartSyncNotifications(options: {
        identifier: string;
    }): Promise<{
        success: boolean;
    }>;
    sendTerminateAndStopSyncNotifications(options: {
        identifier: string;
    }): Promise<void>;
    addListener(eventName: 'hrData', listenerFunc: (data: any) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'ecgData', listenerFunc: (data: any) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'accData', listenerFunc: (data: any) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'gyroData', listenerFunc: (data: any) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'magnetometerData', listenerFunc: (data: any) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'ppgData', listenerFunc: (data: any) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'ppiData', listenerFunc: (data: any) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'temperatureData', listenerFunc: (data: any) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'pressureData', listenerFunc: (data: any) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'deviceConnected', listenerFunc: (data: PolarDeviceInfo) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'deviceConnecting', listenerFunc: (data: PolarDeviceInfo) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'deviceDisconnected', listenerFunc: (data: PolarDeviceInfo) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'deviceFound', listenerFunc: (data: PolarDeviceInfo) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'blePowerStateChanged', listenerFunc: (data: {
        enabled: boolean;
    }) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'batteryLevelReceived', listenerFunc: (data: {
        identifier: string;
        level: number;
    }) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'disInformationReceived', listenerFunc: (data: {
        identifier: string;
        uuid: string;
        value: string;
    }) => void): Promise<PluginListenerHandle>;
    addListener(eventName: 'sdkFeatureReady', listenerFunc: (data: {
        identifier: string;
        feature: number;
    }) => void): Promise<PluginListenerHandle>;
    removeAllListeners(): Promise<void>;
}
