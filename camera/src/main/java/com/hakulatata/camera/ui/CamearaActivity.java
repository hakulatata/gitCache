package com.hakulatata.camera.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;


import com.hakulatata.camera.R;
import com.hakulatata.camera.adapter.CamaraPhotoAdapter;
import com.hakulatata.camera.util.LogUtil;
import com.hakulatata.camera.view.GalleryPopupWindow;
import com.hakulatata.camera.view.ImageDialogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hakulatata on 2017/6/26.
 */

public class CamearaActivity extends AppCompatActivity {

    public ImageDialogHelper mImageSelectorDialog;
    public List<String> mImagePathList;
    private View mRootView;
    private GalleryPopupWindow mGalleryPopupWindow;
    private GridView gridPhoto;
    private Button tvImagAdd;
    private CamaraPhotoAdapter camaraPhotoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameara);
        initFindView();
        initImageSelector();
        initCamera();
        initListener();
    }

    private void initListener() {
        tvImagAdd.setOnClickListener(v -> {
            mImageSelectorDialog.show();
        });

        gridPhoto.setOnItemClickListener((parent, view, position, id) -> {
                    if (position == camaraPhotoAdapter.mImagePaths.size()) {
                        mImageSelectorDialog.show();
                    } else {
                        mGalleryPopupWindow.show(position);
                    }

                }
        );

        camaraPhotoAdapter.setOnImageDeletedListener(position -> {
            LogUtil.d("position"+position);
            LogUtil.d("camaraPhotoAdapter.mImagePaths.size()"+camaraPhotoAdapter.mImagePaths.size());
            if (position!=camaraPhotoAdapter.mImagePaths.size()){
                mImageSelectorDialog.removeSelectedImage(position);
                mImagePathList.remove(position);
                camaraPhotoAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initFindView() {
        mRootView = ((ViewGroup) (((Activity) this).findViewById(android.R.id.content))).getChildAt(0);
        tvImagAdd = (Button) findViewById(R.id.tv_imgadd);
        gridPhoto = (GridView) findViewById(R.id.grid_photo);
    }

    private void initCamera() {
        mImagePathList = new ArrayList<>();
        mGalleryPopupWindow = new GalleryPopupWindow(this, mRootView);
        camaraPhotoAdapter = new CamaraPhotoAdapter(this, mImagePathList);
        camaraPhotoAdapter.setGridView(gridPhoto);
        camaraPhotoAdapter.setGalleryPopupWindow(mGalleryPopupWindow);
        gridPhoto.setAdapter(camaraPhotoAdapter);
    }

    //适配各种癫狂的手机
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mImageSelectorDialog.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageSelectorDialog.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 初始化 照相返回
     */
    private void initImageSelector() {
        mImageSelectorDialog = new ImageDialogHelper((Activity) this);
        mImageSelectorDialog.setMaxCount(4);
        mImageSelectorDialog.setOnImageSelectedListener(new ImageDialogHelper.OnImageSelectedListener() {
            @Override
            public void OnImageSelected(List<String> imagePaths) {
                mImagePathList.clear();
                mImagePathList.addAll(imagePaths);
                camaraPhotoAdapter.setData(mImagePathList);
                camaraPhotoAdapter.notifyDataSetChanged();
                gridPhoto.setVisibility(mImagePathList.size() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void OnImageCaptured(String imagePath) {
                LogUtil.d("imagePath=" + imagePath);
                mImagePathList.add(imagePath);
                camaraPhotoAdapter.setData(mImagePathList);
                camaraPhotoAdapter.notifyDataSetChanged();
                gridPhoto.setVisibility(mImagePathList.size() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageSelectorDialog.onImageResult(requestCode, resultCode, data);
    }
}
