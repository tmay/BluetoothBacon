package tmay.bluetoothbacon.beacons;

import android.app.Activity;
import android.os.RemoteException;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collection;

import tmay.bluetoothbacon.R;

/**
 * Created by Terry on 10/7/14.
 */
@EActivity(R.layout.activity_main_beacon)
public class RangeBeaconActivity extends Activity implements BeaconConsumer {
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
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    updateText(beacons.iterator().next().getDistance());
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("here", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void updateText(double message) {
        txtRegion.setText(String.format("%.2f",message)+" meters");
    }
}
