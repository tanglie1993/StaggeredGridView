package tanglie.mystaggeredgridview;

import android.content.Context;
import android.icu.util.Measure;
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
public class MyStaggeredGridView extends ViewGroup {

    private ListAdapter adapter;
    private int columnCount = 10;

    public MyStaggeredGridView(Context context) {
        super(context);
//        setOrientation(VERTICAL);
    }

    public MyStaggeredGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setOrientation(VERTICAL);
    }

    public MyStaggeredGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setOrientation(VERTICAL);
    }

    public MyStaggeredGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
//        setOrientation(VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int columnMaxWidth = widthSpecSize / columnCount;
        for(int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            view.measure(0, 0);
            if(view.getMeasuredWidth() > columnMaxWidth){
                float scaleFactor = (float) columnMaxWidth / (float) view.getMeasuredWidth();
                int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(columnMaxWidth, MeasureSpec.EXACTLY);
                int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (scaleFactor * view.getMeasuredHeight()), MeasureSpec.EXACTLY);
                view.measure(newWidthMeasureSpec, newHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cumulativeHeight = 0;
        for(int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            view.layout(0, cumulativeHeight, view.getMeasuredWidth(), view.getMeasuredHeight() + cumulativeHeight);
            cumulativeHeight += view.getMeasuredHeight();
        }
        invalidate();
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        removeAllViews();
        if(adapter != null){
            for(int i = 0; i < adapter.getCount(); i++){
                View view = adapter.getView(i, null, this);
                addView(view);
            }
        }
    }
}
