package com.slquest.commands;

import com.slquest.quests.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestRerollCommand implements CommandExecutor {

    private final QuestManager manager;

    public QuestRerollCommand(QuestManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("slquest.admin")) return true;

        if (args.length == 0) {
            manager.rerollAllQuests();
            sender.sendMessage("§aВсе квесты были сброшены!");
        } else if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cИгрок не найден!");
                return true;
            }
            int tier = Integer.parseInt(args[1]);
            manager.rerollQuest(target, tier);
            sender.sendMessage("§aКвест тира " + tier + " для " + target.getName() + " был сброшен!");
        }

        return true;
    }
}
