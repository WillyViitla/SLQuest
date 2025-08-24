package com.slquest.quests;

import com.slquest.SLQuest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class QuestManager {

    private final SLQuest plugin;
    private final Map<Integer, Quest> activeQuests = new HashMap<>();
    private final Map<UUID, QuestProgress> playerQuests = new HashMap<>();
    private final File dataFile;
    private final YamlConfiguration data;

    public QuestManager(SLQuest plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "quests.yml");
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void setQuest(Quest quest) {
        activeQuests.put(quest.getTier(), quest);
        saveData();
    }

    public Quest getQuest(int tier) {
        return activeQuests.get(tier);
    }

    public void assignQuest(Player player, int tier) {
        Quest quest = activeQuests.get(tier);
        if (quest == null) return;
        QuestProgress progress = new QuestProgress(player.getUniqueId(), quest);
        playerQuests.put(player.getUniqueId(), progress);
        player.sendMessage("§aВы взяли квест: §f" + quest.getName());
    }

    public QuestProgress getPlayerQuest(UUID playerId) {
        return playerQuests.get(playerId);
    }

    public Set<UUID> getPlayersWithActiveQuests() {
        return playerQuests.keySet();
    }

    public void completeQuest(Player player) {
        QuestProgress progress = playerQuests.get(player.getUniqueId());
        if (progress == null || !progress.isCompleted()) return;
        Quest quest = progress.getQuest();
        Material mat = Material.matchMaterial(quest.getRewardItem());
        if (mat != null) {
            player.getInventory().addItem(new org.bukkit.inventory.ItemStack(mat, quest.getRewardAmount()));
            player.sendMessage("§aКвест завершён! Вы получили " + quest.getRewardItem() + " x" + quest.getRewardAmount());
        }
        playerQuests.remove(player.getUniqueId());
    }

    public void rerollAllQuests() {
        activeQuests.clear();
        saveData();
    }

    public void rerollQuest(Player player, int tier) {
        activeQuests.remove(tier);
        saveData();
    }

    public Map<Integer, Quest> getActiveQuests() {
        return activeQuests;
    }

    public void saveData() {
        try {
            data.set("quests", null);
            for (Map.Entry<Integer, Quest> entry : activeQuests.entrySet()) {
                Quest q = entry.getValue();
                String path = "quests." + entry.getKey();
                data.set(path + ".name", q.getName());
                data.set(path + ".flag", q.getFlag());
                data.set(path + ".target", q.getTarget());
                data.set(path + ".amount", q.getAmount());
                data.set(path + ".rewardItem", q.getRewardItem());
                data.set(path + ".rewardAmount", q.getRewardAmount());
                data.set(path + ".durationDays", q.getDurationDays());
            }
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void loadData() {
        activeQuests.clear();
        if (!data.contains("quests")) return;
        for (String key : data.getConfigurationSection("quests").getKeys(false)) {
            String path = "quests." + key;
            Quest q = new Quest(
                    Integer.parseInt(key),
                    data.getString(path + ".name"),
                    data.getString(path + ".flag"),
                    data.getString(path + ".target"),
                    data.getInt(path + ".amount"),
                    data.getString(path + ".rewardItem"),
                    data.getInt(path + ".rewardAmount"),
                    data.getLong(path + ".durationDays")
            );
            activeQuests.put(q.getTier(), q);
        }
    }
}
