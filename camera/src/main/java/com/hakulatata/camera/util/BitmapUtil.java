package com.hakulatata.camera.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import net.bither.util.NativeUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by hakulatata on 2017/6/26.
 */


public class BitmapUtil {

    /*
	 * 质量压缩图片
	 */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        Log.e("fadasafas","========"+baos.toByteArray().length / 1024);
        while ( baos.toByteArray().length / 1024>100) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /*
	 * 图片按比例大小压缩方法（根据路径获取图片并压缩）
	 *
	 */
    public static String compressNativeImage(String srcPath,float hh,float ww,String fileName, boolean optimize, int quality) {
        File file = new File(srcPath);
        if(file.length()/1024<300){
            return srcPath;
        }
//		Log.e("beok",FileUtils.getFileSize(new File(srcPath))+"-filesize--");
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        NativeUtil.compressBitmap(bitmap, quality, fileName, optimize);
        File f = new File(fileName);
        while (f.length()/1024>300){
            Log.e("beok","文件长度"+(f.length()/1024)+"KB");
            Bitmap btp = BitmapFactory.decodeFile(fileName);
            NativeUtil.compressBitmap(btp, quality, fileName, optimize);
        }
        return fileName;
    }
}
