package tmay.bluetoothbacon.ledstrip.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.ledstrip.adapters.ColorGridAdapter;
import tmay.bluetoothbacon.ledstrip.util.ColorUtility;
import tmay.bluetoothbacon.ledstrip.util.LedstripFrameManager;
import tmay.bluetoothbacon.ledstrip.util.RedBearServiceUUID;

/**
 * Created by Terry on 10/4/14.
 */
@EFragment(R.layout.fragment_led_strip_control)
public class LedStripControlFragment extends Fragment {

    public static final String TAG = "led_strip";

    public static LedStripControlFragment newInstance(BluetoothDevice device) {
        LedStripControlFragment fragment = new LedStripControlFragment_();
        Bundle args = new Bundle();
        args.putParcelable("device", device);
        fragment.setArguments(args);
        return fragment;
    }


    @Bean
    ColorUtility colorUtility;

    @Bean
    ColorGridAdapter adapter;

    @ViewById(R.id.color_grid)
    GridView gridLayout;

    private BluetoothDevice device;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic serialTX;
    private BluetoothGattCharacteristic serialRX;
    private int writeCount = 59;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("Clear Strip").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                clearStrip();
                return false;
            }
        });
    }

    @AfterViews
    void onAfterViews() {
        this.setHasOptionsMenu(true);
        gridLayout.setAdapter(adapter);
        adapter.setColors(colorUtility.getColorArray());

        gridLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EditColorFragment.newInstance(adapter.getColor(i)).show(getFragmentManager(), "editcolor");
            }
        });

        this.device = (BluetoothDevice) this.getArguments().get("device");
        gatt = device.connectGatt(getActivity(), true, gattCallback);

    }

    @Override
    public void onPause() {
        super.onPause();
        clearStrip();
        gatt.disconnect();
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(TAG, "Connected to GATT server.");
                    Log.i(TAG, "Attempting to start service discovery:");
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                }
            }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            serialTX = gatt.getService(RedBearServiceUUID.BLE_SHIELD_SERVICE)
                    .getCharacteristic(RedBearServiceUUID.BLE_SHIELD_TX);
            serialRX = gatt.getService(RedBearServiceUUID.BLE_SHIELD_SERVICE)
                    .getCharacteristic(RedBearServiceUUID.BLE_SHIELD_RX);
            setupRX();


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
            if (b[0] == 49 && writeCount > 0) {
                writeCount--;
                updateStrip();
            } else if (b[0] == ((byte) 100)) {
                Log.i("serial", "clear");
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            updateStrip();
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

    private void setupRX() {
        gatt.setCharacteristicNotification(serialRX, true);
        BluetoothGattDescriptor descriptor = serialRX.getDescriptor(RedBearServiceUUID.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    int count = 0;
    public void updateStrip() {
        setPixel(writeCount, adapter.getColor(writeCount));
    }



    private void setPixel(int position, int color) {
        Log.d("serial", position+" "+color);
        byte[] pixel = {((byte)position), ((byte)color)};
        serialTX.setValue(LedstripFrameManager.buildMessageFrame(pixel));

        gatt.writeCharacteristic(serialTX);

    }

    void clearStrip() {
        byte[] command = {0x64, 0};
        serialTX.setValue(LedstripFrameManager.buildMessageFrame(command));
        gatt.writeCharacteristic(serialTX);
    }
}
