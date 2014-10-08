package tmay.bluetoothbacon;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.ParcelUuid;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;

import tmay.bluetoothbacon.beacons.MainBeaconActivity_;
import tmay.bluetoothbacon.beacons.RangeBeaconActivity;
import tmay.bluetoothbacon.beacons.RangeBeaconActivity_;
import tmay.bluetoothbacon.blescanner.BleScannerActivity;
import tmay.bluetoothbacon.blescanner.BleScannerActivity_;
import tmay.bluetoothbacon.blescanner.fragments.BleScannerFragment;
import tmay.bluetoothbacon.ledstrip.LedStripMainActivity_;
import tmay.bluetoothbacon.ledstrip.util.RedBearServiceUUID;
import tmay.bluetoothbacon.lightbluebean.LightBlueBeanMain_;


@EActivity(R.layout.activity_main_menu)
public class MainMenuActivity extends Activity {

    public static final int BLUETOOTH_DEVICE_SELECTION_RESULT = 0x1;


    @Click(R.id.btn_ble_scanner)
    void onBleScannerClick() {
        BleScannerActivity_.intent(this).start();
    }

    @Click(R.id.btn_find_led_strip)
    void onFindLedStrip() {
        //there's a bug in Android that prevents scanning for a known service
        //so I hacked it to know the MAC adress for the LED Strip controller
        ParcelUuid serviceID = new ParcelUuid(RedBearServiceUUID.BLE_SHIELD_SERVICE);
        BleScannerActivity_.intent(this)
                .knownAddress(RedBearServiceUUID.LED_STRIP_ADDRESS)
                .startForResult(BLUETOOTH_DEVICE_SELECTION_RESULT);
    }

    @OnActivityResult(BLUETOOTH_DEVICE_SELECTION_RESULT)
    void onLedStripFound(Intent data) {
        BluetoothDevice device = null;
        if (data != null)
          device = data.getParcelableExtra(BleScannerActivity.BLUETOOTH_DEVICE);
        if (device != null)
            LedStripMainActivity_.intent(this)
                    .device(device)
                    .start();
    }

    @Click(R.id.btn_light_blue)
    void onFindBeans() {
        LightBlueBeanMain_.intent(this)
                .start();
    }

    @Click(R.id.btn_scan_beacons)
    void onScanForBeacons() {
        MainBeaconActivity_.intent(this)
                .start();
    }

    @Click(R.id.btn_range_beacons)
    void onRangeForBeacons() {
        RangeBeaconActivity_.intent(this)
                .start();
    }
}
