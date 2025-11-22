/**
 * Polar Protocol Buffer Definitions for Web Bluetooth
 * Based on native Polar SDK protobuf schemas
 */
export declare const initProtobuf: () => void;
export declare const createPftpOperation: (command: 'GET' | 'PUT', path: string) => Uint8Array;
export interface FtuConfig {
    gender: 'MALE' | 'FEMALE';
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
export declare const encodeFtuConfig: (config: FtuConfig) => Uint8Array;
export declare const encodeUserIdentifier: (deviceTime: string) => Uint8Array;
export declare const encodeSetLocalTimeParams: (time: Date) => Uint8Array;
