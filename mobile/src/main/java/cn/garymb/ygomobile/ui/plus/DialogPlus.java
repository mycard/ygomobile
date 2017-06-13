package cn.garymb.ygomobile.ui.plus;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.garymb.ygomobile.lite.R;

public class DialogPlus {
    private Context context;
    private AlertDialog.Builder mBuilder;
    private LayoutInflater mLayoutInflater;
    private View mView;
    private TextView mTitleView;
    private View closeView;
    private FrameLayout mFrameLayout;
    private Button mLeft;
    private Dialog mDialog;
    private View mCancelView;
    private Button mRight;
    private View mContentView;
    private int mMaxHeight;
    private String mUrl;
    private WebViewPlus mWebView;

    public DialogPlus(Context context) {
        this.context = context;
        mBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog_Translucent);
        mMaxHeight = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.7f);
        mLayoutInflater = LayoutInflater.from(context);
        mView = mLayoutInflater.inflate(R.layout.dialog_plus_base, null);
        mBuilder.setView(mView);
        mTitleView = bind(R.id.title);
        closeView = bind(R.id.close);
        mFrameLayout = bind(R.id.container);
        mLeft = bind(R.id.button_ok);
        mRight = bind(R.id.button_cancel);
        mCancelView = bind(R.id.space_cancel);
        setCloseLinster((dlg, id) -> {
            dlg.dismiss();
        });
    }

    public DialogPlus hideButton() {
        mLeft.setVisibility(View.GONE);
        mRight.setVisibility(View.GONE);
        return this;
    }

    public DialogPlus setCancelable(boolean cancelable) {
        mBuilder.setCancelable(cancelable);
        return this;
    }

    public boolean isShowing() {
        return mDialog != null && mDialog.isShowing();
    }

    public DialogPlus setCloseLinster(DialogInterface.OnClickListener clickListener) {
        closeView.setOnClickListener((v) -> {
            if (clickListener != null) {
                clickListener.onClick(mDialog, DialogInterface.BUTTON_NEGATIVE);
            }else{
                mDialog.dismiss();
            }
        });
        return this;
    }

    public DialogPlus setRightButtonListener(DialogInterface.OnClickListener clickListener) {
        mCancelView.setVisibility(View.VISIBLE);
        mRight.setVisibility(View.VISIBLE);
        mRight.setOnClickListener((v) -> {
            if (clickListener != null) {
                clickListener.onClick(mDialog, DialogInterface.BUTTON_NEUTRAL);
            }else{
                mDialog.dismiss();
            }
        });
        return this;
    }

    public DialogPlus setLeftButtonListener(DialogInterface.OnClickListener clickListener) {
        mLeft.setVisibility(View.VISIBLE);
        mLeft.setOnClickListener((v) -> {
            if (clickListener != null) {
                clickListener.onClick(mDialog, DialogInterface.BUTTON_POSITIVE);
            }
        });
        return this;
    }

    public DialogPlus setOnCancelListener(DialogInterface.OnCancelListener clickListener) {
        mBuilder.setOnCancelListener(clickListener);
        return this;
    }

    public DialogPlus setTitle(int id) {
        return setTitle(context.getString(id));
    }

    public DialogPlus setMessage(int id) {
        return setMessage(context.getString(id));
    }

    public DialogPlus setMessageGravity(int g) {
        TextView textView = bind(R.id.text);
        textView.setGravity(g);
        return this;
    }

    public DialogPlus setMessage(CharSequence text) {
        TextView textView = bind(R.id.text);
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
        return this;
    }

    public DialogPlus setTitle(String text) {
        if (mTitleView != null) {
            mTitleView.setText(text);
        }
        return this;
    }

    public DialogPlus setRightButtonText(int id) {
        return setRightButtonText(context.getString(id));
    }

    public DialogPlus setRightButtonText(String text) {
        mRight.setVisibility(View.VISIBLE);
        mRight.setText(text);
        mCancelView.setVisibility(View.VISIBLE);
        return this;
    }

    public DialogPlus setLeftButtonText(int id) {
        return setLeftButtonText(context.getString(id));
    }

    public DialogPlus setLeftButtonText(String text) {
        mLeft.setText(text);
        return this;
    }

    public DialogPlus setView(int id) {
        View view = mLayoutInflater.inflate(id, null);
        return setContentView(view);
    }

    public View getContentView() {
        return mContentView;
    }

    public DialogPlus setView(View view) {
        setContentView(view);
        return this;
    }

    public DialogPlus setContentView(int id) {
        View view = mLayoutInflater.inflate(id, null);
        return setContentView(view);
    }

    public DialogPlus setContentView(View view) {
        this.mContentView = view;
        mFrameLayout.removeAllViews();
        mFrameLayout.addView(view);
        return this;
    }

    public Dialog show() {
        if (mDialog == null) {
            mDialog = mBuilder.show();
        } else {
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
        if (mWebView != null && !TextUtils.isEmpty(mUrl)) {
            mWebView.loadUrl(mUrl);
        }
        return mDialog;
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private <T extends View> T bind(int id) {
        return (T) mView.findViewById(id);
    }

    public <T extends View> T findViewById(int id) {
        return (T) mContentView.findViewById(id);
    }

    public DialogPlus loadHtml(String html, int bgColor) {
        if (mWebView == null) {
            FrameLayout frameLayout = new FrameLayout(context);
            WebViewPlus webView = new WebViewPlus(context);
            webView.setBackgroundColor(bgColor);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            frameLayout.addView(webView, layoutParams);
            setView(frameLayout);
            setLeftButtonListener((dlg, v) -> {
                dlg.dismiss();
            });
            mWebView = webView;
            webView.loadData(html, "text/html", "UTF-8");
        }
        return this;
    }

    public DialogPlus loadUrl(String url, int bgColor) {
        if (mWebView == null) {
            FrameLayout frameLayout = new FrameLayout(context);
            WebViewPlus webView = new WebViewPlus(context);
            webView.setBackgroundColor(bgColor);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            frameLayout.addView(webView, layoutParams);
            setView(frameLayout);
            setLeftButtonListener((dlg, v) -> {
                dlg.dismiss();
            });
            mWebView = webView;
        }
        mUrl = url;
        return this;
    }
}
