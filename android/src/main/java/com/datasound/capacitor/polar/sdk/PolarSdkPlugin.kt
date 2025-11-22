package com.datasound.capacitor.polar.sdk

import android.Manifest
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.Permission
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiCallbackProvider
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.PolarH10OfflineExerciseApi
import com.polar.sdk.api.model.LedConfig
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarExerciseEntry
import com.polar.sdk.api.model.PolarFirstTimeUseConfig
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import com.polar.sdk.api.model.PolarSensorSetting
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object DateSerializer : JsonDeserializer<Date>, JsonSerializer<Date> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date =
        Date(json?.asJsonPrimitive?.asLong ?: 0)

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement =
        JsonPrimitive(src?.time)
}

private fun runOnUiThread(runnable: () -> Unit) {
    Handler(Looper.getMainLooper()).post { runnable() }
}

const val TAG = "PolarSdkPlugin"

@CapacitorPlugin(
    name = "PolarSdk",
    permissions = [
        Permission(
            strings = [
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ], alias = "ACCESS_COARSE_LOCATION"
        ),
        Permission(
            strings = [
                Manifest.permission.ACCESS_FINE_LOCATION,
            ], alias = "ACCESS_FINE_LOCATION"
        ),
        Permission(
            strings = [
                Manifest.permission.BLUETOOTH,
            ], alias = "BLUETOOTH"
        ),
        Permission(
            strings = [
                Manifest.permission.BLUETOOTH_ADMIN,
            ], alias = "BLUETOOTH_ADMIN"
        ),
        Permission(
            strings = [
                "android.permission.BLUETOOTH_SCAN",
            ], alias = "BLUETOOTH_SCAN"
        ),
        Permission(
            strings = [
                "android.permission.BLUETOOTH_CONNECT",
            ], alias = "BLUETOOTH_CONNECT"
        ),
    ]
)
class PolarSdkPlugin : Plugin() {
    private val gson: Gson = GsonBuilder().registerTypeAdapter(Date::class.java, DateSerializer).create()
    private var streamingDisposables: MutableMap<String, Disposable> = mutableMapOf()
    private var searchDisposable: Disposable? = null

    private val api: PolarBleApi by lazy {
        PolarBleApiDefaultImpl.defaultImplementation(
            bridge.activity.applicationContext,
            PolarBleApi.PolarBleSdkFeature.values().toSet()
        )
    }

    override fun load() {
        api.setApiLogger { str: String -> Log.d(TAG, str) }
    }

