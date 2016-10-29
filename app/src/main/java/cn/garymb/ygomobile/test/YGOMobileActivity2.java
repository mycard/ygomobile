/*
 * YGOMobileActivity.java
 *
 *  Created on: 2014年2月24日
 *      Author: mabin
 */
package cn.garymb.ygomobile.test;

import android.app.NativeActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.nio.ByteBuffer;

import cn.garymb.ygodata.YGOGameOptions;
import cn.garymb.ygomobile.NativeInitOptions;
import cn.garymb.ygomobile.R;
import cn.garymb.ygomobile.SignUtils;
import cn.garymb.ygomobile.YGOMobileActivity;
import cn.garymb.ygomobile.controller.NetworkController;
import cn.garymb.ygomobile.core.IrrlichtBridge;
import cn.garymb.ygomobile.widget.ComboBoxCompat;
import cn.garymb.ygomobile.widget.EditWindowCompat;
import cn.garymb.ygomobile.widget.overlay.OverlayOvalView;
import cn.garymb.ygomobile.widget.overlay.OverlayRectView;
import cn.garymb.ygomobile.widget.overlay.OverlayView;

/**
 * @author mabin
 */
public class YGOMobileActivity2 extends NativeActivity implements
        IrrlichtBridge.IrrlichtHost,
        View.OnClickListener,
        PopupWindow.OnDismissListener,
        TextView.OnEditorActionListener,
        OverlayOvalView.OnDuelOptionsSelectListener {
    private static final String TAG = YGOMobileActivity.class.getSimpleName();
//    protected final int windowsFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            | View.SYSTEM_UI_FLAG_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    private static final int CHAIN_CONTROL_PANEL_X_POSITION_LEFT_EDGE = 205;
    private static final int CHAIN_CONTROL_PANEL_Y_REVERT_POSITION = 100;

    protected final GameSettings settings = GameSettings.get();
    protected View mContentView;
    protected ComboBoxCompat mGlobalComboBox;
    protected EditWindowCompat mGlobalEditText;
    protected PowerManager mPM;
    private OverlayRectView mChainOverlayView;
    private OverlayOvalView mOverlayView;
    private NetworkController mNetController;
    private volatile boolean mOverlayShowRequest = false;
    private volatile int mCompatGUIMode;
    private static int sChainControlXPostion = -1;
    private static int sChainControlYPostion = -1;
    private Handler handler = new Handler();
    static{
        System.loadLibrary("YGOMobile");
    }
    @SuppressWarnings("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sChainControlXPostion < 0) {
            initPostion();
        }
//        setRequestedOrientation(settings.getGameScreenOritation());
//        fullscreen();
//        final View decorView = getWindow().getDecorView();
//        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                    decorView.setSystemUiVisibility(windowsFlags);
//                }
//            }
//        });
        initExtraView();
        mPM = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mNetController = new NetworkController(getApplicationContext());
        handleExternalCommand(getIntent());
    }

    private void initPostion() {
        final Resources res = getResources();
        sChainControlXPostion = (int) (CHAIN_CONTROL_PANEL_X_POSITION_LEFT_EDGE * settings
                .getXScale());
        sChainControlYPostion = (int) (settings.getSmallerSize()
                - CHAIN_CONTROL_PANEL_Y_REVERT_POSITION
                * settings.getYScale() - (res
                .getDimensionPixelSize(R.dimen.chain_control_button_height) * 2 + res
                .getDimensionPixelSize(R.dimen.chain_control_margin)));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleExternalCommand(intent);
    }

    private void handleExternalCommand(Intent intent) {
        YGOGameOptions options = intent
                .getParcelableExtra(YGOGameOptions.YGO_GAME_OPTIONS_BUNDLE_KEY);
        if (options != null) {
            Log.i(TAG, "receive from mycard:" + options.toString());
            ByteBuffer buffer = options.toByteBuffer();
            IrrlichtBridge.joinGame(buffer, buffer.position());
        } else {
            Log.i(TAG, "receive from mycard:null");
        }
    }

