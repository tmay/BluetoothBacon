package tmay.bluetoothbacon.beacons.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;


import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import tmay.bluetoothbacon.R;

/**
 * Created by Terry on 10/6/14.
 */
@EFragment(R.layout.fragment_beacon_dectector)
public class BeaconDetectorFragment extends Fragment implements BeaconConsumer {

    public static BeaconDetectorFragment newInstance() {
        return new BeaconDetectorFragment_();
    }
    private BeaconManager beaconManager;

    private Context context;


    @AfterViews
    void onAfterViews() {
        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        BeaconManager.getInstanceForApplication(getApplicationContext()).setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i("beacon", "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("beacon", "I just left an beacon for the first time!");
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("here", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }
}
