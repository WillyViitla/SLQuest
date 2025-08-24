package com.slquest;

import com.slquest.commands.*;
import com.slquest.listeners.QuestListener;
import com.slquest.menu.QuestMenu;
import com.slquest.quests.QuestManager;
import com.slquest.tasks.QuestRerollTask;

import org.bukkit.plugin.java.JavaPlugin;

public class SLQuest extends JavaPlugin {

    private static SLQuest instance;
    private QuestManager questManager;

    @Override
    public void onEnable() {
        instance = this;
        this.questManager = new QuestManager(this);

        getCommand("quest").setExecutor(new QuestCommand(this));
        getCommand("questcreate").setExecutor(new QuestCreateCommand(questManager));
        getCommand("questflags").setExecutor(new QuestFlagsCommand());
        getCommand("questreroll").setExecutor(new QuestRerollCommand(questManager));

        getCommand("quest").setExecutor(new QuestCommand(this));
        getServer().getPluginManager().registerEvents(new QuestListener(questManager), this);

        questManager.loadData();

        getLogger().info("SLQuest включен!");

        QuestRerollTask rerollTask = new QuestRerollTask(questManager);
        rerollTask.startTask();
    }

    @Override
    public void onDisable() {
        questManager.saveData();
        getLogger().info("SLQuest выключен.");
    }

    public static SLQuest getInstance() {
        return instance;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }
}
