package cn.garymb.ygomobile.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.utils.IOUtils;

/**
 * Created by keyongyu on 2017/1/31.
 */

public class ImageUpdater implements DialogInterface.OnCancelListener {
    private Context mContext;
    private CardLoader mCardLoader;
    private final static int SubThreads = 4;
    private int mDownloading = 0;
    private final List<Long> mCardStatus = new ArrayList<>();
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private OkHttpClient mOkHttpClient;
    private File mPicsPath;
    private ProgressDialog mDialog;
    private int mIndex;
    private int mCount;
    private int mCompleted;
    private boolean isRun = false;
    private boolean mStop = false;

    public ImageUpdater(Context context) {
        mContext = context;
        mCardLoader = new CardLoader(context);
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        mPicsPath = new File(AppsSettings.get().getResourcePath(), Constants.CORE_IMAGE_PATH);
    }

    public boolean isRunning() {
        if (isRun) return true;
        synchronized (mCardStatus) {
            if (mDownloading > 0) {
                return true;
            }
        }
        return false;
    }

    public void close() {
        mExecutorService.shutdown();
    }

    public void start() {
        if (isRunning()) return;
        isRun = true;
        synchronized (mCardStatus) {
            if (mCardStatus.size() == 0) {
                loadCardsLocked();
            }
        }
        mCompleted = 0;
        mIndex = 0;
        mDownloading = 0;
        mStop = false;
        mPicsPath = new File(AppsSettings.get().getResourcePath(), Constants.CORE_IMAGE_PATH);
        if (mDialog != null) {
            mDialog.show();
        } else {
            mDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.download_image_progress, mCompleted, mCount), true, true);
            mDialog.setOnCancelListener(this);
        }
        IOUtils.createNoMedia(mPicsPath.getAbsolutePath());
//        Log.i("kk", "download " + mCompleted + "/" + mCount);
        for (int i = 0; i < SubThreads; i++) {
            long id = nextCard();
            if (id > 0) {
                if (!submit(id)) {
                    i--;
                }
            }
        }
        synchronized (mCardStatus) {
            if (mDownloading <= 0) {
                onEnd();
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        synchronized (mCardStatus) {
            mStop = true;
        }
    }

    private boolean submit(long id) {
        if (id > 0) {
//            Log.i("kk", "submit " + id);
            if (!mExecutorService.isShutdown()) {
                synchronized (mCardStatus) {
                    mDownloading++;
                }
                String url = String.format(Constants.IMAGE_URL, id + "");
                File file = new File(mPicsPath, id + Constants.IMAGE_URL_EX);
                mExecutorService.submit(new DownloadTask(url, file));
                return true;
            }
        }
        return false;
    }

    private class DownloadTask implements Runnable {
        String url;
        File file;
        File tmpFile;

        private DownloadTask(String url, File file) {
            this.url = url;
            this.file = file;
            this.tmpFile = new File(file.getAbsolutePath() + ".tmp");
        }

        @Override
        public void run() {
            if (file.exists()) {

            } else {
                if (download(url, tmpFile) && tmpFile.exists()) {
                    tmpFile.renameTo(file);
                }
            }
            synchronized (mCardStatus) {
                mDownloading--;
                mCompleted++;
                if (mDialog != null) {
                    VUiKit.post(() -> {
                        mDialog.setMessage(mContext.getString(R.string.download_image_progress, mCompleted, mCount));
                    });
                }
            }
            boolean needNext;
            synchronized (mCardStatus) {
                needNext = !mStop;
            }
            if (needNext) {
                long id = nextCard();
                if (id > 0) {
                    submit(id);
                } else {
                    //当前没任务
                    synchronized (mCardStatus) {
                        if (mDownloading <= 0) {
                            onEnd();
                        }
                    }
                }
            }
        }
    }

    private boolean download(String url, File file) {
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        Request request = new Request.Builder().url(url).build();
        boolean ok = false;
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                inputStream = response.body().byteStream();
                outputStream = new FileOutputStream(file);
                byte[] tmp = new byte[4096];
                int len;
                while ((len = inputStream.read(tmp)) != -1) {
                    outputStream.write(tmp, 0, len);
                }
                ok = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStream);
            IOUtils.close(outputStream);
        }
        return ok;
    }

    private long nextCard() {
        synchronized (mCardStatus) {
//            Log.i("kk", "submit " + mIndex);
            if (mIndex >= mCount) {
                return -1;
            }
            mIndex++;
            return mCardStatus.get(mIndex);
        }
    }

    private void onEnd() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        isRun = false;
    }

    private void loadCardsLocked() {
        if (!mCardLoader.isOpen()) {
            mCardLoader.openDb();
        }
        List<Long> ids = mCardLoader.readAllCardCodes();
        mCardStatus.clear();
        mCardStatus.addAll(ids);
        mCount = ids.size();
    }
}
