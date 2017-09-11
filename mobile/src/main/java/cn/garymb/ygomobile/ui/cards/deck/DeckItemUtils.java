package cn.garymb.ygomobile.ui.cards.deck;

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
import java.util.Map;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.Deck;
import cn.garymb.ygomobile.bean.DeckInfo;
import cn.garymb.ygomobile.loader.CardLoader;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.MD5Util;
import ocgcore.data.Card;
import ocgcore.data.LimitList;

class DeckItemUtils {

    public static String makeMd5(List<DeckItem> items) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#main");
        for (int i = DeckItem.MainStart; i < DeckItem.MainStart + Constants.DECK_MAIN_MAX; i++) {
            DeckItem deckItem = items.get(i);
            if (deckItem.getType() == DeckItemType.Space) {
                break;
            }
            Card cardInfo = deckItem.getCardInfo();
            if (cardInfo != null) {
                stringBuilder.append("\n");
//                if(!cardInfo.isExtraCard()) {
                stringBuilder.append(cardInfo.Code);
//                }
            }
        }
        stringBuilder.append("\n#extra");
        for (int i = DeckItem.ExtraStart; i < DeckItem.ExtraStart + Constants.DECK_EXTRA_MAX; i++) {
            DeckItem deckItem = items.get(i);
            if (deckItem.getType() == DeckItemType.Space) {
                break;
            }
            Card cardInfo = deckItem.getCardInfo();
            if (cardInfo != null) {
                stringBuilder.append("\n");
//                if(cardInfo.isExtraCard()) {
                stringBuilder.append(cardInfo.Code);
//                }
            }
        }
        stringBuilder.append("\n!side");
        for (int i = DeckItem.SideStart; i < DeckItem.SideStart + Constants.DECK_SIDE_MAX; i++) {
            DeckItem deckItem = items.get(i);
            if (deckItem.getType() == DeckItemType.Space) {
                break;
            }
            Card cardInfo = deckItem.getCardInfo();
            if (cardInfo != null) {
                stringBuilder.append("\n");
                stringBuilder.append(cardInfo.Code);
            }
        }
        return MD5Util.getStringMD5(stringBuilder.toString());
    }

    public static Deck toDeck(List<DeckItem> items, File file) {
        Deck deck;
        if (file == null) {
            deck = new Deck();
        } else {
            deck = new Deck(file.getName());
        }
        try {
            for (int i = DeckItem.MainStart; i < DeckItem.MainStart + Constants.DECK_MAIN_MAX; i++) {
                DeckItem deckItem = items.get(i);
                if (deckItem.getType() == DeckItemType.Space) {
                    break;
                }
                Card cardInfo = deckItem.getCardInfo();
                if (cardInfo != null) {
//                    if(!cardInfo.isExtraCard()) {
                    deck.addMain(cardInfo.Code);
//                    }
                }
            }
            for (int i = DeckItem.ExtraStart; i < DeckItem.ExtraStart + Constants.DECK_EXTRA_MAX; i++) {
                DeckItem deckItem = items.get(i);
                if (deckItem.getType() == DeckItemType.Space) {
                    break;
                }
                Card cardInfo = deckItem.getCardInfo();
                if (cardInfo != null) {
//                    if(cardInfo.isExtraCard()) {
                    deck.addExtra(cardInfo.Code);
//                    }
                }
            }
            for (int i = DeckItem.SideStart; i < DeckItem.SideStart + Constants.DECK_SIDE_MAX; i++) {
                DeckItem deckItem = items.get(i);
                if (deckItem.getType() == DeckItemType.Space) {
                    break;
                }
                Card cardInfo = deckItem.getCardInfo();
                if (cardInfo != null) {
                    deck.addSide(cardInfo.Code);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deck;
    }

    public static boolean save(List<DeckItem> items, File file) {
        FileOutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            if (file == null) {
                return false;
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            writer = new OutputStreamWriter(outputStream, "utf-8");
            writer.write("#created by ygomobile".toCharArray());
            writer.write("\n#main".toCharArray());
            for (int i = DeckItem.MainStart; i < DeckItem.MainStart + Constants.DECK_MAIN_MAX; i++) {
                DeckItem deckItem = items.get(i);
                if (deckItem.getType() == DeckItemType.Space) {
                    break;
                }
                Card cardInfo = deckItem.getCardInfo();
                if (cardInfo != null) {
                    writer.write(("\n" + cardInfo.Code).toCharArray());
                }
            }
            writer.write("\n#extra".toCharArray());
            for (int i = DeckItem.ExtraStart; i < DeckItem.ExtraStart + Constants.DECK_EXTRA_MAX; i++) {
                DeckItem deckItem = items.get(i);
                if (deckItem.getType() == DeckItemType.Space) {
                    break;
                }
                Card cardInfo = deckItem.getCardInfo();
                if (cardInfo != null) {
                    writer.write(("\n" + cardInfo.Code).toCharArray());
                }
            }
            writer.write("\n!side".toCharArray());
            for (int i = DeckItem.SideStart; i < DeckItem.SideStart + Constants.DECK_SIDE_MAX; i++) {
                DeckItem deckItem = items.get(i);
                if (deckItem.getType() == DeckItemType.Space) {
                    break;
                }
                Card cardInfo = deckItem.getCardInfo();
                if (cardInfo != null) {
                    writer.write(("\n" + cardInfo.Code).toCharArray());
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
            Log.e("deckreader", "read 1", e);
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
                if (line.startsWith("!side")) {
                    type = DeckItemType.SideCard;
                    continue;
                }
                if (line.startsWith("#")) {
                    if (line.startsWith("#main")) {
                        type = DeckItemType.MainCard;
                    } else if (line.startsWith("#extra")) {
                        type = DeckItemType.ExtraCard;
                    }
                    continue;
                }
                line = line.trim();
                if (line.length() == 0 || !TextUtils.isDigitsOnly(line)) {
                    if (Constants.DEBUG)
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
            Log.e("deckreader", "read 2", e);
        } finally {
            IOUtils.close(in);
        }
        DeckInfo deckInfo = new DeckInfo();
        Map<Long, Card> tmp = cardLoader.readCards(main, limitList);
        for (Long id : main) {
            deckInfo.addMainCards(tmp.get(id));
        }
        tmp = cardLoader.readCards(extra, limitList);
        for (Long id : extra) {
            deckInfo.addExtraCards(tmp.get(id));
        }
        tmp = cardLoader.readCards(side, limitList);
//        Log.i("kk", "desk:" + tmp.size()+"/"+side.size());
        for (Long id : side) {
            deckInfo.addSideCards(tmp.get(id));
        }
        return deckInfo;
    }

    public static void makeItems(DeckInfo mDeck, DeckAdapater adapater) {
        if (mDeck != null) {
            adapater.addItem(new DeckItem(DeckItemType.HeadView));
            adapater.addItem(new DeckItem(DeckItemType.MainLabel));
            List<Card> main = mDeck.getMainCards();
            if (main == null) {
                for (int i = 0; i < Constants.DECK_MAIN_MAX; i++) {
                    adapater.addItem(new DeckItem());
                }
            } else {
                for (Card card : main) {
                    adapater.addItem(new DeckItem(card, DeckItemType.MainCard));
                }
                for (int i = main.size(); i < Constants.DECK_MAIN_MAX; i++) {
                    adapater.addItem(new DeckItem());
                }
            }
            List<Card> extra = mDeck.getExtraCards();
            adapater.addItem(new DeckItem(DeckItemType.ExtraLabel));
            if (extra == null) {
                for (int i = 0; i < Constants.DECK_EXTRA_COUNT; i++) {
                    adapater.addItem(new DeckItem());
                }
            } else {
                for (Card card : extra) {
                    adapater.addItem(new DeckItem(card, DeckItemType.ExtraCard));
                }
                for (int i = extra.size(); i < Constants.DECK_EXTRA_COUNT; i++) {
                    adapater.addItem(new DeckItem());
                }
            }
            List<Card> side = mDeck.getSideCards();
            adapater.addItem(new DeckItem(DeckItemType.SideLabel));
            if (side == null) {
                for (int i = 0; i < Constants.DECK_SIDE_COUNT; i++) {
                    adapater.addItem(new DeckItem());
                }
            } else {
                for (Card card : side) {
                    adapater.addItem(new DeckItem(card, DeckItemType.SideCard));
                }
                for (int i = side.size(); i < Constants.DECK_SIDE_COUNT; i++) {
                    adapater.addItem(new DeckItem());
                }
            }
        }
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
