package com.hakulatata.camera.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hakulatata.camera.R;
import com.hakulatata.camera.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hakulatata on 2017/6/26.
 */

public class GalleryPopupWindow extends PopupWindow {

    private int mWidth, mHeight;
    private View mConvertView;


    private TextView tv_position;
    private ViewPager mViewPager;

    private List<String> mImages;
    private int currentPage;


    private GalleryAdapter mGalleryAdapter;
    private List<View> mViews = new ArrayList<>();
    private Context mContext;
    private View mParentView;

    @SuppressLint("ClickableViewAccessibility")
    public GalleryPopupWindow(Context context, View parentView) {
        this.mContext = context;
        this.mParentView = parentView;

        calWidthAndHeight();

        mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_gallery, null);

        mGalleryAdapter = new GalleryAdapter(mViews);


        setContentView(mConvertView);

        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setTouchInterceptor((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                dismiss();
                return true;
            }
            return false;
        });

        initViews();
        initEvent();
    }

    private void initEvent() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
                tv_position.setText((currentPage + 1) + "/" + mImages.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initViews() {
        tv_position = (TextView) mConvertView.findViewById(R.id.tv_position);
        mViewPager = (ViewPager) mConvertView.findViewById(R.id.vp_image);
    }

    /**
     * 计算PopupWindow的高度和宽度
     */
    private void calWidthAndHeight() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mWidth = outMetrics.widthPixels;
        mHeight = outMetrics.heightPixels;
    }

    public void show(int position) {
        mGalleryAdapter = new GalleryAdapter(mViews);
        mViewPager.setAdapter(mGalleryAdapter);

        this.currentPage = position;
        tv_position.setText((currentPage + 1) + "/" + mImages.size());
        mViewPager.setCurrentItem(position);

        setAnimationStyle(R.style.gallery_popupwindow_anim);
        showAtLocation(mParentView, Gravity.CENTER, 0, 0);
    }
    public void setImages(List<String> images) {
        this.mImages = images;
        updateView();
    }

    private void updateView() {
        List<View> views = new ArrayList<>();
        for (String imagePath : mImages) {
            LogUtil.e("URL : " + imagePath);
            ImageView imageView = new ImageView(mContext);
            Glide.with(mContext).load(imagePath).asBitmap().error(R.drawable.empty_photo).into(imageView);
            imageView.setOnClickListener(v -> dismiss());
            views.add(imageView);
        }
        mViews.clear();
        mViews.addAll(views);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        mViewPager.setAdapter(null);
    }

    private class GalleryAdapter extends PagerAdapter {

        private List<View> mViewList;

        public GalleryAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
