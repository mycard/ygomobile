package cn.garymb.ygomobile.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.List;

import cn.garymb.ygomobile.lite.R;
import ocgcore.data.Card;

@SuppressLint("AppCompatCustomView")
public class DeckLabel extends TextView {
    public DeckLabel(Context context) {
        this(context, null);
    }

    public DeckLabel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeckLabel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.veil);
        setMinLines(1);
        setSingleLine();
    }
}
