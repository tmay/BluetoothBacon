package tmay.bluetoothbacon.lightbluebean;

import android.app.Activity;
import android.view.Window;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.WindowFeature;

import nl.littlerobots.bean.Bean;
import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.lightbluebean.fragments.BeanDeviceDisplay;
import tmay.bluetoothbacon.lightbluebean.fragments.BlueBeanFinder;

@WindowFeature({Window.FEATURE_INDETERMINATE_PROGRESS})
@EActivity(R.layout.activity_main)
public class LightBlueBeanMain extends Activity {


    @AfterViews
    void onAfterViews() {
        showBeanFinder();
    }

    private void showBeanFinder() {
        BlueBeanFinder beanFinder = BlueBeanFinder.newInstance();

        getFragmentManager().beginTransaction()
                .replace(R.id.container, beanFinder, BlueBeanFinder.class.getSimpleName())
                .commit();

        beanFinder.setOnBeanSelectedListener(new BlueBeanFinder.OnBeanSelectionListener() {
            @Override
            public void onBeanSelected(Bean bean) {
                showBeanDevice(bean);
            }
        });
    }

    @UiThread
    void showBeanDevice(Bean bean) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, BeanDeviceDisplay.newInstance(bean))
                .commit();
    }

}
