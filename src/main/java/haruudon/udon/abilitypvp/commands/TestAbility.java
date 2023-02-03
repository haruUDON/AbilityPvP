package haruudon.udon.abilitypvp.commands;

import haruudon.udon.abilitypvp.AbilityPvP;
import haruudon.udon.abilitypvp.Mana;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TestAbility implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p){
            if (p.isOp()){
                if (label.equalsIgnoreCase("testability")){
                    if (args.length == 1){
                        try {
                            ItemStack item = AbilityPvP.getData("ability").getItemStack(args[0] + ".item1");
                            p.getInventory().setItem(2, item);
                            Mana.setInitialMana(p);
                        } catch (IllegalArgumentException e){
                            p.sendMessage(ChatColor.RED + "エラー");
                        }
                    }
                } else if (label.equalsIgnoreCase("test")){
                    Location location = p.getLocation();
                    AbilityPvP.getData("player").set("TestLocation", location);
                    AbilityPvP.savePlayerData();
                }
            }
        }
        return false;
    }
}
