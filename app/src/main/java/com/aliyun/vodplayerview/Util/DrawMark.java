package com.aliyun.vodplayerview.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class DrawMark {
    public Bitmap setBitmap(Bitmap oriBitmap, int status, String id){
        Bitmap newBitmap=Bitmap.createBitmap(oriBitmap.getWidth(), oriBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ColorMatrix hueMatrix = new ColorMatrix();
        ColorMatrix imageMatrix = new ColorMatrix();
        /**0:green,1:yellow,2:red,3:grey*/
        switch (status){
            case Constant.RED:
                break;
            case Constant.YELLOW:
                hueMatrix.setRotate(2,-17);
                imageMatrix.postConcat(hueMatrix);
                break;
            case Constant.GREEN:
                hueMatrix.setRotate(2,-120);
                imageMatrix.postConcat(hueMatrix);
                break;
            default://默认为灰色
                ColorMatrix saturationMatrix = new ColorMatrix();
                saturationMatrix.setSaturation(0);
                imageMatrix.postConcat(saturationMatrix);
                break;
        }
        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
        canvas.drawBitmap(oriBitmap, 0, 0, paint);
        Paint paint1=new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setTextSize(50);
        paint1.setTextAlign(Paint.Align.LEFT);
        switch (id.length()){
            case 3:
                canvas.drawText(id,25,70,paint1);
                break;
            case 2:
                canvas.drawText(id,40,70,paint1);
                break;
            case 1:
                canvas.drawText(id,55,70,paint1);
                break;
            default:
                canvas.drawText(id,25,70,paint1);
                break;
        }
        return newBitmap;
    }
}