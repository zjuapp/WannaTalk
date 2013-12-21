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
        // ���isjustdecodebounds����Ҫ
        opt.inJustDecodeBounds = true;
        bm = BitmapFactory.decodeFile(filePath, opt);
        
        // ��ȡ�����ͼƬ��ԭʼ��Ⱥ͸߶�
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;
        
        // ��ȡ���Ŀ�Ⱥ͸߶�
        /*
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        */
        // isSampleSize�Ǳ�ʾ��ͼƬ�����ų̶ȣ�����ֵΪ2ͼƬ�Ŀ�Ⱥ͸߶ȶ���Ϊ��ǰ��1/2
        opt.inSampleSize = 1;
        // �������Ĵ�С��ͼƬ��С��������ű���
        if (picWidth > picHeight) {
            if (picWidth > width)
                opt.inSampleSize = picWidth / width;
        } else {
            if (picHeight > height)

                opt.inSampleSize = picHeight / height;
        }
        // ���������������һ�������صģ����������˵�bitmap
        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(filePath, opt);

        return compressImage(bm,50);

    }

    private Bitmap compressImage(Bitmap image,int size) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
            int options = 100;
            while (baos.toByteArray().length/1024 > size) { // ѭ���ж����ѹ����ͼƬ�Ƿ����50kb,���ڼ���ѹ��
                baos.reset();// ����baos�����baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ����options=50����ѹ��������ݴ�ŵ�baos��              
                options -= 10;// ÿ�ζ�����10	
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(
                    baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } 
    }

}
