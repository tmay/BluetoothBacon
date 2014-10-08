package tmay.bluetoothbacon.blescanner.views;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.StringRes;

import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.blescanner.models.BleScannerObject;

/**
 * Created by Terry on 10/4/14.
 */
@EViewGroup(R.layout.list_item_ble_item)
public class BleScannerItemView extends LinearLayout {

    @ViewById(R.id.txt_device_address)
    TextView deviceAddressText;

    @ViewById(R.id.txt_device_rssi)
    TextView deviceRssiText;

    @ViewById(R.id.img_device_icon)
    ImageView iconImage;

    @StringRes(R.string.lbl_device_address)
    String deviceAddressLabel;

    @StringRes(R.string.lbl_rssi)
    String rssiLabel;

    @DrawableRes(R.drawable.bluetooth)
    Drawable iconDrawable;

    public BleScannerItemView(Context context) {
        super(context);
    }

    public void bind(BleScannerObject deviceInfo) {
        String name = deviceInfo.device.getName();
        name = (name == null) ? "Unknown" : name;
        String type = "Type: "+getDeviceType(deviceInfo.device.getType());

        deviceAddressText.setText(name+"\n"+deviceInfo.device.getAddress()+"\n"+type);
        deviceRssiText.setText(rssiLabel+" "+deviceInfo.rssi);
        iconImage.setImageDrawable(iconDrawable);
    }

    private String getDeviceType(int type) {
        switch (type) {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return "Classic";
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return "Dual";
            case BluetoothDevice.DEVICE_TYPE_LE:
                return "BLE";
            default:
                return "Unknown";
        }
    }
}
