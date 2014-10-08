package tmay.bluetoothbacon.ledstrip;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.ledstrip.fragments.EditColorFragment;
import tmay.bluetoothbacon.ledstrip.fragments.LedStripControlFragment;
import tmay.bluetoothbacon.ledstrip.fragments.LedStripFragment;
import tmay.bluetoothbacon.ledstrip.services.StripControlService;

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
                .replace(R.id.container, new LedStripFragment().newInstance(device))
                .commit();


    }
}
