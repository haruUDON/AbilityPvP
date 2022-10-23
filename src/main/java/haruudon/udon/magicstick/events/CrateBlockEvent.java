package haruudon.udon.magicstick.events;

import haruudon.udon.magicstick.GUIManager;
import haruudon.udon.magicstick.MagicStick;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CrateBlockEvent implements Listener {
    @EventHandler
    public void CrateBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (p.isOp()) {
            if (e.getItemInHand().getType() == Material.ENCHANTMENT_TABLE || e.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "クレート")) {
                p.getInventory().setItem(p.getInventory().getHeldItemSlot(), null);
                p.sendMessage(ChatColor.GREEN + "クレートを生成しました。");
                Location loc = e.getBlockPlaced().getLocation();
                MagicStick.getBlockData().set("Crate", loc);
                MagicStick.saveBlockData();
            }
        }
    }

    @EventHandler
    public void CrateBlockClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_BLOCK)){
            if (e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE){
                Location loc1 = (Location) MagicStick.getBlockData().get("Crate");
                Location loc2 = e.getClickedBlock().getLocation();
                if (loc1.equals(loc2)){
                    e.setCancelled(true);
                    GUIManager.OpenCrateMenu(p);
                }
            }
        }
    }
}
