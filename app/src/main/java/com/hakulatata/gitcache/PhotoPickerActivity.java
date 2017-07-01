package com.hakulatata.gitcache;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.hakulatata.camera.R;
import com.hakulatata.camera.util.LogUtil;
import com.hakulatata.camera.view.CamaraMananger;

import java.util.List;


/**
 * Created by hakulatata on 2017/6/26.
 */
public class PhotoPickerActivity extends AppCompatActivity {

    private FrameLayout camaraContent;
    private CamaraMananger camaraMananger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takephoto);
        camaraContent = (FrameLayout) findViewById(R.id.fl_camara_cotent);
        initdata();
        initListener();
    }

    private void initListener() {

        camaraMananger.setOnCamaraSuccessListener(new CamaraMananger.OnCamaraSuccessListener() {
            @Override
            public void onSuccessListener(List<String> imgPathList) {
                for (String s : imgPathList) {
                    LogUtil.d(s);
                }
            }
        });
    }

    private void initdata() {
        camaraMananger = new CamaraMananger(this);
        camaraContent.addView(camaraMananger.camaraView);
        camaraMananger.setImagCount(6);
    }

    /**
     * 手机适配问题，activity
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        camaraMananger.onSaveInstanceState(outState);
    }

    /**
     * 手机适配问题，activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        camaraMananger.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        camaraMananger.onActivityResult(requestCode, resultCode, data);
    }
}
