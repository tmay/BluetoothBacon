package tmay.bluetoothbacon.blescanner;

import android.app.Activity;
import android.os.ParcelUuid;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.blescanner.fragments.BleScannerFragment;

/**
 * Created by Terry on 10/4/14.
 */
@EActivity(R.layout.activity_ble_scanner)
public class BleScannerActivity extends Activity {

    @Extra
    ParcelUuid serviceID;

    @AfterViews
    void onAfterViews() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, BleScannerFragment.newInstance(serviceID))
                .commit();
    }
}
