package tmay.bluetoothbacon.blescanner;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.ParcelUuid;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.WindowFeature;

import tmay.bluetoothbacon.MainMenuActivity;
import tmay.bluetoothbacon.MainMenuActivity_;
import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.blescanner.fragments.BleDeviceFragment;
import tmay.bluetoothbacon.blescanner.fragments.BleScannerFragment;

/**
 * Created by Terry on 10/4/14.
 */
@WindowFeature({Window.FEATURE_INDETERMINATE_PROGRESS})
@EActivity(R.layout.activity_ble_scanner)
public class BleScannerActivity extends Activity implements BleScannerFragment.OnBleDeviceSelectionListener {

    public static final String BLUETOOTH_DEVICE = "bluetooth_device";

    @Extra
    ParcelUuid serviceID;

    @Extra
    String knownAddress;

    @AfterViews
    void onAfterViews() {
        BleScannerFragment scanner = BleScannerFragment.newInstance(knownAddress, serviceID);
        scanner.setSelectionListener(this);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, scanner)
                .commit();
    }

    @Override
    public void onBleDeviceSelection(BluetoothDevice device) {
        if (serviceID != null || knownAddress != null) {
            Intent i = new Intent();
            i.putExtra(BLUETOOTH_DEVICE, device);
            setResult(MainMenuActivity.BLUETOOTH_DEVICE_SELECTION_RESULT, i);
            finish();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, BleDeviceFragment.newInstance(device))
                    .addToBackStack("ble_device")
                    .commit();
        }
    }

}
