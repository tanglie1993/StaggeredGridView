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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for(int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(params);
            view.measure(0, 0);
        }
    }

    private int cumulativeHeight = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            view.layout(0, view.getMeasuredHeight() * i, view.getMeasuredWidth(), view.getMeasuredHeight() + view.getMeasuredHeight() * i);
            cumulativeHeight += view.getMeasuredHeight();
        }
        invalidate();
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        removeAllViews();
        cumulativeHeight = 0;
        if(adapter != null){
            for(int i = 0; i < adapter.getCount(); i++){
                View view = adapter.getView(i, null, this);
                addView(view);
            }
        }
    }
}
