package tmay.bluetoothbacon.beacons;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import tmay.bluetoothbacon.R;

/**
 * Created by Terry on 10/5/14.
 */
@EActivity(R.layout.activity_main_beacon)
public class MainBeaconActivity extends Activity  implements BeaconConsumer {
    public static final String TAG = "main_beacons";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    @ViewById(R.id.txt_beacon_region)
    TextView txtRegion;

    @AfterViews
    void onAfterViews() {
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                updateText("Entered: " + region.getUniqueId());
            }

            @Override
            public void didExitRegion(Region region) {
                updateText("Exited: " + region.getUniqueId());
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+i);
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    @UiThread
    void updateText(String message) {
        txtRegion.setText(message);
    }
}
