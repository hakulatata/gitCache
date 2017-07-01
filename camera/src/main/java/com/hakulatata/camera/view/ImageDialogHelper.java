package com.hakulatata.camera.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;


import com.hakulatata.camera.ui.AlbumActivity;
import com.hakulatata.camera.ui.ImageEditActivity;
import com.hakulatata.camera.util.BitmapUtil;
import com.hakulatata.camera.util.LogUtil;
import com.hakulatata.camera.util.ScreenUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.hakulatata.camera.config.Constants.PictureInfo.REQUEST_CODE_ALBUM;
import static com.hakulatata.camera.config.Constants.PictureInfo.REQUEST_CODE_CAMERA;
import static com.hakulatata.camera.config.Constants.PictureInfo.REQUEST_CODE_EDIT_PHOTO;

/**
 * Created by hakulatata on 2017/6/26.
 */

public class ImageDialogHelper {

    private IosBottomDialog mDialog;
    private Activity mActivity;

    private int maxCount = 9;
    private ArrayList<String> mSelectedList = new ArrayList<>();

    private OnImageSelectedListener mListener;
    private String cameraPath;

    public ImageDialogHelper(Activity activity) {
        ScreenUtils.initScreen(activity);
        this.mActivity = activity;
        this.mDialog = new IosBottomDialog.Builder(mActivity)
                .setTitle("添加图片", Color.rgb(143, 143, 143))
                .addOption("打开照相机", Color.rgb(0, 122, 255), () -> {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri outputMediaFileUri = getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputMediaFileUri);
                    mActivity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
                })
                .addOption("打开相册", Color.rgb(0, 122, 255), () -> {
                    Intent intent = new Intent(mActivity, AlbumActivity.class);
                    intent.putExtra("maxCount", maxCount);
                    intent.putExtra("selected_list", mSelectedList);
                    mActivity.startActivityForResult(intent, REQUEST_CODE_ALBUM);
                }).create();
    }

    //用于拍照时获取输出的Uri
    @SuppressLint("SimpleDateFormat")
    protected Uri getOutputMediaFileUri() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Property");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png");
        cameraPath = mediaFile.getAbsolutePath();
        return Uri.fromFile(mediaFile);
    }

    /**
     * 手机适配
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("stateParam", cameraPath);
    }

    public void onRestoreInstanceState(Bundle outState) {
        cameraPath = outState.getString("stateParam");

    }

    /**
     * 设置选择图片的最大值
     *
     * @param maxCount
     */
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * 设置当前已选列表
     *
     * @param selectedList
     */
    public void setSelectedList(ArrayList<String> selectedList) {
        this.mSelectedList = selectedList;
    }

    // TODO: 2017/5/19
    public void removeSelectedImage(int position) {
        mSelectedList.remove(position);
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.mListener = listener;
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public void onImageResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            /**创建文件夹**/
            File storagePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    Intent intent = new Intent(mActivity, ImageEditActivity.class);
                    intent.putExtra("Image_Path", cameraPath);
                    mActivity.startActivityForResult(intent, REQUEST_CODE_EDIT_PHOTO);
                    break;

                case REQUEST_CODE_ALBUM:
                    List<String> list = data.getStringArrayListExtra("images");
                    List<String> image_list = new ArrayList<>();
                    ArrayList<String> path_list = new ArrayList<>();

                    for (String path : list) {
                        String fileName = storagePublicDirectory.getAbsolutePath() + "/" + path.substring(path.lastIndexOf("/") + 1);
                        String imagePath = BitmapUtil.compressNativeImage(path, ScreenUtils.getScreenH(), ScreenUtils.getScreenW(), fileName, true, 80);
                        LogUtil.e("文件路径 :" + imagePath);
                        LogUtil.e("图片路径 :" + path);
                        image_list.add(imagePath);
                        path_list.add(path);
                    }

                    mSelectedList.clear();
                    mSelectedList.addAll(path_list);
                    if (mListener != null) mListener.OnImageSelected(image_list);
                    break;

                case REQUEST_CODE_EDIT_PHOTO:
                    String path = data.getStringExtra("Image_Path");
                    String fileName = storagePublicDirectory.getAbsolutePath() + "/" + path.substring(path.lastIndexOf("/") + 1);
                    String imagePath = BitmapUtil.compressNativeImage(path, ScreenUtils.getScreenH(), ScreenUtils.getScreenW(), fileName, true, 80);
                    mSelectedList.add(path);
                    if (mListener != null) mListener.OnImageCaptured(imagePath);
                    break;
            }
        }
    }

    public interface OnImageSelectedListener {
        void OnImageSelected(List<String> imagePaths);

        void OnImageCaptured(String imagePath);
    }
}
