package com.slquest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class QuestFlagsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6Доступные флаги квестов:");
        sender.sendMessage("§7mine - добыть блок");
        sender.sendMessage("§7break - сломать блок");
        sender.sendMessage("§7collect - собрать предмет");
        sender.sendMessage("§7kill - убить моба");
        sender.sendMessage("§7enchant - зачаровать предмет");
        sender.sendMessage("§7villager_trade - торг с жителем");
        sender.sendMessage("§7carve - вырезать тыкву");
        sender.sendMessage("§7location - добраться до координат");
        sender.sendMessage("§7time - провести время на сервере");
        sender.sendMessage("§7biome - посетить биом");
        sender.sendMessage("§7item_break - сломать предмет");
        sender.sendMessage("§7random_get - получить случайный предмет");
        return true;
    }
}
