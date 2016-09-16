package tanglie.mystaggeredgridview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;

import java.lang.Override;
import java.util.List;

/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class MyStaggeredGridView extends ViewGroup {



    private AdapterViewManager viewManager = new AdapterViewManager(this);
    
    private int columnCurrentBottom[] = new int[viewManager.getColumnCount()];
    private int columnCurrentTop[] = new int[viewManager.getColumnCount()];

    private View newTopViewConvertView[] = new View[viewManager.getColumnCount()];
    private View newBottomViewConvertView[] = new View[viewManager.getColumnCount()];

    private float lastMotionEventY;
    private float currentTop;

    private boolean hasMeasured;
    private boolean hasLayouted;

    private static final int PIXELS_PER_SECOND = 1000;
    private static final int MAX_VELOCITY = 100000;
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private int pointerId;
    private ValueAnimator scrollAnimator;

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
        if(hasMeasured){
            return;
        }
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        viewManager.setColumnMaxWidth(widthSpecSize / viewManager.getColumnCount());
        int[] tempBottom = new int[columnCurrentBottom.length];
        System.arraycopy(columnCurrentBottom, 0, tempBottom, 0, columnCurrentBottom.length);
        addNewAboveItems();
        addNewBelowItems(tempBottom);
        hasMeasured = true;
        hasLayouted = false;
    }

    private void addNewBelowItems(int[] tempBottom) {
        for(int i = 0; i < viewManager.getColumnCount(); i++){
            while(tempBottom[i] < currentTop + getMeasuredHeight()){
                AdapterViewItem item = viewManager.getViewFromBelow(i, newBottomViewConvertView[i]);
                newBottomViewConvertView[i] = null;
                if(item == null){
                    break;
                }
                addView(item, i % viewManager.getColumnCount());
                tempBottom[i] += item.getView().getMeasuredHeight();
            }
        }
    }

    private void addNewAboveItems() {
        for(int i = 0; i < viewManager.getColumnCount(); i++){
            if(viewManager.hasItem(i)){
                while(columnCurrentTop[i] > currentTop){
                    AdapterViewItem item = viewManager.getViewFromAbove(i, newTopViewConvertView[i]);
                    newTopViewConvertView[i] = null;
                    if(item == null){
                        break;
                    }
                    addView(item, i % viewManager.getColumnCount());
                    columnCurrentTop[i] -= item.getView().getMeasuredHeight();
                }
            }
        }
    }

    

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(hasLayouted){
            return;
        }
        for(int i = 0; i < viewManager.getColumnCount(); i++){
            columnCurrentBottom[i] = columnCurrentTop[i];

            List<AdapterViewItem> visibleViewsInColumn = viewManager.getInScreenViewsInColumn(i);
            if(i == 0){
                System.out.println("visibleViewsInColumn.size(): " + visibleViewsInColumn.size());
            }
            for(AdapterViewItem item : visibleViewsInColumn){

                item.getView().layout(i * viewManager.getColumnMaxWidth(), columnCurrentBottom[i] - (int) currentTop,
                        i * viewManager.getColumnMaxWidth() + item.getView().getMeasuredWidth(), columnCurrentBottom[i] + item.getView().getMeasuredHeight() - (int) currentTop);
                columnCurrentBottom[i] += item.getView().getMeasuredHeight();
            }
            recycleViews(visibleViewsInColumn, i);
        }
        invalidate();
        hasMeasured = false;
        hasLayouted = true;
    }

    private void recycleViews(List<AdapterViewItem> visibleViewsInColumn, int columnNumber) {
        for(int i = 0; i < visibleViewsInColumn.size(); i++){
            AdapterViewItem item = visibleViewsInColumn.get(i);
            if(columnCurrentTop[columnNumber] + item.getView().getMeasuredHeight() < currentTop){
                if(columnNumber == 0){
                    System.out.println("1: columnCurrentTop[columnNumber]: " + columnCurrentTop[columnNumber]
                            + " currentTop: " + currentTop);
                }
                removeView(item, true);
                columnCurrentTop[columnNumber] += item.getView().getMeasuredHeight();
                if(columnNumber == 0){
                    System.out.println("2: columnCurrentTop[columnNumber]: " + columnCurrentTop[columnNumber]
                            + " currentTop: " + currentTop);
                }
                newBottomViewConvertView[columnNumber] = item.getView();
            }else{
                break;
            }
        }
        for(int i = visibleViewsInColumn.size() - 1; i >= 0; i--){
            AdapterViewItem item = visibleViewsInColumn.get(i);
            if(columnCurrentBottom[columnNumber] - item.getView().getMeasuredHeight() > currentTop + getMeasuredHeight()){
                removeView(item, false);
                columnCurrentBottom[columnNumber] -= item.getView().getMeasuredHeight();
                newTopViewConvertView[columnNumber] = item.getView();
            }else{
                break;
            }
        }
    }

    private void addView(AdapterViewItem item, int columnNumber){
        if(item == null){
            return;
        }
        addView(item.getView());
        viewManager.onViewAdded(item, columnNumber);
    }

    private void removeView(AdapterViewItem item, boolean isFromAbove){
        if(item == null){
            return;
        }
        removeView(item.getView());
        viewManager.onViewRemoved(item, isFromAbove);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(scrollAnimator != null){
                    scrollAnimator.cancel();
                }
                lastMotionEventY = event.getY();
                pointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                if(!willExceedTop(event.getY()) && !willExceedBottom(event.getY())){
                    float deltaY = event.getY() - lastMotionEventY;
                    currentTop = currentTop - deltaY;
                    
                    requestLayout();
                }
                lastMotionEventY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND, MAX_VELOCITY);
                final float velocityY = velocityTracker.getYVelocity(pointerId);
                smoothScroll(velocityY);
                velocityTracker.clear();
                break;
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.clear();
                break;
        }
        return true;
    }

    private void smoothScroll(final float velocityY) {
        final float startY = lastMotionEventY;
        scrollAnimator = ValueAnimator.ofFloat(0, 1);
        scrollAnimator.setDuration(1000);
        scrollAnimator.setInterpolator(new DecelerateInterpolator());
        scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float targetY = (velocityY / 10) * (Float) animation.getAnimatedValue() + startY;
                if(!willExceedTop(targetY) && !willExceedBottom(targetY)){
                    float deltaY = targetY - lastMotionEventY;
                    currentTop = currentTop - deltaY;
                    requestLayout();
                    lastMotionEventY = targetY;
                }
            }
        });
        scrollAnimator.start();
    }

    private boolean willExceedBottom(float motionEventY) {
        float[] exceedAmount = new float[viewManager.getColumnCount()];
        float deltaY = motionEventY - lastMotionEventY;
        for(int i = 0; i < columnCurrentBottom.length; i++){
            exceedAmount[i] = currentTop - deltaY + getMeasuredHeight() - columnCurrentBottom[i];
        }

        return viewManager.willExceedBottom(exceedAmount, newBottomViewConvertView);
    }

    private boolean willExceedTop(float motionEventY) {
        float deltaY = motionEventY - lastMotionEventY;
        return currentTop - deltaY < 0;
    }

    public void setAdapter(ListAdapter adapter) {
        if(adapter == null){
            return;
        }
        viewManager.setAdapter(adapter);
        removeAllViews();
    }
}