//    private void fullscreen() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && settings.isImmerSiveMode()) {
//            getWindow().getDecorView().setSystemUiVisibility(windowsFlags);
//        }
//    }

    private void initExtraView() {
        mContentView = getWindow().getDecorView().findViewById(android.R.id.content);
        mGlobalComboBox = new ComboBoxCompat(this);
        mGlobalComboBox.setButtonListener(this);
        mGlobalEditText = new EditWindowCompat(this);
        mGlobalEditText.setEditActionListener(this);
        mGlobalEditText.setOnDismissListener(this);

        mChainOverlayView = new OverlayRectView(this);
        mOverlayView = new OverlayOvalView(this);
        mChainOverlayView.setDuelOpsListener(this);
        mOverlayView.setDuelOpsListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
//            fullscreen();
            mContentView.setHapticFeedbackEnabled(true);
        } else {
            mContentView.setHapticFeedbackEnabled(false);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onDismiss() {
        if (mOverlayShowRequest) {
            mOverlayView.show();
            mChainOverlayView.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel) {
        } else if (v.getId() == R.id.submit) {
            int idx = mGlobalComboBox.getCurrentSelection();
            Log.d(TAG, "showComboBoxCompat: receive selection: " + idx);
            if (mCompatGUIMode == ComboBoxCompat.COMPAT_GUI_MODE_COMBOBOX) {
                IrrlichtBridge.setComboBoxSelection(idx);
            } else if (mCompatGUIMode == ComboBoxCompat.COMPAT_GUI_MODE_CHECKBOXES_PANEL) {
                IrrlichtBridge.setCheckBoxesSelection(idx);
            }
        }
        mGlobalComboBox.dismiss();
    }

    @Override
    public void onDuelOptionsSelected(int mode, boolean action) {
        switch (mode) {
            case OverlayView.MODE_CANCEL_CHAIN_OPTIONS:
                Log.d(TAG, "Constants.MODE_CANCEL_CHAIN_OPTIONS: " + action);
                IrrlichtBridge.cancelChain();
                break;
            case OverlayView.MODE_REFRESH_OPTION:
                Log.d(TAG, "Constants.MODE_REFRESH_OPTION: " + action);
                IrrlichtBridge.refreshTexture();
                break;
            case OverlayView.MODE_REACT_CHAIN_OPTION:
                Log.d(TAG, "Constants.MODE_REACT_CHAIN_OPTION: " + action);
                IrrlichtBridge.reactChain(action);
                break;
            case OverlayView.MODE_IGNORE_CHAIN_OPTION:
                Log.d(TAG, "Constants.MODE_IGNORE_CHAIN_OPTION: " + action);
                IrrlichtBridge.ignoreChain(action);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        final String text = v.getText().toString();
        IrrlichtBridge.insertText(text);
        mGlobalEditText.dismiss();
        return false;
    }

    ///////////////////C++

    @Override
    public void toggleOverlayView(boolean isShow) {
        if (mOverlayShowRequest != isShow) {
            handler.post(() -> {
                mOverlayShowRequest = isShow;
                if (isShow) {
                    mOverlayView.showAtScreen(0, 0);
                    mChainOverlayView.showAtScreen(sChainControlXPostion,
                            sChainControlYPostion);
                } else {
                    mOverlayView.removeFromScreen();
                    mChainOverlayView.removeFromScreen();
                }
            });
        }
    }

    @Override
    public ByteBuffer getInitOptions() {
        return getNativeInitOptions();
    }

    @Override
    public ByteBuffer getNativeInitOptions() {
        NativeInitOptions options = settings.getNativeInitOptions();
        Log.d(TAG, "options:" + options);
        return options.toNativeBuffer();
    }

    @Override
    public void toggleIME(String hint, boolean isShow) {
        handler.post(() -> {
            if (isShow) {
                if (mOverlayShowRequest) {
                    mOverlayView.hide();
                    mChainOverlayView.hide();
                }
                mGlobalEditText.fillContent(hint);
                mGlobalEditText.showAtLocation(mContentView,
                        Gravity.BOTTOM, 0, 0);
            } else {
                mGlobalEditText.dismiss();
            }
        });
    }

    @Override
    public void showComboBoxCompat(String[] items, boolean isShow, int mode) {
        handler.post(() -> {
            mCompatGUIMode = mode;
            Log.i(TAG, "showComboBoxCompat： isShow = " + isShow);
            if (isShow) {
                mGlobalComboBox.fillContent(items);
                mGlobalComboBox.showAtLocation(mContentView,
                        Gravity.BOTTOM, 0, 0);
            }
        });
    }

    @Override
    public void performHapticFeedback() {
        handler.post(() -> {
            mContentView.performHapticFeedback(
                    HapticFeedbackConstants.LONG_PRESS,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        });
    }

    @Override
    public byte[] performTrick() {
        return SignUtils.getSignInfo(this);
    }

    @Override
    public int getLocalAddress() {
        return mNetController.getIPAddress();
    }

    @Override
    public void setNativeHandle(int nativeHandle) {
        IrrlichtBridge.sNativeHandle = nativeHandle;
    }
}
