package tmay.bluetoothbacon.ledstrip.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.ledstrip.adapters.ColorGridAdapter;
import tmay.bluetoothbacon.ledstrip.services.StripControlService;
import tmay.bluetoothbacon.ledstrip.util.ColorUtility;

/**
 * Created by Terry on 10/5/14.
 */
@EFragment(R.layout.fragment_led_strip_control)
public class LedStripFragment extends BaseStripControlFragment {
    public static LedStripFragment newInstance(BluetoothDevice device) {
        LedStripFragment fragment = new LedStripFragment_();
        Bundle args = new Bundle();
        args.putParcelable("device", device);
        fragment.setArguments(args);
        return fragment;
    }

    @Bean
    ColorUtility colorUtility;

    @Bean
    ColorGridAdapter adapter;

    @ViewById(R.id.color_grid)
    GridView gridLayout;

    private BluetoothDevice device;
    private boolean isReadyForInput;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("Clear Strip").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (boundService != null)
                    boundService.fillStrip(0);
                return false;
            }
        });
        menu.add("Fill Random").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int[] rnd = colorUtility.getRandomColorArray();
                boundService.initStrip(rnd);
                if (adapter != null)
                    adapter.setColors(rnd);
                return false;
            }
        });
        menu.add("Run Rainbow").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                boundService.doRainbowWipe();
                return false;
            }
        });
        menu.add("Edit Mode").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (adapter != null)
                    boundService.initStrip(adapter.getColors());
                return false;
            }
        });
    }



    @Receiver(actions = StripControlService.ACTION_GATT_SERVICES_DISCOVERED, registerAt = Receiver.RegisterAt.OnResumeOnPause)
    void onGattServiceDiscovery() {
        Toast.makeText(getActivity(), "Warming up",
                Toast.LENGTH_LONG).show();
        boundService.doRainbowWipe();
    }

    @Receiver(actions = StripControlService.RAINBOW_WIPE_COMPLETE, registerAt = Receiver.RegisterAt.OnResumeOnPause)
    void onRainbowWipeComplete() {
        boundService.initStrip(adapter.getColors());
        Toast.makeText(getActivity(), "ready",
                Toast.LENGTH_LONG).show();
        isReadyForInput = true;
    }

    @Override
    protected void onServiceReady() {
        super.onServiceReady();
        this.device = (BluetoothDevice) this.getArguments().get("device");
        boundService.connectGatt(device);
        Toast.makeText(getActivity(), "Discovering services",
                Toast.LENGTH_LONG).show();
        isReadyForInput = false;
    }

    @AfterViews
    void onAfterViews() {
        this.setHasOptionsMenu(true);
        gridLayout.setAdapter(adapter);
        adapter.setColors(colorUtility.getColorArray());

    }

    @ItemClick(R.id.color_grid)
    void onColorSwatchClick(final int position) {
        if (!isReadyForInput)
            return;
        EditColorFragment dialog = EditColorFragment.newInstance(adapter.getColor(position));
        dialog.show(getFragmentManager(), "editcolor");
        dialog.setColorAdjustListener(new EditColorFragment.OnColorAdjustListener() {
            @Override
            public void onColorChanged(int color) {
                boundService.setPixel(position, color);
                adapter.setColor(position, color);
            }
        });
    }

}
