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
    private final Map<Integer, List<Quest>> allQuests = new HashMap<>();
    private final File dataFile;
    private final YamlConfiguration data;

    private final Map<Player, Quest> activeQuestsByPlayer = new HashMap<>();

    public void assignQuestToPlayer(Player player, Quest quest) {
        activeQuestsByPlayer.put(player, quest);
    }

    public Quest getActiveQuest(Player player) {
        return activeQuestsByPlayer.get(player);
    }

    public QuestManager(SLQuest plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "quests.yml");
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void setQuest(Quest quest) {
        activeQuests.put(quest.getTier(), quest);
        saveData();
    }

    public Quest getQuestByDisplayName(String displayName) {
        for (Quest quest : activeQuests.values()) {
            if (("§e" + quest.getName()).equals(displayName)) return quest;
        }
        return null;
    }

    public void addQuestToPool(Quest quest) {
        allQuests.computeIfAbsent(quest.getTier(), k -> new ArrayList<>()).add(quest);
        saveData(); // persist later if you want
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
        for (int tier = 1; tier <= 3; tier++) {
            rerollQuest(null, tier);
        }
        saveData();
    }

    public void rerollQuest(Player player, int tier) {
        List<Quest> pool = allQuests.getOrDefault(tier, Collections.emptyList());
        if (pool.isEmpty()) {
            if (player != null) player.sendMessage("§cНет доступных квестов для тира " + tier + "!");
            return;
        }
        Quest newQ = pool.get(new Random().nextInt(pool.size()));
        setQuest(newQ);  // <--- must put into activeQuests
        if (player != null) {
            player.sendMessage("§aВам доступен новый квест тира " + tier + "!");
        }
    }


    public Map<Integer, Quest> getActiveQuests() {
        return activeQuests;
    }

    public void saveData() {
        try {
            data.set("quests", null);
            for (Map.Entry<Integer, Quest> entry : activeQuests.entrySet()) {
                String path = "quests." + entry.getKey();
                saveQuest(path, entry.getValue());
            }

            // save quest pool
            data.set("questpool", null);
            for (Map.Entry<Integer, List<Quest>> entry : allQuests.entrySet()) {
                int tier = entry.getKey();
                int index = 0;
                for (Quest q : entry.getValue()) {
                    String path = "questpool." + tier + "." + index;
                    saveQuest(path, q);
                    index++;
                }
            }

            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveQuest(String path, Quest q) {
        data.set(path + ".name", q.getName());
        data.set(path + ".flag", q.getFlag());
        data.set(path + ".target", q.getTarget());
        data.set(path + ".amount", q.getAmount());
        data.set(path + ".rewardItem", q.getRewardItem());
        data.set(path + ".rewardAmount", q.getRewardAmount());
        data.set(path + ".durationDays", q.getDurationDays());
        data.set(path + ".startTime", q.getAdditionalData()
                .getOrDefault("startTime", String.valueOf(System.currentTimeMillis())));
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
        allQuests.clear();

        // load active quests
        if (data.contains("quests")) {
            for (String key : data.getConfigurationSection("quests").getKeys(false)) {
                String path = "quests." + key;
                Quest q = loadQuest(path, Integer.parseInt(key));
                activeQuests.put(q.getTier(), q);
            }
        }

        // load quest pool
        if (data.contains("questpool")) {
            for (String tierKey : data.getConfigurationSection("questpool").getKeys(false)) {
                int tier = Integer.parseInt(tierKey);
                List<Quest> pool = new ArrayList<>();
                for (String idx : data.getConfigurationSection("questpool." + tierKey).getKeys(false)) {
                    String path = "questpool." + tierKey + "." + idx;
                    pool.add(loadQuest(path, tier));
                }
                allQuests.put(tier, pool);
            }
        }

        // ensure actives exist
        for (int tier = 1; tier <= 3; tier++) {
            if (!activeQuests.containsKey(tier) && allQuests.containsKey(tier) && !allQuests.get(tier).isEmpty()) {
                Quest fallback = allQuests.get(tier).get(0); // pick first (or random)
                activeQuests.put(tier, fallback);
            }
        }
    }

    private Quest loadQuest(String path, int tier) {
        Quest q = new Quest(
            tier,
            data.getString(path + ".name"),
            data.getString(path + ".flag"),
            data.getString(path + ".target"),
            data.getInt(path + ".amount"),
            data.getString(path + ".rewardItem"),
            data.getInt(path + ".rewardAmount"),
            data.getLong(path + ".durationDays")
        );
        q.setData("startTime", data.getString(path + ".startTime", String.valueOf(System.currentTimeMillis())));
        return q;
    }
}
