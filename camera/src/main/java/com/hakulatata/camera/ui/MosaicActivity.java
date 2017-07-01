package com.hakulatata.camera.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hakulatata.camera.R;
import com.hakulatata.camera.util.FileUtils;
import com.hakulatata.camera.util.ScreenUtils;
import com.hakulatata.camera.view.mosaic.DrawMosaicView;
import com.hakulatata.camera.view.mosaic.MosaicUtil;


public class MosaicActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_sure;
    private DrawMosaicView mosaicView;

    private String mPath;

    private Bitmap srcBitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosaic);
        getExtra();
        initView();
        initEvent();
    }

    private void getExtra() {
        mPath = getIntent().getStringExtra("Image_Path");
    }

    private void initEvent() {
        tv_back.setVisibility(View.VISIBLE);
        tv_sure.setVisibility(View.VISIBLE);
        tv_title.setText("马赛克");
        tv_sure.setText("确定");


        tv_back.setOnClickListener(this);
        tv_sure.setOnClickListener(this);

        mosaicView.setMosaicBackgroundResource(mPath);

        srcBitmap = BitmapFactory.decodeFile(mPath);

        Bitmap bit = MosaicUtil.getMosaic(srcBitmap);

        mosaicView.setMosaicResource(bit);
        mosaicView.setMosaicBrushWidth(20);
    }

    public void initView() {
        ScreenUtils.initScreen(this);

        tv_back = (TextView) findViewById(R.id.tv_header_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_sure = (TextView) findViewById(R.id.tv_header_right);

        mosaicView = (DrawMosaicView) findViewById(R.id.mosaic_view);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id==R.id.tv_header_left){
            finish();

        }else if (id==R.id.tv_header_right){
            Bitmap bit = mosaicView.getMosaicBitmap();

            FileUtils.writeImage(bit, mPath, 100);

            Intent okData = new Intent();
            okData.putExtra("Image_Path", mPath);
            setResult(RESULT_OK, okData);
            recycle();
            this.finish();
        }
    }

    private void recycle() {
        if (srcBitmap != null) {
            srcBitmap.recycle();
            srcBitmap = null;
        }
    }
}
