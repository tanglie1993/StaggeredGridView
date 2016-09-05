package tanglie.mystaggeredgridview;

import android.view.View;
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

    private Map<Integer, View> tempMap = new HashMap<>();

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
                result.add(getItem(index));
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
        tempMap.put(item.getViewIndex(), item.getView());
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

    }

    public AdapterViewItem getViewFromAbove(int columnNumber) {
        for(int i = columnNumber; i < itemState.length; i+= columnCount){
            if(itemState[i] == ABOVE_SCREEN){
                if(i + columnCount >= itemState.length || itemState[i + columnCount] != ABOVE_SCREEN){
                    return getItem(i);
                }
            }
        }
        return null;
    }

    public AdapterViewItem getViewFromBelow(int columnNumber) {
        for(int i = columnNumber; i < itemState.length; i+= columnCount){
            if(itemState[i] == BELOW_SCREEN){
                return getItem(i);
            }
        }
        return null;
    }

    private AdapterViewItem getItem(int itemIndex) {
        AdapterViewItem item = new AdapterViewItem();
        View view = tempMap.get(itemIndex);
        if(view != null){
            item.setView(view);
        }else{
            item.setView(adapter.getView(itemIndex, null, null));
        }
        item.setViewIndex(itemIndex);
        return item;
    }


}
