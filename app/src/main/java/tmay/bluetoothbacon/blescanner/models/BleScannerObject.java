package tmay.bluetoothbacon.blescanner.models;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * Created by Terry on 10/4/14.
 */
public class BleScannerObject {
    public BluetoothDevice device;
    public int rssi;

    public BleScannerObject(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public static boolean isDuplicateDevice(BluetoothDevice d, ArrayList<BleScannerObject> objects) {
        for (BleScannerObject o:objects) {
            if (o.device.hashCode() == d.hashCode()) {
                return true;
            }
        }
        return false;
    }

    public static int findDeviceIndex(BluetoothDevice device, ArrayList<BleScannerObject> objects) {
        for (int i=0; i < objects.size(); i++) {
            if (objects.get(i).hashCode() == device.hashCode()) {
                return i;
            }
        }
        return -1;
    }
}
