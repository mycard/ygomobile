package cn.garymb.ygomobile.core;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.core.loader.ICardLoader;
import cn.garymb.ygomobile.core.loader.ILoadCallBack;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.LimitManager;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.CardAttribute;
import cn.ygo.ocgcore.enums.CardCategory;
import cn.ygo.ocgcore.enums.LimitType;
import cn.ygo.ocgcore.enums.CardOt;
import cn.ygo.ocgcore.enums.CardRace;
import cn.ygo.ocgcore.enums.CardType;

public class CardSearcher implements View.OnClickListener,ILoadCallBack{

    private EditText prefixWord;
    private EditText suffixWord;
    private Spinner otSpinner;
    private Spinner limitSpinner;
    private Spinner limitListSpinner;
    private Spinner typeSpinner;
    private Spinner typeMonsterSpinner;
    private Spinner typeSTSpinner;

    private Spinner setcodeSpinner;
    private Spinner categorySpinner;
    private Spinner raceSpinner;
    private Spinner levelSpinner;
    private Spinner attributeSpinner;
    private EditText atkText;
    private EditText defText;
    private Button searchButton;
    private Button resetButton;
    private View view;
    private View layout_monster;
    private ICardLoader dataLoader;
    private DrawerLayout drawerlayout;
    private Context mContext;
    protected StringManager mStringManager;
    protected LimitManager mLimitManager;
    protected AppsSettings mSettings;

