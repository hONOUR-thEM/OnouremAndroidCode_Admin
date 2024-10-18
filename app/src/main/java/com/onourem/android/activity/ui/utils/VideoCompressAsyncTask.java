package com.onourem.android.activity.ui.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.onourem.android.activity.R;

import java.lang.ref.WeakReference;

public class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

    @SuppressLint("StaticFieldLeak")
    Context mContext;

    // ***** Hold weak reference *****
    private WeakReference<VideoCompressionTaskInformer> mCallBack;
    private ProgressDialog mProgressDialog;

    public VideoCompressAsyncTask(Context context, VideoCompressionTaskInformer callback) {
        this.mCallBack = new WeakReference<>(callback);
        mContext = context;
    }

    public VideoCompressAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Create ProgressBar
        mProgressDialog = new ProgressDialog(mContext);
        // Set your ProgressBar Title
        mProgressDialog.setTitle("Onourem");
        mProgressDialog.setIcon(R.drawable.ic_logo);
        // Set your ProgressBar Message
        mProgressDialog.setMessage("Compressing Video, Please Wait!");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // Show ProgressBar
        mProgressDialog.setCancelable(false);
        //  mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

    }

    @Override
    protected String doInBackground(String... paths) {
        String filePath = null;
        try {

            //This bellow is just a temporary solution to test that method call works
            boolean b = Boolean.parseBoolean(paths[0]);
            if (b) {
                // filePath = SiliCompressor.with(mContext).compressVideo(paths[1], paths[2]);
            } else {
                Uri videoContentUri = Uri.parse(paths[1]);
                // Example using the bitrate and video size parameters
                    /*filePath = SiliCompressor.with(mContext).compressVideo(
                            videoContentUri,
                            paths[2],
                            1280,
                            720,
                            1500000);*/
//                filePath = SiliCompressor.with(mContext).compressVideo(
//                        videoContentUri,
//                        paths[2]);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;

    }


    @Override
    protected void onPostExecute(String compressedFilePath) {
        super.onPostExecute(compressedFilePath);

        // Here you can't guarantee that Activity/Fragment is alive who started this AsyncTask

        // Make sure your caller is active

        final VideoCompressionTaskInformer callBack = mCallBack.get();

        if (callBack != null) {
            callBack.onVideoCompressionDone(compressedFilePath);
        }

        mProgressDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mProgressDialog.setProgress(Integer.parseInt(values[0]));
    }
}
