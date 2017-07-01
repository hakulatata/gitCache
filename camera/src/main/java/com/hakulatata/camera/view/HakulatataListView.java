package com.hakulatata.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by hakulatata on 2017/6/26.
 */
public class HakulatataListView extends ListView {
    public HakulatataListView(Context context) {
        super(context);
    }

    public HakulatataListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HakulatataListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