    public void setDataLoader(ICardLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public CardSearcher(DrawerLayout drawerlayout, View view) {
        this.view = view;
        this.mContext = view.getContext();
        this.drawerlayout = drawerlayout;
        this.mSettings = AppsSettings.get();
        mStringManager = StringManager.get();
        mLimitManager = LimitManager.get();
        prefixWord = findViewById(R.id.edt_word1);
        suffixWord = findViewById(R.id.edt_word2);
        otSpinner = findViewById(R.id.sp_ot);
        limitSpinner = findViewById(R.id.sp_limit);
        limitListSpinner = findViewById(R.id.sp_limit_list);
        typeSpinner = findViewById(R.id.sp_type1);
        typeMonsterSpinner = findViewById(R.id.sp_type2);
        typeSTSpinner = findViewById(R.id.sp_type3);
        setcodeSpinner = findViewById(R.id.sp_setcode);
        categorySpinner = findViewById(R.id.sp_category);
        raceSpinner = findViewById(R.id.sp_race);
        levelSpinner = findViewById(R.id.sp_level);
        attributeSpinner = findViewById(R.id.sp_attribute);
        atkText = findViewById(R.id.edt_atk);
        defText = findViewById(R.id.edt_def);
        searchButton = findViewById(R.id.btn_search);
        resetButton = findViewById(R.id.btn_reset);
        layout_monster = findViewById(R.id.layout_monster);
        searchButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        limitListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long value = sel(limitListSpinner);
                if (value == 0) {
                    limitSpinner.setVisibility(View.INVISIBLE);
                } else {
                    limitSpinner.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long value = sel(typeSpinner);
                if (value == 0) {
                    layout_monster.setVisibility(View.INVISIBLE);
                    typeMonsterSpinner.setVisibility(View.GONE);
                    typeSTSpinner.setVisibility(View.INVISIBLE);
                    resetMonster();
                } else if (value == CardType.Spell.value() || value == CardType.Trap.value()) {
                    layout_monster.setVisibility(View.INVISIBLE);
                    typeMonsterSpinner.setVisibility(View.GONE);
                    typeSTSpinner.setVisibility(View.VISIBLE);
                    resetMonster();
                } else {
                    layout_monster.setVisibility(View.VISIBLE);
                    typeMonsterSpinner.setVisibility(View.VISIBLE);
                    typeSTSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onLoad(boolean ok) {

    }

    public void initItems() {
        initOtSpinners(otSpinner);
        initLimitSpinners(limitSpinner);
        initLimitListSpinners(limitListSpinner);
        initTypeSpinners(typeSpinner, new CardType[]{CardType.None, CardType.Monster, CardType.Spell, CardType.Trap});
        initTypeSpinners(typeMonsterSpinner, new CardType[]{CardType.None, CardType.Normal, CardType.Effect, CardType.Fusion, CardType.Ritual,
                                                            CardType.Synchro, CardType.Pendulum, CardType.Xyz, CardType.Spirit, CardType.Union,
                                                            CardType.Dual, CardType.Tuner, CardType.Flip, CardType.Toon, CardType.Token
        });
        initTypeSpinners(typeSTSpinner, new CardType[]{CardType.None, CardType.QuickPlay,
                                                       CardType.Continuous, CardType.Equip, CardType.Field, CardType.Counter
        });
        initLevelSpinners(levelSpinner);
        initAttributes(attributeSpinner);
        initRaceSpinners(raceSpinner);
        initSetNameSpinners(setcodeSpinner);
        initCategorySpinners(categorySpinner);
    }

    protected <T extends View> T findViewById(int id) {
        return (T) view.findViewById(id);
    }

    private void initOtSpinners(Spinner spinner) {
        CardOt[] ots = CardOt.values();
        List<SpItem> items = new ArrayList<>();
        items.add(new SpItem(0, getString(R.string.label_ot)));
        for (CardOt item : ots) {
            if (item.ordinal() != 0) {
                items.add(new SpItem(item.ordinal(), item.toString()));
            }
        }
        SpAdapter adapter = new SpAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    protected String getString(int id) {
        return mContext.getString(id);
    }

    private void initLimitSpinners(Spinner spinner) {
        LimitType[] eitems = LimitType.values();
        List<SpItem> items = new ArrayList<>();
        for (LimitType item : eitems) {
            long val = item.value();
            if (val == 0) {
                items.add(new SpItem(val, getString(R.string.label_limit)));
            } else {
                items.add(new SpItem(val, mStringManager.getLimitString(val)));
            }
        }
        SpAdapter adapter = new SpAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initLimitListSpinners(Spinner spinner) {
        List<SpItem> items = new ArrayList<>();
        Collection<Integer> ids = mLimitManager.getLists();
        items.add(new SpItem(0, getString(R.string.label_limitlist)));
        for (Integer id : ids) {
            LimitList list = mLimitManager.getLimit(id);
            items.add(new SpItem(id, list.getName()));
        }
        SpAdapter adapter = new SpAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initLevelSpinners(Spinner spinner) {
        List<SpItem> items = new ArrayList<>();
        for (int i = 0; i <= 13; i++) {
            if (i == 0) {
                items.add(new SpItem(i, getString(R.string.label_level)));
            } else {
                items.add(new SpItem(i, "" + i));
            }
        }
        SpAdapter adapter = new SpAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initSetNameSpinners(Spinner spinner) {
        Map<Long, String> setnames = mStringManager.getSetname();
        List<SpItem> items = new ArrayList<>();
        items.add(new SpItem(0, getString(R.string.label_set)));
        for (Map.Entry<Long, String> item : setnames.entrySet()) {
            items.add(new SpItem(item.getKey(), item.getValue()));
        }
        SpAdapter adapter = new SpAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initTypeSpinners(Spinner spinner, CardType[] eitems) {
        if (eitems == null) {
            eitems = CardType.values();
        }
        List<SpItem> items = new ArrayList<>();
        for (CardType item : eitems) {
            long val = item.value();
            if (val == 0) {
                items.add(new SpItem(val, getString(R.string.label_type)));
            } else {
                items.add(new SpItem(val, mStringManager.getTypeString(val)));
            }
        }
        SpAdapter adapter = new SpAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initAttributes(Spinner spinner) {
        CardAttribute[] attributes = CardAttribute.values();
        List<SpItem> items = new ArrayList<>();
        for (CardAttribute item : attributes) {
            long val = item.value();
            if (val == 0) {
                items.add(new SpItem(val, getString(R.string.label_attr)));
            } else {
                items.add(new SpItem(val, mStringManager.getAttributeString(val)));
            }
        }
        SpAdapter adapter = new SpAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initRaceSpinners(Spinner spinner) {
        CardRace[] attributes = CardRace.values();
        List<SpItem> items = new ArrayList<>();
        for (CardRace item : attributes) {
            long val = item.value();
            if (val == 0) {
                items.add(new SpItem(val, mContext.getString(R.string.label_race)));
            } else {
                items.add(new SpItem(val, mStringManager.getRaceString(val)));
            }
        }
        SpAdapter adapter = new SpAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initCategorySpinners(Spinner spinner) {
        CardCategory[] attributes = CardCategory.values();
        List<SpItem> items = new ArrayList<>();
        for (CardCategory item : attributes) {
            long val = item.value();
            if (val == 0) {
                items.add(new SpItem(val, mContext.getString(R.string.label_category)));
            } else {
                items.add(new SpItem(val, mStringManager.getCategoryString(val)));
            }
        }
        SpAdapter adapter = new SpAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    protected void reset(Spinner spinner) {
        if (spinner.getCount() > 0) {
            spinner.setSelection(0);
        }
    }

    protected String text(EditText editText) {
        CharSequence charSequence = editText.getText();
        if (charSequence == null) {
            return null;
        }
        return charSequence.toString();
    }

    protected long sel(Spinner spinner) {
        if (spinner.getCount() > 0) {
            Object item = spinner.getSelectedItem();
            if (item != null && item instanceof SpItem) {
                SpItem spItem = (SpItem) item;
                return spItem.value;
            }
        }
        return 0;
    }

    protected class SpItem {
        public long value;
        public String text;

        public SpItem(long value, String text) {
            this.value = value;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    protected class SpAdapter extends BaseAdapterPlus<SpItem> {
        public SpAdapter(Context context) {
            super(context);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            View view = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            view.setTag(textView);
            return view;
        }

        @Override
        protected void attach(View view, SpItem item, int position) {
            TextView textView = (TextView) view.getTag();
            if (item != null) {
                textView.setText(item.toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            onSearch();
        } else if (v == resetButton) {
            prefixWord.setText(null);
            suffixWord.setText(null);
            reset(otSpinner);
            reset(limitSpinner);
            reset(limitListSpinner);
            reset(typeSpinner);
            reset(typeSTSpinner);
            reset(setcodeSpinner);
            reset(categorySpinner);
            resetMonster();
        }
    }

    protected void onSearch(){
        if (drawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            drawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        }
        search();
    }

    public void onOpen(){

    }

    protected void search(){
        if (dataLoader != null) {
            dataLoader.search(text(prefixWord), text(suffixWord), sel(attributeSpinner)
                    , sel(levelSpinner), sel(raceSpinner), sel(limitListSpinner), sel(limitSpinner), text(atkText), text(defText), sel(setcodeSpinner)
                    , sel(categorySpinner), sel(otSpinner), sel(typeSpinner), sel(typeMonsterSpinner), sel(typeSTSpinner));
        }
    }

    protected void resetMonster() {
        reset(typeMonsterSpinner);
        reset(raceSpinner);
        reset(levelSpinner);
        reset(attributeSpinner);
        atkText.setText(null);
        defText.setText(null);
    }
}
