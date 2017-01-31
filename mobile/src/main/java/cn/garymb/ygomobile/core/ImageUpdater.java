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
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.ygo.ocgcore.enums.CardType;

/**
 * Created by keyongyu on 2017/1/31.
 */

public class ImageUpdater implements DialogInterface.OnCancelListener {
    private Context mContext;
    private CardLoader mCardLoader;
    private final static int SubThreads = 4;
    private int mDownloading = 0;
    private final List<Item> mCardStatus = new ArrayList<>();
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(SubThreads);
    private OkHttpClient mOkHttpClient;
    private ProgressDialog mDialog;
    private int mIndex;
    private int mCount;
    private int mCompleted;
    private boolean isRun = false;
    private boolean mStop = false;
    private ZipFile mZipFile;

    public ImageUpdater(Context context) {
        mContext = context;
        mCardLoader = new CardLoader(context);
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
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
        File zip = new File(AppsSettings.get().getResourcePath(), Constants.CORE_PICS_ZIP);
        if (mDialog != null) {
            mDialog.show();
        } else {
            mDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.download_image_progress, mCompleted, mCount), true, true);
            mDialog.setOnCancelListener(this);
        }
        if (mZipFile == null) {
            if (zip.exists()) {
                try {
                    mZipFile = new ZipFile(zip);
                } catch (IOException e) {
                }
            }
        }
//        Log.i("kk", "download " + mCompleted + "/" + mCount);
        for (int i = 0; i < SubThreads; i++) {
            Item item = nextCard();
            if (item != null) {
                if (!submit(item)) {
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

    private boolean submit(Item item) {
        if (item != null) {
//            Log.i("kk", "submit " + id);
            if (!mExecutorService.isShutdown()) {
                synchronized (mCardStatus) {
                    mDownloading++;
                }
                mExecutorService.submit(new DownloadTask(item));
                return true;
            }
        }
        return false;
    }

    private class DownloadTask implements Runnable {
        Item item;
        File tmpFile;

        private DownloadTask(Item item) {
            this.item = item;
            this.tmpFile = new File(item.file + ".tmp");
        }

        private boolean existImage() {
            File img = new File(item.file);
            if (item.isField) {
                return img.exists();
            }
            String name = Constants.CORE_IMAGE_PATH + "/" + item.code;
            for (String ex : Constants.IMAGE_EX) {
                File file = new File(img.getParentFile(), item.code + ex);
                if (file.exists()) {
                    return true;
                }
            }
            if (mZipFile != null) {
                ZipEntry entry = null;
                for (String ex : Constants.IMAGE_EX) {
                    entry = mZipFile.getEntry(name + ex);
                    if (entry != null) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void run() {
            boolean needNext;
            synchronized (mCardStatus) {
                needNext = !mStop;
            }
            if (needNext) {
                if (existImage()) {
                } else {
                    if (download(item.url, tmpFile) && tmpFile.exists()) {
                        File file = new File(item.file);
                        if (!file.exists()) {
                            tmpFile.renameTo(file);
                        }
                    }
                }
                synchronized (mCardStatus) {
                    mDownloading--;
                    mCompleted++;
                    if (mDialog != null) {
                        VUiKit.post(() -> {
//                            Log.d("kk", mCompleted+"/"+mCount);
                            mDialog.setMessage(mContext.getString(R.string.download_image_progress, mCompleted, mCount));
                        });
                    }
                }
                synchronized (mCardStatus) {
                    needNext = !mStop;
                }
            }
            if (needNext) {
                Item item = nextCard();
                if (item != null) {
                    submit(item);
                } else {
                    //当前没任务
                    synchronized (mCardStatus) {
                        if (mDownloading <= 0) {
                            onEnd();
                        }
                    }
                }
            } else {
                synchronized (mCardStatus) {
                    if (mDownloading <= 0) {
                        onEnd();
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
                byte[] tmp = new byte[8192];
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

    private Item nextCard() {
        synchronized (mCardStatus) {
//            Log.i("kk", "submit " + mIndex);
            if (mIndex >= mCount) {
                return null;
            }
            mIndex++;
            return mCardStatus.get(mIndex);
        }
    }

    private void onEnd() {
        synchronized (mCardStatus) {
            mCardStatus.clear();
        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
        isRun = false;
        if (mZipFile != null) {
            try {
                mZipFile.close();
            } catch (IOException e) {
            }
        }
    }

    private void loadCardsLocked() {
        if (!mCardLoader.isOpen()) {
            mCardLoader.openDb();
        }
        Map<Long, Long> cards = mCardLoader.readAllCardCodes();
        mCardStatus.clear();
        File picsPath = new File(AppsSettings.get().getResourcePath(), Constants.CORE_IMAGE_PATH);
        File fieldPath = new File(AppsSettings.get().getResourcePath(), Constants.CORE_IMAGE_FIELD_PATH);
        IOUtils.createNoMedia(picsPath.getAbsolutePath());
        IOUtils.createNoMedia(fieldPath.getAbsolutePath());
        for (Map.Entry<Long, Long> e : cards.entrySet()) {
            if (CardInfo.isType(e.getValue(), CardType.Field)) {
                String png = new File(fieldPath, e.getKey() + Constants.IMAGE_FIELD_URL_EX).getAbsolutePath();
                String pngUrl = String.format(Constants.IMAGE_FIELD_URL, e.getKey() + "");
                mCardStatus.add(new Item(pngUrl, png, e.getKey(), true));
            }
            String jpg = new File(picsPath, e.getKey() + Constants.IMAGE_URL_EX).getAbsolutePath();
            String jpgUrl = String.format(Constants.IMAGE_URL, e.getKey() + "");
            mCardStatus.add(new Item(jpgUrl, jpg, e.getKey(), false));
        }
        mCount = mCardStatus.size();
    }

    private static class Item {
        String url;
        String file;
        long code;
        boolean isField;

        public Item(String url, String file, long code, boolean isField) {
            this.url = url;
            this.file = file;
            this.code = code;
            this.isField = isField;
        }
    }
}
