package cn.garymb.ygomobile.deck;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.ygo.ocgcore.Card;

class DeckDrager {
    private DeckAdapater deckAdapater;

    public DeckDrager(DeckAdapater deckAdapater) {
        this.deckAdapater = deckAdapater;
    }

    public void onDragStart(DeckViewHolder viewHolder) {
    }

    public void onDragEnd(DeckViewHolder viewHolder) {
//        deckAdapater.notifyDataSetChanged();
    }

    public boolean move(DeckViewHolder viewHolder, DeckViewHolder target, int left, int right) {
        if (left < 0) {
            return false;
        }
        if (DeckItemUtils.isMain(left)) {
            if (right == 0) {
                return removeMain(left);
            }
            if (DeckItemUtils.isMain(right)) {
                return moveMain(left, right);
            }
            if (DeckItemUtils.isSide(right)) {
                return moveMainToSide(left, right);
            }
        } else if (DeckItemUtils.isExtra(left)) {
            if (right == 0) {
                return removeExtra(left);
            }
            if (DeckItemUtils.isExtra(right)) {
                return moveExtra(left, right);
            }
            if (DeckItemUtils.isSide(right)) {
                return moveExtraToSide(left, right);
            }
        } else if (DeckItemUtils.isSide(left)) {
            if (right == 0) {
                return removeSide(left);
            }
            if (DeckItemUtils.isSide(right)) {
                return moveSide(left, right);
            }
            if (DeckItemUtils.isMain(right)) {
                if (Card.isExtraCard(viewHolder.getCardType())) {
                    return false;
                }
                if (deckAdapater.getMainCount() >= Constants.DECK_MAIN_MAX) {
                    return false;
                }
                return moveSideToMain(left, right);
            }
            if (DeckItemUtils.isExtra(right)) {
                if (!Card.isExtraCard(viewHolder.getCardType())) {
                    return false;
                }
                if (deckAdapater.getExtraCount() >= Constants.DECK_EXTRA_MAX) {
                    return false;
                }
                return moveSideToExtra(left, right);
            }
        }
        return false;
    }

    public boolean removeMain(int pos) {
        int left = pos - DeckItem.MainStart;
        if (left >= 0 && left < deckAdapater.getMainCount()) {
            deckAdapater.removeMain(left);
            deckAdapater.mItems.remove(pos);
            deckAdapater.mItems.add(DeckItem.MainEnd, new DeckItem());
            deckAdapater.notifyItemRemoved(pos);
            deckAdapater.notifyItemInserted(DeckItem.MainEnd);
            return true;
        }
        return false;
    }

    public boolean removeExtra(int pos) {
        int left = pos - DeckItem.ExtraStart;
        if (left >= 0 && left < deckAdapater.getExtraCount()) {
            deckAdapater.removeExtra(left);
            deckAdapater.mItems.remove(pos);
            deckAdapater.mItems.add(DeckItem.ExtraEnd, new DeckItem());
            deckAdapater.notifyItemRemoved(pos);
            deckAdapater.notifyItemInserted(DeckItem.ExtraEnd);
            return true;
        }
        return false;
    }

    public boolean removeSide(int pos) {
        int left = pos - DeckItem.SideStart;
        if (left >= 0 && left < deckAdapater.getSideCount()) {
            deckAdapater.removeSide(left);
            deckAdapater.mItems.remove(pos);
            deckAdapater.mItems.add(DeckItem.SideEnd, new DeckItem());
            deckAdapater.notifyItemRemoved(pos);
            deckAdapater.notifyItemInserted(DeckItem.SideEnd);
            return true;
        }
        return false;
    }

