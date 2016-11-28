package cn.garymb.ygomobile.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import cn.garymb.ygomobile.bean.CardInfo;

/***
 *  卡片详情
 */
public class CardDialog extends AlertDialog {
    public CardDialog(@NonNull Context context, CardInfo cardInfo) {
        super(context);
    }
}
