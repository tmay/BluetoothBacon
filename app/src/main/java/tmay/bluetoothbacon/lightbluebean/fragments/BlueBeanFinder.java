package tmay.bluetoothbacon.lightbluebean.fragments;

import android.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanDiscoveryListener;
import nl.littlerobots.bean.BeanManager;
import tmay.bluetoothbacon.lightbluebean.adapters.BlueBleanAdapter;

/**
 * Created by Terry on 9/19/14.
 */

@EFragment
public class BlueBeanFinder extends ListFragment implements BeanDiscoveryListener {

    public final static int DISCOVERY_TIMEOUT = 20000;

    private MenuItem menuScanItem;
    private boolean isScanning;
    private OnBeanSelectionListener listener;
    private Timer timer;

    public static BlueBeanFinder newInstance() {
        return new BlueBeanFinder_();
    }

    public interface OnBeanSelectionListener {
        public void onBeanSelected(Bean bean);
    }

    @org.androidannotations.annotations.Bean
    BlueBleanAdapter adapter;

    @AfterViews
    void onAfterViews() {
        setHasOptionsMenu(true);

        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (listener != null) {
                stopDiscovery();
                listener.onBeanSelected(adapter.getItem(i));

            }
        }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setProgressBarIndeterminateVisibility(false);
        stopDiscovery();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menuScanItem = menu.add("Scan for Beans")
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    startDiscovery(DISCOVERY_TIMEOUT);
                    setScanningState();
                    return false;
                }
            })
            .setEnabled(false);
        menuScanItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menuScanItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        startDiscovery(DISCOVERY_TIMEOUT);
        setScanningState();
    }

    @Override
    public void onBeanDiscovered(final Bean bean) {
        update();
        BeanManager.getInstance().cancelDiscovery();
    }

    @Override
    public void onDiscoveryComplete() {
        //this never gets called!
        //update();
        stopDiscovery();
    }

    public void setOnBeanSelectedListener(OnBeanSelectionListener listener) {
        this.listener = listener;
    }

    private void startDiscovery(int timeout) {
        adapter.clear();
        BeanManager.getInstance().startDiscovery(this);
        isScanning = true;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                stopDiscovery();
               // update();
            }
        }, timeout, 1);

    }

    private void stopDiscovery() {
        getActivity().setProgressBarIndeterminateVisibility(false);
        BeanManager.getInstance().cancelDiscovery();
        timer.cancel();
        isScanning = false;
    }

    @UiThread
    void update() {
        Collection<Bean> beans = BeanManager.getInstance().getBeans();
        adapter.setBeans(new ArrayList<Bean>(beans));
        this.setListAdapter(adapter);
        setScanningState();
    }

    @UiThread
    void setScanningState() {
        menuScanItem.setEnabled(!isScanning);
        getActivity().setProgressBarIndeterminateVisibility(isScanning);
    }

}
