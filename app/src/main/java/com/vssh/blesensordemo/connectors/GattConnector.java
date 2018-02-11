package com.vssh.blesensordemo.connectors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import com.vssh.blesensordemo.model.GourmetSensor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by varun on 10.02.18.
 */

public class GattConnector {
    private static final long GATT_SIG_BITS = 0x800000805f9b34fbL;

    private static final String SERIAL_NUMBER_UUID = "2a25";
    private static final String SYSTEM_ID_UUID = "2a23";
    private static final String DEVICE_INFO_SERVICE_UUID = "180a";
    private static final String TEMPERATURE_SERVICE_UUID = "f000aa00-0451-4000-b000-000000000000";
    private static final String TEMPERATURE_CHARACTERISTIC_UUID = "f000aa01-0451-4000-b000-000000000000";
    private static final String TEMPERATURE_MONITOR_CHARACTERISTIC_UUID = "2a21";
    private static final String CLIENT_CONFIG_DESCRIPTOR_UUID = "2902";

    private GattCallback gattCallback;
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private BluetoothLeAdvertiser advertiser;
    private BluetoothGattServer gattServer;

    private BluetoothGattService temperatureService;
    private BluetoothGattCharacteristic tempCharacteristic;
    private BluetoothGattDescriptor tempClientConfigDescriptor;
    private BluetoothGattCharacteristic tempMonitorCharacteristic;

    private BluetoothGattService deviceInfoService;
    private BluetoothGattCharacteristic serialCharacteristic;
    private BluetoothGattCharacteristic sysCharacteristic;

    private GourmetSensor.SensorUpdateIntervalListener intervalListener;

    private short tempUpdateRateinSec = 0;

    Set<BluetoothDevice> connectedDevices = new HashSet<>();

    public GattConnector(Context context, GourmetSensor.SensorUpdateIntervalListener intervalListener) {
        this.context = context;
        this.intervalListener = intervalListener;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setUpServer();
    }

    private void setUpServer() {
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        gattCallback = new GattCallback();
        gattServer = manager.openGattServer(context, gattCallback);

        tempCharacteristic = new BluetoothGattCharacteristic(UUID.fromString(TEMPERATURE_CHARACTERISTIC_UUID), BluetoothGattCharacteristic.PROPERTY_NOTIFY, 0);
        tempClientConfigDescriptor = new BluetoothGattDescriptor(new UUID(Long.parseLong(CLIENT_CONFIG_DESCRIPTOR_UUID, 16) << 32 | 0x1000, GATT_SIG_BITS), BluetoothGattDescriptor.PERMISSION_WRITE);
        tempClientConfigDescriptor.setValue(new byte[] {0x1, 0x0});
        tempCharacteristic.addDescriptor(tempClientConfigDescriptor);

        tempMonitorCharacteristic = new BluetoothGattCharacteristic(new UUID(Long.parseLong(TEMPERATURE_MONITOR_CHARACTERISTIC_UUID, 16) << 32 | 0x1000, GATT_SIG_BITS),
                BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);

        UUID temperatureServiceUuid = UUID.fromString(TEMPERATURE_SERVICE_UUID);
        temperatureService = new BluetoothGattService(temperatureServiceUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        temperatureService.addCharacteristic(tempCharacteristic);
        temperatureService.addCharacteristic(tempMonitorCharacteristic);

        // create device info service
        UUID deviceInfoUuid = new UUID (Long.parseLong(DEVICE_INFO_SERVICE_UUID, 16) << 32 | 0x1000, GATT_SIG_BITS);
        deviceInfoService = new BluetoothGattService(deviceInfoUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // set random value as serial number
        UUID serialNumber = new UUID(Long.parseLong(SERIAL_NUMBER_UUID, 16) << 32 | 0x1000, GATT_SIG_BITS);
        serialCharacteristic = new BluetoothGattCharacteristic(serialNumber, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        serialCharacteristic.setValue("0123456");

        // set mac address as system ID
        UUID sysId = new UUID(Long.parseLong(SYSTEM_ID_UUID, 16) << 32 | 0x1000, GATT_SIG_BITS);
        sysCharacteristic = new BluetoothGattCharacteristic(sysId, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        byte[] addressBytes = bluetoothAdapter.getAddress().getBytes();
        byte[] sysIDArray;
        if (addressBytes.length == 6) {
            // sysId from MAC address according to GATT spec
            sysIDArray = new byte[] {addressBytes[0], addressBytes[1], addressBytes[2], 0xE, 0xF, 0xF, 0xF, addressBytes[3], addressBytes[4], addressBytes[5]};
        }
        else {
            sysIDArray = addressBytes;
        }
        sysCharacteristic.setValue(sysIDArray);
        deviceInfoService.addCharacteristic(serialCharacteristic);
        deviceInfoService.addCharacteristic(sysCharacteristic);

        gattServer.addService(this.temperatureService);
        gattServer.addService(deviceInfoService);
    }

    public void startAdvertising() {
        if(bluetoothAdapter.isMultipleAdvertisementSupported()) {
            advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setConnectable(true)
                    .build();

            UUID deviceInfoUuid = new UUID (Long.parseLong(DEVICE_INFO_SERVICE_UUID, 16) << 32 | 0x1000, GATT_SIG_BITS);
            UUID temperatureServiceUuid = UUID.fromString(TEMPERATURE_SERVICE_UUID);

            AdvertiseData data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(true)
                    .addServiceUuid(new ParcelUuid(temperatureServiceUuid))
                    .addServiceUuid(new ParcelUuid(deviceInfoUuid))
                    .build();

            advertiser.startAdvertising(settings, data, new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    Log.d("GattConnector", "successfully started advertising");
                }

                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);

                    Log.d("GattConnector", "failed to start advertising");
                }
            });
        }
        else {
            Toast.makeText(context, "Bluetooth LE Advertising not supported", Toast.LENGTH_LONG).show();
        }
    }

