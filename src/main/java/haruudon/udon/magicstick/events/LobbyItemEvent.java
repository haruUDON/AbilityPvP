package haruudon.udon.magicstick.events;

import haruudon.udon.magicstick.GUIManager;
import haruudon.udon.magicstick.Join;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static haruudon.udon.magicstick.Join.GamePlayer;
import static haruudon.udon.magicstick.Join.JoinPlayer;

public class LobbyItemEvent implements Listener {
    @EventHandler
    public void RightClickLobbyItem(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.WORKBENCH && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "キットをカスタマイズ")) {
                e.setCancelled(true);
                if (!(JoinPlayer.contains(p)) && !(GamePlayer.contains(p))) {
                    GUIManager.MainSetAbilityMenu(p);
                } else {
                    p.sendMessage(ChatColor.RED + "現在は使用できません");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            } else if (item.getType() == Material.IRON_SWORD && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "ゲームに参加する")) {
                e.setCancelled(true);
                if (!(JoinPlayer.contains(p)) && !(GamePlayer.contains(p))) {
                    GUIManager.SelectCustomKit(p);
                } else {
                    p.sendMessage(ChatColor.RED + "あなたはすでにゲームに参加しています");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            } else if (item.getType() == Material.BED && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.RED + "退出する")) {
                e.setCancelled(true);
                Join.QuitPlayer(p);
            }
        }
    }

    @EventHandler
    public void BreakBlockCancel(BlockBreakEvent e){
        Player p = e.getPlayer();
        if (!(p.isOp()) || GamePlayer.contains(p)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void PlaceBlockCancel(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if (!(p.isOp()) || GamePlayer.contains(p)){
            e.setCancelled(true);
        }
    }
}
