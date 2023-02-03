package haruudon.udon.abilitypvp.events;

import haruudon.udon.abilitypvp.AbilityPvP;
import haruudon.udon.abilitypvp.Mana;
import haruudon.udon.abilitypvp.cooldown.Cooldown;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static haruudon.udon.abilitypvp.AbilityPvP.getData;
import static haruudon.udon.abilitypvp.GameMain.Alive;
import static haruudon.udon.abilitypvp.events.AbilityEvent.ShapeMob;

public class WeaponEvent implements Listener {
    @EventHandler
    public void AttackEvent(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player attacker){
            if (e.getEntity() instanceof Player victim){
                ItemStack item = attacker.getInventory().getItemInMainHand();
                ItemMeta itemMeta = item.getItemMeta();
                if (((victim.getHealth() - e.getDamage()) > 0)){
                    if (item.getType() == Material.STONE_SPADE && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "マナサイズ")) {
                        if (Mana.mana.containsKey(attacker.getUniqueId())){
                            int attackDamage = (int) e.getFinalDamage();
                            Mana.addMana(attacker, attackDamage / 2);
                        }
                    } else if (item.getType() == Material.STONE_SPADE && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "ヒールサイズ")) {
                        double attackDamage = e.getFinalDamage();
                        attacker.setHealth(attacker.getHealth() + (attackDamage / 2));
                    }
                }
            }
        }
    }
    @EventHandler
    public void useExcalibur(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (p.getInventory().getItemInMainHand() == null) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.IRON_SWORD && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "エクスカリバー")) {
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                Cooldown.setCooldown(p, getData("ability").getInt("Excalibur.cooltime"), "Excalibur", p.getInventory().getHeldItemSlot());
                p.getWorld().playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 5, 0);
                new BukkitRunnable() {
                    final int circlePoints = 10;
                    final double radius = 2;
                    final Location playerLoc = p.getEyeLocation();
                    final World world = playerLoc.getWorld();
                    final Vector dir = p.getLocation().getDirection().normalize();
                    final double pitch = (playerLoc.getPitch() + 90.0F) * 0.017453292F;
                    final double yaw = -playerLoc.getYaw() * 0.017453292F;
                    final double increment = (2 * Math.PI) / circlePoints;
                    double circlePointOffset = 0;
                    int beamLength = 30;
                    @Override
                    public void run() {
                        beamLength--;
                        if(beamLength < 1){
                            this.cancel();
                            return;
                        }
                        for (int i = 0; i < circlePoints; i++) {
                            double angle = i * increment + circlePointOffset;
                            double x = radius * Math.cos(angle);
                            double z = radius * Math.sin(angle);
                            Vector vec = new Vector(x, 0, z);
                            VectorUtils.rotateAroundAxisX(vec, pitch);
                            VectorUtils.rotateAroundAxisY(vec, yaw);
                            playerLoc.add(vec);
                            world.spawnParticle(Particle.END_ROD, playerLoc, 0);
                            playerLoc.subtract(vec);
                        }
                        playerLoc.add(dir);
                        for (Player another : playerLoc.getChunk().getWorld().getPlayers()) {
                            if (another.getLocation().distance(playerLoc) < 4){
                                if (another != (p)) {
                                    if (Alive.contains(another)) {
                                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                        world.playSound(another.getLocation(), Sound.ENTITY_LIGHTNING_IMPACT, 1, 1);
                                        another.damage(1, p);
                                        another.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 250, 0, true));
                                        Mana.mana.put(another.getUniqueId(), 0);
                                        FoodLevel.reloadFoodLevel(another);
                                        this.cancel();
                                        return;
                                    }
                                }
                            }
                        }
                        playerLoc.subtract(dir);

                        circlePointOffset += increment / 3;
                        if (circlePointOffset >= increment) {
                            circlePointOffset = 0;
                        }
                        playerLoc.add(dir);
                    }
                }.runTaskTimer(AbilityPvP.getPlugin(), 0, 1);
            }
        }
    }
}
