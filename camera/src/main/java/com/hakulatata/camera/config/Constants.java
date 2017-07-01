package com.hakulatata.camera.config;

import android.os.Environment;

/**
 * Created by hakulatata on 2017/6/25.
 */

public class Constants {
    public static final class PictureInfo {

        /***添加图片***/
        public static final int REQUEST_CODE_CAMERA = 0x101;
        public static final int REQUEST_CODE_ALBUM = 0x102;
        public static final int REQUEST_CODE_EDIT_PHOTO = 0x103;
        public static final int REQUEST_CODE_EDIT_PHOTO_SCRAWL = 0x104;
        public static final int REQUEST_CODE_EDIT_PHOTO_MOSAIC = 0x105;

        public static final String BEOK_CAMERA_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        public static final String BEOK_CAMERA_TEMP_FILE = "temp.jpg";
    }

    // TODO: 2017/6/25 ?  "beok/property/image1/" 地址为什么不可以更改？为什么。。。。。文件创建decodefile 失败。。你大爷
    public static final class PackageInfo {
        public static final String IMG_PACKAGE_NAME = "beok/property/image1/";
        public static final String PHOTO_PACKAGE_NAME = "pmjoy/photo/";
    }
}
