package com.slquest.listeners;

import com.slquest.quests.Quest;
import com.slquest.quests.QuestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class QuestGuiListener implements Listener {

    private final QuestManager questManager;

    public QuestGuiListener(QuestManager questManager) {
        this.questManager = questManager;
    }

    @EventHandler
    public void onQuestGuiClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equals("§6§lКвесты")) return; // Only your quest GUI

        e.setCancelled(true); // Prevent taking the item

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String displayName = meta.getDisplayName();
        Quest clickedQuest = questManager.getQuestByDisplayName(displayName); // Implement this

        if (clickedQuest != null) {
            questManager.assignQuestToPlayer(player, clickedQuest);
            player.sendMessage("§aВы приняли квест: §f" + clickedQuest.getName());

            player.closeInventory(); // optional
        }
    }
}
