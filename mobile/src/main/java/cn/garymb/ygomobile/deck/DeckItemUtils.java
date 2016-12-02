package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.enums.LimitType;

public class DeckItemUtils {

    public static void save(DeckInfo deckInfo, String file) throws IOException {
        FileOutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            outputStream = new FileOutputStream(file);
            writer = new OutputStreamWriter(outputStream, "utf-8");
            writer.write("#created by ygomobile\n".toCharArray());
            writer.write("#main\n".toCharArray());
            if (deckInfo.getMainCards() != null) {
                List<CardInfo> items = deckInfo.getMainCards();
                for (int i = 0; i < Constants.DECK_MAIN_MAX && i < items.size(); i++) {
                    CardInfo cardInfo = items.get(i);
                    writer.write((cardInfo.Code + "\n").toCharArray());
                }
            }
            writer.write("#extra\n".toCharArray());
            if (deckInfo.getExtraCards() != null) {
                List<CardInfo> items = deckInfo.getExtraCards();
                for (int i = 0; i < Constants.DECK_EXTRA_MAX && i < items.size(); i++) {
                    CardInfo cardInfo = items.get(i);
                    writer.write((cardInfo.Code + "\n").toCharArray());
                }
            }
            writer.write("!side\n".toCharArray());
            if (deckInfo.getSideCards() != null) {
                List<CardInfo> items = deckInfo.getSideCards();
                for (int i = 0; i < Constants.DECK_SIDE_MAX && i < items.size(); i++) {
                    CardInfo cardInfo = items.get(i);
                    writer.write((cardInfo.Code + "\n").toCharArray());
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.close(writer);
            IOUtils.close(outputStream);
        }

    }

    public static DeckInfo readDeck(SQLiteDatabase db, File file, LimitList limitList) {
        DeckInfo deckInfo = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            deckInfo = readDeck(db, inputStream, limitList);
        } catch (Exception e) {

        } finally {
            IOUtils.close(inputStream);
        }
        return deckInfo;
    }

    public static DeckInfo readDeck(SQLiteDatabase db, InputStream inputStream, LimitList limitList) throws IOException {
        List<Long> main = new ArrayList<>();
        List<Long> extra = new ArrayList<>();
        List<Long> side = new ArrayList<>();
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(inputStream, "utf-8");
            BufferedReader reader = new BufferedReader(in);
            String line = null;
            DeckItemType type = DeckItemType.Space;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    if (line.startsWith("#main")) {
                        type = DeckItemType.MainCard;
                    } else if (line.startsWith("#extra")) {
                        type = DeckItemType.ExtraCard;
                    } else {
                        continue;
                    }
                }
                if (line.startsWith("!side")) {
                    type = DeckItemType.SideCard;
                }
                try {
                    long id = Long.parseLong(line);
                    if (type == DeckItemType.MainCard) {
                        main.add(id);
                    } else if (type == DeckItemType.ExtraCard) {
                        extra.add(id);
                    } else if (type == DeckItemType.SideCard) {
                        side.add(id);
                    }
                } catch (Exception e) {

                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.close(in);
        }
        DeckInfo deckInfo = new DeckInfo();
        deckInfo.setMainCards(readCards(db, main, limitList));
        deckInfo.setExtraCards(readCards(db, extra, limitList));
        deckInfo.setSideCards(readCards(db, side, limitList));
        return deckInfo;
    }

    public static List<CardInfo> readCards(SQLiteDatabase db, List<Long> ids, LimitList limitList) {
        StringBuilder stringBuilder = new StringBuilder(CardInfo.SQL_BASE);
        stringBuilder.append(" and " + CardInfo.COL_ID + " in (");
        int i = 0;
        for (Long id : ids) {
            if (i != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(id);
            i++;
        }
        stringBuilder.append(")");
        String sql = stringBuilder.toString();
        Cursor reader = null;
        try {
            reader = db.rawQuery(sql, null);
        } catch (Exception e) {
        }
        List<CardInfo> tmp = new ArrayList<CardInfo>();
        if (reader != null) {
            if (reader.moveToFirst()) {
//                Log.d("kk", "find card count=" + reader.getCount());
                do {
                    CardInfo cardInfo = new CardInfo(reader);
                    if (limitList != null) {
                        if (limitList.isForbidden(cardInfo.Code)) {
                            cardInfo.setLimitType(LimitType.Forbidden);
                        } else if (limitList.isLimit(cardInfo.Code)) {
                            cardInfo.setLimitType(LimitType.Limit);
                        } else if (limitList.isSemiLimit(cardInfo.Code)) {
                            cardInfo.setLimitType(LimitType.SemiLimit);
                        }
                    }
                    tmp.add(cardInfo);

                } while (reader.moveToNext());
            }
            reader.close();
        }
        return tmp;
    }

    public static List<DeckItem> makeItems(Context context, DeckInfo mDeck) {
        final List<DeckItem> mItems = new ArrayList<>();
        mItems.clear();
        if (mDeck != null) {
            mItems.add(new DeckItem(context.getResources().getString(R.string.deck_main), 0));
            List<CardInfo> main = mDeck.getMainCards();
            if (main == null) {
                for (int i = 0; i < Constants.DECK_MAIN_MAX; i++) {
                    mItems.add(new DeckItem());
                }
            } else {
                for (CardInfo card : main) {
                    mItems.add(new DeckItem(card, DeckItemType.MainCard));
                }
                for (int i = main.size(); i < Constants.DECK_MAIN_MAX; i++) {
                    mItems.add(new DeckItem());
                }
            }
            List<CardInfo> extra = mDeck.getExtraCards();
            mItems.add(new DeckItem(context.getResources().getString(R.string.deck_extra), 0));
            if (extra == null) {
                for (int i = 0; i < Constants.DECK_EXTRA_COUNT; i++) {
                    mItems.add(new DeckItem());
                }
            } else {
                for (CardInfo card : extra) {
                    mItems.add(new DeckItem(card, DeckItemType.ExtraCard));
                }
                for (int i = extra.size(); i < Constants.DECK_EXTRA_COUNT; i++) {
                    mItems.add(new DeckItem());
                }
            }
            List<CardInfo> side = mDeck.getSideCards();
            mItems.add(new DeckItem(context.getResources().getString(R.string.deck_side), 0));
            if (side == null) {
                for (int i = 0; i < Constants.DECK_SIDE_COUNT; i++) {
                    mItems.add(new DeckItem());
                }
            } else {
                for (CardInfo card : side) {
                    mItems.add(new DeckItem(card, DeckItemType.SideCard));
                }
                for (int i = side.size(); i < Constants.DECK_SIDE_COUNT; i++) {
                    mItems.add(new DeckItem());
                }
            }
        }
        return mItems;
    }

    public static boolean isMain(int pos) {
        return pos >= DeckItem.MainStart && pos <= DeckItem.MainEnd;
    }

    public static boolean isExtra(int pos) {
        return pos >= DeckItem.ExtraStart && pos <= DeckItem.ExtraEnd;
    }

    public static boolean isSide(int pos) {
        return pos >= DeckItem.SideStart && pos <= DeckItem.SideEnd;
    }

    public static boolean isLabel(int position) {
        if (position == DeckItem.MainLabel || position == DeckItem.SideLabel || position == DeckItem.ExtraLabel) {
            return true;
        }
        return false;
    }
}
