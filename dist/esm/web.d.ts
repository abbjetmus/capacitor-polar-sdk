import { WebPlugin } from '@capacitor/core';
import type { PolarSdkPlugin, PolarSensorSetting, PolarExerciseEntry, PolarExerciseData, PolarOfflineRecordingEntry, PolarOfflineRecordingData, LedConfig, PolarFirstTimeUseConfig, PolarStepsData, PolarDistanceData, PolarActiveTimeData, PolarActivitySampleData, RecordingInterval, SampleType } from './definitions';
import { PolarDataType } from './definitions';
export declare class PolarSdkWeb extends WebPlugin implements PolarSdkPlugin {
    private devices;
    private initialized;
    private ftuStatusMap;
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
    requestStreamSettings(_options: {
        identifier: string;
        feature: PolarDataType;
    }): Promise<PolarSensorSetting>;
    startHrStreaming(options: {
        identifier: string;
    }): Promise<{
        value: boolean;
    }>;
    startEcgStreaming(_options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startAccStreaming(_options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startGyroStreaming(_options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startMagnetometerStreaming(_options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startPpgStreaming(_options: {
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
    startTemperatureStreaming(_options: {
        identifier: string;
        settings?: PolarSensorSetting;
    }): Promise<{
        value: boolean;
    }>;
    startPressureStreaming(_options: {
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
    private setupBatteryService;
    private parseHeartRate;
    private parseBatteryLevel;
    private extractDeviceId;
    private getDataTypeName;
    private handleDeviceDisconnected;
    private parsePmdData;
    private parsePpiData;
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
}
