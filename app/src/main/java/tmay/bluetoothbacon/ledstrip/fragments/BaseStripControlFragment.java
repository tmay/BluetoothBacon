package tmay.bluetoothbacon.ledstrip.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import tmay.bluetoothbacon.ledstrip.services.StripControlService;

/**
 * Created by Terry on 10/5/14.
 */
@EFragment
public class BaseStripControlFragment extends Fragment{

    protected StripControlService boundService;
    protected ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            boundService = ((StripControlService.LocalBinder) service).getService();
            Toast.makeText(getActivity(), "Connecting to device",
                    Toast.LENGTH_LONG).show();
            onServiceReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(getActivity(), "Strip Control Service Disco",
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {

        getActivity().bindService(new Intent(getActivity(), StripControlService.class),
                connection, getActivity().getBaseContext().BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (boundService != null) {
            boundService.disconnectGatt();
        }
        getActivity().unbindService(connection);
    }

    protected void onServiceReady() {
        //extend me
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        doBindService();
    }

    @Override
    public void onPause() {
        super.onPause();
        doUnbindService();
    }

    @Override
    public void onResume() {
        super.onResume();
        doBindService();
    }
}
