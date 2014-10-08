package tmay.bluetoothbacon.beacons.fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import tmay.bluetoothbacon.R;

/**
 * Created by Terry on 10/6/14.
 */
@EFragment(R.layout.fragment_beacon_dectector)
public class BeaconDectectorFragment extends Fragment implements BeaconConsumer {

    public static final String TAG = "main_beacons";
    private BeaconManager beaconManager;

    public static BeaconDectectorFragment newInstance() {
        return new BeaconDectectorFragment_();
    }

    @ViewById(R.id.txt_beacon_region)
    TextView txtRegion;

    private MonitorNotifier monitor = new MonitorNotifier() {
        @Override
        public void didEnterRegion(Region region) {
            txtRegion.setText("Entered: "+region.getUniqueId());
        }

        @Override
        public void didExitRegion(Region region) {
            txtRegion.setText("Exited: "+region.getUniqueId());
        }

        @Override
        public void didDetermineStateForRegion(int i, Region region) {

        }
    };

    @AfterViews
    void onAfterViews() {
        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }



    @Override
    public void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(monitor);
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("around_me", null, null, null));
        } catch (RemoteException e) {   }
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }
}
