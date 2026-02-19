package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.events.RegisteredCEnchantmentEvent;
import com.badbones69.crazyenchantments.paper.api.events.UnregisterCEnchantmentEvent;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CEnchantment {

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

    private String name;
    private String customName;
    private boolean activated;
    private int maxLevel;
    private String infoName;
    private int chance;
    private int chanceIncrease;
    private int cooldown;
    private List<String> infoDescription;
    private final List<Category> categories;
    private EnchantmentType enchantmentType;
    private final List<EnchantmentType> enchantmentTypes;
    private final CEnchantment instance;
    private Sound sound;
    private List<String> conflicts;
    private List<Double> strengths;

    public CEnchantment(String name) {
        this.instance = this;
        this.name = name;
        this.customName = name;
        this.activated = true;
        this.maxLevel = 3;
        this.infoName = ColorUtils.color("&7" + name);
        this.chance = 0;
        this.chanceIncrease = 0;
        this.cooldown = 0;
        this.infoDescription = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.enchantmentType = null;
        this.enchantmentTypes = new ArrayList<>();
        this.sound = Sound.ENTITY_PLAYER_LEVELUP;
        this.conflicts = new ArrayList<>();
        this.strengths = new ArrayList<>();
    }

    public List<String> getConflicts() {
        return this.conflicts;
    }

    public CEnchantment setConflicts(List<String> conflicts) {
        this.conflicts = conflicts != null ? conflicts : new ArrayList<>();
        return this;
    }

    /**
     * Check if this enchantment conflicts with another enchantment.
     *
     * @param other The enchantment to check against
     * @return True if there is a conflict.
     */
    public boolean conflictsWith(CEnchantment other) {
        return hasConflict(other.getName());
    }

    /**
     * Check if this enchantment conflicts with a vanilla or third-party enchantment.
     *
     * @param other The enchantment to check against.
     * @return True if there is a conflict.
     */
    public boolean conflictsWith(Enchantment other) {
        if (other == null) return false;

        final String namespaced = other.getKey().toString();
        final String key = other.getKey().getKey();

        return hasConflict(namespaced) || hasConflict(key);
    }

    @NotNull
    public Sound getSound() {
        return this.sound;
    }
    public CEnchantment setSound(String soundString) {
        if (soundString == null || soundString.isBlank()) {
            this.sound = Sound.ENTITY_PLAYER_LEVELUP;
            return this;
        }

        try {
            this.sound = Sound.valueOf(soundString);
        } catch (IllegalArgumentException e) {
            //plugin.getLogger().warning(name + " has an invalid sound set.");
            this.sound = Sound.ENTITY_PLAYER_LEVELUP;
        }

        return this;
    }

    public String getName() {
        return this.name;
    }

    public CEnchantment setName(String name) {
        this.name = name;

        return this;
    }

    public String getCustomName() {
        return this.customName;
    }

    public CEnchantment setCustomName(String customName) {
        this.customName = customName;

        return this;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public CEnchantment setActivated(boolean activated) {
        this.activated = activated;

        return this;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public CEnchantment setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;

        return this;
    }

    public String getInfoName() {
        return this.infoName;
    }

    public CEnchantment setInfoName(String infoName) {
        this.infoName = ColorUtils.color(infoName);

        return this;
    }

    public int getChance() {
        return this.chance;
    }

    public CEnchantment setChance(int chance) {
        this.chance = chance;

        return this;
    }

    public int getChanceIncrease() {
        return this.chanceIncrease;
    }

    public CEnchantment setChanceIncrease(int chanceIncrease) {
        this.chanceIncrease = chanceIncrease;

        return this;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public CEnchantment setCooldown(int cooldown) {
        this.cooldown = Math.max(0, cooldown);

        return this;
    }

    public boolean hasChanceSystem() {
        return this.chance > 0;
    }

    public boolean chanceSuccessful(int enchantmentLevel) {
        return this.chanceSuccessful(enchantmentLevel, 1.0);
    }

    public boolean chanceSuccessful(int enchantmentLevel, double multiplier) {
        int newChance = this.chance + (this.chanceIncrease * (enchantmentLevel - 1));
        int pickedChance = this.methods.getRandomNumber (0, 100);

        newChance = (int) (newChance * multiplier);

        return newChance >= 100 || newChance <= 0 || pickedChance <= newChance;
    }

    public List<String> getInfoDescription() {
        return this.infoDescription;
    }

    public CEnchantment setInfoDescription(List<String> infoDescription) {
        List<String> info = new ArrayList<>();

        infoDescription.forEach(lore -> info.add(ColorUtils.color(lore)));

        this.infoDescription = info;

        return this;
    }

    public CEnchantment addCategory(Category category) {
        if (category != null) this.categories.add(category);

        return this;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public CEnchantment setCategories(List<String> categories) {

        for (String categoryString : categories) {
            Category category = this.enchantmentBookSettings.getCategory(categoryString);

            if (category != null) this.categories.add(category);
        }

        return this;
    }

    public EnchantmentType getEnchantmentType() {
        return this.enchantmentType != null ? this.enchantmentType : (!this.enchantmentTypes.isEmpty() ? this.enchantmentTypes.get(0) : null);
    }

    @NotNull
    public List<EnchantmentType> getEnchantmentTypes() {
        return this.enchantmentTypes;
    }

    /**
     * Checks if this cEnchantment may be applied to the given {@link
     * ItemStack}.
     *
     * @param item Item to test
     * @return True if the cEnchantment may be applied, otherwise False
     */
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return this.enchantmentTypes.stream().anyMatch(type -> type.canEnchantItem(item));
    }

    public CEnchantment setEnchantmentType(EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;
        this.enchantmentTypes.clear();
        if (enchantmentType != null) this.enchantmentTypes.add(enchantmentType);

        return this;
    }

    public CEnchantment setEnchantmentTypes(List<EnchantmentType> enchantmentTypes) {
        this.enchantmentTypes.clear();

        if (enchantmentTypes != null) {
            Set<EnchantmentType> uniqueTypes = new LinkedHashSet<>();
            enchantmentTypes.stream().filter(type -> type != null).forEach(uniqueTypes::add);
            this.enchantmentTypes.addAll(uniqueTypes);
        }

        this.enchantmentType = this.enchantmentTypes.isEmpty() ? null : this.enchantmentTypes.get(0);

        return this;
    }

    public CEnchantment setStrengths(List<Double> strengths) {
        this.strengths = strengths != null ? strengths : new ArrayList<>();
        return this;
    }

    @NotNull
    public List<Double> getStrengths() {
        return this.strengths;
    }

    /**
     * Resolves a configurable strength value for the provided level.
     * If no values are configured, the provided fallback value is returned.
     *
     * @param level Enchantment level.
     * @param fallbackValue Value to use if no strength values are configured.
     * @return The resolved strength value.
     */
    public double getStrengthAtLevel(int level, double fallbackValue) {
        if (this.strengths.isEmpty()) return fallbackValue;

        int index = Math.max(1, level) - 1;
        if (index >= this.strengths.size()) index = this.strengths.size() - 1;

        return this.strengths.get(index);
    }

    private boolean hasConflict(String otherName) {
        if (otherName == null || otherName.isBlank()) return false;

        String normalizedOtherFull = normalizeFull(otherName);
        String normalizedOtherSimple = normalizeSimple(otherName);

        for (String conflict : this.conflicts) {
            if (conflict == null || conflict.isBlank()) continue;

            String normalizedConflictFull = normalizeFull(conflict);
            String normalizedConflictSimple = normalizeSimple(conflict);

            if (normalizedConflictFull.contains(":")) {
                if (normalizedConflictFull.equals(normalizedOtherFull)) return true;
                continue;
            }

            if (normalizedConflictSimple.equals(normalizedOtherSimple)) return true;
        }

        return false;
    }

    private String normalizeFull(String input) {
        return input.toLowerCase(Locale.ROOT).trim().replace(" ", "");
    }

    private String normalizeSimple(String input) {
        String normalized = normalizeFull(input);

        if (normalized.contains(":")) {
            normalized = normalized.substring(normalized.indexOf(':') + 1);
        }

        return normalized.replace("-", "").replace("_", "");
    }

    public void registerEnchantment() {
        RegisteredCEnchantmentEvent event = new RegisteredCEnchantmentEvent(this.instance);
        this.plugin.getServer().getPluginManager().callEvent(event);
        this.crazyManager.registerEnchantment(this.instance);

        this.enchantmentTypes.forEach(type -> {
            if (!type.getEnchantments().contains(this.instance)) type.addEnchantment(this.instance);
        });

        this.categories.forEach(category -> category.addEnchantment(this.instance));
    }

    public void unregisterEnchantment() {
        UnregisterCEnchantmentEvent event = new UnregisterCEnchantmentEvent(this.instance);
        this.plugin.getServer().getPluginManager().callEvent(event);
        this.crazyManager.unregisterEnchantment(this.instance);

        this.enchantmentTypes.forEach(type -> type.removeEnchantment(this.instance));

        this.categories.forEach(category -> category.addEnchantment(this.instance));
    }
}
