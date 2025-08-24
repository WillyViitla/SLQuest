package com.slquest.quests;

import java.util.HashMap;
import java.util.Map;

public class Quest {

    private final int tier;
    private final String name;
    private final String flag;
    private final String target;
    private final int amount;
    private final String rewardItem;
    private final int rewardAmount;
    private final long durationDays;
    private final Map<String, String> additionalData = new HashMap<>();

    public Quest(int tier, String name, String flag, String target, int amount,
                 String rewardItem, int rewardAmount, long durationDays) {
        this.tier = tier;
        this.name = name;
        this.flag = flag;
        this.target = target;
        this.amount = amount;
        this.rewardItem = rewardItem;
        this.rewardAmount = rewardAmount;
        this.durationDays = durationDays;
    }

    public int getTier() { return tier; }
    public String getName() { return name; }
    public String getFlag() { return flag; }
    public String getTarget() { return target; }
    public int getAmount() { return amount; }
    public String getRewardItem() { return rewardItem; }
    public int getRewardAmount() { return rewardAmount; }
    public long getDurationDays() { return durationDays; }
    public Map<String, String> getAdditionalData() { return additionalData; }

    public void setData(String key, String value) { additionalData.put(key, value); }
}
