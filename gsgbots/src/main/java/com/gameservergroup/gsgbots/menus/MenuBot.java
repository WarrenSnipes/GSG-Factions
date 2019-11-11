package com.gameservergroup.gsgbots.menus;

import com.gameservergroup.gsgbots.GSGBots;
import com.gameservergroup.gsgbots.objs.Bot;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class MenuBot extends Menu {

    private static double SAND_PRICE;

    private transient final Bot bot;

    public MenuBot(Bot bot) {
        super(GSGBots.getInstance().getConfig().getString("bot.menu.title"), GSGBots.getInstance().getConfig().getInt("bot.menu.slots"));
        this.bot = bot;
        initialize();
    }

    public static double getSandPrice() {
        return SAND_PRICE;
    }

    public static void setSandPrice(double sandPrice) {
        SAND_PRICE = sandPrice;
    }

    @Override
    public void initialize() {
        MenuItems[] values = MenuItems.values();
        for (int i = 1; i < values.length; i++) {
            MenuItems menuItems = values[i];
            menuItems.updateItem(this);
        }

        if (MenuItems.ITEM_FILLED.menuItem != null) {
            while (getInventory().firstEmpty() != -1) {
                setItem(getInventory().firstEmpty(), MenuItems.ITEM_FILLED.menuItem);
            }
        }
    }

    public Bot getBot() {
        return bot;
    }

    private enum MenuItems {
        ITEM_FILLED, ITEM_BALANCE, ITEM_REMOVE, ITEM_TOGGLE, ITEM_REFRESH;

        private static ChatColor CHATCOLOR_ENABLED, CHATCOLOR_DISABLED;
        private static DyeColor DYECOLOR_ENABLED, DYECOLOR_DISABLED;

        private int slot;
        private MenuItem menuItem;

        MenuItems() {
            init();
        }

        private void init() {
            switch (this) {
                case ITEM_FILLED:
                    if (GSGBots.getInstance().getConfig().getBoolean("bot.menu.fill.enabled")) {
                        DyeColor dyeColor = DyeColor.valueOf(GSGBots.getInstance().getConfig().getString("bot.menu.fill.glass-pane-color").toUpperCase());
                        ItemStackBuilder itemStackBuilder = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).setDyeColor(dyeColor).setDisplayName(" ");
                        if (GSGBots.getInstance().getConfig().getBoolean("bot.menu.fill.enchanted")) {
                            itemStackBuilder.addEnchant(Enchantment.DURABILITY, 1);
                            itemStackBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        }
                        this.menuItem = MenuItem.of(itemStackBuilder.build());
                    }
                    return;
                case ITEM_BALANCE: {
                    ItemStack itemStack = ItemStackBuilder.of(GSGBots.getInstance().getConfig().getConfigurationSection("bot.menu.items.balance-item")).build();
                    this.menuItem = MenuItem.of(itemStack).setInventoryClickEventConsumer(event -> {
                        MenuBot menuBot = (MenuBot) event.getClickedInventory();
                        if (GSGBots.getEconomy().withdrawPlayer(((Player) event.getWhoClicked()), SAND_PRICE).transactionSuccess()) {
                            Bot bot = menuBot.getBot();
                            bot.setMoneyBalance(bot.getMoneyBalance() + SAND_PRICE);
                            bot.setSandBalance((int) (bot.getMoneyBalance() / SAND_PRICE));
                            updateItem(menuBot);
                        }
                        event.setCancelled(true);
                    });
                    return;
                }
                case ITEM_REMOVE:
                    return;
                case ITEM_TOGGLE: {
                    String enabledColor = GSGBots.getInstance().getConfig().getString("bot.menu.items.toggle-item.meta.enabled-color").toUpperCase();
                    CHATCOLOR_ENABLED = ChatColor.valueOf(enabledColor);
                    DYECOLOR_ENABLED = DyeColor.valueOf(enabledColor);

                    String disabledColor = GSGBots.getInstance().getConfig().getString("bot.menu.items.toggle-item.meta.disabled-color").toUpperCase();
                    DYECOLOR_DISABLED = DyeColor.valueOf(disabledColor);
                    CHATCOLOR_DISABLED = ChatColor.valueOf(disabledColor);

                    ItemStackBuilder itemStackBuilder = ItemStackBuilder.of(GSGBots.getInstance().getConfig().getConfigurationSection("bot.menu.items.toggle-item"));
                    ItemStack itemStack = itemStackBuilder.build();
                    this.menuItem = MenuItem.of(itemStack).setInventoryClickEventConsumer(event -> {
                        MenuBot menuBot = (MenuBot) event.getClickedInventory();
                        menuBot.getBot().setStatus(!menuBot.getBot().isStatus());
                        event.setCancelled(true);
                        updateItem(menuBot);
                    });
                    return;
                }
                case ITEM_REFRESH:
                    return;
            }
        }

        public void updateItem(MenuBot menuBot) {
            if (this == MenuItems.ITEM_BALANCE) {
                MenuItem clone = menuBot.getMenuItem(slot).get();
                ItemStack itemStack = clone.getItemStack();
                clone.setItemStack(ItemStackBuilder.of(itemStack).setLore(itemStack.getItemMeta().getLore()
                        .stream()
                        .map(s -> s.replace("{balance}", String.valueOf(menuBot.getBot().getMoneyBalance()).replace("{total-sand}", String.valueOf(menuBot.getBot().getSandBalance()))))
                        .collect(Collectors.toList())).build());
                menuBot.setItem(this.slot, clone);
            } else if (this == MenuItems.ITEM_TOGGLE) {
                MenuItem clone = menuBot.getMenuItem(slot).get();
                ItemStack itemStack = clone.getItemStack();
                clone.setItemStack(ItemStackBuilder.of(itemStack)
                        .setDyeColor(menuBot.getBot().isStatus() ? DYECOLOR_ENABLED : DYECOLOR_DISABLED)
                        .setLore(itemStack.getItemMeta()
                                .getLore()
                                .stream()
                                .map(s -> s.replace("{status}", menuBot.getBot().isStatus() ? CHATCOLOR_ENABLED + "disabled" : CHATCOLOR_DISABLED + "disabled"))
                                .collect(Collectors.toList())).build());
                menuBot.setItem(this.slot, clone);
            }
        }
    }
}
