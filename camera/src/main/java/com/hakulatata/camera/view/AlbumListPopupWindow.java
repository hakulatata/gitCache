package com.hakulatata.camera.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hakulatata.camera.R;
import com.hakulatata.camera.adapter.CommonAdapter;
import com.hakulatata.camera.bean.AlbumBean;
import com.hakulatata.camera.holder.ViewHolder;

import java.util.List;


/**
 * Created by Silver on 2017/4/6.
 */

public class AlbumListPopupWindow extends PopupWindow {

    private int mWidth, mHeight;
    private View mConvertView;
    private ListView mListView;
    private List<AlbumBean> mDatas;
    private AlbumAdapter mAlbumAdapter;

    public interface OnAlbumSelectedListener {

        void OnSelected(AlbumBean folderBean);

    }

    public OnAlbumSelectedListener mListener;

    public void setOnAlbumSelectedListener(OnAlbumSelectedListener listener) {
        this.mListener = listener;
    }

    public AlbumListPopupWindow(Context context, List<AlbumBean> datas) {
        calWidthAndHeight(context);

        mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_album, null);

        mAlbumAdapter = new AlbumAdapter(context, datas, R.layout.adapter_album_list);

        this.mDatas = datas;

        setContentView(mConvertView);

        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initViews();
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null)
                    mListener.OnSelected(mDatas.get(position));
            }
        });
    }

    private void initViews() {
        mListView = (ListView) mConvertView.findViewById(R.id.lv_album);
        mListView.setAdapter(mAlbumAdapter);
    }

    /**
     * 计算PopupWindow的高度和宽度
     *
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mWidth = outMetrics.widthPixels;
        mHeight = (int) (outMetrics.heightPixels * 0.8);
//        mHeight = outMetrics.heightPixels;
    }

    private class AlbumAdapter extends CommonAdapter<AlbumBean> {

        public AlbumAdapter(Context mContext, List<AlbumBean> datas, int layoutId) {
            super(mContext, datas, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, AlbumBean albumBean) {
            ImageView img_preview = holder.getView(R.id.img_preview);
            TextView tv_album_name = holder.getView(R.id.tv_album_name);
            TextView tv_album_count = holder.getView(R.id.tv_album_count);

            Glide.with(mContext).load(albumBean.getFirstImgPath()).asBitmap().into(img_preview);

            tv_album_name.setText(albumBean.getName());
            tv_album_count.setText(albumBean.getCount() + "张");
        }
    }
}
