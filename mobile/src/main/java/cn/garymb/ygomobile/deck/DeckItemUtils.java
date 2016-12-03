package cn.garymb.ygomobile.deck;

import android.text.TextUtils;
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
import java.util.HashMap;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.CardLoader;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.ygo.ocgcore.LimitList;

public class DeckItemUtils {

    public static boolean save(DeckInfo deckInfo, File file) {
        FileOutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            if(file==null||!file.exists()){
                return false;
            }
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
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
            writer.flush();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtils.close(writer);
            IOUtils.close(outputStream);
        }
        return true;
    }

    public static DeckInfo readDeck(CardLoader cardLoader, File file, LimitList limitList) {
        DeckInfo deckInfo = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            deckInfo = readDeck(cardLoader, inputStream, limitList);
        } catch (Exception e) {
            Log.e("kk", "read 1", e);
        } finally {
            IOUtils.close(inputStream);
        }
        return deckInfo;
    }

    public static DeckInfo readDeck(CardLoader cardLoader, InputStream inputStream, LimitList limitList) {
        List<Long> main = new ArrayList<>();
        List<Long> extra = new ArrayList<>();
        List<Long> side = new ArrayList<>();
        HashMap<Long, Integer> mIds = new HashMap<>();
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
                    }
                    continue;
                }
                if (line.startsWith("!side")) {
                    type = DeckItemType.SideCard;
                    continue;
                }
                line = line.trim();
                if (!TextUtils.isDigitsOnly(line)) {
                    Log.w("kk", "read not number " + line);
                    continue;
                }
                long id = Long.parseLong(line);
                if (type == DeckItemType.MainCard && main.size() < Constants.DECK_MAIN_MAX) {
                    Integer i = mIds.get(id);
                    if (i == null) {
                        mIds.put(id, 1);
                        main.add(id);
                    } else if (i < Constants.CARD_MAX_COUNT) {
                        mIds.put(id, i + 1);
                        main.add(id);
                    }
                } else if (type == DeckItemType.ExtraCard && extra.size() < Constants.DECK_EXTRA_MAX) {
                    Integer i = mIds.get(id);
                    if (i == null) {
                        mIds.put(id, 1);
                        extra.add(id);
                    } else if (i < Constants.CARD_MAX_COUNT) {
                        mIds.put(id, i + 1);
                        extra.add(id);
                    }
                } else if (type == DeckItemType.SideCard && side.size() < Constants.DECK_SIDE_MAX) {
                    Integer i = mIds.get(id);
                    if (i == null) {
                        mIds.put(id, 1);
                        side.add(id);
                    } else if (i < Constants.CARD_MAX_COUNT) {
                        mIds.put(id, i + 1);
                        side.add(id);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("kk", "read 2", e);
        } finally {
            IOUtils.close(in);
        }
        DeckInfo deckInfo = new DeckInfo();
        HashMap<Long, CardInfo> tmp = cardLoader.readCards(main, limitList);
        if (tmp.size() == main.size()) {
            deckInfo.setMainCards(tmp.values());
        } else {
            for (Long id : main) {
                deckInfo.addMainCards(tmp.get(id));
            }
        }
        tmp = cardLoader.readCards(extra, limitList);
        if (tmp.size() == extra.size()) {
            deckInfo.setExtraCards(tmp.values());
        } else {
            for (Long id : extra) {
                deckInfo.addExtraCards(tmp.get(id));
            }
        }
        tmp = cardLoader.readCards(side, limitList);
        if (tmp.size() == side.size()) {
            deckInfo.setSideCards(tmp.values());
        } else {
            for (Long id : extra) {
                deckInfo.addSideCards(tmp.get(id));
            }
        }
        return deckInfo;
    }

    public static List<DeckItem> makeItems(DeckInfo mDeck, DeckAdapater adapater) {
        final List<DeckItem> mItems = new ArrayList<>();
        if (mDeck != null) {
            mItems.add(new DeckItem(DeckItemType.MainLabel));
            List<CardInfo> main = mDeck.getMainCards();
            if (main == null) {
                for (int i = 0; i < Constants.DECK_MAIN_MAX; i++) {
                    mItems.add(new DeckItem());
                }
            } else {
                for (CardInfo card : main) {
                    adapater.addCount(card, DeckItemType.MainCard);
                    mItems.add(new DeckItem(card, DeckItemType.MainCard));
                }
                for (int i = main.size(); i < Constants.DECK_MAIN_MAX; i++) {
                    mItems.add(new DeckItem());
                }
            }
            if (DeckItem.SeacondIsSide) {
                List<CardInfo> side = mDeck.getSideCards();
                mItems.add(new DeckItem(DeckItemType.SideLabel));
                if (side == null) {
                    for (int i = 0; i < Constants.DECK_SIDE_COUNT; i++) {
                        mItems.add(new DeckItem());
                    }
                } else {
                    for (CardInfo card : side) {
                        adapater.addCount(card, DeckItemType.SideCard);
                        mItems.add(new DeckItem(card, DeckItemType.SideCard));
                    }
                    for (int i = side.size(); i < Constants.DECK_SIDE_COUNT; i++) {
                        mItems.add(new DeckItem());
                    }
                }
            }
            List<CardInfo> extra = mDeck.getExtraCards();
            mItems.add(new DeckItem(DeckItemType.ExtraLabel));
            if (extra == null) {
                for (int i = 0; i < Constants.DECK_EXTRA_COUNT; i++) {
                    mItems.add(new DeckItem());
                }
            } else {
                for (CardInfo card : extra) {
                    adapater.addCount(card, DeckItemType.ExtraCard);
                    mItems.add(new DeckItem(card, DeckItemType.ExtraCard));
                }
                for (int i = extra.size(); i < Constants.DECK_EXTRA_COUNT; i++) {
                    mItems.add(new DeckItem());
                }
            }
            if (!DeckItem.SeacondIsSide) {
                List<CardInfo> side = mDeck.getSideCards();
                mItems.add(new DeckItem(DeckItemType.SideLabel));
                if (side == null) {
                    for (int i = 0; i < Constants.DECK_SIDE_COUNT; i++) {
                        mItems.add(new DeckItem());
                    }
                } else {
                    for (CardInfo card : side) {
                        adapater.addCount(card, DeckItemType.SideCard);
                        mItems.add(new DeckItem(card, DeckItemType.SideCard));
                    }
                    for (int i = side.size(); i < Constants.DECK_SIDE_COUNT; i++) {
                        mItems.add(new DeckItem());
                    }
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
