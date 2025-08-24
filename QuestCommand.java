package com.slquest.commands;

import com.slquest.SLQuest;
import com.slquest.menu.QuestMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestCommand implements CommandExecutor {

    private final SLQuest plugin;

    public QuestCommand(SLQuest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cЭта команда только для игроков!");
            return true;
        }

        QuestMenu.open(player, plugin.getQuestManager());
        return true;
    }
}
