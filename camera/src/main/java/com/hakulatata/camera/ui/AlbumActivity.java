package com.hakulatata.camera.ui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hakulatata.camera.R;
import com.hakulatata.camera.adapter.CommonAdapter;
import com.hakulatata.camera.bean.AlbumBean;
import com.hakulatata.camera.holder.ViewHolder;
import com.hakulatata.camera.util.LogUtil;
import com.hakulatata.camera.util.ToastUtils;
import com.hakulatata.camera.view.AlbumListPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Created by hakulatata on 2017/6/26.
 */
public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_sure;
    private ImageView img_down_arrow;
    private GridView gv_photo;
    private RelativeLayout rl_top;

    private ImageAdapter mImageAdapter;
    private List<AlbumBean> albumList = new ArrayList<>();
    private List<String> photoList = new ArrayList<>();
    private List<String> latelyPhotoList = new ArrayList<>();
    private ArrayList<String> selectList = new ArrayList<>();
    private File mCurrentAlbum;

    private int mMaxCount = 9;
//    private int mCurrCount = 0;

    private ProgressDialog progressDialog;

    private AlbumListPopupWindow mAlbumPopupWindow;

    private static final int DATA_LOADED = 0x111;
    private static final String LATELY_ALBUM_NAME = "最近照片";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DATA_LOADED) {
                progressDialog.dismiss();
                //填充图片到View中
                Image2View();
                initAlbumPopupWindow();
            }
        }
    };
    private Context mContext;

    private void initAlbumPopupWindow() {
        mAlbumPopupWindow = new AlbumListPopupWindow(mContext, albumList);
        mAlbumPopupWindow.setOnDismissListener(() -> lightOn());
        mAlbumPopupWindow.setOnAlbumSelectedListener(albumBean -> {
            LogUtil.e(albumBean.getName());
            if (LATELY_ALBUM_NAME.equals(albumBean.getName())) {
                mImageAdapter = new ImageAdapter(mContext, latelyPhotoList, "", R.layout.adapter_album_photo);
            } else {
                mCurrentAlbum = new File(albumBean.getPath());

                photoList = Arrays.asList(mCurrentAlbum.list((dir, filename) -> {
                    if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))
                        return true;
                    return false;
                }));

                mImageAdapter = new ImageAdapter(mContext, photoList, mCurrentAlbum.getAbsolutePath(), R.layout.adapter_album_photo);
            }

            gv_photo.setAdapter(mImageAdapter);

            tv_title.setText(albumBean.getName());

            mAlbumPopupWindow.dismiss();
        });
    }

    /**
     * 内容区域变亮
     */
    private void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    /**
     * 内容区域变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
    }

    private void Image2View() {
        mImageAdapter = new ImageAdapter(this, latelyPhotoList, "", R.layout.adapter_album_photo);
        gv_photo.setAdapter(mImageAdapter);

        tv_title.setText(LATELY_ALBUM_NAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        mContext = this;
        getExtra();
        initView();
        scanPictures();
        initEvent();
    }

    private void getExtra() {
        mMaxCount = getIntent().getIntExtra("maxCount", 9);
        if (getIntent().hasExtra("selected_list"))
            selectList = getIntent().getStringArrayListExtra("selected_list");
    }

    private void scanPictures() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(mContext, null, "正在加载...");

        new Thread() {
            @Override
            public void run() {

                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = AlbumActivity.this.getContentResolver();

                Cursor cursor = cr.query(mImgUri, new String[]{MediaStore.Images.Media.DATA}, MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpg", "image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

                Set<String> mAlbumPaths = new HashSet<>();

                AlbumBean latelyAlbum = new AlbumBean();
                latelyAlbum.setCount(100);
                latelyAlbum.setPath(File.separator + LATELY_ALBUM_NAME);

                if (cursor != null && cursor.moveToLast()) {
                    do {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        if (latelyPhotoList.size() < 100) {
                            latelyPhotoList.add(path);
                            latelyAlbum.setFirstImgPath(path);
                        }

                        File parentFile = new File(path).getParentFile();

                        if (parentFile == null)
                            continue;

                        String dirPath = parentFile.getAbsolutePath();

                        AlbumBean albumBean = null;

                        if (mAlbumPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mAlbumPaths.add(dirPath);
                            albumBean = new AlbumBean();
                            albumBean.setPath(dirPath);
                            albumBean.setFirstImgPath(path);
                        }

                        if (parentFile.list() == null) {
                            continue;
                        }

                        int picSize = parentFile.list((dir, filename) -> {
                            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))
                                return true;
                            return false;
                        }).length;

                        albumBean.setCount(picSize);

                        albumList.add(albumBean);
                    } while (cursor.moveToPrevious());
                }
                albumList.add(0, latelyAlbum);

                cursor.close();
                //通知handler扫描完成
                mHandler.sendEmptyMessage(DATA_LOADED);
            }
        }.start();
    }

    private void initEvent() {
        tv_back.setVisibility(View.VISIBLE);
        tv_sure.setVisibility(View.VISIBLE);
        img_down_arrow.setVisibility(View.VISIBLE);
        tv_title.setText("所有照片");
        tv_sure.setText("确定(" + selectList.size() + "/" + mMaxCount + ")");

        tv_back.setOnClickListener(this);
        tv_title.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
    }

    public void initView() {
        tv_back = (TextView) findViewById(R.id.tv_header_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_sure = (TextView) findViewById(R.id.tv_header_right);

        img_down_arrow = (ImageView) findViewById(R.id.img_down_arrow);

        gv_photo = (GridView) findViewById(R.id.gv_photo);

        rl_top = (RelativeLayout) findViewById(R.id.rl_top);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id==R.id.tv_header_left){
            finish();
        }else if (id==R.id.tv_title){
            mAlbumPopupWindow.setAnimationStyle(R.style.album_popupwindow_anim);
            mAlbumPopupWindow.showAsDropDown(rl_top, 0, 0);
            lightOff();
        }else if (id==R.id.tv_header_right){
            if (selectList.size() == 0) {
                ToastUtils.show(mContext, "至少选择一张图片！");
                return;
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra("images", selectList);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    private class ImageAdapter extends CommonAdapter<String> {

        private String mDirPath;

        public ImageAdapter(Context mContext, List<String> datas, String dirPath, int layoutId) {
            super(mContext, datas, layoutId);
            this.mDirPath = dirPath;
        }

        @Override
        public void convert(ViewHolder holder, String s) {
            final ImageView image = holder.getView(R.id.image);
            final CheckBox cb_select = holder.getView(R.id.cb_select);

            image.setImageResource(R.drawable.empty_photo);
            image.setColorFilter(null);
            cb_select.setChecked(false);

            final String filePath = mDirPath + "/" + s;
            LogUtil.e(filePath);
            Glide.with(mContext).load(filePath).asBitmap().error(R.drawable.empty_photo).into(image);

            image.setOnClickListener(v -> {

                if (selectList.contains(filePath)) {
                    selectList.remove(filePath);
                    image.setColorFilter(null);
                    cb_select.setChecked(false);
                } else {
                    if (selectList.size() == mMaxCount) {
                        ToastUtils.show(mContext, "最大可选" + mMaxCount + "张");
                        return;
                    }
                    LogUtil.e("选中图片 : " + filePath);
                    selectList.add(filePath);
                    image.setColorFilter(Color.parseColor("#77000000"));
                    cb_select.setChecked(true);
                }
                tv_sure.setText("确定(" + (selectList.size()) + "/" + mMaxCount + ")");
            });

            if (selectList.contains(filePath)) {
                image.setColorFilter(Color.parseColor("#77000000"));
                cb_select.setChecked(true);
            }

        }
    }
}
