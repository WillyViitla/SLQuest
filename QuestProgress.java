package com.slquest.quests;

import java.util.UUID;

public class QuestProgress {

    private final UUID playerId;
    private final Quest quest;
    private int progress;
    private boolean completed;

    public QuestProgress(UUID playerId, Quest quest) {
        this.playerId = playerId;
        this.quest = quest;
        this.progress = 0;
        this.completed = false;
    }

    public UUID getPlayerId() { return playerId; }
    public Quest getQuest() { return quest; }
    public int getProgress() { return progress; }
    public boolean isCompleted() { return completed; }

    public void addProgress(int amount) {
        if (completed) return;
        progress += amount;
        if (progress >= quest.getAmount()) completed = true;
    }
}
