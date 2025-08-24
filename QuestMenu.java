package com.slquest.menu;

import com.slquest.quests.Quest;
import com.slquest.quests.QuestManager;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QuestMenu {

    private final QuestManager manager;

    public QuestMenu(QuestManager manager) {
        this.manager = manager;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "§6§lКвесты");

        for (int tier = 1; tier <= 3; tier++) {
            Quest quest = manager.getQuest(tier);
            if (quest == null) continue;

            ItemStack display = new ItemStack(Material.BOOK);
            ItemMeta meta = display.getItemMeta();

            // Title = Tier
            meta.setDisplayName("§e§lКвест Тир " + tier);

            // Lore
            List<String> lore = new ArrayList<>();
            lore.addAll(formatQuestLore(quest));

            meta.setLore(lore);
            display.setItemMeta(meta);

            inv.setItem(tier - 1, display);
        }

        player.openInventory(inv);
    }

    private List<String> formatQuestLore(Quest quest) {
        List<String> lore = new ArrayList<>();

        // Translate action
        String action = switch (quest.getFlag()) {
            case "mine" -> "§7Добыть";
            case "kill" -> "§7Убить";
            case "collect" -> "§7Собрать";
            default -> quest.getFlag();
        };

        // Translate target
        String targetName = quest.getTarget();
        try {
            if (quest.getFlag().equals("kill")) {
                EntityType type = EntityType.valueOf(quest.getTarget());
                targetName = new TranslatableComponent(type.getTranslationKey()).toLegacyText();
            } else {
                Material mat = Material.valueOf(quest.getTarget());
                targetName = new TranslatableComponent(mat.getTranslationKey()).toLegacyText();
            }
        } catch (Exception ignored) {}

        // Translate reward
        String rewardName = quest.getRewardItem();
        try {
            Material mat = Material.matchMaterial(quest.getRewardItem());
            if (mat != null) {
                rewardName = new TranslatableComponent(mat.getTranslationKey()).toLegacyText();
            }
        } catch (Exception ignored) {}

        // Duration left (in days)
        long startTime = Long.parseLong(quest.getAdditionalData()
                .getOrDefault("startTime", String.valueOf(System.currentTimeMillis())));
        long elapsed = (System.currentTimeMillis() - startTime) / (1000L * 60 * 60 * 24);
        long left = Math.max(0, quest.getDurationDays() - elapsed);

        lore.add("§f" + action + ": §e" + targetName + " §fx" + quest.getAmount());
        lore.add("");
        lore.add("§aНаграда: §f" + rewardName + " §fx" + quest.getRewardAmount());
        lore.add("§7Осталось дней: §f" + left);

        return lore;
    }
}
