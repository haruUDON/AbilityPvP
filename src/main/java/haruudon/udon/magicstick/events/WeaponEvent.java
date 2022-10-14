package haruudon.udon.magicstick.events;

import haruudon.udon.magicstick.Mana;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WeaponEvent implements Listener {
    @EventHandler
    public void onAttackEvent(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player attacker){
            ItemStack item = attacker.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.STONE_SPADE && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "マナサイズ")){
                if (Mana.mana.containsKey(attacker.getUniqueId())){
                    int attackDamage = (int) e.getFinalDamage();
                    Mana.addMana(attacker, attackDamage / 2);
                }
            } else if (item.getType() == Material.STONE_SPADE && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "ヒールサイズ")){
                double attackDamage = e.getFinalDamage();
                attacker.setHealth(attacker.getHealth() + (attackDamage / 2));
            }
        }
    }
}