    public void stopAdvertising() {
        advertiser.stopAdvertising(new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
            }
        });
    }

    public void setTempValues(Short coldJuctionTemp, Short temp1, Short temp2, Short temp3, Short temp4, Short temp5, Short batteryVoltage) {
        ByteBuffer buffer = ByteBuffer.allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(coldJuctionTemp)
                .putShort(temp1)
                .putShort(temp2)
                .putShort(temp3)
                .putShort(temp4)
                .putShort(temp5)
                .putShort(batteryVoltage);

        tempCharacteristic.setValue(buffer.array());
    }

    public void sendTempNotification() {
        short notifyVal = ByteBuffer.wrap(tempClientConfigDescriptor.getValue()).order(ByteOrder.LITTLE_ENDIAN).getShort();
        if(notifyVal > 0) {
            for(BluetoothDevice device : connectedDevices) {
                gattServer.notifyCharacteristicChanged(device, tempCharacteristic, false);
            }
        }
    }

    public short getTempUpdateRateinSec() {
        return tempUpdateRateinSec;
    }

    public void setTempUpdateRateinSec(short tempUpdateRateinSec) {
        this.tempUpdateRateinSec = tempUpdateRateinSec;

        byte[] val =ByteBuffer.allocate(2)
                .putShort(tempUpdateRateinSec)
                .array();

        tempMonitorCharacteristic.setValue(val);
    }

    public void clear() {
        stopAdvertising();

        for(BluetoothDevice device : connectedDevices) {
            gattServer.cancelConnection(device);
        }
        connectedDevices.clear();
        gattServer.close();
    }

    private class GattCallback extends BluetoothGattServerCallback {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if(newState == BluetoothProfile.STATE_CONNECTED) {
                    connectedDevices.add(device);
                }
                else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                    connectedDevices.remove(device);
                }
            }
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);

            if(characteristic.getUuid().equals(tempMonitorCharacteristic.getUuid())) {
                if(value.length > 0) {
                    ByteBuffer wrapped = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN); // little-endian
                    short num = wrapped.getShort();

                    tempUpdateRateinSec = num;
                    tempMonitorCharacteristic.setValue(value);
                    intervalListener.setUpdateInterval(num);
                }
                if(responseNeeded) {
                    gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
                }
            }
            else {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, 0, null);
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);

            if(characteristic.getUuid().equals(sysCharacteristic.getUuid())) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, sysCharacteristic.getValue());
            }
            else if(characteristic.getUuid().equals(serialCharacteristic.getUuid())) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, serialCharacteristic.getValue());
            }
            else if(characteristic.getUuid().equals(tempMonitorCharacteristic.getUuid())) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, tempMonitorCharacteristic.getValue());
            }
            else if(characteristic.getUuid().equals(tempCharacteristic.getUuid())) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, tempCharacteristic.getValue());
            }
            else {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, 0, null);
            }
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);

            if(descriptor.getUuid().equals(tempClientConfigDescriptor.getUuid())) {
                //TODO: should actually save notification state per device
                tempClientConfigDescriptor.setValue(value);
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
            }
            else {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, 0, null);
            }
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);

            Log.d("GattConnector", "Notification sent to: "+device.getName());
        }
    }
}
