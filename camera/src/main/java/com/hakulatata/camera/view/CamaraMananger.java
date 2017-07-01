package com.hakulatata.camera.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


import com.hakulatata.camera.R;
import com.hakulatata.camera.adapter.CamaraPhotoAdapter;
import com.hakulatata.camera.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hakulatata on 2017/6/27.
 */

public class CamaraMananger {
    public ImageDialogHelper mImageSelectorDialog;
    public List<String> mImagePathList;
    public View camaraView;
    private Context mContext;
    private HakulatataGridView gridViewPhoto;
    private View mRootView;
    private GalleryPopupWindow mGalleryPopupWindow;
    private CamaraPhotoAdapter camaraPhotoAdapter;
    private OnCamaraSuccessListener onCamaraSuccessListener;

    public CamaraMananger(Context context) {
        this.mContext = context;
        camaraView = initView();
        initData();
    }

    private void initData() {
        initImageSelector();
        initCamara();
        initListener();
    }

    private View initView() {
        View camaraView = View.inflate(mContext, R.layout.hakulatata_camara, null);
        mRootView = ((ViewGroup) (((Activity) mContext).findViewById(android.R.id.content))).getChildAt(0);
        gridViewPhoto = (HakulatataGridView) camaraView.findViewById(R.id.gridview_photo);
        return camaraView;
    }

    private void initCamara() {
        mImagePathList = new ArrayList<>();
        mGalleryPopupWindow = new GalleryPopupWindow(mContext, mRootView);
        camaraPhotoAdapter = new CamaraPhotoAdapter(mContext, mImagePathList);
        camaraPhotoAdapter.setGridView(gridViewPhoto);
        camaraPhotoAdapter.setGalleryPopupWindow(mGalleryPopupWindow);
        gridViewPhoto.setAdapter(camaraPhotoAdapter);
    }

    private void initListener() {
        gridViewPhoto.setOnItemClickListener((parent, view, position, id) -> {
                    if (position == camaraPhotoAdapter.mImagePaths.size()) {
                        mImageSelectorDialog.show();
                    } else {
                        mGalleryPopupWindow.show(position);
                    }

                }
        );

        camaraPhotoAdapter.setOnImageDeletedListener(position -> {
            if (position != camaraPhotoAdapter.mImagePaths.size()) {
                mImageSelectorDialog.removeSelectedImage(position);
                mImagePathList.remove(position);
                camaraPhotoAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 初始化 照相返回
     */
    private void initImageSelector() {
        mImageSelectorDialog = new ImageDialogHelper((Activity) mContext);
        mImageSelectorDialog.setOnImageSelectedListener(new ImageDialogHelper.OnImageSelectedListener() {
            @Override
            public void OnImageSelected(List<String> imagePaths) {
                mImagePathList.clear();
                mImagePathList.addAll(imagePaths);
                camaraPhotoAdapter.setData(mImagePathList);
                camaraPhotoAdapter.notifyDataSetChanged();
                if (onCamaraSuccessListener!=null){
                    onCamaraSuccessListener.onSuccessListener(mImagePathList);
                }
            }

            @Override
            public void OnImageCaptured(String imagePath) {
                LogUtil.d("imagePath=" + imagePath);
                mImagePathList.add(imagePath);
                camaraPhotoAdapter.setData(mImagePathList);
                camaraPhotoAdapter.notifyDataSetChanged();
                if (onCamaraSuccessListener!=null){
                    onCamaraSuccessListener.onSuccessListener(mImagePathList);
                }
            }
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        mImageSelectorDialog.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mImageSelectorDialog.onRestoreInstanceState(savedInstanceState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageSelectorDialog.onImageResult(requestCode, resultCode, data);
    }

    /**
     * 设置相机数量
     *
     * @param imagCount
     */
    public void setImagCount(int imagCount) {
        mImageSelectorDialog.setMaxCount(imagCount);

    }

    public void setOnCamaraSuccessListener(OnCamaraSuccessListener listener) {
        onCamaraSuccessListener = listener;

    }

    public interface OnCamaraSuccessListener {
        void onSuccessListener(List<String> imgPathList);
    }
}
