package tmay.bluetoothbacon.ledstrip.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import tmay.bluetoothbacon.R;
import tmay.bluetoothbacon.ledstrip.util.ColorUtility;

/**
 * Created by Terry on 10/5/14.
 */
@EFragment(R.layout.fragment_edit_color)
public class EditColorFragment extends DialogFragment {

    public static EditColorFragment newInstance(int color) {
        EditColorFragment fragment = new EditColorFragment_();
        Bundle args = new Bundle();
        args.putInt("color", color);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnColorAdjustListener {
        public void onColorChanged(int color);
    }

    @Bean
    ColorUtility colorUtility;

    @ViewById(R.id.color_swatch)
    FrameLayout colorSwatch;

    @ViewById(R.id.slider_adjust)
    SeekBar slider;

    public OnColorAdjustListener listener;

    public void setColorAdjustListener(OnColorAdjustListener listener) {
       this.listener = listener;
    }

    @AfterViews
    void onAfterViews() {
        int color = getArguments().getInt("color");
        colorSwatch.setBackgroundColor(colorUtility.wheel(color));
        slider.setProgress(color);
        slider.setMax(255);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                colorSwatch.setBackgroundColor(colorUtility.wheel(i));
                if (listener != null)
                    listener.onColorChanged(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
