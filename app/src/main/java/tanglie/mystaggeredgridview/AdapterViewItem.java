package tanglie.mystaggeredgridview;

import android.view.View;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
public class AdapterViewItem {

    private View view;
    private int viewIndex;

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
}
