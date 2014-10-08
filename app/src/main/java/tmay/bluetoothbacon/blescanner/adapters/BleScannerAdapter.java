package tmay.bluetoothbacon.blescanner.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;

import tmay.bluetoothbacon.blescanner.models.BleScannerObject;
import tmay.bluetoothbacon.blescanner.views.BleScannerItemView;
import tmay.bluetoothbacon.blescanner.views.BleScannerItemView_;

/**
 * Created by Terry on 10/4/14.
 */
@EBean
public class BleScannerAdapter extends BaseAdapter {

    private ArrayList<BleScannerObject> items;
    private Context context;
    public BleScannerAdapter(Context context) {
        this.context = context;
        clear();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public BluetoothDevice getItem(int i) {
        return items.get(i).device;
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).device.hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BleScannerItemView itemView;

        if (view == null) {
            itemView = BleScannerItemView_.build(context);
        } else {
            itemView = (BleScannerItemView) view;
        }
        itemView.bind(items.get(i));

        return itemView;
    }

    public void clear() {
        items = new ArrayList<BleScannerObject>();
        notifyDataSetChanged();
    }

    public void addDevice(BluetoothDevice device, int rssi) {
        if (!BleScannerObject.isDuplicateDevice(device, items)) {
            items.add(new BleScannerObject(device, rssi));
            notifyDataSetChanged();
        } else {
//            int index = BleScannerObject.findDeviceIndex(device, items);
//            BleScannerObject target = items.get(index);
//            if (rssi != target.rssi) {
//                target.rssi = rssi;
//                notifyDataSetChanged();
//            }
        }
    }
}
