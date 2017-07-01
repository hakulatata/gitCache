package com.hakulatata.camera.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hakulatata.camera.R;
import com.hakulatata.camera.util.FileUtils;
import com.hakulatata.camera.util.ScreenUtils;
import com.hakulatata.camera.view.scrawl.DrawAttribute;
import com.hakulatata.camera.view.scrawl.DrawingBoardView;
import com.hakulatata.camera.view.scrawl.ScrawlTools;

import java.util.Timer;
import java.util.TimerTask;


public class ScrawlActivity extends AppCompatActivity implements View.OnClickListener {

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
        }
    };
    private TextView tv_back, tv_title, tv_sure;
    private DrawingBoardView draw_view;
    private RelativeLayout rl_center;
    private ScrawlTools casualWaterUtil = null;
    private String mPath;
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (rl_center.getWidth() != 0) {
                    timer.cancel();
                    compressed();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrawl);
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
        tv_title.setText("涂鸦");
        tv_sure.setText("确定");

        timer.schedule(task, 10, 1000);

        tv_back.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
    }

    public void initView() {
        ScreenUtils.initScreen(this);

        tv_back = (TextView) findViewById(R.id.tv_header_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_sure = (TextView) findViewById(R.id.tv_header_right);
        rl_center = (RelativeLayout) findViewById(R.id.rl_center);
        draw_view = (DrawingBoardView) findViewById(R.id.draw_view);
    }

    private void compressed() {

        Bitmap resizeBmp = ScreenUtils.compressionFiller(mPath, rl_center);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                resizeBmp.getWidth(), resizeBmp.getHeight());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        draw_view.setLayoutParams(layoutParams);

        casualWaterUtil = new ScrawlTools(this, draw_view, resizeBmp);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap paintBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.marker);

        casualWaterUtil.creatDrawPainter(DrawAttribute.DrawStatus.PEN_WATER,
                paintBitmap, Color.RED);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_header_left) {
            finish();
        } else if (id == R.id.tv_header_right) {
            Bitmap bit = casualWaterUtil.getBitmap();

            FileUtils.writeImage(bit, mPath, 100);

            Intent okData = new Intent();
            okData.putExtra("Image_Path", mPath);
            setResult(RESULT_OK, okData);

            this.finish();
        }

    }
}
