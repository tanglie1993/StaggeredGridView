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
    private int columnCount = 5;
    private int columnMaxWidth = 0;
    private int columnCurrentTop[] = new int[columnCount];

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
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        columnMaxWidth = widthSpecSize / columnCount;
        for(int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            view.measure(0, 0);
            if(view.getMeasuredWidth() > columnMaxWidth){
                float scaleFactor = (float) columnMaxWidth / (float) view.getMeasuredWidth();
                int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(columnMaxWidth, MeasureSpec.EXACTLY);
                int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (scaleFactor * view.getMeasuredHeight()), MeasureSpec.EXACTLY);
                view.measure(newWidthMeasureSpec, newHeightMeasureSpec);
                System.out.println("0: " + view.getMeasuredWidth());
                System.out.println("columnMaxWidth: " + columnMaxWidth);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int indexInCurrentRow = 0;
        for(int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            System.out.println("1: " + view.getMeasuredWidth());
            if(indexInCurrentRow >= columnCount){
                indexInCurrentRow = 0;
            }
            view.layout(indexInCurrentRow * columnMaxWidth, columnCurrentTop[indexInCurrentRow],
                    indexInCurrentRow * columnMaxWidth + view.getMeasuredWidth(), view.getMeasuredHeight() + columnCurrentTop[indexInCurrentRow]);
            columnCurrentTop[indexInCurrentRow] += view.getMeasuredHeight();
            indexInCurrentRow++;
        }
        invalidate();
        for(int i = 0; i < columnCurrentTop.length; i++){
            columnCurrentTop[i] = 0;
        }
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
