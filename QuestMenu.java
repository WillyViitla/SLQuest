package com.slquest.menu;

import com.slquest.quests.QuestManager;
import com.slquest.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class QuestMenu {

    // Add QuestManager parameter
    public static void open(Player player, QuestManager manager) {
        Inventory inv = Bukkit.createInventory(null, 9, "§6Квесты");

        for (int i = 1; i <= 3; i++) {
            Quest quest = manager.getQuest(i);
            if (quest == null) continue;

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a" + quest.getName() + " §7(Тир " + quest.getTier() + ")");

            List<String> lore = new ArrayList<>();
            lore.add("§7Флаг: §f" + quest.getFlag());
            lore.add("§7Цель: §f" + quest.getTarget() + " x" + quest.getAmount());
            lore.add("§7Награда: §f" + quest.getRewardItem() + " x" + quest.getRewardAmount());
            meta.setLore(lore);

            item.setItemMeta(meta);
            inv.addItem(item);
        }

        player.openInventory(inv);
    }
}
