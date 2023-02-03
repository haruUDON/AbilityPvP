package haruudon.udon.abilitypvp.events;

import haruudon.udon.abilitypvp.Mana;
import haruudon.udon.abilitypvp.TypeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

public class FoodLevel implements Listener {
    @EventHandler
    public void onUseFoodLevel(FoodLevelChangeEvent e){
        Player p = (Player) e.getEntity();
        e.setFoodLevel(p.getFoodLevel());
    }

    public static void reloadFoodLevel(Player p){
        if (Mana.checkMana(p)){
            int playerMana = Mana.getMana(p);
            p.setFoodLevel(playerMana);
        }
    }

    @EventHandler
    public void PlayerDamageEvent(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            if (e.getCause() == STARVATION){
                e.setCancelled(true);
            } else if (e.getCause() == FALL){
                e.setCancelled(true);
            } else if (e.getCause() == SUFFOCATION){
                e.setCancelled(true);
            } else if (e.getCause() == POISON){
                if (TypeEvent.CheckType((Player) e.getEntity(), "spider")){
                    e.setCancelled(true);
                }
            }
        }
    }
}
