package tanglie.mystaggeredgridview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
public class AdapterViewManager {

    public static final int IN_SCREEN = 1;
    public static final int ABOVE_SCREEN = 2;
    public static final int BELOW_SCREEN = 3;

    private ListAdapter adapter;
    private int columnCount = 3;
    private int[] itemState;

    private int columnMaxWidth = 0;

    private Map<Integer, View> inScreenViewMap = new HashMap<>();

    private ViewGroup parent;

    public AdapterViewManager(ViewGroup parent) {
        this.parent = parent;
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        itemState = new int[adapter.getCount()];
        Arrays.fill(itemState, BELOW_SCREEN);
    }

    public int getColumnCount() {
        return columnCount;
    }

    public boolean hasItem(int columnNumber) {
        int index = columnNumber;
        while(index < itemState.length){
            if(itemState[index] == IN_SCREEN){
                return true;
            }
            index += columnCount;
        }
        return false;
    }

    public List<AdapterViewItem> getInScreenViewsInColumn(int columnNumber){
        List<AdapterViewItem> result = new ArrayList<>();
        int index = columnNumber;
        while(index < itemState.length){
            if(itemState[index] == IN_SCREEN){
                result.add(getItem(index, null));
            }
            index += columnCount;
        }
        return result;
    }

    public void onViewAdded(AdapterViewItem item) {
        if(item.getViewIndex() % columnCount == 0){
            System.out.println("onViewAdded " + item.getViewIndex());
        }
        itemState[item.getViewIndex()] = IN_SCREEN;
    }

    public void onViewRemoved(AdapterViewItem item, boolean isFromAbove) {
        if(item.getViewIndex() % columnCount == 0){
            System.out.println("onViewRemoved " + item.getViewIndex() + " " +isFromAbove);
        }
        if(isFromAbove){
            itemState[item.getViewIndex()] = ABOVE_SCREEN;
        }else{
            itemState[item.getViewIndex()] = BELOW_SCREEN;
        }
        inScreenViewMap.remove(item.getViewIndex());
    }

    public AdapterViewItem getViewFromAbove(int columnNumber, View convertView) {
        for(int i = columnNumber; i < itemState.length; i+= columnCount){
            if(itemState[i] == ABOVE_SCREEN){
                if(i + columnCount >= itemState.length || itemState[i + columnCount] != ABOVE_SCREEN){
                    return getItem(i, convertView);
                }
            }
        }
        return null;
    }

    public AdapterViewItem getViewFromBelow(int columnNumber, View convertView) {
        for(int i = columnNumber; i < itemState.length; i+= columnCount){
            if(itemState[i] == BELOW_SCREEN){
                return getItem(i, convertView);
            }
        }
        return null;
    }

    private AdapterViewItem getItem(int itemIndex, View convertView) {
        AdapterViewItem item = new AdapterViewItem();
        View view = inScreenViewMap.get(itemIndex);
        if(view != null){
            item.setView(view);
        }else{
            view = adapter.getView(itemIndex, convertView, parent);
            inScreenViewMap.put(itemIndex, view);
            view.measure(0, 0);
            if(view.getMeasuredWidth() > columnMaxWidth){
                scaleView(view);
            }
            item.setView(view);
        }
        item.setViewIndex(itemIndex);
        return item;
    }

    private void scaleView(View view) {
        float scaleFactor = (float) columnMaxWidth / (float) view.getMeasuredWidth();
        int newWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(columnMaxWidth, View.MeasureSpec.EXACTLY);
        int newHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (scaleFactor * view.getMeasuredHeight()), View.MeasureSpec.EXACTLY);
        view.measure(newWidthMeasureSpec, newHeightMeasureSpec);
    }

    public boolean willExceedBottom(float[] exceedAmount, View[] convertViews) {
        for(int i = 0; i < exceedAmount.length; i++){
            if(exceedAmount[i] > 0){
                AdapterViewItem item = getViewFromBelow(i, convertViews[i]);
                convertViews[i] = null;
                if(item == null){
                    return true;
                }
                if(item.getView().getMeasuredHeight() < exceedAmount[i]){
                    return true;
                }
            }
        }
        return false;
    }

    public int getColumnMaxWidth() {
        return columnMaxWidth;
    }

    public void setColumnMaxWidth(int columnMaxWidth) {
        this.columnMaxWidth = columnMaxWidth;
    }
}
