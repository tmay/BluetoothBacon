package tmay.bluetoothbacon.blescanner.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.StringRes;

import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.blescanner.adapters.ServiceListAdapter;
import tmay.bluetoothbacon.blescanner.services.BleService;
import tmay.bluetoothbacon.ledstrip.services.StripControlService;

/**
 * Created by Terry on 10/6/14.
 */
@EFragment(R.layout.fragment_ble_device)
public class BleDeviceFragment extends BaseBleServiceFragment {
    public static final String BLUETOOTH_DEVICE = "bluetooth_device";

    public static BleDeviceFragment newInstance(BluetoothDevice device) {
        BleDeviceFragment fragment = new BleDeviceFragment_();
        Bundle args = new Bundle();
        args.putParcelable(BLUETOOTH_DEVICE, device);
        fragment.setArguments(args);
        return fragment;
    }

    @Bean
    ServiceListAdapter adapter;

    @ViewById(R.id.txt_device_address)
    TextView deviceAddressText;

    @ViewById(R.id.txt_device_rssi)
    TextView deviceRssiText;

    @ViewById(R.id.img_device_icon)
    ImageView iconImage;

    @ViewById(R.id.list_services)
    ListView serviceList;

    @StringRes(R.string.lbl_device_address)
    String deviceAddressLabel;

    @StringRes(R.string.lbl_rssi)
    String rssiLabel;

    @DrawableRes(R.drawable.bluetooth)
    Drawable iconDrawable;

    private BluetoothDevice device;

    @Override
    protected void onServiceReady() {
        super.onServiceReady();
        this.device = (BluetoothDevice) this.getArguments().get(BLUETOOTH_DEVICE);
        boundService.connectGatt(device);
        Toast.makeText(getActivity(), "Discovering Services",
                Toast.LENGTH_LONG).show();
        getActivity().setProgressBarIndeterminateVisibility(true);
        bind();
    }

    @AfterViews
    void onAfterViews() {

    }

    @Receiver(actions = BleService.ACTION_GATT_CONNECTED, registerAt = Receiver.RegisterAt.OnResumeOnPause)
    void onGattConnected() {
        boundService.discoverGattServices();
    }

    @Receiver(actions = BleService.ACTION_GATT_SERVICES_DISCOVERED, registerAt = Receiver.RegisterAt.OnResumeOnPause)
    void onServiceDiscovery() {
        adapter.setServices(boundService.getGattServiceList());
        serviceList.setAdapter(adapter);
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    private void bind() {
        String name = device.getName();
        name = (name == null) ? "Unknown" : name;
        String type = "Type: "+getDeviceType(device.getType());

        deviceAddressText.setText(name+"\n"+device.getAddress()+"\n"+type);
        //deviceRssiText.setText(rssiLabel+" "+rssi);
        iconImage.setImageDrawable(iconDrawable);



    }

    private String getDeviceType(int type) {
        switch (type) {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return "Classic";
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return "Dual";
            case BluetoothDevice.DEVICE_TYPE_LE:
                return "BLE";
            default:
                return "Unknown";
        }
    }
}
