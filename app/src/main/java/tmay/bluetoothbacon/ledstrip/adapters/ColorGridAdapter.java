package tmay.bluetoothbacon.ledstrip.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.androidannotations.annotations.EBean;

import tmay.bluetoothbacon.ledstrip.views.ColorGridItemView;
import tmay.bluetoothbacon.ledstrip.views.ColorGridItemView_;

/**
 * Created by Terry on 10/5/14.
 */
@EBean
public class ColorGridAdapter extends BaseAdapter {

    private Context context;

    private int[] colors;

    public ColorGridAdapter(Context context) {
        this.context = context;
        colors = new int[0];
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int i) {
        return colors[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ColorGridItemView colorGridItemView;

        if (view == null) {
            colorGridItemView = ColorGridItemView_.build(context);
        } else {
            colorGridItemView = (ColorGridItemView) view;
        }

        colorGridItemView.bind(colors[i]);

        return colorGridItemView;
    }

    public int getColor(int i) {
        return colors[i];
    }

    public void setColors(int[] colors) {
        this.colors = colors;
        notifyDataSetChanged();
    }

    public void setColor(int position, int color) {
        colors[position] = color;
        notifyDataSetChanged();
    }

    public int[] getColors() {
        return colors;
    }
}
