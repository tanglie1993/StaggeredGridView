package tanglie.mystaggeredgridview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.lang.Override;

/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class MyStaggeredGridView extends ViewGroup {

    private ListAdapter adapter;
    private int columnCount = 3;
    private int columnMaxWidth = 0;
    private int columnCurrentTop[] = new int[columnCount];

    private float lastMotionEventY;
    private float currentTop;

    public MyStaggeredGridView(Context context) {
        super(context);
    }

    public MyStaggeredGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyStaggeredGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyStaggeredGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int indexInCurrentRow = 0;
        for(int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            if(indexInCurrentRow >= columnCount){
                indexInCurrentRow = 0;
            }
            int bottom = Math.round(columnCurrentTop[indexInCurrentRow] - currentTop + view.getMeasuredHeight());
            if(bottom >= 0){
                view.layout(indexInCurrentRow * columnMaxWidth, bottom - view.getMeasuredHeight(),
                        indexInCurrentRow * columnMaxWidth + view.getMeasuredWidth(), bottom);
//                System.out.println("top: " + top + "currentTop: " + currentTop);
            }else{
                view.layout(indexInCurrentRow * columnMaxWidth, - view.getMeasuredHeight(),
                        indexInCurrentRow * columnMaxWidth + view.getMeasuredWidth(), 0);
            }

            columnCurrentTop[indexInCurrentRow] += view.getMeasuredHeight();
            indexInCurrentRow++;
        }
        invalidate();
        for(int i = 0; i < columnCurrentTop.length; i++){
            columnCurrentTop[i] = 0;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastMotionEventY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = event.getY() - lastMotionEventY;
                if(currentTop + deltaY > 0){
                    currentTop = currentTop + deltaY;
                    scrollTo(currentTop + deltaY);
                }
                lastMotionEventY = event.getY();
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private void scrollTo(float Y) {
        requestLayout();
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
