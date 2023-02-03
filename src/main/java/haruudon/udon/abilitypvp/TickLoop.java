package haruudon.udon.abilitypvp;

import haruudon.udon.abilitypvp.events.AbilityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TickLoop {
    public static void StartLoop(){
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player loop : Bukkit.getOnlinePlayers()){
                    loop.stopSound(Sound.ENTITY_ZOMBIE_AMBIENT);
                    loop.stopSound(Sound.ENTITY_ZOMBIE_STEP);
                }
                for (Player loop : GameMain.Alive){
                    for (Block trap : AbilityEvent.SpiderTrap.keySet()){
                        Player trapper = AbilityEvent.SpiderTrap.get(trap);
                        if (loop.getLocation().distanceSquared(trap.getLocation()) <= 1){
                            if (loop != trapper){
                                AbilityEvent.SpiderTrap.remove(trap);
                                loop.damage(4, trapper);
                                loop.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3 * 20, 0, true));
                                loop.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 0, true));
                                trap.setType(Material.AIR);
                                int radius = 2;
                                for (int x = radius; x >= -radius; x--) {
                                    for (int z = radius; z >= -radius; z--) {
                                        Block block = trap.getRelative(x, 0, z);
                                        List<Block> changes = new ArrayList<>();
                                        if (block.getType() == Material.AIR){
                                            block.setType(Material.WEB);
                                            changes.add(block);
                                            new BukkitRunnable(){
                                                @Override
                                                public void run() {
                                                    for (Block change : changes){
                                                        change.setType(Material.AIR);
                                                    }
                                                }
                                            }.runTaskLater(AbilityPvP.getPlugin(), 100);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(AbilityPvP.getPlugin(), 0, 1);
    }
}
