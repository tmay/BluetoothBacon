package tmay.bluetoothbacon.ledstrip.util;

import android.graphics.Color;

import org.androidannotations.annotations.EBean;

import java.util.Random;

/**
 * Created by Terry on 10/5/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ColorUtility {

    public int[] getColorArray() {
        int[] colors = new int[60];
        for (int i = 0; i < 60; i++) {
            colors[i] = i*2;
        }
        return colors;
    }

    public int[] getRandomColorArray() {
        int[] colors = new int[60];
        Random randomGen = new Random();
        for (int i = 0; i < 60; i++) {
            colors[i] = randomGen.nextInt(255);
        }
        return colors;
    }

    public int wheel(int WheelPos) {
        if(WheelPos < 85) {
            return Color.rgb(WheelPos * 2, 255 - WheelPos * 3, 0);
            //return strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
        } else if(WheelPos < 170) {
            WheelPos -= 85;
            return Color.rgb(255 - WheelPos * 2, 0, WheelPos * 3);
            //return strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
        } else {
            WheelPos -= 170;
            return Color.rgb(0, WheelPos * 3, 255 - WheelPos * 3);
            //return strip.Color(0, WheelPos * 3, 255 - WheelPos * 3);
        }
    }
}
