package com.gameservergroup.gsgcore.items;

import com.gameservergroup.gsgcore.utils.NBTItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustomItem {

    private static HashMap<String, CustomItem> customItems = new HashMap<>();
    private String name;
    private ItemStack itemStack;
    private EnumSet<CustomItemType> customItemTypes;

    private CustomItem(String name, ItemStack itemStack, CustomItemType... customItemTypes) {
        this.name = name;
        this.itemStack = itemStack;
        this.customItemTypes = EnumSet.copyOf(Arrays.asList(customItemTypes));
        customItems.put(name, this);
    }

    public static CustomItem of(ConfigurationSection section, String name) {
        return of(ItemStackBuilder.of(section), name);
    }

    public static CustomItem of(ConfigurationSection configurationSection) {
        return of(configurationSection, configurationSection.getName());
    }

    public static CustomItem of(ItemStackBuilder itemStackBuilder, String name) {
        return new CustomItem(name, new NBTItem(itemStackBuilder.build()).set(name, true).buildItemStack());
    }

    public static HashMap<String, CustomItem> getCustomItems() {
        return customItems;
    }

    public static CustomItem findCustomItem(ItemStack itemStack) {
        return new NBTItem(itemStack).getKeys()
                .stream()
                .map(key -> customItems.get(key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public static CustomItem getCustomItem(String name) {
        return customItems.get(name);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getName() {
        return name;
    }

    public Set<CustomItemType> getCustomItemTypes() {
        return customItemTypes;
    }
}
