package tanglie.mystaggeredgridview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class ImageAdapter extends BaseAdapter {

    private LayoutInflater inflator;

    public ImageAdapter(Activity activity){
        this.inflator = activity.getLayoutInflater();
    }
    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflator.inflate(R.layout.item_layout, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        imageView.setBackgroundResource(R.drawable.aa);
        convertView.setDrawingCacheEnabled(true);
        convertView.getDrawingCache();
        return convertView;
    }
}
