package tmay.bluetoothbacon.ledstrip;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.ledstrip.fragments.LedStripControlFragment;

/**
 * Created by Terry on 10/4/14.
 */
@EActivity(R.layout.activity_led_strip_main)
public class LedStripMainActivity extends Activity {

    @Extra
    BluetoothDevice device;

    @AfterViews
    void onAfterViews() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new LedStripControlFragment().newInstance(device))
                .commit();
    }
}
