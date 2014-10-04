package tmay.bluetoothbacon.blescanner.fragments;

import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;
import java.util.UUID;

import tmay.bluetoothbacon.blescanner.adapters.BleScannerAdapter;

/**
 * Created by Terry on 10/4/14.
 */
@EFragment
public class BleScannerFragment extends ListFragment {

    private static final String SERVICE_UUID = "service_uuid";
    private static final int REQUEST_ENABLE_BT = 0x01;
    public static BleScannerFragment newInstance(ParcelUuid serviceId) {
        BleScannerFragment fragment = new BleScannerFragment_();
        Bundle args = new Bundle();
        args.putParcelable(SERVICE_UUID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnBleDeviceSelectionListener {
        public void onBleDeviceSelection(BluetoothDevice device);
    }

    @Bean
    BleScannerAdapter adapter;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private OnBleDeviceSelectionListener listener;

    @AfterViews
    void onAfterViews() {
        this.setListAdapter(adapter);

        ParcelUuid puuid = (ParcelUuid) this.getArguments().get(SERVICE_UUID);
        if (checkForBluetooth()) {
            if (puuid == null) scan();
            else scanForService(puuid.getUuid());
        } else {
            showBluetoothSettings();
        }
    }

    public void setSelectionListener(OnBleDeviceSelectionListener listener) {
        this.listener = listener;
    }

    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] bytes) {
            adapter.addDevice(device, rssi);
        }
    };

    private void scan() {
        bluetoothAdapter.startLeScan(leScanCallback);
    }

    private void scanForService(UUID serviceId) {
        UUID[] uuids = new UUID[1];
        uuids[0] = serviceId;
        bluetoothAdapter.startLeScan(uuids, leScanCallback);
    }

    private boolean checkForBluetooth() {
        bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            Log.e("BLEService", "Unable to initialize BluetoothManager.");
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    private void showBluetoothSettings() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
}
