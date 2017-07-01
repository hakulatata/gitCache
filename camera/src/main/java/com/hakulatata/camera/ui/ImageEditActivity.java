package com.hakulatata.camera.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.hakulatata.camera.R;
import com.hakulatata.camera.config.Constants;
import com.hakulatata.camera.util.FileUtils;
import com.hakulatata.camera.util.ScreenUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class ImageEditActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_sure;
    private ImageView img_target;
    private TextView tv_date, tv_time, tv_address;
    private ImageView btn_scrawl, btn_mosaic;
    private RelativeLayout rl_content, rl_center;

    private String tempPath;
    private String imagePath;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        mContext = this;
        getExtra();
        initView();
        initEvent();
    }

    private void getExtra() {
        tempPath = getIntent().getStringExtra("Image_Path");
    }

    private void initEvent() {
        ScreenUtils.initScreen(this);

        tv_back.setVisibility(View.VISIBLE);
        tv_sure.setVisibility(View.VISIBLE);
        tv_title.setText("编辑");
        tv_sure.setText("确定");
        tv_address.setText("大不列颠岛");
        timer.schedule(task, 10, 1000);

        tv_back.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        btn_mosaic.setOnClickListener(this);
        btn_scrawl.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_date.setText(new SimpleDateFormat("yyyy - MM - dd").format(new Date(System.currentTimeMillis())));
        tv_time.setText(new SimpleDateFormat("hh : mm").format(new Date(System.currentTimeMillis())));
    }

    public void initView() {
        rl_center = (RelativeLayout) findViewById(R.id.rl_center);
        rl_content = (RelativeLayout) findViewById(R.id.rl_content);
        tv_back = (TextView) findViewById(R.id.tv_header_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_sure = (TextView) findViewById(R.id.tv_header_right);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_address = (TextView) findViewById(R.id.tv_address);
        img_target = (ImageView) findViewById(R.id.img_target);
        btn_scrawl = (ImageView) findViewById(R.id.btn_scrawl);
        btn_mosaic = (ImageView) findViewById(R.id.btn_mosaic);
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (rl_center.getWidth() != 0) {
                timer.cancel();
                compressed();
            }
        }
    };

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            myHandler.sendEmptyMessage(0);
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();
        if (id==R.id.tv_header_left){
            finish();

        }else if (id==R.id.tv_header_right){
            Bitmap bit = getBitmap(rl_content);

            FileUtils.writeImage(bit, imagePath, 100);

            Intent okData = new Intent();
            okData.putExtra("Image_Path", imagePath);
            setResult(RESULT_OK, okData);
            finish();
        }else if (id==R.id.btn_scrawl){
            intent = new Intent(this, ScrawlActivity.class);
            intent.putExtra("Image_Path", imagePath);
            this.startActivityForResult(intent, Constants.PictureInfo.REQUEST_CODE_EDIT_PHOTO_SCRAWL);
        }else if (id==R.id.btn_mosaic){
            intent = new Intent(this, MosaicActivity.class);
            intent.putExtra("Image_Path", imagePath);
            this.startActivityForResult(intent, Constants.PictureInfo.REQUEST_CODE_EDIT_PHOTO_MOSAIC);
        }
    }

    private Bitmap getBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    private void compressed() {
        Bitmap resizeBmp = ScreenUtils.compressionFiller(tempPath, rl_center);
        if (resizeBmp!=null){
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(resizeBmp.getWidth(), resizeBmp.getHeight());
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            rl_content.setLayoutParams(layoutParams);
            img_target.setImageBitmap(resizeBmp);
            imagePath = SaveBitmap(resizeBmp, "" + System.currentTimeMillis());
            FileUtils.deleteFile(new File(tempPath));
        }
    }

    // 将生成的图片保存到内存中
    public String SaveBitmap(Bitmap bitmap, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(Constants.PictureInfo.BEOK_CAMERA_DIR);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(Constants.PictureInfo.BEOK_CAMERA_DIR + name + ".jpg");
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case Constants.PictureInfo.REQUEST_CODE_EDIT_PHOTO_SCRAWL:
            case Constants.PictureInfo.REQUEST_CODE_EDIT_PHOTO_MOSAIC:
                String resultPath = data.getStringExtra("Image_Path");
                Bitmap resultBitmap = BitmapFactory.decodeFile(resultPath);
                img_target.setImageBitmap(resultBitmap);
                break;
        }
    }
}
