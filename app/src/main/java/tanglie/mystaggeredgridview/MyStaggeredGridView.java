package tanglie.mystaggeredgridview;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.lang.Override;
import java.util.List;

/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class MyStaggeredGridView extends ViewGroup {

    private AdapterViewManager viewManager = new AdapterViewManager();
    private int columnMaxWidth = 0;
    private int columnCurrentBottom[] = new int[viewManager.getColumnCount()];
    private int columnCurrentTop[] = new int[viewManager.getColumnCount()];



    private float lastMotionEventY;
    private float currentTop;

    private boolean hasMeasured;
    private boolean hasLayouted;

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
        columnMaxWidth = widthSpecSize / viewManager.getColumnCount();
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
                AdapterViewItem item = viewManager.getViewFromBelow(i);
                if(item == null){
                    break;
                }
                addView(item);
                tempBottom[i] += item.getView().getMeasuredHeight();
            }
        }
    }

    private void addNewAboveItems() {
        for(int i = 0; i < viewManager.getColumnCount(); i++){
            if(viewManager.hasItem(i)){
                while(columnCurrentTop[i] > currentTop){
                    AdapterViewItem item = viewManager.getViewFromAbove(i);
                    if(item == null){
                        break;
                    }
                    addView(item);
                    columnCurrentTop[i] -= item.getView().getMeasuredHeight();
                }
            }
        }
    }

    private void scaleView(View view) {
        float scaleFactor = (float) columnMaxWidth / (float) view.getMeasuredWidth();
        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(columnMaxWidth, MeasureSpec.EXACTLY);
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (scaleFactor * view.getMeasuredHeight()), MeasureSpec.EXACTLY);
        view.measure(newWidthMeasureSpec, newHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(hasLayouted){
            return;
        }
        for(int i = 0; i < viewManager.getColumnCount(); i++){
            columnCurrentBottom[i] = columnCurrentTop[i];

            List<AdapterViewItem> visibleViewsInColumn = viewManager.getInScreenViewsInColumn(i);
            for(AdapterViewItem item : visibleViewsInColumn){
                item.getView().layout(i * columnMaxWidth, columnCurrentBottom[i] - (int) currentTop,
                        i * columnMaxWidth + item.getView().getMeasuredWidth(), columnCurrentBottom[i] + item.getView().getMeasuredHeight() - (int) currentTop);
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
                removeView(item, true);
                columnCurrentTop[columnNumber] += item.getView().getMeasuredHeight();
            }else{
                break;
            }
        }
        for(int i = visibleViewsInColumn.size() - 1; i >= 0; i--){
            AdapterViewItem item = visibleViewsInColumn.get(i);
            if(columnCurrentBottom[columnNumber] - item.getView().getMeasuredHeight() > currentTop + getMeasuredHeight()){
                removeView(item, false);
                columnCurrentBottom[columnNumber] -= item.getView().getMeasuredHeight();
            }else{
                break;
            }
        }
    }

    private void addView(AdapterViewItem item){
        if(item == null){
            return;
        }
        item.getView().measure(0, 0);
        if(item.getView().getMeasuredWidth() > columnMaxWidth){
            scaleView(item.getView());
        }
        addView(item.getView());
        viewManager.onViewAdded(item);
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
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastMotionEventY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!willExceedTop(event.getY()) && !willExceedBottom(event.getY())){
                    float deltaY = event.getY() - lastMotionEventY;
                    currentTop = currentTop - deltaY;
                    scrollTo(currentTop - deltaY);
                }
                lastMotionEventY = event.getY();
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private boolean willExceedBottom(float motionEventY) {
        float bottom = 0;
        for(int i = 0; i < columnCurrentBottom.length; i++){
            if(columnCurrentBottom[i] > bottom){
                bottom = columnCurrentBottom[i];
            }
        }
        float deltaY = motionEventY - lastMotionEventY;
        return currentTop - deltaY + getMeasuredHeight() > bottom;
    }

    private boolean willExceedTop(float motionEventY) {
        float deltaY = motionEventY - lastMotionEventY;
        return currentTop - deltaY < 0;
    }

    private void scrollTo(float Y) {
        requestLayout();
    }

    public void setAdapter(ListAdapter adapter) {
        if(adapter == null){
            return;
        }
        viewManager.setAdapter(adapter);
        removeAllViews();
    }
}
