package cn.garymb.ygomobile.deck;

import android.util.Log;

import java.util.Collections;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.ygo.ocgcore.Card;

class DeckDrager {
    private DeckAdapater deckAdapater;
    private boolean isExtra;
    private int mDragId;

    public DeckDrager(DeckAdapater deckAdapater) {
        this.deckAdapater = deckAdapater;
    }

    public void onDragStart(DeckViewHolder viewHolder) {
        isExtra = Card.isExtraCard(viewHolder.getCardType());
        mDragId = viewHolder.getAdapterPosition();
    }

    public void onDragEnd(DeckViewHolder viewHolder) {
//        deckAdapater.notifyDataSetChanged();
    }

    public boolean move(int left, int right) {
        if (left < 0) {
            return false;
        }
        if (DeckItemUtils.isMain(left)) {
            if (DeckItemUtils.isMain(right)) {
                return moveMain(left, right);
            }
            if (DeckItemUtils.isSide(right)) {
                return moveMainToSide(left, right);
            }
        } else if (DeckItemUtils.isExtra(left)) {
            if (DeckItemUtils.isExtra(right)) {
                return moveExtra(left, right);
            }
            if (DeckItemUtils.isSide(right)) {
                return moveExtraToSide(left, right);
            }
        } else if (DeckItemUtils.isSide(left)) {
            if (DeckItemUtils.isSide(right)) {
                return moveSide(left, right);
            }
            if (DeckItemUtils.isMain(right)) {
                if ((left == mDragId) && isExtra) {
                    return false;
                }
                if ((left == mDragId) && deckAdapater.getMainCount() >= Constants.DECK_MAIN_MAX) {
                    return false;
                }
                return moveSideToMain(left, right);
            }
            if (DeckItemUtils.isExtra(right)) {
                if ((left == mDragId) && !isExtra) {
                    return false;
                }
                if ((left == mDragId) && deckAdapater.getExtraCount() >= Constants.DECK_EXTRA_MAX) {
                    return false;
                }
                return moveSideToExtra(left, right);
            }
        }
        return false;
    }

    public boolean moveMain(int src, int to) {
        int left = src - DeckItem.MainStart;
        int right = to - DeckItem.MainStart;
        int count = deckAdapater.getMainCount();
//        if (left >= count && right >= count) {
//            return;
//        }
//        if (left >= count) {
//            left = count - 1;
//        }
        if (right >= count) {
            right = count - 1;
        }
        Collections.swap(deckAdapater.getMainCards(), left, right);
        Collections.swap(deckAdapater.mItems, DeckItem.MainStart + left, DeckItem.MainStart + right);
        deckAdapater.notifyItemMoved(DeckItem.MainStart + left, DeckItem.MainStart + right);
//        loadData();
//        notifyDataSetChanged();
        return true;
    }

    public boolean moveSide(int src, int to) {
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.SideStart;
        int count = deckAdapater.getSideCount();
//        if (left >= count && right >= count) {
//            return;
//        }
//        if (left >= count) {
//            left = count - 1;
//        }
        if (right >= count) {
            right = count - 1;
        }
        Collections.swap(deckAdapater.getSideCards(), left, right);
        Collections.swap(deckAdapater.mItems, DeckItem.SideStart + left, DeckItem.SideStart + right);
        deckAdapater.notifyItemMoved(DeckItem.SideStart + left, DeckItem.SideStart + right);
        return true;
    }

    public boolean moveExtra(int src, int to) {
        int left = src - DeckItem.ExtraStart;
        int right = to - DeckItem.ExtraStart;
        int count = deckAdapater.getExtraCount();
//        if (left >= count && right > count) {
//            return;
//        }
//        if (left >= count) {
//            left = count - 1;
//        }
        if (right >= count) {
            right = count - 1;
        }
        Collections.swap(deckAdapater.getExtraCards(), left, right);
        Collections.swap(deckAdapater.mItems, DeckItem.ExtraStart + left, DeckItem.ExtraStart + right);
        deckAdapater.notifyItemMoved(DeckItem.ExtraStart + left, DeckItem.ExtraStart + right);
        return true;
    }

    public boolean moveSideToExtra(int src, int to) {
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.ExtraStart;
        int maincount = deckAdapater.getExtraCount();
        if (right >= maincount) {
            right = maincount;
        }
        CardInfo cardInfo = deckAdapater.removeSide(left);
        deckAdapater.addExtra(right, cardInfo);
        Collections.swap(deckAdapater.mItems, DeckItem.SideStart + left, DeckItem.ExtraStart + right);
        deckAdapater.notifyItemMoved(DeckItem.SideStart + left, DeckItem.ExtraStart + right);
        //多出一个空白
        for (int i = DeckItem.SideStart + left; i < DeckItem.SideEnd; i++) {
            Collections.swap(deckAdapater.mItems, i, i + 1);
        }
        deckAdapater.notifyItemRemoved(DeckItem.ExtraEnd);
        deckAdapater.notifyItemInserted(DeckItem.SideEnd);
        return true;
    }

