package haruudon.udon.magicstick.events;

import haruudon.udon.magicstick.MagicStick;
import haruudon.udon.magicstick.Mana;
import haruudon.udon.magicstick.TypeEvent;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class GenerateManaEvent implements Listener {
    HashMap<UUID, Boolean> holdSneak = new HashMap<>();


    @EventHandler
    public void onManaShiftEvent(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (Mana.checkMana(p) && Mana.getMana(p) < 20){
            if (!(AbilityEvent.ShapeMob.containsKey(p.getUniqueId()))){
                if (e.isSneaking()) {
                    holdSneak.put(p.getUniqueId(), true);
                    new BukkitRunnable(){
                        int i = 0;
                        @Override
                        public void run(){
                            if (Mana.checkMana(p)){
                                if (!(holdSneak.containsKey(p.getUniqueId()))){
                                    p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                                    this.cancel();
                                } else if (Mana.getMana(p) >= 20){
                                    p.playSound(p.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
                                    this.cancel();
                                } else if (i < 20){
                                    i++;
                                    p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 1, 0, 0, 0, 0.07);
                                } else {
                                    if (i % 3 == 0) {
                                        Mana.addMana(p, 1);
                                        if (!(Mana.mana.get(p.getUniqueId()) == 20)){
                                            if (TypeEvent.CheckType(p, "zombie")){
                                                if (!(p.getHealth() == p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())){
                                                    Random random = new Random();
                                                    int randomInt = random.nextInt(6);
                                                    if (randomInt == 0){
                                                        p.setHealth(p.getHealth() + 1);
                                                        p.getWorld().spawnParticle(Particle.HEART, p.getLocation().add(0, 2, 0), 5, 0.2, 0.3, 0.2, 1);
                                                        p.sendMessage(ChatColor.WHITE + "パッシブ発動: " + ChatColor.DARK_GREEN + "ゾンビ");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    i++;
                                    p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 1, 0, 0, 0, 0.07);
                                }
                            } else {
                                holdSneak.remove(p.getUniqueId());
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(MagicStick.getPlugin(), 0L, 1L);
                } else {
                    holdSneak.remove(p.getUniqueId());
                }
            }
        }
    }
}
