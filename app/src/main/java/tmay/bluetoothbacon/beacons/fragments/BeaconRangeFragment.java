package tmay.bluetoothbacon.beacons.fragments;

import android.app.Fragment;

import org.androidannotations.annotations.EFragment;

/**
 * Created by Terry on 10/7/14.
 */
@EFragment
public class BeaconRangeFragment extends Fragment {
    public BeaconRangeFragment newInstance() {
        return new BeaconRangeFragment_();
    }
}
