package cn.garymb.ygomobile.core;

public interface ICardLoader extends IDataLoader{
    void loadString();
    void search(String prefixWord, String suffixWord,
                long attribute, long level, String atk, String def, long setcode, long category, long ot, long... types);
}
