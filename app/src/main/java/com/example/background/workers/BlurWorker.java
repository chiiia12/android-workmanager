package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.background.Constants;

import androidx.work.Data;
import androidx.work.Worker;

public class BlurWorker extends Worker {
    private static final String TAG = BlurWorker.class.getCanonicalName();

    @NonNull
    @Override
    public WorkerResult doWork() {
        Context applicationContext = getApplicationContext();
        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI, null);
        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }
            ContentResolver resolver = applicationContext.getContentResolver();
            Bitmap picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));
            Bitmap output = WorkerUtils.blurBitmap(picture, applicationContext);
            Uri outputUri = WorkerUtils.writeBitmapToFile(applicationContext, output);
            setOutputData(new Data.Builder().putString(Constants.KEY_IMAGE_URI, outputUri.toString()).build());
            WorkerUtils.makeStatusNotification("Output is "
                    + outputUri.toString(), applicationContext);
            return WorkerResult.SUCCESS;
        } catch (Throwable throwable) {
            Log.e(TAG, "Error applying blur", throwable);
            return WorkerResult.FAILURE;
        }
    }
}
