package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

public class DeckLayoutManager extends GridLayoutManager {
    private Context context;

    public DeckLayoutManager(Context context, final int span) {
        super(context, span);
        this.context = context;
        setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (DeckItemUtils.isLabel(position)||position==DeckItem.HeadView) {
                    return span;
                }
                return 1;
            }
        });
    }

}
