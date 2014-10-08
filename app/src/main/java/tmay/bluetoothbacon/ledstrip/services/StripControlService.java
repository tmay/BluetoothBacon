package tmay.bluetoothbacon.ledstrip.services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import tmay.bluetoothbacon.ledstrip.util.LedstripFrameManager;
import tmay.bluetoothbacon.ledstrip.util.RedBearServiceUUID;

/**
 * Created by Terry on 10/5/14.
 */

public class StripControlService extends Service {

    public final static String SERVICE_READY = "SERVICE_READY";
    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_GATT_RSSI = "ACTION_GATT_RSSI";
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "EXTRA_DATA";
    public final static String RAINBOW_WIPE_COMPLETE = "RAINBOW_WIPE_COMPLETE";


    public static final String TAG = "strip_control_service";

    public class LocalBinder extends Binder {
        public StripControlService getService() {
            return StripControlService.this;
        }
    }
    private final IBinder binder = new LocalBinder();

    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic serialTX;
    private BluetoothGattCharacteristic serialRX;
    private int writeCount = 0;
    private boolean isConnected;
    private boolean isDisconnecting;
    private boolean isRainbowWiping;
    private int[] colors;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //close();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:");
                isConnected = true;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disco from GATT server.");
                close();
                isDisconnecting = false;
                isConnected = false;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            serialTX = gatt.getService(RedBearServiceUUID.BLE_SHIELD_SERVICE)
                    .getCharacteristic(RedBearServiceUUID.BLE_SHIELD_TX);
            serialRX = gatt.getService(RedBearServiceUUID.BLE_SHIELD_SERVICE)
                    .getCharacteristic(RedBearServiceUUID.BLE_SHIELD_RX);
            gatt.setCharacteristicNotification(serialRX, true);
            BluetoothGattDescriptor descriptor = serialRX.getDescriptor(RedBearServiceUUID.CLIENT_CHARACTERISTIC_CONFIG);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] b = characteristic.getValue();
            for (byte a:b) {
                Log.i("incoming byte", a+"");
            }

            if (b[0] == 49 && writeCount > 0 && colors != null) {
                writeCount--;
                setPixel(writeCount, colors[writeCount]);
            } else if (b[0] == 50 && isDisconnecting) {
                gatt.disconnect();
            } else if (b[0] == 50 && isRainbowWiping) {
                isRainbowWiping = false;
                sendBroadcast(new Intent(RAINBOW_WIPE_COMPLETE));
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            sendBroadcast(new Intent(ACTION_GATT_SERVICES_DISCOVERED));
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    public void connectGatt(BluetoothDevice device) {
        gatt = device.connectGatt(this, true, gattCallback);
    }

    public void disconnectGatt() {
        isDisconnecting = true;
        fillStrip(0);
    }

    public void initStrip(int[] colors) {
        this.colors = colors;
        writeCount = colors.length-1;
        setPixel(writeCount, colors[writeCount]);
    }

    public void setPixel(int position, int color) {
        if (!isConnected)
            return;
        byte[] pixel = {((byte)position), ((byte)color)};
        serialTX.setValue(LedstripFrameManager.buildMessageFrame(pixel));

        gatt.writeCharacteristic(serialTX);
    }

    public void doRainbowWipe() {
        if (!isConnected)
            return;
        isRainbowWiping = true;
        byte[] command = {0x65, 0};
        serialTX.setValue(LedstripFrameManager.buildMessageFrame(command));
        gatt.writeCharacteristic(serialTX);
    }

    public void fillStrip(int color) {
        if (!isConnected)
            return;
        byte[] command = {0x64, ((byte) color)};
        serialTX.setValue(LedstripFrameManager.buildMessageFrame(command));
        gatt.writeCharacteristic(serialTX);
    }

    private void close() {
        if (gatt == null) return;
        gatt.close();
        gatt = null;
    }
}
