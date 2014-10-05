package tmay.bluetoothbacon.ledstrip.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EView;

/**
 * Created by Terry on 10/4/14.
 */

public class LedGridView extends View {
    final private int LEFT_MARGIN = 10;
    final private int TOP_MARGIN = 10;

    Paint paint = new Paint();
    public Context context;

    public LedGridView(Context context) {
        super(context);
        this.context = context;
    }

    public LedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public LedGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int totalWidth;
        int totalHeight = dpToPixels(TOP_MARGIN);
        int gap = dpToPixels(10);
        int width = dpToPixels(40);
        int height = dpToPixels(40);
        int count = 0;
        for (int c=0; c<9;c++) {
            totalWidth = dpToPixels(LEFT_MARGIN);
            for (int r=0; r<5; r++) {
                paint.setColor(wheel(count));
                count += 5;
                paint.setStrokeWidth(0);
                canvas.drawRect(totalWidth, totalHeight, totalWidth+width, totalHeight+height, paint);
                totalWidth += width + gap;
                //count++;
            }
            totalHeight += height + gap;
        }
    }

    private int wheel(int WheelPos) {
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

    public int dpToPixels(final int dpUnits) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dpUnits * scale) + 0.5);
    }
}
