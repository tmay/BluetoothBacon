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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import tmay.bluetoothbacon.MainMenuActivity_;
import tmay.bluetoothbacon.blescanner.adapters.BleScannerAdapter;

/**
 * Created by Terry on 10/4/14.
 */
@EFragment
public class BleScannerFragment extends ListFragment {

    private static final String SERVICE_UUID = "service_uuid";
    private static final String KNOWN_ADDRESS = "known_address";
    private static final int SCAN_TIMEOUT = 10*1000;

    private static final int REQUEST_ENABLE_BT = 0x01;
    public static BleScannerFragment newInstance(String deviceAddress, ParcelUuid serviceId) {
        BleScannerFragment fragment = new BleScannerFragment_();
        Bundle args = new Bundle();
        args.putString(KNOWN_ADDRESS, deviceAddress);
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
    private String knownAddress;
    private UUID serviceID;
    private Timer timer;
    private MenuItem scanMenuItem;

    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] bytes) {
            if (knownAddress == null)
                updateAdapter(device, rssi);
            else if (device.getAddress().equals(knownAddress))
                updateAdapter(device, rssi);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        scanMenuItem = menu.add("Stop Scan").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                stopScan();
                return false;
            }
        });
        scanMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        scanMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @AfterViews
    void onAfterViews() {
        this.setHasOptionsMenu(true);
        this.setListAdapter(adapter);
        this.knownAddress = this.getArguments().getString(KNOWN_ADDRESS);
        ParcelUuid puuid = (ParcelUuid) this.getArguments().get(SERVICE_UUID);
        if (puuid != null)
            this.serviceID = puuid.getUuid();
        initScan();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        stopScan();
        listener.onBleDeviceSelection(adapter.getItem(position));
    }

    public void setSelectionListener(OnBleDeviceSelectionListener listener) {
        this.listener = listener;
    }


    @UiThread
    void initScan() {
        if (checkForBluetooth()) {
            if (serviceID == null) scan();
            else scanForService(serviceID);
        } else {
            showBluetoothSettings();
        }
    }

    private void scan() {
        getActivity().setProgressBarIndeterminateVisibility(true);
        adapter.clear();
        bluetoothAdapter.startLeScan(leScanCallback);
        startScanTimer();
    }

    private void scanForService(UUID serviceId) {
        UUID[] uuids = new UUID[1];
        uuids[0] = serviceId;
        getActivity().setProgressBarIndeterminateVisibility(true);
        adapter.clear();
        bluetoothAdapter.startLeScan(uuids, leScanCallback);
        startScanTimer();
    }

    @UiThread
    void updateAdapter(BluetoothDevice device, int rssi) {
        adapter.addDevice(device, rssi);
    }

    private void startScanTimer() {
        if (timer != null)
            timer.cancel();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                stopScan();
            }
        }, SCAN_TIMEOUT, 1);
    }

    @UiThread
    void stopScan() {
        bluetoothAdapter.stopLeScan(leScanCallback);

        if (getActivity() != null)
            getActivity().setProgressBarIndeterminateVisibility(false);

        if (timer != null)
            timer.cancel();

        scanMenuItem.setTitle("Start Scan").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                initScan();
                return false;
            }
        });
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
