package com.slquest.commands;

import com.slquest.quests.Quest;
import com.slquest.quests.QuestManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestCreateCommand implements CommandExecutor {

    private final QuestManager manager;

    public QuestCreateCommand(QuestManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("slquest.admin")) {
            sender.sendMessage("§cУ вас нет прав для этой команды!");
            return true;
        }

        if (args.length < 7) {
            sender.sendMessage("§eИспользование: /questcreate <tier> <название> <флаг> <цель> <кол-во> <наградаМатериал> <наградаКол-во>");
            return true;
        }

        try {
            int tier = Integer.parseInt(args[0]);
            String name = args[1];
            String flag = args[2];
            String target = args[3];
            int amount = Integer.parseInt(args[4]);
            String rewardItem = args[5].toUpperCase();
            int rewardAmount = Integer.parseInt(args[6]);

            if (Material.matchMaterial(rewardItem) == null) {
                sender.sendMessage("§cНеверный предмет награды: " + rewardItem);
                return true;
            }

            long durationDays = switch (tier) {
                case 1 -> 1;
                case 2 -> 3;
                case 3 -> 7;
                default -> 1;
            };

            Quest quest = new Quest(tier, name, flag, target, amount, rewardItem, rewardAmount, durationDays);
            manager.setQuest(quest);

            sender.sendMessage("§aКвест создан!");
            sender.sendMessage("§7Тир: §f" + tier);
            sender.sendMessage("§7Название: §f" + name);
            sender.sendMessage("§7Флаг: §f" + flag);
            sender.sendMessage("§7Цель: §f" + target + " x" + amount);
            sender.sendMessage("§7Награда: §f" + rewardItem + " x" + rewardAmount);
            sender.sendMessage("§7Время: §f" + durationDays + " дн.");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cНеверный формат числа!");
        }

        return true;
    }
}
