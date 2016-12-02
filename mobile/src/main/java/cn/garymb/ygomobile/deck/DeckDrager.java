package cn.garymb.ygomobile.deck;

import java.util.Collections;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;

class DeckDrager {
    private DeckAdapater deckAdapater;

    public DeckDrager(DeckAdapater deckAdapater) {
        this.deckAdapater = deckAdapater;
    }

    public void moveMain(int src, int to) {
        int left = src - DeckItem.MainStart;
        int right = to - DeckItem.MainStart;
        int count = deckAdapater.getMainCount();
        if (left >= count && right >= count) {
            return;
        }
        if (left >= count) {
            left = count - 1;
        }
        if (right >= count) {
            right = count - 1;
        }
        Collections.swap(deckAdapater.getMainCards(), left, right);
        Collections.swap(deckAdapater.mItems, DeckItem.MainStart + left, DeckItem.MainStart + right);
        deckAdapater.notifyItemMoved(DeckItem.MainStart + left, DeckItem.MainStart + right);
//        loadData();
//        notifyDataSetChanged();
    }

    public void moveSide(int src, int to) {
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.SideStart;
        int count = deckAdapater.getSideCount();
        if (left >= count && right >= count) {
            return;
        }
        if (left >= count) {
            left = count - 1;
        }
        if (right >= count) {
            right = count - 1;
        }
        Collections.swap(deckAdapater.getSideCards(), left, right);
        Collections.swap(deckAdapater.mItems, DeckItem.SideStart + left, DeckItem.SideStart + right);
        deckAdapater.notifyItemMoved(DeckItem.SideStart + left, DeckItem.SideStart + right);
    }

    public void moveExtra(int src, int to) {
        int left = src - DeckItem.ExtraStart;
        int right = to - DeckItem.ExtraStart;
        int count = deckAdapater.getExtraCount();
//        Log.d("kk", "move extra count= " + count + " :" + left + " " + right);
        if (left >= count && right > count) {
            return;
        }
        if (left >= count) {
            left = count - 1;
        }
        if (right >= count) {
            right = count - 1;
        }
        Collections.swap(deckAdapater.getExtraCards(), left, right);
        Collections.swap(deckAdapater.mItems, DeckItem.ExtraStart + left, DeckItem.ExtraStart + right);
        deckAdapater.notifyItemMoved(DeckItem.ExtraStart + left, DeckItem.ExtraStart + right);
    }

    public void moveSideToExtra(int src, int to) {
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.ExtraStart;
        int maincount = deckAdapater.getExtraCount();
        if (right >= maincount) {
            right = maincount;
        }
        CardInfo cardInfo = deckAdapater.removeSide(left);
        deckAdapater.addExtra(right, cardInfo);
//        Collections.swap(mDeck.getExtraCards(), left, right);
        Collections.swap(deckAdapater.mItems, DeckItem.SideStart + left, DeckItem.ExtraStart + right);
        //多出一个空白
        deckAdapater.notifyItemMoved(DeckItem.SideStart + left, DeckItem.ExtraStart + right);
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.ExtraEnd);
        deckAdapater.mItems.add(DeckItem.SideEnd, new DeckItem());
        deckAdapater.notifyItemRemoved(DeckItem.ExtraEnd);
        deckAdapater.notifyItemInserted(DeckItem.SideEnd);
    }

    public void moveExtraToSide(int src, int to) {
        int left = src - DeckItem.ExtraStart;
        int right = to - DeckItem.SideStart;
        int maincount = deckAdapater.getSideCount();
        if (right >= maincount) {
            right = maincount;
        }
        //
        CardInfo cardInfo = deckAdapater.removeExtra(left);
        deckAdapater.addSide(right, cardInfo);
        Collections.swap(deckAdapater.mItems, DeckItem.ExtraStart + left, DeckItem.SideStart + right);
        deckAdapater.notifyItemMoved(DeckItem.ExtraStart + left, DeckItem.SideStart + right);
        //多出一个空白
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.SideEnd);
        deckAdapater.mItems.add(DeckItem.ExtraEnd, new DeckItem());
        deckAdapater.notifyItemRemoved(DeckItem.SideEnd);
        deckAdapater.notifyItemInserted(DeckItem.ExtraEnd);
    }

    public void moveSideToMain(int src, int to) {
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.MainStart;
        int maincount = deckAdapater.getMainCount();
        if (right >= maincount) {
            right = maincount;
        }
//        Collections.swap(mDeck.getExtraCards(), left, right);
        Collections.swap(deckAdapater.mItems, DeckItem.SideStart + left, DeckItem.MainStart + right);
        //多出一个空白
        deckAdapater.notifyItemMoved(DeckItem.SideStart + left, DeckItem.MainStart + right);
        CardInfo cardInfo = deckAdapater.removeSide(left);
        if (cardInfo != null) {
            deckAdapater.addMain(right, cardInfo);
        }
        int end = (deckAdapater.getMainCount() < Constants.DECK_MAIN_MAX) ? DeckItem.MainEnd : DeckItem.MainEnd + 1;
        DeckItem deckItem = deckAdapater.mItems.remove(end);
        deckAdapater.mItems.add(DeckItem.SideEnd, new DeckItem());
        deckAdapater.notifyItemRemoved(end);
        deckAdapater.notifyItemInserted(DeckItem.SideEnd);
    }

    public void removeMainEnd(){
        int end = (deckAdapater.getMainCount() < Constants.DECK_MAIN_MAX) ? DeckItem.MainEnd : DeckItem.MainEnd + 1;
        DeckItem deckItem = deckAdapater.mItems.remove(end);
        deckAdapater.mItems.add(DeckItem.SideEnd, new DeckItem());
        deckAdapater.notifyItemRemoved(end);
        deckAdapater.notifyItemInserted(DeckItem.SideEnd);
    }

    public void removeExtraEnd(){
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.ExtraEnd);
        deckAdapater.mItems.add(DeckItem.SideEnd, new DeckItem());
        deckAdapater.notifyItemRemoved(DeckItem.ExtraEnd);
        deckAdapater.notifyItemInserted(DeckItem.SideEnd);
    }

    public void moveMainToSide(int src, int to) {
        int left = src - DeckItem.MainStart;
        int right = to - DeckItem.SideStart;
        int maincount = deckAdapater.getSideCount();
        if (right >= maincount) {
            right = maincount - 1;
        }
        Collections.swap(deckAdapater.mItems, DeckItem.MainStart + left, DeckItem.SideStart + right);
        deckAdapater.notifyItemMoved(DeckItem.MainStart + left, DeckItem.SideStart + right);
        //多出一个空白
        //
        CardInfo cardInfo = deckAdapater.removeMain(left);
        if (cardInfo != null) {
            deckAdapater.addSide(right, cardInfo);
        }
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.SideEnd);
        deckAdapater.mItems.add(DeckItem.MainEnd, new DeckItem());
        deckAdapater.notifyItemRemoved(DeckItem.SideEnd);
        deckAdapater.notifyItemInserted(DeckItem.MainEnd);
    }
}
