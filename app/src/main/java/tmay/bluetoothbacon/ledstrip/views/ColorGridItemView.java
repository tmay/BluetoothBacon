package tmay.bluetoothbacon.ledstrip.views;

import android.content.Context;
import android.widget.FrameLayout;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;

import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.ledstrip.util.ColorUtility;

/**
 * Created by Terry on 10/5/14.
 */
@EViewGroup(R.layout.list_item_color)
public class ColorGridItemView extends FrameLayout {

    @Bean
    ColorUtility colorUtility;
    public ColorGridItemView(Context context) {
        super(context);
    }

    public void bind(int color) {
        this.setBackgroundColor(colorUtility.wheel(color));
    }
}
