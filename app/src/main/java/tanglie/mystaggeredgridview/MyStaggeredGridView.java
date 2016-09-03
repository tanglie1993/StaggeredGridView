package tanglie.mystaggeredgridview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.Override;

/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class MyStaggeredGridView extends LinearLayout {

    private ListAdapter adapter;

    public MyStaggeredGridView(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public MyStaggeredGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public MyStaggeredGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    public MyStaggeredGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(adapter != null){
            for(int i = 0; i < adapter.getCount(); i++){
                View view = adapter.getView(i, null, this);
                final int widthSpec = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);
                final int heightSpec = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);
                view.measure(widthSpec, heightSpec);
                view.layout(0,0,200,200);
                addView(view);
            }
        }
        invalidate();
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        requestLayout();
    }
}
