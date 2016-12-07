package cn.garymb.ygomobile.core;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.core.loader.ICardLoader;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.spinner.SimpleSpinnerAdapter;
import cn.garymb.ygomobile.plus.spinner.SimpleSpinnerItem;
import cn.ygo.ocgcore.CardSet;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.LimitManager;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.CardAttribute;
import cn.ygo.ocgcore.enums.CardCategory;
import cn.ygo.ocgcore.enums.CardOt;
import cn.ygo.ocgcore.enums.CardRace;
import cn.ygo.ocgcore.enums.CardType;
import cn.ygo.ocgcore.enums.LimitType;

public class CardSearcher implements View.OnClickListener {
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
    private Spinner pScale;
    private Button searchButton;
    private Button resetButton;
    private View view;
    private View layout_monster;
    private ICardLoader dataLoader;
    private Context mContext;
    protected StringManager mStringManager;
    protected LimitManager mLimitManager;
    protected AppsSettings mSettings;

    public CardSearcher(View view, ICardLoader dataLoader) {
        this.view = view;
        this.mContext = view.getContext();
        this.dataLoader = dataLoader;
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
        pScale = findViewById(R.id.sp_scale);
        searchButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        limitListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long value = getSelect(limitListSpinner);
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
                long value = getSelect(typeSpinner);
                if (value == 0) {
                    layout_monster.setVisibility(View.INVISIBLE);
                    typeMonsterSpinner.setVisibility(View.GONE);
                    typeSTSpinner.setVisibility(View.INVISIBLE);
                    pScale.setVisibility(View.INVISIBLE);
                    resetMonster();
                } else if (value == CardType.Spell.value() || value == CardType.Trap.value()) {
                    layout_monster.setVisibility(View.INVISIBLE);
                    typeMonsterSpinner.setVisibility(View.GONE);
                    typeSTSpinner.setVisibility(View.VISIBLE);
                    pScale.setVisibility(View.INVISIBLE);
                    resetMonster();
                } else {
                    layout_monster.setVisibility(View.VISIBLE);
                    typeMonsterSpinner.setVisibility(View.VISIBLE);
                    typeSTSpinner.setVisibility(View.GONE);
                    pScale.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        initTypeSpinners(typeSTSpinner, new CardType[]{CardType.None, CardType.Normal, CardType.QuickPlay,
                                                       CardType.Continuous, CardType.Equip, CardType.Field, CardType.Counter
        });
        initLevelSpinners(levelSpinner);
        initPscaleSpinners(pScale);
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
        List<SimpleSpinnerItem> items = new ArrayList<>();
        items.add(new SimpleSpinnerItem(0, getString(R.string.label_ot)));
        for (CardOt item : ots) {
            if (item.ordinal() != 0) {
                items.add(new SimpleSpinnerItem(item.ordinal(), item.toString()));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    protected String getString(int id) {
        return mContext.getString(id);
    }

    private void initLimitSpinners(Spinner spinner) {
        LimitType[] eitems = LimitType.values();
        List<SimpleSpinnerItem> items = new ArrayList<>();
        for (LimitType item : eitems) {
            long val = item.value();
            if (val == 0) {
                items.add(new SimpleSpinnerItem(val, getString(R.string.label_limit)));
            } else if (val == LimitType.All.value()) {
                items.add(new SimpleSpinnerItem(val, getString(R.string.all)));
            } else {
                items.add(new SimpleSpinnerItem(val, mStringManager.getLimitString(val)));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initLimitListSpinners(Spinner spinner) {
        List<SimpleSpinnerItem> items = new ArrayList<>();
        List<Integer> ids = mLimitManager.getLists();
        items.add(new SimpleSpinnerItem(0, getString(R.string.label_limitlist)));
        int index = -1;
        int i = 0;
        for (Integer id : ids) {
            LimitList list = mLimitManager.getLimitFromIndex(id);
            if (dataLoader != null && list == dataLoader.getLimitList()) {
                index = i;
            }
            items.add(new SimpleSpinnerItem(id, list.getName()));
            i++;
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
        if (index >= 0) {
            spinner.setSelection(index);
        }
    }

    private void initPscaleSpinners(Spinner spinner) {
        List<SimpleSpinnerItem> items = new ArrayList<>();
        for (int i = 0; i <= 13; i++) {
            if (i == 0) {
                items.add(new SimpleSpinnerItem(i, getString(R.string.label_pendulum)));
            } else {
                items.add(new SimpleSpinnerItem(i, "" + i));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initLevelSpinners(Spinner spinner) {
        List<SimpleSpinnerItem> items = new ArrayList<>();
        for (int i = 0; i <= 13; i++) {
            if (i == 0) {
                items.add(new SimpleSpinnerItem(i, getString(R.string.label_level)));
            } else {
                items.add(new SimpleSpinnerItem(i, "" + i));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initSetNameSpinners(Spinner spinner) {
        List<CardSet> setnames = mStringManager.getCardSets();
        List<SimpleSpinnerItem> items = new ArrayList<>();
        items.add(new SimpleSpinnerItem(0, getString(R.string.label_set)));
        for (CardSet set : setnames) {
            items.add(new SimpleSpinnerItem(set.getCode(), set.getName()));
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initTypeSpinners(Spinner spinner, CardType[] eitems) {
        if (eitems == null) {
            eitems = CardType.values();
        }
        List<SimpleSpinnerItem> items = new ArrayList<>();
        for (CardType item : eitems) {
            long val = item.value();
            if (val == 0) {
                items.add(new SimpleSpinnerItem(val, getString(R.string.label_type)));
            } else {
                items.add(new SimpleSpinnerItem(val, mStringManager.getTypeString(val)));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initAttributes(Spinner spinner) {
        CardAttribute[] attributes = CardAttribute.values();
        List<SimpleSpinnerItem> items = new ArrayList<>();
        for (CardAttribute item : attributes) {
            long val = item.value();
            if (val == 0) {
                items.add(new SimpleSpinnerItem(val, getString(R.string.label_attr)));
            } else {
                items.add(new SimpleSpinnerItem(val, mStringManager.getAttributeString(val)));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initRaceSpinners(Spinner spinner) {
        CardRace[] attributes = CardRace.values();
        List<SimpleSpinnerItem> items = new ArrayList<>();
        for (CardRace item : attributes) {
            long val = item.value();
            if (val == 0) {
                items.add(new SimpleSpinnerItem(val, mContext.getString(R.string.label_race)));
            } else {
                items.add(new SimpleSpinnerItem(val, mStringManager.getRaceString(val)));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void initCategorySpinners(Spinner spinner) {
        CardCategory[] attributes = CardCategory.values();
        List<SimpleSpinnerItem> items = new ArrayList<>();
        for (CardCategory item : attributes) {
            long val = item.value();
            if (val == 0) {
                items.add(new SimpleSpinnerItem(val, mContext.getString(R.string.label_category)));
            } else {
                items.add(new SimpleSpinnerItem(val, mStringManager.getCategoryString(val)));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(mContext);
        adapter.set(items);
        spinner.setAdapter(adapter);
    }

    private void reset(Spinner spinner) {
        if (spinner.getCount() > 0) {
            spinner.setSelection(0);
        }
    }

    private long getSelect(Spinner spinner) {
        return SimpleSpinnerAdapter.getSelect(spinner);
    }

    protected String text(EditText editText) {
        CharSequence charSequence = editText.getText();
        if (charSequence == null) {
            return null;
        }
        return charSequence.toString();
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            search();
        } else if (v == resetButton) {
            resetAll();
        }
    }

    private void search() {
        if (dataLoader != null) {
            dataLoader.search(text(prefixWord), text(suffixWord), getSelect(attributeSpinner)
                    , getSelect(levelSpinner), getSelect(raceSpinner), getSelect(limitListSpinner), getSelect(limitSpinner), text(atkText), text(defText),
                    getSelect(pScale),
                    getSelect(setcodeSpinner)
                    , getSelect(categorySpinner), getSelect(otSpinner), getSelect(typeSpinner), getSelect(typeMonsterSpinner), getSelect(typeSTSpinner));
        }
    }

    private void resetAll() {
        if (dataLoader != null) {
            dataLoader.onReset();
        }
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

    private void resetMonster() {
        reset(pScale);
        reset(typeMonsterSpinner);
        reset(raceSpinner);
        reset(levelSpinner);
        reset(attributeSpinner);
        atkText.setText(null);
        defText.setText(null);
    }
}
