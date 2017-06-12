package cn.garymb.ygomobile.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.ui.cards.deck.DeckItem;
import cn.garymb.ygomobile.ui.cards.deck.DeckUtils;

import static cn.garymb.ygomobile.Constants.QUERY_EXTRA;
import static cn.garymb.ygomobile.Constants.QUERY_MAIN;
import static cn.garymb.ygomobile.Constants.QUERY_SIDE;
import static cn.garymb.ygomobile.Constants.QUERY_YDK;
import static cn.garymb.ygomobile.Constants.YDK_FILE_EX;

public class Deck implements Parcelable {
    private String name;
    private final ArrayList<Long> mainlist;
    private final ArrayList<Long> extraList;
    private final ArrayList<Long> sideList;

    public Deck() {
        mainlist = new ArrayList<>();
        extraList = new ArrayList<>();
        sideList = new ArrayList<>();
    }

    public Deck(Uri uri) {
        this(uri.getQueryParameter(QUERY_YDK));
        String main = uri.getQueryParameter(QUERY_MAIN);
        String extra = uri.getQueryParameter(QUERY_EXTRA);
        String side = uri.getQueryParameter(QUERY_SIDE);
        if (!TextUtils.isEmpty(main)) {
            String[] mains = main.split(",");
            for (String m : mains) {
                long id = toId(m);
                if (id > 0) {
                    mainlist.add(id);
                }
            }
        }
        if (!TextUtils.isEmpty(extra)) {
            String[] extras = extra.split(",");
            for (String m : extras) {
                long id = toId(m);
                if (id > 0) {
                    extraList.add(id);
                }
            }
        }
        if (!TextUtils.isEmpty(side)) {
            String[] sides = side.split(",");
            for (String m : sides) {
                long id = toId(m);
                if (id > 0) {
                    sideList.add(id);
                }
            }
        }
    }

    public Uri toAppUri() {
        return toUri(Constants.SCHEME_APP);
    }

    public Uri toHttpUri() {
        return toUri(Constants.SCHEME_HTTP);
    }

    public Uri toUri(String host) {
        Uri.Builder uri = Uri.parse(host + "://")
                .buildUpon()
                .authority(Constants.URI_HOST)
                .path(Constants.PATH_DECK);
        if (!TextUtils.isEmpty(name)) {
            uri.appendQueryParameter(Constants.QUERY_NAME, name);
        }
        uri.appendQueryParameter(Constants.QUERY_MAIN, toString(mainlist))
                .appendQueryParameter(Constants.QUERY_EXTRA, toString(extraList))
                .appendQueryParameter(Constants.QUERY_SIDE, toString(sideList));
        return uri.build();
    }

    private String toString(List<Long> ids) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Long id : ids) {
            if (i > 0) {
                builder.append(",");
            }
            if (id > 0) {
                builder.append(id);
                i++;
            }
        }
        return builder.toString();
    }


    public String getName() {
        return name;
    }


    public File saveTemp(String dir) {
        if (TextUtils.isEmpty(name)) {
            name = "__noname.ydk";
        }
        if (!name.endsWith(YDK_FILE_EX)) {
            name += YDK_FILE_EX;
        }
        File file = new File(dir, name);
        DeckUtils.save(this, file);
        return file;
    }

    private long toId(String str) {
        if (TextUtils.isEmpty(str)) return 0;
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public Deck(String name) {
        this();
        this.name = name;
    }

    public List<Long> getSideList() {
        return sideList;
    }

    public List<Long> getMainlist() {
        return mainlist;
    }

    public List<Long> getExtraList() {
        return extraList;
    }

    public void addMain(Long id) {
        mainlist.add(id);
    }

    public void addExtra(Long id) {
        extraList.add(id);
    }

    public void addSide(Long id) {
        sideList.add(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeList(this.mainlist);
        dest.writeList(this.extraList);
        dest.writeList(this.sideList);
    }

    protected Deck(Parcel in) {
        this.name = in.readString();
        this.mainlist = new ArrayList<Long>();
        in.readList(this.mainlist, Long.class.getClassLoader());
        this.extraList = new ArrayList<Long>();
        in.readList(this.extraList, Long.class.getClassLoader());
        this.sideList = new ArrayList<Long>();
        in.readList(this.sideList, Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<Deck> CREATOR = new Parcelable.Creator<Deck>() {
        @Override
        public Deck createFromParcel(Parcel source) {
            return new Deck(source);
        }

        @Override
        public Deck[] newArray(int size) {
            return new Deck[size];
        }
    };
}
