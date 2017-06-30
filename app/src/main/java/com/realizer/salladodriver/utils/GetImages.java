package com.realizer.salladodriver.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by Bhagyashri on 9/12/2016.
 */
public class GetImages extends AsyncTask<Object, Object, Object> {
    private String requestUrl, imagename_;
    private ImageView view;
    private Bitmap bitmap ;
    private FileOutputStream fos;
    public GetImages(String requestUrl, ImageView view, String _imagename_) {
        this.requestUrl = requestUrl;
        this.view = view;
        this.imagename_ = _imagename_ ;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (Exception ex) {
            Log.d("log",ex.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        if(bitmap != null) {
            if (!ImageStorage.checkifImageExists(imagename_)) {
                view.setImageBitmap(bitmap);
                ImageStorage.saveToSdCard(bitmap, imagename_);
            }
        }
        else
        {
           // view.setImageResource(R.drawable.user_icon);
        }
    }
}

