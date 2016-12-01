package cn.garymb.ygomobile.deck;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.ygo.ocgcore.LimitList;

public class DeckInfo {
    private List<CardInfo> mainCards;
    private List<CardInfo> extraCards;
    private List<CardInfo> sideCards;

    public DeckInfo() {

    }

    public void setMainCards(List<CardInfo> mainCards) {
        this.mainCards = mainCards;
    }

    public void setExtraCards(List<CardInfo> extraCards) {
        this.extraCards = extraCards;
    }

    public void setSideCards(List<CardInfo> sideCards) {
        this.sideCards = sideCards;
    }

    public List<CardInfo> getMainCards() {
        return mainCards;
    }

    public List<CardInfo> getExtraCards() {
        return extraCards;
    }

    public List<CardInfo> getSideCards() {
        return sideCards;
    }
}
