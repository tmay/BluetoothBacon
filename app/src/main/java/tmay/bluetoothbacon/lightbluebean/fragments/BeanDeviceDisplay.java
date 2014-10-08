package tmay.bluetoothbacon.lightbluebean.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanListener;
import nl.littlerobots.bean.message.Acceleration;
import nl.littlerobots.bean.message.Callback;
import nl.littlerobots.bean.message.DeviceInfo;
import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.lightbluebean.colorpicker.ColorPicker;


/**
 * Created by Terry on 9/21/14.
 */
@EFragment(R.layout.fragment_bean_device)
public class BeanDeviceDisplay extends Fragment implements BeanListener {
    public final static String BEAN_EXTRA = "bean";

    public static BeanDeviceDisplay newInstance(Bean bean) {
        Bundle args = new Bundle();
        args.putParcelable(BEAN_EXTRA, bean);

        BeanDeviceDisplay fragment = new BeanDeviceDisplay_();
        fragment.setArguments(args);
        return fragment;
    }

    @ViewById(R.id.txt_temp)
    TextView txtTemp;

    @ViewById(R.id.txt_firmware_version)
    TextView txtFirmwareVersion;

    @ViewById(R.id.txt_hardware_version)
    TextView txtHardwareVersion;

    @ViewById(R.id.txt_software_version)
    TextView txtSoftwareVersion;

    @ViewById(R.id.txt_accel_data)
    TextView accelData;

    @ViewById(R.id.btn_advertising)
    Button btnAccel;

    //https://github.com/chiralcode/Android-Color-Picker/
    @ViewById(R.id.widget_color_picker)
    ColorPicker colorPicker;

    private Bean bean;
    private boolean readAccelerometer;

    @AfterViews
    void onAfterViews() {
        this.bean = getArguments().getParcelable(BEAN_EXTRA);

        if (bean != null && !bean.isConnected()) {
            bean.connect(getActivity(), this);
            getActivity().setProgressBarIndeterminateVisibility(true);
        }
    }

    @Click(R.id.btn_advertising)
    void onReadAccelerometer() {
        if (!readAccelerometer) {
            beginAccelerometerRead(5000);
            btnAccel.setText("Stop Rerading");
        } else {
            stopAccelerometerRead();
            btnAccel.setText("Read Accelerometer");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bean.setLed(0,0,0);
        //bean.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bean.disconnect();
        bean = null;
    }

    @UiThread
    void updateTempDisplay(Integer temp) {
        txtTemp.setText(temp+"°");
    }

    @UiThread
    void updateDeviceInfo(DeviceInfo result) {
        txtFirmwareVersion.setText(result.firmwareVersion());
        txtHardwareVersion.setText(result.hardwareVersion());
        txtSoftwareVersion.setText(result.softwareVersion());
    }

    @Override
    public void onConnected() {
        getActivity().setProgressBarIndeterminateVisibility(false);
        bean.readTemperature(new Callback<Integer>() {
            @Override
            public void onResult(Integer integer) {
                updateTempDisplay(integer);
            }
        });

        bean.readDeviceInfo(new Callback<DeviceInfo>() {
            @Override
            public void onResult(DeviceInfo result) {
                updateDeviceInfo(result);
            }
        });


        colorPicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int color = colorPicker.getColor();
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);

                bean.setLed(red, green, blue);
                return false;
            }
        });



    }

    @Override
    public void onConnectionFailed() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onSerialMessageReceived(byte[] bytes) {

    }

    @Override
    public void onScratchValueChanged(int i, byte[] bytes) {

    }

    @Background
    void beginAccelerometerRead(int interval) {
        readAccelerometer = true;
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (readAccelerometer == false) {
                    timer.cancel();
                } else {
                    bean.readAcceleration(new Callback<Acceleration>() {
                        @Override
                        public void onResult(Acceleration result) {
                            updateAccelData(result.x(), result.y(), result.z());
                        }
                    });
                }
            }
        }, interval, 100);
    }

    private void stopAccelerometerRead() {
        readAccelerometer = false;
    }

    @UiThread
    void updateAccelData(double x, double y, double z) {
        accelData.setText("x:"+formatValue(x)+" y:"+formatValue(y)+" z:"+formatValue(z));
    }

    private String formatValue(double value) {
        return String.format("%.2f",value);
    }
}
