package com.slquest.listeners;

import com.slquest.SLQuest;
import com.slquest.quests.Quest;
import com.slquest.quests.QuestManager;
import com.slquest.quests.QuestProgress;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;

import java.util.UUID;

public class QuestListener implements Listener {

    private final QuestManager manager;

    public QuestListener(QuestManager manager) {
        this.manager = manager;
    }

    private void sendProgress(Player player, Quest quest, QuestProgress progress) {
        player.sendMessage("§aПрогресс квеста §f" + quest.getName() + "§a: " +
                progress.getProgress() + "/" + quest.getAmount());
        if (progress.isCompleted()) {
            manager.completeQuest(player);
        }
    }

    // MINE / BREAK
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        QuestProgress progress = manager.getPlayerQuest(player.getUniqueId());
        if (progress == null) return;
        Quest quest = progress.getQuest();
        Block block = e.getBlock();
        if ((quest.getFlag().equals("mine") || quest.getFlag().equals("break")) &&
                block.getType().name().equalsIgnoreCase(quest.getTarget())) {
            progress.addProgress(1);
            sendProgress(player, quest, progress);
        }
    }

    // COLLECT
    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        QuestProgress progress = manager.getPlayerQuest(player.getUniqueId());
        if (progress == null) return;
        Quest quest = progress.getQuest();
        ItemStack item = e.getItem().getItemStack();
        if (quest.getFlag().equals("collect") &&
                item.getType().name().equalsIgnoreCase(quest.getTarget())) {
            progress.addProgress(item.getAmount());
            sendProgress(player, quest, progress);
        }
    }

    // KILL
    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        if (!(e.getEntity().getKiller() instanceof Player)) return;
        Player player = (Player) e.getEntity().getKiller();
        QuestProgress progress = manager.getPlayerQuest(player.getUniqueId());
        if (progress == null) return;
        Quest quest = progress.getQuest();
        if (quest.getFlag().equals("kill") &&
                e.getEntityType().name().equalsIgnoreCase(quest.getTarget())) {
            progress.addProgress(1);
            sendProgress(player, quest, progress);
        }
    }

    // ENCHANT
    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Player player = e.getEnchanter();
        QuestProgress progress = manager.getPlayerQuest(player.getUniqueId());
        if (progress == null) return;
        Quest quest = progress.getQuest();
        if (quest.getFlag().startsWith("enchant")) {
            String enchantTarget = quest.getTarget();
            for (Enchantment enc : e.getEnchantsToAdd().keySet()) {
                if (enc.getName().equalsIgnoreCase(enchantTarget)) {
                    progress.addProgress(1);
                    sendProgress(player, quest, progress);
                }
            }
        }
    }

    // ITEM_BREAK
    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent e) {
        Player player = e.getPlayer();
        QuestProgress progress = manager.getPlayerQuest(player.getUniqueId());
        if (progress == null) return;
        Quest quest = progress.getQuest();
        if (quest.getFlag().equals("item_break") &&
                e.getBrokenItem().getType().name().equalsIgnoreCase(quest.getTarget())) {
            progress.addProgress(1);
            sendProgress(player, quest, progress);
        }
    }

    // LOCATION & BIOME
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        QuestProgress progress = manager.getPlayerQuest(player.getUniqueId());
        if (progress == null) return;
        Quest quest = progress.getQuest();
        Location loc = player.getLocation();

        // LOCATION
        if (quest.getFlag().equals("location")) {
            String[] parts = quest.getTarget().split(",");
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);
            Location target = new Location(player.getWorld(), x, y, z);
            if (loc.distanceSquared(target) <= 15*15) {
                progress.addProgress(1);
                sendProgress(player, quest, progress);
            }
        }

        // BIOME
        if (quest.getFlag().equals("biome")) {
            if (loc.getBlock().getBiome().name().equalsIgnoreCase(quest.getTarget())) {
                progress.addProgress(1);
                sendProgress(player, quest, progress);
            }
        }
    }

    // TIME Quest Scheduler
    public void startTimeQuestScheduler() {
        Bukkit.getScheduler().runTaskTimer(SLQuest.getInstance(), () -> {
            for (UUID uuid : manager.getPlayersWithActiveQuests()) {
                QuestProgress progress = manager.getPlayerQuest(uuid);
                if (progress == null) continue;
                Quest quest = progress.getQuest();
                if (quest.getFlag().equals("time")) {
                    progress.addProgress(1); // 1 min
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && progress.getProgress()%15==0) {
                        player.sendMessage("§aПрогресс квеста " + quest.getName() + ": " +
                                progress.getProgress() + "/" + quest.getAmount() + " минут");
                    }
                    if (progress.isCompleted() && player!=null) manager.completeQuest(player);
                }
            }
        }, 20*60, 20*60);
    }
}
