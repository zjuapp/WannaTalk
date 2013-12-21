package com.wannatalk.android.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.WindowManager;

public class CompressImage {
	private Bitmap bm;
    private String filePath;
    private Activity context;
    private int width;
    private int height;
    public CompressImage(Activity context,String filePath, int width, int height) {
        this.filePath = filePath;
        this.context = context;
    }
    public Bitmap getBitmap() throws Exception{
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        bm = BitmapFactory.decodeFile(filePath, opt);
        
        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;
        
        // 获取屏的宽度和高度
        /*
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        */
        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > width)
                opt.inSampleSize = picWidth / width;
        } else {
            if (picHeight > height)

                opt.inSampleSize = picHeight / height;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(filePath, opt);

        return compressImage(bm,50);

    }

    private Bitmap compressImage(Bitmap image,int size) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length/1024 > size) { // 循环判断如果压缩后图片是否大于50kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩比options=50，把压缩后的数据存放到baos中              
                options -= 10;// 每次都减少10	
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(
                    baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } 
    }

}
