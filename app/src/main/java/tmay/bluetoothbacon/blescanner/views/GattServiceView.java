package tmay.bluetoothbacon.blescanner.views;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import tmay.bluetoothbacon.R;

/**
 * Created by Terry on 10/6/14.
 */
@EViewGroup(R.layout.list_item_gatt_service)
public class GattServiceView extends LinearLayout {

    @ViewById(R.id.txt_uuids)
    TextView txtUUIDS;

    @ViewById(R.id.txt_characteristics)
    TextView txtCharacteristics;

    public GattServiceView(Context context) {
        super(context);
    }

    public void bind(BluetoothGattService service) {
        txtUUIDS.setText("UUID: "+service.getUuid().toString());
        String chars = "Characteristics: \n";
        for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()) {
            chars += characteristic.getUuid().toString();
            chars += "\n";
        }
        txtCharacteristics.setText(chars);
    }
}
