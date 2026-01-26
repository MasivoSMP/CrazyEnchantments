package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.Random;

public class FishingRodEnchantments implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final Random random = new Random();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTreasureHunt(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;

        Player player = event.getPlayer();
        ItemStack rod = this.methods.getItemInHand(player);
        if (rod == null || rod.getType() != Material.FISHING_ROD) return;

        Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(rod);

        if (!EnchantUtils.isEventActive(CEnchantments.TREASUREHUNT, player, rod, enchantments)) return;

        int level = enchantments.getOrDefault(CEnchantments.TREASUREHUNT.getEnchantment(), 0);
        if (level <= 0) return;

        ItemStack treasure = getTreasureItem(level);
        if (treasure == null) return;

        caught.setItemStack(treasure);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMultiLineCatch(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;

        Player player = event.getPlayer();
        ItemStack rod = this.methods.getItemInHand(player);
        if (rod == null || rod.getType() != Material.FISHING_ROD) return;

        Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(rod);

        if (!EnchantUtils.isEventActive(CEnchantments.MULTILINE, player, rod, enchantments)) return;

        int level = enchantments.getOrDefault(CEnchantments.MULTILINE.getEnchantment(), 0);
        if (level <= 0) return;

        ItemStack base = caught.getItemStack();

        for (int i = 0; i < level; i++) {
            ItemStack extra = base.clone();
            if (extra.getAmount() <= 0) extra.setAmount(1);
            caught.getWorld().dropItemNaturally(caught.getLocation(), extra);
        }
    }

    @Nullable
    private ItemStack getTreasureItem(int level) {
        Map<Material, Map<Integer, Double>> pool = this.crazyManager.getTreasureHuntItemPool();

        if (pool.isEmpty()) return null;

        double totalChance = 0D;

        for (Map.Entry<Material, Map<Integer, Double>> entry : pool.entrySet()) {
            double chance = entry.getValue().getOrDefault(level, 0D);
            if (chance > 0D) totalChance += chance;
        }

        if (totalChance <= 0D) return null;

        double roll = this.random.nextDouble() * 100D;
        if (roll > totalChance) return null;

        double pick = this.random.nextDouble() * totalChance;
        double running = 0D;

        for (Map.Entry<Material, Map<Integer, Double>> entry : pool.entrySet()) {
            double chance = entry.getValue().getOrDefault(level, 0D);
            if (chance <= 0D) continue;
            running += chance;
            if (pick <= running) return new ItemStack(entry.getKey());
        }

        return null;
    }
}
