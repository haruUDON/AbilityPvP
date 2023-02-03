package haruudon.udon.abilitypvp;

import haruudon.udon.abilitypvp.events.AbilityEvent;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static haruudon.udon.abilitypvp.AbilityPvP.getData;
import static haruudon.udon.abilitypvp.events.GenerateManaEvent.holdSneak;


public class TypeEvent implements Listener {
    public static boolean CheckType(Player p, String type){
        String select = getData("player").getString(p.getUniqueId().toString() + ".customkit.select");
        List<String> types = getData("player").getStringList(p.getUniqueId().toString() + ".customkit." + select + ".type");
        for (String pt : types) {
            if (pt.equalsIgnoreCase(type)){
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void SpiderPassive(PlayerToggleSneakEvent e){
        Player p = e.getPlayer();
        if (Mana.checkMana(p) && Mana.getMana(p) >= 20){
            if (!(AbilityEvent.ShapeMob.containsKey(p.getUniqueId()))){
                if (e.isSneaking()){
                    holdSneak.put(p.getUniqueId(), true);
                    if (CheckType(p, "spider")){
                        Location loc = p.getLocation();
                        Vector direction = loc.getDirection();
                        Location front = loc.add(direction);
                        for (int y = 0; y < 2; y++){
                            if (front.getBlock().getType() == Material.AIR) return;
                            front.add(0, 1, 0);
                        }
                        MobDisguise spider = new MobDisguise(DisguiseType.SPIDER);
                        spider.setEntity(p);
                        spider.startDisguise();
                        AbilityEvent.ShapeMob.put(p.getUniqueId(), "spider");
                        p.setLevel(0);
                        p.setExp(0);
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                if (Mana.checkMana(p)){
                                    Location l = p.getLocation();
                                    Vector d = l.getDirection();
                                    Location f = l.add(d);
                                    for (int y = 0; y < 2; y++){
                                        if (f.getBlock().getType() == Material.AIR){
                                            p.setExp(0);
                                            AbilityEvent.ShapeMob.remove(p.getUniqueId());
                                            DisguiseAPI.getDisguise(p).stopDisguise();
                                            cancel();
                                        }
                                        f.add(0, 1, 0);
                                    }
                                    if (!(holdSneak.containsKey(p.getUniqueId()))){
                                        p.setExp(0);
                                        AbilityEvent.ShapeMob.remove(p.getUniqueId());
                                        DisguiseAPI.getDisguise(p).stopDisguise();
                                        this.cancel();
                                    } else if (Mana.getMana(p) < 20){
                                        p.setExp(0);
                                        AbilityEvent.ShapeMob.remove(p.getUniqueId());
                                        DisguiseAPI.getDisguise(p).stopDisguise();
                                        this.cancel();
                                    } else if (p.getLevel() >= 1){
                                        p.setVelocity(new Vector(0, 2, 0));
                                        p.playSound(p.getLocation(), Sound.ENTITY_SPIDER_AMBIENT, 1, 1);
                                        p.setLevel(0);
                                        p.setExp(0);
                                        cancel();
                                        new BukkitRunnable(){
                                            @Override
                                            public void run() {
                                                DisguiseAPI.getDisguise(p).stopDisguise();
                                                AbilityEvent.ShapeMob.remove(p.getUniqueId());
                                                p.playSound(p.getLocation(), Sound.ENTITY_SPIDER_AMBIENT, 1, 0);
                                            }
                                        }.runTaskLater(AbilityPvP.getPlugin(), 35);
                                    } else {
                                        p.giveExp(1);
                                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                                    }
                                } else {
                                    holdSneak.remove(p.getUniqueId());
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(AbilityPvP.getPlugin(), 0, 1);
                    }
                } else {
                    holdSneak.remove(p.getUniqueId());
                }
            }
        }
    }
}
