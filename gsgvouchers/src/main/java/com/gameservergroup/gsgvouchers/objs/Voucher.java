package com.gameservergroup.gsgvouchers.objs;

import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.utils.Text;
import com.gameservergroup.gsgvouchers.GSGVouchers;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Voucher extends CustomItem {

    private List<String> commands;

    public Voucher(String name, ConfigurationSection configurationSection, List<String> strings) {
        super(of(GSGVouchers.getInstance(), configurationSection, name));
        this.commands = strings;
    }

    public void execute(Player player) {
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }
        commands.stream().map(command -> command.replace("{player}", player.getName())).forEach(command -> {
            if (command.startsWith("@broadcast")) {
                Bukkit.broadcastMessage(Text.toColor(command.substring(11)));
            } else if (command.startsWith("@message")) {
                Bukkit.broadcastMessage(Text.toColor(command.substring(8)));
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        });
    }
}