    public boolean moveExtraToSide(int src, int to) {
        int left = src - DeckItem.ExtraStart;
        int right = to - DeckItem.SideStart;
        int maincount = deckAdapater.getSideCount();
        if (right >= maincount) {
            right = maincount;
        }
        CardInfo cardInfo = deckAdapater.removeExtra(left);
        deckAdapater.addSide(right, cardInfo);
        Collections.swap(deckAdapater.mItems, DeckItem.ExtraStart + left, DeckItem.SideStart + right);
        deckAdapater.notifyItemMoved(DeckItem.ExtraStart + left, DeckItem.SideStart + right);
        //多出一个空白
        for (int i = DeckItem.ExtraStart + left; i < DeckItem.ExtraEnd; i++) {
            Collections.swap(deckAdapater.mItems, i, i + 1);
        }
        deckAdapater.notifyItemRemoved(DeckItem.SideEnd);
        deckAdapater.notifyItemInserted(DeckItem.ExtraEnd);
        return true;
    }

    public boolean moveSideToMain(int src, int to) {
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.MainStart;
        int maincount = deckAdapater.getMainCount();
        int sidecount = deckAdapater.getSideCount();
        int extracount = deckAdapater.getExtraCount();
        if (maincount >= Constants.DECK_MAIN_MAX) {
            return false;
        }
        if (right > maincount) {
            right = maincount;
        }
        //交换
        CardInfo cardInfo = deckAdapater.removeSide(left);
        deckAdapater.addMain(right, cardInfo);
        //index最大的在前面
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.SideStart + left);
        DeckItem space = deckAdapater.mItems.remove(DeckItem.MainEnd);
        deckAdapater.mItems.add(DeckItem.MainStart + right, deckItem);
        deckAdapater.mItems.add(DeckItem.SideEnd, space);
        //空白向后移
//        for (int i = DeckItem.SideStart + left; i < DeckItem.SideStart + sidecount; i++) {
           // Collections.swap(deckAdapater.mItems, i, i + 1);
//        }
        //move
        deckAdapater.notifyItemMoved(DeckItem.SideStart + left, DeckItem.MainStart + right);
        deckAdapater.notifyItemRemoved(DeckItem.MainEnd);
        deckAdapater.notifyItemInserted(DeckItem.SideEnd);
        //label
        deckAdapater.notifyItemChanged(DeckItem.MainLabel);
        deckAdapater.notifyItemChanged(DeckItem.SideLabel);
        return true;
    }

    public boolean moveMainToSide(int src, int to) {
        int left = src - DeckItem.MainStart;
        int right = to - DeckItem.SideStart;
        int sidecount = deckAdapater.getSideCount();
        int maincount = deckAdapater.getMainCount();
        if (right > sidecount) {
            right = sidecount;
        }
        //交换
        CardInfo cardInfo = deckAdapater.removeMain(left);
        deckAdapater.addSide(right, cardInfo);
        DeckItem space = deckAdapater.mItems.remove(DeckItem.SideEnd);
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.MainStart + left);
        deckAdapater.mItems.add(DeckItem.SideStart + right, deckItem);
        deckAdapater.mItems.add(DeckItem.MainEnd, space);
        //空白向后移
//        for (int i = DeckItem.MainStart + left; i < DeckItem.MainStart + maincount; i++) {
//            Collections.swap(deckAdapater.mItems, i, i + 1);
//        }
        //move
        deckAdapater.notifyItemMoved(DeckItem.MainStart + left, DeckItem.SideStart + right);
        deckAdapater.notifyItemRemoved(DeckItem.SideEnd);
        deckAdapater.notifyItemInserted(DeckItem.MainEnd);
        //label
        deckAdapater.notifyItemChanged(DeckItem.MainLabel);
        deckAdapater.notifyItemChanged(DeckItem.SideLabel);
        return true;
    }

    /**
     * 减少main的一个，增加side
     */
    public void removeMainEnd() {
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.MainEnd);
        deckAdapater.mItems.add(DeckItem.SideEnd, deckItem);
        deckAdapater.notifyItemRemoved(DeckItem.MainEnd);
        deckAdapater.notifyItemInserted(DeckItem.SideEnd);
    }

    public void addMainEnd() {
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.SideEnd);
        deckAdapater.mItems.add(DeckItem.MainEnd, deckItem);
        deckAdapater.notifyItemRemoved(DeckItem.SideEnd);
        deckAdapater.notifyItemInserted(DeckItem.MainEnd);
    }

    /**
     * 减少extra的一个，增加side
     */
    public void removeExtraEnd() {
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.ExtraEnd);
        deckAdapater.mItems.add(DeckItem.SideEnd, deckItem);
        deckAdapater.notifyItemRemoved(DeckItem.ExtraEnd);
        deckAdapater.notifyItemInserted(DeckItem.SideEnd);
    }

    public void addExtraEnd() {
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.SideEnd);
        deckAdapater.mItems.add(DeckItem.ExtraEnd, deckItem);
        deckAdapater.notifyItemRemoved(DeckItem.SideEnd);
        deckAdapater.notifyItemInserted(DeckItem.ExtraEnd);
    }

}