    public boolean moveMain(int src, int to) {
        int left = src - DeckItem.MainStart;
        int right = to - DeckItem.MainStart;
        int count = deckAdapater.getMainCount();
        if (left >= count && right >= count) {
            return false;
        }
        if (left >= count) {
            left = count - 1;
        }
        if (right >= count) {
            right = count - 1;
        }
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.MainStart + left);
        deckAdapater.mItems.add(DeckItem.MainStart + right, deckItem);
        CardInfo cardInfo = deckAdapater.getMainCards().remove(left);
        deckAdapater.getMainCards().add(right, cardInfo);
        deckAdapater.notifyItemMoved(DeckItem.MainStart + left, DeckItem.MainStart + right);
//        loadData();
//        notifyDataSetChanged();
        return true;
    }

    public boolean moveSide(int src, int to) {
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.SideStart;
        int count = deckAdapater.getSideCount();
        if (left >= count && right >= count) {
            return false;
        }
        if (left >= count) {
            left = count - 1;
        }
        if (right >= count) {
            right = count - 1;
        }
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.SideStart + left);
        deckAdapater.mItems.add(DeckItem.SideStart + right, deckItem);
        CardInfo cardInfo = deckAdapater.getSideCards().remove(left);
        deckAdapater.getSideCards().add(right, cardInfo);
        deckAdapater.notifyItemMoved(DeckItem.SideStart + left, DeckItem.SideStart + right);
        return true;
    }

    public boolean moveExtra(int src, int to) {
        int left = src - DeckItem.ExtraStart;
        int right = to - DeckItem.ExtraStart;
        int count = deckAdapater.getExtraCount();
        if (left >= count && right > count) {
            return false;
        }
        if (left >= count) {
            left = count - 1;
        }
        if (right >= count) {
            right = count - 1;
        }
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.ExtraStart + left);
        deckAdapater.mItems.add(DeckItem.ExtraStart + right, deckItem);
        CardInfo cardInfo = deckAdapater.getExtraCards().remove(left);
        deckAdapater.getExtraCards().add(right, cardInfo);
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
        //交换
        CardInfo cardInfo = deckAdapater.removeSide(left);
        deckAdapater.addExtra(right, cardInfo);
        //index最大的在前面
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.SideStart + left);
        DeckItem space = deckAdapater.mItems.remove(DeckItem.ExtraEnd);
        deckItem.setType(DeckItemType.ExtraCard);
        deckAdapater.mItems.add(DeckItem.ExtraStart + right, deckItem);
        deckAdapater.mItems.add(DeckItem.SideEnd, space);
        //空白向后移
        //move
        deckAdapater.notifyItemMoved(DeckItem.SideStart + left, DeckItem.ExtraStart + right);
        deckAdapater.notifyItemRemoved(DeckItem.ExtraEnd);
        if (deckAdapater.getMainCount() == Constants.DECK_MAIN_MAX) {
            deckAdapater.notifyItemChanged(DeckItem.ExtraEnd);
        }
        deckAdapater.notifyItemInserted(DeckItem.SideEnd);
        //label
        deckAdapater.notifyItemChanged(DeckItem.ExtraLabel);
        deckAdapater.notifyItemChanged(DeckItem.SideLabel);

        return true;
    }

    public boolean moveExtraToSide(int src, int to) {
        int left = src - DeckItem.ExtraStart;
        int right = to - DeckItem.SideStart;
        int count = deckAdapater.getSideCount();
        if (right >= count) {
            right = count-1;
        }

        //交换
        CardInfo cardInfo = deckAdapater.removeExtra(left);
        deckAdapater.addSide(right, cardInfo);
        DeckItem space = deckAdapater.mItems.remove(DeckItem.SideEnd);
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.ExtraStart + left);
        deckItem.setType(DeckItemType.SideCard);
        deckAdapater.mItems.add(DeckItem.SideStart + right, deckItem);
        deckAdapater.mItems.add(DeckItem.ExtraEnd, space);
        //空白向后移
        //move
        deckAdapater.notifyItemMoved(DeckItem.ExtraStart + left, DeckItem.SideStart + right);
        deckAdapater.notifyItemRemoved(DeckItem.SideEnd);
        deckAdapater.notifyItemInserted(DeckItem.ExtraEnd);
        //label
        deckAdapater.notifyItemChanged(DeckItem.ExtraLabel);
        deckAdapater.notifyItemChanged(DeckItem.SideLabel);

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
        deckItem.setType(DeckItemType.MainCard);
        DeckItem space = deckAdapater.mItems.remove(DeckItem.MainEnd);
        deckAdapater.mItems.add(DeckItem.MainStart + right, deckItem);
        deckAdapater.mItems.add(DeckItem.SideEnd, space);
        //空白向后移
        //move
        deckAdapater.notifyItemMoved(DeckItem.SideStart + left, DeckItem.MainStart + right);
        deckAdapater.notifyItemRemoved(DeckItem.MainEnd);
        if (deckAdapater.getMainCount() == Constants.DECK_MAIN_MAX) {
            deckAdapater.notifyItemChanged(DeckItem.MainEnd);
        }
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
            right = sidecount-1;
        }
        //交换
        CardInfo cardInfo = deckAdapater.removeMain(left);
        deckAdapater.addSide(right, cardInfo);
        DeckItem space = deckAdapater.mItems.remove(DeckItem.SideEnd);
        DeckItem deckItem = deckAdapater.mItems.remove(DeckItem.MainStart + left);
        deckItem.setType(DeckItemType.SideCard);
        deckAdapater.mItems.add(DeckItem.SideStart + right, deckItem);
        deckAdapater.mItems.add(DeckItem.MainEnd, space);
        //空白向后移
        //move
        deckAdapater.notifyItemMoved(DeckItem.MainStart + left, DeckItem.SideStart + right);
        deckAdapater.notifyItemRemoved(DeckItem.SideEnd);
        deckAdapater.notifyItemInserted(DeckItem.MainEnd);
        //label
        deckAdapater.notifyItemChanged(DeckItem.MainLabel);
        deckAdapater.notifyItemChanged(DeckItem.SideLabel);
        return true;
    }
}
