/**
 * Polar Protocol Buffer Definitions for Web Bluetooth
 * Based on native Polar SDK protobuf schemas
 */
import protobuf from 'protobufjs';
// PFTP Protocol Definitions
const pftpProto = `
syntax = "proto2";

package PftpRequest;

message PbPFtpOperation {
  enum Command {
    GET = 0;
    PUT = 1;
  }
  required Command command = 1;
  required string path = 2;
}

message PbPFtpError {
  required uint32 error = 1;
}

message PbDate {
  required uint32 year = 1;
  required uint32 month = 2;
  required uint32 day = 3;
}

message PbTime {
  required uint32 hour = 1;
  required uint32 minute = 2;
  required uint32 seconds = 3;
  optional uint32 millis = 4;
}

message PbPFtpSetLocalTimeParams {
  required PbDate date = 1;
  required PbTime time = 2;
  optional int32 tz_offset = 3;
}
`;
// User Identifier Protocol Definition
const userIdSchema = {
    nested: {
        UserData: {
            nested: {
                PbDate: {
                    fields: {
                        year: { type: 'uint32', id: 1, rule: 'required' },
                        month: { type: 'uint32', id: 2, rule: 'required' },
                        day: { type: 'uint32', id: 3, rule: 'required' }
                    }
                },
                PbTime: {
                    fields: {
                        hour: { type: 'uint32', id: 1, rule: 'required' },
                        minute: { type: 'uint32', id: 2, rule: 'required' },
                        second: { type: 'uint32', id: 3, rule: 'required' },
                        millis: { type: 'uint32', id: 4, rule: 'optional' },
                        tz_offset: { type: 'int32', id: 5, rule: 'optional' }
                    }
                },
                PbSystemDateTime: {
                    fields: {
                        date: { type: 'PbDate', id: 1, rule: 'required' },
                        time: { type: 'PbTime', id: 2, rule: 'required' },
                        trusted: { type: 'bool', id: 3, rule: 'required' }
                    }
                },
                PbUserIdentifier: {
                    fields: {
                        master_identifier: { type: 'uint64', id: 1, rule: 'required' },
                        user_id_last_modified: { type: 'PbSystemDateTime', id: 100, rule: 'optional' }
                    }
                }
            }
        }
    }
};
// FTU Config Protocol Definitions using JSON (more reliable)
const ftuSchema = {
    nested: {
        PolarUserData: {
            nested: {
                PbDate: {
                    fields: {
                        year: { type: 'uint32', id: 1, rule: 'required' },
                        month: { type: 'uint32', id: 2, rule: 'required' },
                        day: { type: 'uint32', id: 3, rule: 'required' }
                    }
                },
                PbTime: {
                    fields: {
                        hour: { type: 'uint32', id: 1, rule: 'required' },
                        minute: { type: 'uint32', id: 2, rule: 'required' },
                        second: { type: 'uint32', id: 3, rule: 'required' },
                        millis: { type: 'uint32', id: 4, rule: 'optional' },
                        tz_offset: { type: 'int32', id: 5, rule: 'optional' }
                    }
                },
                PbSystemDateTime: {
                    fields: {
                        date: { type: 'PbDate', id: 1, rule: 'required' },
                        time: { type: 'PbTime', id: 2, rule: 'required' },
                        trusted: { type: 'bool', id: 3, rule: 'required' }
                    }
                },
                PbGender: {
                    values: {
                        MALE: 0,
                        FEMALE: 1
                    }
                },
                PbTypicalDay: {
                    values: {
                        MOSTLY_SITTING: 1,
                        MOSTLY_STANDING: 2,
                        MOSTLY_MOVING: 3
                    }
                },
                PbUserPhysData: {
                    fields: {
                        gender: { type: 'PbGender', id: 1, rule: 'required' },
                        birthDate: { type: 'PbDate', id: 2, rule: 'required' },
                        weight: { type: 'float', id: 3, rule: 'required' },
                        height: { type: 'float', id: 4, rule: 'required' },
                        maxHeartRate: { type: 'uint32', id: 5, rule: 'required' },
                        vo2Max: { type: 'uint32', id: 6, rule: 'required' },
                        restingHeartRate: { type: 'uint32', id: 7, rule: 'required' },
                        trainingBackground: { type: 'uint32', id: 8, rule: 'required' },
                        typicalDay: { type: 'PbTypicalDay', id: 9, rule: 'required' },
                        sleepGoal: { type: 'uint32', id: 10, rule: 'required' },
                        lastModified: { type: 'PbSystemDateTime', id: 11, rule: 'required' }
                    }
                }
            }
        }
    }
};
// Create root
let pftpRoot;
let ftuRoot;
let userIdRoot;
export const initProtobuf = () => {
    try {
        pftpRoot = protobuf.parse(pftpProto).root;
        ftuRoot = protobuf.Root.fromJSON(ftuSchema);
        userIdRoot = protobuf.Root.fromJSON(userIdSchema);
        console.log('Protocol Buffers initialized successfully');
        // Debug: Log what types were created
        const physData = ftuRoot.lookupType('PolarUserData.PbUserPhysData');
        console.log('FTU protobuf type created:', physData);
        console.log('Available fields:', Object.keys(physData.fields));
    }
    catch (error) {
        console.error('Failed to initialize protobuf:', error);
        throw error;
    }
};
export const createPftpOperation = (command, path) => {
    if (!pftpRoot)
        initProtobuf();
    const PbPFtpOperation = pftpRoot.lookupType('PftpRequest.PbPFtpOperation');
    const message = PbPFtpOperation.create({
        command: command === 'GET' ? 0 : 1,
        path: path
    });
    return PbPFtpOperation.encode(message).finish();
};
export const encodeFtuConfig = (config) => {
    if (!ftuRoot)
        initProtobuf();
    const PbUserPhysData = ftuRoot.lookupType('PolarUserData.PbUserPhysData');
    console.log('Protobuf type fields:', PbUserPhysData.fields);
    console.log('Input config:', config);
    // Parse birth date
    const birthDate = new Date(config.birthDate);
    console.log('Birth date parsed:', birthDate);
    // Parse device time
    const deviceTime = new Date(config.deviceTime);
    const tzOffset = -deviceTime.getTimezoneOffset(); // Convert to minutes
    console.log('Device time parsed:', deviceTime, 'TZ offset:', tzOffset);
    const messageData = {
        gender: config.gender === 'MALE' ? 0 : 1,
        birthDate: {
            year: birthDate.getFullYear(),
            month: birthDate.getMonth() + 1,
            day: birthDate.getDate()
        },
        weight: config.weight,
        height: config.height,
        maxHeartRate: config.maxHeartRate,
        vo2Max: config.vo2Max,
        restingHeartRate: config.restingHeartRate,
        trainingBackground: config.trainingBackground,
        typicalDay: config.typicalDay,
        sleepGoal: config.sleepGoalMinutes,
        lastModified: {
            date: {
                year: deviceTime.getFullYear(),
                month: deviceTime.getMonth() + 1,
                day: deviceTime.getDate()
            },
            time: {
                hour: deviceTime.getHours(),
                minute: deviceTime.getMinutes(),
                second: deviceTime.getSeconds(),
                millis: deviceTime.getMilliseconds(),
                tz_offset: tzOffset
            },
            trusted: true
        }
    };
    console.log('Creating protobuf message with data:', messageData);
    const message = PbUserPhysData.create(messageData);
    console.log('Message created:', message);
    console.log('Message fields:', JSON.stringify(message, null, 2));
    // Verify the message
    const errMsg = PbUserPhysData.verify(message);
    if (errMsg) {
        console.error('Protobuf verification failed:', errMsg);
        throw new Error(`Invalid FTU message: ${errMsg}`);
    }
    console.log('Message verified successfully');
    const encoded = PbUserPhysData.encode(message).finish();
    console.log('Encoded to', encoded.length, 'bytes:', Array.from(encoded).map(b => '0x' + b.toString(16).padStart(2, '0')).join(' '));
    // Try to decode it back to verify
    try {
        const decoded = PbUserPhysData.decode(encoded);
        console.log('Decode test successful:', decoded);
    }
    catch (error) {
        console.error('Decode test failed:', error);
    }
    return encoded;
};
export const encodeUserIdentifier = (deviceTime) => {
    if (!userIdRoot)
        initProtobuf();
    const PbUserIdentifier = userIdRoot.lookupType('UserData.PbUserIdentifier');
    const now = new Date(deviceTime);
    const tzOffset = -now.getTimezoneOffset();
    const messageData = {
        master_identifier: '18446744073709551615',
        user_id_last_modified: {
            date: {
                year: now.getFullYear(),
                month: now.getMonth() + 1,
                day: now.getDate()
            },
            time: {
                hour: now.getHours(),
                minute: now.getMinutes(),
                second: now.getSeconds(),
                millis: now.getMilliseconds(),
                tz_offset: tzOffset
            },
            trusted: true
        }
    };
    console.log('Creating user identifier protobuf:', messageData);
    const message = PbUserIdentifier.create(messageData);
    const encoded = PbUserIdentifier.encode(message).finish();
    console.log('User identifier encoded to', encoded.length, 'bytes');
    return encoded;
};
export const encodeSetLocalTimeParams = (time) => {
    if (!pftpRoot)
        initProtobuf();
    const PbPFtpSetLocalTimeParams = pftpRoot.lookupType('PftpRequest.PbPFtpSetLocalTimeParams');
    const tzOffset = -time.getTimezoneOffset();
    const messageData = {
        date: {
            year: time.getFullYear(),
            month: time.getMonth() + 1,
            day: time.getDate()
        },
        time: {
            hour: time.getHours(),
            minute: time.getMinutes(),
            seconds: time.getSeconds(),
            millis: time.getMilliseconds()
        },
        tz_offset: tzOffset
    };
    console.log('Creating setLocalTime params:', messageData);
    const message = PbPFtpSetLocalTimeParams.create(messageData);
    const encoded = PbPFtpSetLocalTimeParams.encode(message).finish();
    console.log('SetLocalTime params encoded to', encoded.length, 'bytes');
    return encoded;
};
//# sourceMappingURL=polar-protobuf.js.map