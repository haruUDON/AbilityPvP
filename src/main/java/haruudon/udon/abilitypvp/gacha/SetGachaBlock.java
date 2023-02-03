package haruudon.udon.abilitypvp.gacha;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetGachaBlock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (sender instanceof Player p){
            if (p.isOp()){
                ItemStack item = new ItemStack(Material.ENCHANTMENT_TABLE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.DARK_PURPLE + "ガチャ");
                item.setItemMeta(meta);
                p.getInventory().addItem(item);
                return true;
            }
        }
        return false;
    }
}
