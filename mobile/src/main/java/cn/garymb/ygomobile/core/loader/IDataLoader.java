package cn.garymb.ygomobile.core.loader;

public interface IDataLoader {
    void setCallBack(ILoadCallBack loadCallBack);
    void loadData();
    ILoadCallBack getCallBack();
}
