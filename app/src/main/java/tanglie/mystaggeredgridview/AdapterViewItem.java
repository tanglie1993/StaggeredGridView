package tanglie.mystaggeredgridview;

import android.content.ClipData;
import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
public class AdapterViewItem {

    public static final int IN_SCREEN = 1;
    public static final int ABOVE_SCREEN = 2;
    public static final int BELOW_SCREEN = 3;

    private View view;
    private int viewIndex;
    private int itemState = BELOW_SCREEN;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getViewIndex() {
        return viewIndex;
    }

    public void setViewIndex(int viewIndex) {
        this.viewIndex = viewIndex;
    }

    public int getItemState() {
        return itemState;
    }

    public void setItemState(int itemState) {
        this.itemState = itemState;
    }

    public static boolean contains(AdapterViewItem item, List<AdapterViewItem> list){
        for(AdapterViewItem listItem : list){
            if(listItem.getViewIndex() == item.getViewIndex()){
                return true;
            }
        }
        return false;
    }

    public static void insert(AdapterViewItem item, List<AdapterViewItem> columnItems) {
        for(int i = 0; i < columnItems.size(); i++){
            if(columnItems.get(i).getViewIndex() > item.getViewIndex()){
                columnItems.add(i, item);
                return;
            }
        }
        columnItems.add(item);
    }
}
