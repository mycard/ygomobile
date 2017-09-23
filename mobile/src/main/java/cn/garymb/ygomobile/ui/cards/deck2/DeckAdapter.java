package cn.garymb.ygomobile.ui.cards.deck2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelperPlus;
import android.support.v7.widget.helper.OnItemDragListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.garymb.ygomobile.lite.R;


public class DeckAdapter extends RecyclerView.Adapter<DeckViewHolder> implements IDeckLayout {
    private final Context mContext;
    private final RecyclerView mRecyclerView;
    private final LayoutInflater mLayoutInflater;
    private final DeckLayoutManager mDeckLayoutManager;
    private int mWidth;
    private int mHeight;
    private int mMainCount = 60, mExtraCount = 15, mSideCount = 15;
    private int mPWidth;

    public DeckAdapter(Context context, RecyclerView recyclerView, OnItemDragListener listener) {
        mContext = context;
        mRecyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(context);
        recyclerView.addItemDecoration(new DeckItemDecoration(this));
        mDeckLayoutManager = new DeckLayoutManager(getContext(), getLineLimitCount(), this);
        recyclerView.setLayoutManager(mDeckLayoutManager);

        DeckHelperCallback deckHelperCallback = new DeckHelperCallback(this);
        ItemTouchHelperPlus touchHelper = new ItemTouchHelperPlus(getContext(), deckHelperCallback);
        touchHelper.setEnableClickDrag(true);
        touchHelper.attachToRecyclerView(recyclerView);
        touchHelper.setItemDragListener(listener);

        deckHelperCallback.setItemTouchHelper(touchHelper);
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public DeckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_deck_card2, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public int getLineLimitCount() {
        return 15;
    }

    //region width/height
    private void makeHeight() {
        mWidth = getWidth10();
        mHeight = scaleHeight(mWidth);
    }

    @Override
    public int getMaxWidth() {
        if (mPWidth == 0) {
            mPWidth = mRecyclerView.getMeasuredWidth()
                    - mRecyclerView.getPaddingRight()
                    - mRecyclerView.getPaddingLeft();
        }
        return mPWidth;
    }

    @Override
    public int getWidth15() {
        return getMaxWidth() / 15;
    }

    @Override
    public int getWidth10() {
        return getMaxWidth() / 10;
    }

    private int scaleHeight(int width) {
        return Math.round((float) width * ((float) 255 / 177));
    }

    //endregion

    //region count/limit
    @Override
    public int getMainCount() {
        return Math.max(31, mMainCount);
    }

    public int getMainRealCount() {
        return mMainCount;
    }

    public void setMainCount(int mainCount) {
        if (mainCount >= 60) {
            mainCount = 60;
        } else if (mainCount < 0) {
            mainCount = 0;
        }
        mMainCount = mainCount;
    }

    @Override
    public int getExtraCount() {
        return Math.max(1, mExtraCount);
    }

    public int getExtraRealCount() {
        return mExtraCount;
    }

    public void setExtraCount(int extraCount) {
        if (extraCount >= 15) {
            extraCount = 15;
        } else if (extraCount < 0) {
            extraCount = 0;
        }
        mExtraCount = extraCount;
    }

    @Override
    public int getSideCount() {
        return Math.max(1, mSideCount);
    }

    public int getSideRealCount() {
        return mSideCount;
    }

    public void setSideCount(int sideCount) {
        if (sideCount >= 15) {
            sideCount = 15;
        } else if (sideCount < 0) {
            sideCount = 0;
        }
        mSideCount = sideCount;
    }

    @Override
    public int getMainLimit() {
        return Math.max(10, (int) Math.ceil(getMainCount() / 4.0f));
    }

    @Override
    public int getExtraLimit() {
        return Math.max(getLineCardCount(), getExtraCount());
    }

    @Override
    public int getSideLimit() {
        return Math.max(getLineCardCount(), getSideCount());
    }

    //endregion

    //region index

    @Override
    public boolean isMain(int pos) {
        return pos >= getMainStart() && pos <= getMainEnd();
    }

    @Override
    public boolean isExtra(int pos) {
        return pos >= getExtraStart() && pos <= getExtraEnd();
    }

    @Override
    public boolean isSide(int pos) {
        return pos >= getSideStart() && pos <= getSideEnd();
    }

    @Override
    public boolean isLabel(int position) {
        if (position == getMainLabel() || position == getExtraLabel() || position == getSideLabel()) {
            return true;
        }
        return false;
    }

    private int getMainLabel() {
        return 0;
    }

    private int getMainStart() {
        return getMainLabel() + 1;
    }

    private int getMainEnd() {
        return getMainStart() + getMainCount() - 1;
    }

    private int getExtraLabel() {
        return getMainEnd() + 1;
    }

    private int getExtraStart() {
        return getExtraLabel() + 1;
    }

    private int getExtraEnd() {
        return getExtraStart() + getExtraCount() - 1;
    }

    private int getSideLabel() {
        return getExtraEnd() + 1;
    }

    private int getSideStart() {
        return getSideLabel() + 1;
    }

    private int getSideEnd() {
        return getSideStart() + getSideCount() - 1;
    }

    @Override
    public int getMainIndex(int pos) {
        return pos - getMainStart();
    }

    @Override
    public int getExtraIndex(int pos) {
        return pos - getExtraStart();
    }

    @Override
    public int getSideIndex(int pos) {
        return pos - getSideStart();
    }

    //endregion

    @Override
    public int getItemCount() {
        return getMainCount() + getExtraCount() + getSideCount() + 3;
    }

    @Override
    public int getLineCardCount() {
        return 10;
    }

    @Override
    public void onBindViewHolder(DeckViewHolder holder, int position) {
        if (isLabel(position)) {
            holder.textLayout.setVisibility(View.VISIBLE);
            holder.cardImage.setVisibility(View.GONE);
            if (position == getMainLabel()) {
                holder.labelText.setText("main");
            } else if (position == getExtraLabel()) {
                holder.labelText.setText("extra");
            } else if (position == getSideLabel()) {
                holder.labelText.setText("side");
            }
            holder.setSize(-1, -1);
        } else {
            holder.textLayout.setVisibility(View.GONE);
            holder.cardImage.setVisibility(View.VISIBLE);
            holder.cardImage.setImageResource(R.drawable.unknown);
            if (mHeight <= 0) {
                makeHeight();
            }
            holder.setSize(mWidth, mHeight);
            if (isMain(position)) {
                position = getMainIndex(position);
                if (position >= getMainRealCount()) {
                    holder.empty();
                }
            } else if (isExtra(position)) {
                position = getExtraIndex(position);
                if (position >= getExtraRealCount()) {
                    holder.empty();
                }
            } else if (isSide(position)) {
                position = getSideIndex(position);
                if (position >= getSideRealCount()) {
                    holder.empty();
                }
            }
        }
    }


    @Override
    public boolean moveItem(int from, int to) {
        if (isMain(from)) {
            if (isMain(to)) {
                int pos = getMainIndex(to);
                if (pos >= getMainRealCount()) {
                    to = getMainRealCount() - 1 + getMainStart();
                }
                notifyItemMoved(from, to);
                return true;
            }
            if (isSide(to)) {
                //TODO check side
                return false;
            }
        } else if (isExtra(from)) {
            if (isExtra(to)) {
                notifyItemMoved(from, to);
                return true;
            }
            if (isSide(to)) {
                //TODO check side
                return false;
            }
        } else if (isSide(from)) {
            if (isSide(to)) {
                notifyItemMoved(from, to);
                return true;
            }
            if (isExtra(to)) {
                //TODO check extra
                return false;
            }
            if (isMain(to)) {
                //TODO check main
                int pos = getMainIndex(to);
                if (pos >= getMainRealCount()) {
                    to = getMainRealCount() - 1 + getMainStart();
                }
                return false;
            }
        }
        return false;
    }
}