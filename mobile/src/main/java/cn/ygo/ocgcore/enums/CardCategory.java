package cn.ygo.ocgcore.enums;

public enum CardCategory {
    None(0),
    CATEGORY_DESTROY(0x1),//破坏效果
    CATEGORY_RELEASE(0x2),//解放效果
    CATEGORY_REMOVE(0x4),//除外效果
    CATEGORY_TOHAND(0x8),//加入手牌效果
    CATEGORY_TODECK(0x10),//回卡组效果
    CATEGORY_TOGRAVE(0x20),//送去墓地效果
    CATEGORY_DECKDES(0x40),//卡组破坏效果
    CATEGORY_HANDES(0x80),//手牌破坏效果
    CATEGORY_SUMMON(0x100),//含召唤的效果
    CATEGORY_SPECIAL_SUMMON(0x200),//含特殊召唤的效果
    CATEGORY_TOKEN(0x400),//含衍生物效果
    CATEGORY_FLIP(0x800),//含翻转效果
    CATEGORY_POSITION(0x1000),//改变表示形式效果
    CATEGORY_CONTROL(0x2000),//改变控制权效果
    CATEGORY_DISABLE(0x4000),//使效果无效效果
    CATEGORY_DISABLE_SUMMON(0x8000),//无效召唤效果
    CATEGORY_DRAW(0x10000),//抽卡效果
    CATEGORY_SEARCH(0x20000),//检索卡组效果
    CATEGORY_EQUIP(0x40000),//装备效果
    CATEGORY_DAMAGE(0x80000),//伤害效果
    CATEGORY_RECOVER(0x100000),//回复效果
    CATEGORY_ATKCHANGE(0x200000),//改变攻击效果
    CATEGORY_DEFCHANGE(0x400000),//改变防御效果
    CATEGORY_COUNTER(0x800000),//指示物效果
    CATEGORY_COIN(0x1000000),//硬币效果
    CATEGORY_DICE(0x2000000),//骰子效果
    CATEGORY_LEAVE_GRAVE(0x4000000),//离开墓地效果
    CATEGORY_LVCHANGE(0x8000000),//改变等级效果
    CATEGORY_NEGATE(0x10000000),//使发动无效效果
    CATEGORY_ANNOUNCE(0x20000000),//發動時宣言卡名的效果
    CATEGORY_FUSION_SUMMON(0x40000000);
    private long value = 0;

    private CardCategory(long value) {
        this.value = value;
    }

    public static CardCategory valueOf(long value) {
        CardCategory[] attributes = CardCategory.values();
        for (CardCategory attribute : attributes) {
            if (attribute.value() == value) {
                return attribute;
            }
        }
        return null;
    }

    public long value() {
        return this.value;
    }
}
