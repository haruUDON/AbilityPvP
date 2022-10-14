package haruudon.udon.magicstick.commands;

import haruudon.udon.magicstick.MagicStick;
import haruudon.udon.magicstick.Mana;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TestAbility implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p){
            if (label.equalsIgnoreCase("testability")){
                if (args.length == 1){
                    try {
                        ItemStack item = MagicStick.getAbilityData().getItemStack(args[0] + ".item1");
                        p.getInventory().setItem(2, item);
                        Mana.setInitialMana(p);
                    } catch (IllegalArgumentException e){
                        p.sendMessage(ChatColor.RED + "エラー");
                    }
                }
            } else if (label.equalsIgnoreCase("test")){
                p.sendMessage("テスト: " + p.getMaxHealth());
                p.sendMessage("テスト: " + p.getHealth());
            }
        }
        return false;
    }
}
