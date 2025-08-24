package com.slquest.tasks;

import com.slquest.SLQuest;
import com.slquest.quests.Quest;
import com.slquest.quests.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestRerollTask implements Runnable {

    private final QuestManager manager;
    private final Plugin plugin;

    public QuestRerollTask(QuestManager manager) {
        this.manager = manager;
        this.plugin = SLQuest.getInstance();
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        List<Integer> expiredTiers = new ArrayList<>();

        // Find expired quests
        for (Map.Entry<Integer, Quest> entry : manager.getActiveQuests().entrySet()) {
            Quest quest = entry.getValue();
            long questStartTime = quest.getAdditionalData().containsKey("startTime") ?
                    Long.parseLong(quest.getAdditionalData().get("startTime")) :
                    now; // if no startTime, use now
            long durationMs = quest.getDurationDays() * 24 * 60 * 60 * 1000L;

            if (now - questStartTime >= durationMs) {
                expiredTiers.add(entry.getKey());
            }
        }

        // Remove expired quests and optionally generate new ones
        for (int tier : expiredTiers) {
            manager.getActiveQuests().remove(tier);
            plugin.getLogger().info("§eКвест тира " + tier + " истёк и был сброшен.");
            // Optionally: create a new random quest for this tier here
        }

        manager.saveData();
    }

    // Utility to start the scheduled task
    public void startTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, this, 20L * 60 * 5, 20L * 60 * 5);
        plugin.getLogger().info("QuestRerollTask запущен.");
    }
}