    @PluginMethod
    fun initialize(call: PluginCall) {
        val aliases = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                "BLUETOOTH_SCAN",
                "BLUETOOTH_CONNECT",
                "ACCESS_FINE_LOCATION",
            )
        } else {
            arrayOf(
                "ACCESS_COARSE_LOCATION",
                "ACCESS_FINE_LOCATION",
                "BLUETOOTH",
                "BLUETOOTH_ADMIN",
            )
        }
        requestPermissionForAliases(aliases, call, "initializeCallback")
    }

    @com.getcapacitor.annotation.PermissionCallback
    private fun initializeCallback(call: PluginCall) {
        call.resolve()
    }

    @PluginMethod
    fun connectToDevice(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        try {
            api.connectToDevice(identifier)
            call.resolve()
        } catch (e: Exception) {
            call.reject("Failed to connect: ${e.message}", e)
        }
    }

    @PluginMethod
    fun disconnectFromDevice(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        try {
            api.disconnectFromDevice(identifier)
            call.resolve()
        } catch (e: Exception) {
            call.reject("Failed to disconnect: ${e.message}", e)
        }
    }

    @PluginMethod
    fun searchForDevice(call: PluginCall) {
        searchDisposable?.dispose()
        searchDisposable = api.searchForDevice()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { deviceInfo ->
                    val data = JSObject().apply {
                        put("deviceId", deviceInfo.deviceId)
                        put("address", deviceInfo.address)
                        put("rssi", deviceInfo.rssi)
                        put("name", deviceInfo.name)
                        put("isConnectable", deviceInfo.isConnectable)
                    }
                    notifyListeners("deviceFound", data)
                },
                { error ->
                    call.reject("Search failed: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("value", true))
                }
            )
    }

    @PluginMethod
    fun getAvailableOnlineStreamDataTypes(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.getAvailableOnlineStreamDataTypes(identifier)
            .subscribe(
                { dataTypes ->
                    val types = JSArray()
                    dataTypes.forEach { types.put(it.ordinal) }
                    call.resolve(JSObject().put("types", types))
                },
                { error ->
                    call.reject("Failed to get available data types: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun requestStreamSettings(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val featureIndex = call.getInt("feature") ?: run {
            call.reject("Missing feature")
            return
        }

        val feature = PolarBleApi.PolarDeviceDataType.values()[featureIndex]

        api.requestStreamSettings(identifier, feature)
            .subscribe(
                { settings ->
                    val settingsObj = JSObject()
                    val settingsMap = JSObject()
                    settings.settings.forEach { (key, values) ->
                        val arr = JSArray()
                        values.forEach { arr.put(it.toLong()) }
                        settingsMap.put(key.toString(), arr)
                    }
                    settingsObj.put("settings", settingsMap)
                    call.resolve(settingsObj)
                },
                { error ->
                    call.reject("Failed to request stream settings: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun startHrStreaming(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        streamingDisposables["$identifier-hr"] = api.startHrStreaming(identifier)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { hrData ->
                    hrData.samples.forEach { sample ->
                        val data = JSObject().apply {
                            put("bpm", sample.hr)
                            put("rrs", JSArray(sample.rrsMs))
                            put("timestamp", System.currentTimeMillis())
                        }
                        notifyListeners("hrData", data)
                    }
                },
                { error ->
                    call.reject("HR stream failed: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("value", true))
                }
            )
    }

    @PluginMethod
    fun startEcgStreaming(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        val settings = call.getObject("settings")?.let { parseSettings(it) }
            ?: PolarSensorSetting(mapOf(
                PolarSensorSetting.SettingType.SAMPLE_RATE to 130,
                PolarSensorSetting.SettingType.RESOLUTION to 14
            ))

        streamingDisposables["$identifier-ecg"] = api.startEcgStreaming(identifier, settings)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { ecgData ->
                    val data = JSObject().apply {
                        put("data", gson.toJson(ecgData))
                    }
                    notifyListeners("ecgData", data)
                },
                { error ->
                    call.reject("ECG stream failed: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("value", true))
                }
            )
    }

    @PluginMethod
    fun startAccStreaming(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        val settings = call.getObject("settings")?.let { parseSettings(it) }
            ?: PolarSensorSetting(emptyMap())

        streamingDisposables["$identifier-acc"] = api.startAccStreaming(identifier, settings)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { accData ->
                    accData.samples.forEach { sample ->
                        val data = JSObject().apply {
                            put("x", sample.x)
                            put("y", sample.y)
                            put("z", sample.z)
                            put("timestamp", sample.timeStamp)
                        }
                        notifyListeners("accData", data)
                    }
                },
                { error ->
                    call.reject("ACC stream failed: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("value", true))
                }
            )
    }

    @PluginMethod
    fun startGyroStreaming(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        val settings = call.getObject("settings")?.let { parseSettings(it) }
            ?: PolarSensorSetting(emptyMap())

        streamingDisposables["$identifier-gyro"] = api.startGyroStreaming(identifier, settings)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { gyroData ->
                    gyroData.samples.forEach { sample ->
                        val data = JSObject().apply {
                            put("x", sample.x)
                            put("y", sample.y)
                            put("z", sample.z)
                            put("timestamp", sample.timeStamp)
                        }
                        notifyListeners("gyroData", data)
                    }
                },
                { error ->
                    call.reject("Gyro stream failed: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("value", true))
                }
            )
    }

    @PluginMethod
    fun startMagnetometerStreaming(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        val settings = call.getObject("settings")?.let { parseSettings(it) }
            ?: PolarSensorSetting(emptyMap())

        streamingDisposables["$identifier-mag"] = api.startMagnetometerStreaming(identifier, settings)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { magData ->
                    magData.samples.forEach { sample ->
                        val data = JSObject().apply {
                            put("x", sample.x)
                            put("y", sample.y)
                            put("z", sample.z)
                            put("timestamp", sample.timeStamp)
                        }
                        notifyListeners("magnetometerData", data)
                    }
                },
                { error ->
                    call.reject("Magnetometer stream failed: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("value", true))
                }
            )
    }

    @PluginMethod
    fun startPpgStreaming(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        val settings = call.getObject("settings")?.let { parseSettings(it) }
            ?: PolarSensorSetting(emptyMap())

        streamingDisposables["$identifier-ppg"] = api.startPpgStreaming(identifier, settings)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { ppgData ->
                    ppgData.samples.forEach { sample ->
                        val data = JSObject().apply {
                            put("ppg0", sample.channelSamples.getOrNull(0) ?: 0)
                            put("ppg1", sample.channelSamples.getOrNull(1) ?: 0)
                            put("ppg2", sample.channelSamples.getOrNull(2) ?: 0)
                            put("ambient", sample.channelSamples.getOrNull(3) ?: 0)
                            put("timestamp", sample.timeStamp)
                        }
                        notifyListeners("ppgData", data)
                    }
                },
                { error ->
                    call.reject("PPG stream failed: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("value", true))
                }
            )
    }

    @PluginMethod
    fun startPpiStreaming(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        streamingDisposables["$identifier-ppi"] = api.startPpiStreaming(identifier)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { ppiData ->
                    val data = JSObject().apply {
                        put("data", gson.toJson(ppiData))
                    }
                    notifyListeners("ppiData", data)
                },
                { error ->
                    call.reject("PPI stream failed: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("value", true))
                }
            )
    }

    @PluginMethod
    fun startTemperatureStreaming(call: PluginCall) {
        call.reject("Temperature streaming not implemented yet")
    }

    @PluginMethod
    fun startPressureStreaming(call: PluginCall) {
        call.reject("Pressure streaming not implemented yet")
    }

    @PluginMethod
    fun stopStreaming(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val featureIndex = call.getInt("feature") ?: run {
            call.reject("Missing feature")
            return
        }

        val featureName = when (featureIndex) {
            0 -> "hr"
            1 -> "ecg"
            2 -> "acc"
            3 -> "ppg"
            4 -> "ppi"
            5 -> "gyro"
            6 -> "mag"
            else -> "unknown"
        }

        streamingDisposables["$identifier-$featureName"]?.dispose()
        call.resolve(JSObject().put("value", true))
    }

    @PluginMethod
    fun startRecording(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val exerciseId = call.getString("exerciseId") ?: run {
            call.reject("Missing exerciseId")
            return
        }
        val intervalIndex = call.getInt("interval") ?: run {
            call.reject("Missing interval")
            return
        }
        val sampleTypeIndex = call.getInt("sampleType") ?: run {
            call.reject("Missing sampleType")
            return
        }

        val interval = PolarH10OfflineExerciseApi.RecordingInterval.values()[intervalIndex]
        val sampleType = PolarH10OfflineExerciseApi.SampleType.values()[sampleTypeIndex]

        api.startRecording(identifier, exerciseId, interval, sampleType)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to start recording: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun stopRecording(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.stopRecording(identifier)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to stop recording: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun requestRecordingStatus(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.requestRecordingStatus(identifier)
            .subscribe(
                { status ->
                    call.resolve(JSObject().apply {
                        put("result", gson.toJson(status))
                    })
                },
                { error ->
                    call.reject("Failed to get recording status: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun listExercises(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        val entries = JSArray()
        api.listExercises(identifier)
            .subscribe(
                { entry ->
                    entries.put(JSObject().apply {
                        put("entry", gson.toJson(entry))
                    })
                },
                { error ->
                    call.reject("Failed to list exercises: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("entries", entries))
                }
            )
    }

    @PluginMethod
    fun fetchExercise(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val entryObj = call.getObject("entry") ?: run {
            call.reject("Missing entry")
            return
        }

        val entry = PolarExerciseEntry(
            entryObj.getString("path") ?: "",
            Date(entryObj.getLong("date")),
            entryObj.getString("entryId") ?: ""
        )

        api.fetchExercise(identifier, entry)
            .subscribe(
                { data ->
                    call.resolve(JSObject().apply {
                        put("data", gson.toJson(data))
                    })
                },
                { error ->
                    call.reject("Failed to fetch exercise: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun removeExercise(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val entryObj = call.getObject("entry") ?: run {
            call.reject("Missing entry")
            return
        }

        val entry = PolarExerciseEntry(
            entryObj.getString("path") ?: "",
            Date(entryObj.getLong("date")),
            entryObj.getString("entryId") ?: ""
        )

        api.removeExercise(identifier, entry)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to remove exercise: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun setLedConfig(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val configObj = call.getObject("config") ?: run {
            call.reject("Missing config")
            return
        }

        val enabled = configObj.getBoolean("ledEnabled") ?: false
        val config = LedConfig(enabled)

        api.setLedConfig(identifier, config)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to set LED config: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun doFactoryReset(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val preservePairing = call.getBoolean("preservePairingInformation", false) ?: false

        api.doFactoryReset(identifier, preservePairing)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to do factory reset: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun enableSdkMode(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.enableSDKMode(identifier)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to enable SDK mode: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun disableSdkMode(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.disableSDKMode(identifier)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to disable SDK mode: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun isSdkModeEnabled(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.isSDKModeEnabled(identifier)
            .subscribe(
                { enabled ->
                    call.resolve(JSObject().put("enabled", enabled))
                },
                { error ->
                    call.reject("Failed to check SDK mode: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun getAvailableOfflineRecordingDataTypes(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.getAvailableOfflineRecordingDataTypes(identifier)
            .subscribe(
                { dataTypes ->
                    val types = JSArray()
                    dataTypes.forEach { types.put(it.ordinal) }
                    call.resolve(JSObject().put("types", types))
                },
                { error ->
                    call.reject("Failed to get available offline recording data types: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun requestOfflineRecordingSettings(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val featureIndex = call.getInt("feature") ?: run {
            call.reject("Missing feature")
            return
        }

        val feature = PolarBleApi.PolarDeviceDataType.values()[featureIndex]

        api.requestOfflineRecordingSettings(identifier, feature)
            .subscribe(
                { settings ->
                    val settingsObj = JSObject()
                    val settingsMap = JSObject()
                    settings.settings.forEach { (key, values) ->
                        val arr = JSArray()
                        values.forEach { arr.put(it.toLong()) }
                        settingsMap.put(key.toString(), arr)
                    }
                    settingsObj.put("settings", settingsMap)
                    call.resolve(settingsObj)
                },
                { error ->
                    call.reject("Failed to request offline recording settings: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun startOfflineRecording(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val featureIndex = call.getInt("feature") ?: run {
            call.reject("Missing feature")
            return
        }

        val feature = PolarBleApi.PolarDeviceDataType.values()[featureIndex]
        val settings = call.getObject("settings")?.let { parseSettings(it) }

        api.startOfflineRecording(identifier, feature, settings)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to start offline recording: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun stopOfflineRecording(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val featureIndex = call.getInt("feature") ?: run {
            call.reject("Missing feature")
            return
        }

        val feature = PolarBleApi.PolarDeviceDataType.values()[featureIndex]

        api.stopOfflineRecording(identifier, feature)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to stop offline recording: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun getOfflineRecordingStatus(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.getOfflineRecordingStatus(identifier)
            .subscribe(
                { dataTypes ->
                    val features = JSArray()
                    dataTypes.forEach { features.put(it.ordinal) }
                    call.resolve(JSObject().put("features", features))
                },
                { error ->
                    call.reject("Failed to get offline recording status: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun listOfflineRecordings(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        val entries = JSArray()
        api.listOfflineRecordings(identifier)
            .subscribe(
                { entry ->
                    entries.put(JSObject().apply {
                        put("path", entry.path)
                        put("size", entry.size)
                        put("date", entry.date.time)
                        put("type", entry.type.name)
                    })
                },
                { error ->
                    call.reject("Failed to list offline recordings: ${error.message}")
                },
                {
                    call.resolve(JSObject().put("entries", entries))
                }
            )
    }

    @PluginMethod
    fun getOfflineRecord(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val entryObj = call.getObject("entry") ?: run {
            call.reject("Missing entry")
            return
        }

        val entry = PolarOfflineRecordingEntry(
            entryObj.getString("path") ?: "",
            entryObj.getLong("size") ?: 0L,
            Date(entryObj.getLong("date")),
            PolarBleApi.PolarDeviceDataType.valueOf(entryObj.getString("type") ?: "ACC")
        )

        api.getOfflineRecord(identifier, entry)
            .subscribe(
                { data ->
                    val result = JSObject().apply {
                        put("startTime", gson.toJson(data.startTime))
                        data.settings?.let {
                            val settingsMap = JSObject()
                            it.settings.forEach { (key, values) ->
                                val arr = JSArray()
                                values.forEach { arr.put(it.toLong()) }
                                settingsMap.put(key.toString(), arr)
                            }
                            put("settings", settingsMap)
                        }
                    }
                    call.resolve(result)
                },
                { error ->
                    call.reject("Failed to get offline record: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun removeOfflineRecord(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val entryObj = call.getObject("entry") ?: run {
            call.reject("Missing entry")
            return
        }

        val entry = PolarOfflineRecordingEntry(
            entryObj.getString("path") ?: "",
            entryObj.getLong("size") ?: 0L,
            Date(entryObj.getLong("date")),
            PolarBleApi.PolarDeviceDataType.valueOf(entryObj.getString("type") ?: "ACC")
        )

        api.removeOfflineRecord(identifier, entry)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to remove offline record: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun getDiskSpace(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.getDiskSpace(identifier)
            .subscribe(
                { pair ->
                    call.resolve(JSObject().apply {
                        put("result", gson.toJson(pair))
                    })
                },
                { error ->
                    call.reject("Failed to get disk space: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun getLocalTime(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.getLocalTime(identifier)
            .subscribe(
                { calendar ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                    dateFormat.timeZone = calendar.timeZone
                    val timeString = dateFormat.format(calendar.time)
                    call.resolve(JSObject().put("time", timeString))
                },
                { error ->
                    call.reject("Failed to get local time: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun setLocalTime(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val timeString = call.getString("time") ?: run {
            call.reject("Missing time")
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val date = dateFormat.parse(timeString) ?: run {
            call.reject("Invalid time format")
            return
        }

        val calendar = Calendar.getInstance()
        calendar.time = date

        api.setLocalTime(identifier, calendar)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to set local time: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun doFirstTimeUse(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val configObj = call.getObject("config") ?: run {
            call.reject("Missing config")
            return
        }

        val gender = when (configObj.getString("gender")) {
            "Male" -> PolarFirstTimeUseConfig.Gender.MALE
            "Female" -> PolarFirstTimeUseConfig.Gender.FEMALE
            else -> {
                call.reject("Invalid gender")
                return
            }
        }

        val birthDateString = configObj.getString("birthDate") ?: run {
            call.reject("Missing birthDate")
            return
        }
        val birthDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthDateString) ?: run {
            call.reject("Invalid birth date format")
            return
        }

        val height = configObj.getInteger("height")?.toFloat() ?: run {
            call.reject("Missing height")
            return
        }
        val weight = configObj.getInteger("weight")?.toFloat() ?: run {
            call.reject("Missing weight")
            return
        }
        val maxHeartRate = configObj.getInteger("maxHeartRate") ?: run {
            call.reject("Missing maxHeartRate")
            return
        }
        val vo2Max = configObj.getInteger("vo2Max") ?: run {
            call.reject("Missing vo2Max")
            return
        }
        val restingHeartRate = configObj.getInteger("restingHeartRate") ?: run {
            call.reject("Missing restingHeartRate")
            return
        }
        val trainingBackground = configObj.getInteger("trainingBackground") ?: run {
            call.reject("Missing trainingBackground")
            return
        }
        val deviceTime = configObj.getString("deviceTime") ?: run {
            call.reject("Missing deviceTime")
            return
        }
        val typicalDay = when (configObj.getInteger("typicalDay")) {
            1 -> PolarFirstTimeUseConfig.TypicalDay.MOSTLY_MOVING
            2 -> PolarFirstTimeUseConfig.TypicalDay.MOSTLY_SITTING
            3 -> PolarFirstTimeUseConfig.TypicalDay.MOSTLY_STANDING
            else -> {
                call.reject("Invalid typicalDay")
                return
            }
        }
        val sleepGoalMinutes = configObj.getInteger("sleepGoalMinutes") ?: run {
            call.reject("Missing sleepGoalMinutes")
            return
        }

        val config = PolarFirstTimeUseConfig(
            gender,
            birthDate,
            height,
            weight,
            maxHeartRate,
            vo2Max,
            restingHeartRate,
            trainingBackground,
            deviceTime,
            typicalDay,
            sleepGoalMinutes
        )

        api.doFirstTimeUse(identifier, config)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to do first time use: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun isFtuDone(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.isFtuDone(identifier)
            .subscribe(
                { done ->
                    call.resolve(JSObject().put("done", done))
                },
                { error ->
                    call.reject("Failed to check FTU status: ${error.message}")
                }
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @PluginMethod
    fun deleteStoredDeviceData(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val dataTypeIndex = call.getInt("dataType") ?: run {
            call.reject("Missing dataType")
            return
        }
        val untilDateStr = call.getString("until") ?: run {
            call.reject("Missing until date")
            return
        }

        val dataType = PolarBleApi.PolarStoredDataType.values()[dataTypeIndex]
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val untilDate = dateFormat.parse(untilDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid date format")
            return
        }

        api.deleteStoredDeviceData(identifier, dataType, untilDate)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to delete stored device data: ${error.message}")
                }
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @PluginMethod
    fun deleteDeviceDateFolders(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val fromDateStr = call.getString("fromDate") ?: run {
            call.reject("Missing fromDate")
            return
        }
        val toDateStr = call.getString("toDate") ?: run {
            call.reject("Missing toDate")
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = dateFormat.parse(fromDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid from date format")
            return
        }
        val toDate = dateFormat.parse(toDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid to date format")
            return
        }

        api.deleteDeviceDateFolders(identifier, fromDate, toDate)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to delete device date folders: ${error.message}")
                }
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @PluginMethod
    fun getSteps(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val fromDateStr = call.getString("fromDate") ?: run {
            call.reject("Missing fromDate")
            return
        }
        val toDateStr = call.getString("toDate") ?: run {
            call.reject("Missing toDate")
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = dateFormat.parse(fromDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid from date format")
            return
        }
        val toDate = dateFormat.parse(toDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid to date format")
            return
        }

        api.getSteps(identifier, fromDate, toDate)
            .subscribe(
                { stepsDataList ->
                    val data = JSArray()
                    stepsDataList.forEach { stepsData ->
                        data.put(JSObject().apply {
                            put("date", stepsData.date?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "")
                            put("steps", stepsData.steps)
                        })
                    }
                    call.resolve(JSObject().put("data", data))
                },
                { error ->
                    call.reject("Failed to get steps: ${error.message}")
                }
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @PluginMethod
    fun getDistance(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val fromDateStr = call.getString("fromDate") ?: run {
            call.reject("Missing fromDate")
            return
        }
        val toDateStr = call.getString("toDate") ?: run {
            call.reject("Missing toDate")
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = dateFormat.parse(fromDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid from date format")
            return
        }
        val toDate = dateFormat.parse(toDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid to date format")
            return
        }

        api.getDistance(identifier, fromDate, toDate)
            .subscribe(
                { distanceDataList ->
                    val data = JSArray()
                    distanceDataList.forEach { distanceData ->
                        data.put(JSObject().apply {
                            put("date", distanceData.date?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "")
                            put("distance", distanceData.distanceMeters)
                        })
                    }
                    call.resolve(JSObject().put("data", data))
                },
                { error ->
                    call.reject("Failed to get distance: ${error.message}")
                }
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @PluginMethod
    fun getActiveTime(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val fromDateStr = call.getString("fromDate") ?: run {
            call.reject("Missing fromDate")
            return
        }
        val toDateStr = call.getString("toDate") ?: run {
            call.reject("Missing toDate")
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = dateFormat.parse(fromDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid from date format")
            return
        }
        val toDate = dateFormat.parse(toDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid to date format")
            return
        }

        api.getActiveTime(identifier, fromDate, toDate)
            .subscribe(
                { activeTimeDataList ->
                    val data = JSArray()
                    activeTimeDataList.forEach { activeTimeData ->
                        data.put(JSObject().apply {
                            put("date", activeTimeData.date?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "")
                            put("activeTimeSeconds", (activeTimeData.timeNonWear?.seconds ?: 0) +
                                    (activeTimeData.timeSleep?.seconds ?: 0) +
                                    (activeTimeData.timeSedentary?.seconds ?: 0) +
                                    (activeTimeData.timeLightActivity?.seconds ?: 0) +
                                    (activeTimeData.timeContinuousModerateActivity?.seconds ?: 0) +
                                    (activeTimeData.timeIntermittentModerateActivity?.seconds ?: 0) +
                                    (activeTimeData.timeContinuousVigorousActivity?.seconds ?: 0) +
                                    (activeTimeData.timeIntermittentVigorousActivity?.seconds ?: 0))
                        })
                    }
                    call.resolve(JSObject().put("data", data))
                },
                { error ->
                    call.reject("Failed to get active time: ${error.message}")
                }
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @PluginMethod
    fun getActivitySampleData(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }
        val fromDateStr = call.getString("fromDate") ?: run {
            call.reject("Missing fromDate")
            return
        }
        val toDateStr = call.getString("toDate") ?: run {
            call.reject("Missing toDate")
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = dateFormat.parse(fromDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid from date format")
            return
        }
        val toDate = dateFormat.parse(toDateStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: run {
            call.reject("Invalid to date format")
            return
        }

        api.getActivitySampleData(identifier, fromDate, toDate)
            .subscribe(
                { activitySampleDataList ->
                    val data = JSArray()
                    call.resolve(JSObject().put("data", data))
                },
                { error ->
                    call.reject("Failed to get activity sample data: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun sendInitializationAndStartSyncNotifications(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.sendInitializationAndStartSyncNotifications(identifier)
            .subscribe(
                { success ->
                    call.resolve(JSObject().put("success", success))
                },
                { error ->
                    call.reject("Failed to send initialization: ${error.message}")
                }
            )
    }

    @PluginMethod
    fun sendTerminateAndStopSyncNotifications(call: PluginCall) {
        val identifier = call.getString("identifier") ?: run {
            call.reject("Missing identifier")
            return
        }

        api.sendTerminateAndStopSyncNotifications(identifier)
            .subscribe(
                {
                    call.resolve()
                },
                { error ->
                    call.reject("Failed to send termination: ${error.message}")
                }
            )
    }

    private fun parseSettings(settingsObj: JSObject): PolarSensorSetting? {
        val settingsMap = settingsObj.getJSObject("settings") ?: return null
        val polarSettings = mutableMapOf<PolarSensorSetting.SettingType, Int>()

        settingsMap.keys().forEach { key ->
            val settingType = when (key.toLowerCase()) {
                "samplerate" -> PolarSensorSetting.SettingType.SAMPLE_RATE
                "resolution" -> PolarSensorSetting.SettingType.RESOLUTION
                "range" -> PolarSensorSetting.SettingType.RANGE
                "channels" -> PolarSensorSetting.SettingType.CHANNELS
                else -> null
            }

            settingType?.let {
                val values = settingsMap.getJSONArray(key)
                if (values != null && values.length() > 0) {
                    polarSettings[it] = values.getInt(0)
                }
            }
        }

        return PolarSensorSetting(polarSettings)
    }

    override fun handleOnDestroy() {
        streamingDisposables.values.forEach { it.dispose() }
        searchDisposable?.dispose()
        super.handleOnDestroy()
    }
}
