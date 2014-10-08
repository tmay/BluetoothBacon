package tmay.bluetoothbacon.blescanner.adapters;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.androidannotations.annotations.EBean;

import java.util.List;

import tmay.bluetoothbacon.blescanner.views.GattServiceView;
import tmay.bluetoothbacon.blescanner.views.GattServiceView_;

/**
 * Created by Terry on 10/6/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ServiceListAdapter extends BaseAdapter {

    private List<BluetoothGattService> services;
    private Context context;

    public ServiceListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int i) {
        return services.get(i);
    }

    @Override
    public long getItemId(int i) {
        return services.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        GattServiceView gattServiceView;

        if (view == null) {
            gattServiceView = GattServiceView_.build(context);
        } else {
            gattServiceView = (GattServiceView) view;
        }

        gattServiceView.bind(services.get(i));

        return gattServiceView;
    }

    public void setServices(List<BluetoothGattService> services) {
        this.services = services;
        notifyDataSetChanged();
    }
}
