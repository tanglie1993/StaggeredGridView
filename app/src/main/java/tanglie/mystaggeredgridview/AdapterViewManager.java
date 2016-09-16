package tanglie.mystaggeredgridview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
public class AdapterViewManager {

    private ListAdapter adapter;
    private int columnCount = 3;
    private List<List<AdapterViewItem>> items;

    private int columnMaxWidth = 0;

    private Map<Integer, View> inScreenViewMap = new HashMap<>();

    private ViewGroup parent;

    private int addedViewCount = 0;

    public AdapterViewManager(ViewGroup parent) {
        this.parent = parent;
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        items = new ArrayList<>();
        for(int i = 0; i < columnCount; i++){
            items.add(new ArrayList<AdapterViewItem>());
        }
    }

    public int getColumnCount() {
        return columnCount;
    }

    public boolean hasItem(int columnNumber) {
        for(AdapterViewItem item : items.get(columnNumber)){
            if(item.getItemState() == AdapterViewItem.IN_SCREEN){
                return true;
            }
        }
        return false;
    }

    public List<AdapterViewItem> getInScreenViewsInColumn(int columnNumber){
        List<AdapterViewItem> result = new ArrayList<>();
        for(AdapterViewItem item : items.get(columnNumber)){
            if(item.getItemState() == AdapterViewItem.IN_SCREEN){
                result.add(getItem(item.getViewIndex(), null));
            }
        }
        return result;
    }

    public void onViewAdded(AdapterViewItem item, int columnNumber) {
        item.setItemState(AdapterViewItem.IN_SCREEN);
        List<AdapterViewItem> columnItems = items.get(columnNumber);
        for(int i = 0; i < columnItems.size(); i++){
            if(columnItems.get(i).getViewIndex() > item.getViewIndex()){
                columnItems.add(i, item);
                return;
            }
        }
        columnItems.add(item);
    }

    public void onViewRemoved(AdapterViewItem itemToRemove, boolean isFromAbove) {
        AdapterViewItem item = queryItem(itemToRemove);
        if(item == null){
            return;
        }
        if(isFromAbove){
            item.setItemState(AdapterViewItem.ABOVE_SCREEN);
        }else{
            item.setItemState(AdapterViewItem.BELOW_SCREEN);
        }
        inScreenViewMap.remove(item.getViewIndex());

        System.out.println("onViewRemoved: " + getInScreenViewsInColumn(0).size());
    }

    private AdapterViewItem queryItem(AdapterViewItem itemToRemove) {
        for(List<AdapterViewItem> itemList : items){
            for(AdapterViewItem item : itemList){
                if(item.getViewIndex() == itemToRemove.getViewIndex()){
                    return item;
                }
            }
        }
        return null;
    }

    public AdapterViewItem getViewFromAbove(int columnNumber, View convertView) {
        List<AdapterViewItem> itemsInColumn = items.get(columnNumber);
        for(int i = 0; i < itemsInColumn.size(); i++){
            AdapterViewItem item = itemsInColumn.get(i);
            if(item.getItemState() == AdapterViewItem.ABOVE_SCREEN){
                if(i + 1 >= itemsInColumn.size() || itemsInColumn.get(i+1).getItemState() != AdapterViewItem.ABOVE_SCREEN){
                    return getItem(item.getViewIndex(), convertView);
                }
            }
        }
        return null;
    }

    public AdapterViewItem getViewFromBelow(int columnNumber, View convertView) {
        List<AdapterViewItem> itemsInColumn = items.get(columnNumber);
        for(int i = 0; i < itemsInColumn.size(); i++){
            AdapterViewItem item = itemsInColumn.get(i);
            if(item.getItemState() == AdapterViewItem.BELOW_SCREEN){
                return getItem(item.getViewIndex(), convertView);
            }
        }
        while(addedViewCount < adapter.getCount()){
            AdapterViewItem item = getItem(addedViewCount, convertView);
            addedViewCount++;
            return item;
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
        if(negativeExists(exceedAmount)){
            return false;
        }
        for(int i = 0; i < exceedAmount.length; i++){
            System.out.println("exceedAmount[i] " + exceedAmount[i]);
            if(exceedAmount[i] > 0){
                AdapterViewItem item = getViewFromBelow(i, convertViews[i]);
                convertViews[i] = null;
                if(item == null){
                    System.out.println("item == null");
                }else{
                    System.out.println("item.getView().getMeasuredHeight(): "+ item.getView().getMeasuredHeight());
                    System.out.println("exceedAmount[i]: "+ exceedAmount[i]);
                }

                if(item != null && item.getView().getMeasuredHeight() >= exceedAmount[i]){
                    System.out.println("willExceedBottom false");
                    return false;
                }
            }
        }
        System.out.println("willExceedBottom true");
        return true;
    }

    private boolean negativeExists(float[] exceedAmount) {
        for(float value : exceedAmount){
            if(value < 0){
                return true;
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
