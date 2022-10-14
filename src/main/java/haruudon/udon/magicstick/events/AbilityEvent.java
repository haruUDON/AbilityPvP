package haruudon.udon.magicstick.events;

import haruudon.udon.magicstick.MagicStick;
import haruudon.udon.magicstick.Join;
import haruudon.udon.magicstick.Mana;
import haruudon.udon.magicstick.cooldown.Cooldown;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static haruudon.udon.magicstick.Join.*;
import static haruudon.udon.magicstick.MagicStick.*;
import static haruudon.udon.magicstick.MagicStick.getTypeData;
import static haruudon.udon.magicstick.Mana.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class AbilityEvent implements Listener {
    HashMap<Player, Location> lastlocation = new HashMap<>();

    @EventHandler
    public void onUseRewindTime(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.CHORUS_FRUIT_POPPED && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "リワインドタイム")) {
                e.setCancelled(true);
                if (checkUseMana(p, 7)) {
                    removeMana(p, 7);
                    lastlocation.put(p, p.getLocation());
                    // [Player:haruUDON, Location:x10 y20 z30][][][][][][][][][]
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 5, 0);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), null);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), getAbilityData().getItemStack("RewindTime.item2"));
                        }
                    }.runTaskLater(MagicStick.getPlugin(), 1);
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            } else if (item.getType() == Material.WATCH && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "マジックウォッチ")) {
                p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 50, 0.5, 1.5, 0.5, 0.5);
                p.teleport(lastlocation.get(p));
                // [Player:haruUDON, Location:x10 y20 z30][][][][][][][][][] の中から pと同じ名前のプレイヤーと同じ箱に登録してあるLocationを取得する
                Cooldown.setCooldown(p, getAbilityData().getInt("RewindTime.cooltime"), "RewindTime", p.getInventory().getHeldItemSlot()); //クールタイムセット
                p.sendMessage(ChatColor.YELLOW + "時間を巻き戻した。");
                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ENDERCHEST_CLOSE, 5, 1);
                p.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, p.getLocation(), 200, 0.5, 1.5, 0.5);
            }
        }
    }

    @EventHandler
    public void onUseHook(PlayerFishEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        String name = meta.getDisplayName();
        if (name.equalsIgnoreCase(ChatColor.DARK_GREEN + "グラップリングフック")) {
            if (e.getState().equals(PlayerFishEvent.State.FAILED_ATTEMPT) || e.getState().equals(PlayerFishEvent.State.IN_GROUND)) {
                Location playerLocation = p.getLocation();
                Location hookLocation = e.getHook().getLocation();
                Location change = hookLocation.subtract(playerLocation);
                p.setVelocity(change.toVector().multiply(0.3));
            }
        }
    }

//    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "command");

    HashMap<UUID, Boolean> ShapeMob = new HashMap<>();
    HashMap<UUID, Double> BeforeHealth = new HashMap<>();

    @EventHandler
    public void onUseVampireWing(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.FLINT && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "ヴァンパイアウィング")) {
                e.setCancelled(true);
                if (checkUseMana(p, 4)) {
                    removeMana(p, 3);
                    ShapeMob.put(p.getUniqueId(), true);
                    BeforeHealth.put(p.getUniqueId(), p.getHealth());
                    int slot = p.getInventory().getHeldItemSlot();
                    String cmd1 = "lp user %player% permission set idisguise.*";
                    cmd1 = cmd1.replace("%player%", p.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd1); //disコマンドを使用するための権限を与える
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 0, true));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_LOOP, 10, 1);
                    p.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, p.getLocation(), 50, 0.5, 1.5, 0.5, 0.5);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), null);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2);
                    p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), getAbilityData().getItemStack("VampireWing.item2"));
                            p.performCommand("dis bat"); //disコマンドを使用させる
                            String cmd2 = "lp user %player% permission unset idisguise.*";
                            cmd2 = cmd2.replace("%player%", p.getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd2); //disコマンドを使用するための権限を剥奪
                        }
                    }.runTaskLater(MagicStick.getPlugin(), 1);
                    // マナが徐々に減っていく ↓
                    new BukkitRunnable() {
                        int i = 0;

                        @Override
                        public void run() {
                            if (!(ShapeMob.containsKey(p.getUniqueId()))){
                                this.cancel();
                            } else if (getMana(p) == 0){
                                ShapeMob.remove(p.getUniqueId());
                                String cmd1 = "lp user %player% permission set idisguise.*";
                                cmd1 = cmd1.replace("%player%", p.getName());
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd1);
                                p.setFlying(false);
                                p.setAllowFlight(false);
                                p.removePotionEffect(PotionEffectType.GLOWING);
                                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 10, 1);
                                p.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, p.getLocation(), 50, 0.5, 1.5, 0.5, 0.5);
                                int maxhealth = 0;
                                for (String type : getPlayerData().getStringList(p.getUniqueId().toString() + ".customkit." +
                                        getPlayerData().getString(p.getUniqueId().toString() + ".customkit.select") + ".type")) {
                                    maxhealth += getTypeData().getInt(type + ".health");
                                }
                                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxhealth);
                                p.setHealth(BeforeHealth.get(p.getUniqueId()));
                                BeforeHealth.remove(p.getUniqueId());
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        p.performCommand("undis"); //disコマンドを使用させる
                                        String cmd2 = "lp user %player% permission unset idisguise.*";
                                        cmd2 = cmd2.replace("%player%", p.getName());
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd2); //disコマンドを使用するための権限を剥奪
                                        Cooldown.setCooldown(p, getAbilityData().getInt("VampireWing.cooltime"), "VampireWing", slot);
                                    }
                                }.runTaskLater(getPlugin(), 1);
                                cancel();
                            } else {
                                if (i % 10 == 0){
                                    removeMana(p, 1);
                                }
                                i++;
                            }
                        }
                    }.runTaskTimer(MagicStick.getPlugin(), 0L, 1L);
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            } else if (item.getType() == Material.BONE && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "ヴァンパイアボーン")) {
                ShapeMob.remove(p.getUniqueId());
                String cmd1 = "lp user %player% permission set idisguise.*";
                cmd1 = cmd1.replace("%player%", p.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd1); //disコマンドを使用するための権限を与える
                p.setFlying(false);
                p.setAllowFlight(false);
                p.removePotionEffect(PotionEffectType.GLOWING);
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 10, 1);
                p.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, p.getLocation(), 50, 0.5, 1.5, 0.5, 0.5);
                int maxhealth = 0;
                for (String type : getPlayerData().getStringList(p.getUniqueId().toString() + ".customkit." +
                        getPlayerData().getString(p.getUniqueId().toString() + ".customkit.select") + ".type")) {
                    maxhealth += getTypeData().getInt(type + ".health");
                }
                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxhealth);
                p.setHealth(BeforeHealth.get(p.getUniqueId()));
                BeforeHealth.remove(p.getUniqueId());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Cooldown.setCooldown(p, getAbilityData().getInt("VampireWing.cooltime"), "VampireWing", p.getInventory().getHeldItemSlot());
                        p.performCommand("undis"); //disコマンドを使用させる
                        String cmd2 = "lp user %player% permission unset idisguise.*";
                        cmd2 = cmd2.replace("%player%", p.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd2); //disコマンドを使用するための権限を剥奪
                    }
                }.runTaskLater(MagicStick.getPlugin(), 1);
            }
        }
    }

    @EventHandler
    public void onUseVampireHeart(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.BEETROOT && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "ヴァンパイアハート")){
                e.setCancelled(true);
                if (checkUseMana(p, 5)) {
                    removeMana(p, 5);
                    Cooldown.setCooldown(p, getAbilityData().getInt("VampireHeart.cooltime"), "VampireHeart", p.getInventory().getHeldItemSlot());
                    // アビリティの処理
                    for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                        if (other.getWorld() == p.getWorld() && other.getLocation().distanceSquared(p.getLocation()) <= 40) {
                            if (!(other == p)) {
                                if (other.getWorld() == p.getWorld()) {
                                    other.damage(6, p);
                                    other.getWorld().spawnParticle(Particle.BLOCK_CRACK, other.getLocation().add(0, 1, 0), 100, 0.5, 1, 0.5, 0, new MaterialData(Material.REDSTONE_BLOCK));
                                    p.getWorld().spawnParticle(Particle.HEART, p.getLocation(), 10, 0, 1.5, 0, 0.2);
                                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
                                    if ((float) p.getHealth() >= (p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - 2)) {
                                        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                                    } else {
                                        p.setHealth((float) p.getHealth() + (float) 3);
                                    }
                                }
                            }
                        }
                    }
                    //ここまで
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onUseFriezeSpell(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.ENCHANTED_BOOK && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "フリーズスペル")) {
                if (checkUseMana(p, 3)) {
                    removeMana(p, 3);
                    Cooldown.setCooldown(p, getAbilityData().getInt("FriezeSpell.cooltime"), "FriezeSpell", p.getInventory().getHeldItemSlot());
                    p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 2);
                    // アビリティの処理
                    new BukkitRunnable() {
                        final Location loc = p.getLocation();
                        final Vector direction = loc.getDirection();
                        double t = 0;

                        @Override
                        public void run() {
                            t += 1;
                            double x = direction.getX() * t;
                            double y = direction.getY() * t + 1.5;
                            double z = direction.getZ() * t;
                            loc.add(x, y, z);
                            p.getWorld().spawnParticle(Particle.SNOWBALL, loc, 2, 0, 0, 0, 0.05);
                            if (loc.getBlock().getType().isSolid()) {
                                cancel();
                            }
                            for (Entity entity : loc.getChunk().getEntities()) { //チャンク内のエンティティの数だけループする。エンティティをentity変数に入れる
                                if (entity.getLocation().distance(loc) < 2) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                    if (entity != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                        if (entity.getType().isAlive()) { //エンティティが生きているかどうか
                                            Damageable d = (Damageable) entity;
                                            d.damage(5, p);
                                            p.getWorld().spawnParticle(Particle.SNOWBALL, loc, 50, 0, 0, 0, 1);
                                            entity.getWorld().spawnParticle(Particle.BLOCK_CRACK, entity.getLocation().add(0, 1, 0), 100, 0.5, 1, 0.5, 0, new MaterialData(Material.ICE));
                                            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 0);
                                            if (entity instanceof LivingEntity) {
                                                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 9, true));
                                            }
                                            this.cancel();
                                        }
                                    }
                                }
                            }
                            loc.subtract(x, y, z);
                            if (t > 40) {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(getPlugin(), 0, 1);
                    //ここまで
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onUseHealSpell(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.ENCHANTED_BOOK && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "ヒールスペル")) {
                e.setCancelled(true);
                if (checkUseMana(p, 4)) {
                    if (p.getHealth() < p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()){
                        removeMana(p, 4);
                        Cooldown.setCooldown(p, getAbilityData().getInt("HealSpell.cooltime"), "HealSpell", p.getInventory().getHeldItemSlot());
                        // アビリティの処理
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 2, true));
                        //ここまで
                    } else {
                        p.sendMessage(ChatColor.RED + "体力が最大のため使えません。");
                        p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    HashMap<UUID, Integer> DamageCharge = new HashMap<>();

    @EventHandler
    public void onUseVampireBlood(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.INK_SACK && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "ヴァンパイアブラッド")) {
                e.setCancelled(true);
                MaterialData data = item.getData();
                if (data instanceof Dye dye) {
                    if (dye.getColor() == DyeColor.RED) {
                        if (checkUseMana(p, 10)) {
                            if (p.getHealth() > 1) {
                                removeMana(p, 10);
                                p.getInventory().setItem(p.getInventory().getHeldItemSlot(), null);
                                Cooldown.setCooldown(p, getAbilityData().getInt("VampireBlood.cooltime"), "VampireBlood", p.getInventory().getHeldItemSlot());
                                // アビリティの処理
                                p.playSound(p.getLocation(), Sound.BLOCK_CLOTH_BREAK, 1, 1);
                                p.getWorld().spawnParticle(Particle.REDSTONE, p.getLocation(), 50, 0.5, 1, 0.5, 0);
                                DamageCharge.put(p.getUniqueId(), 0);
                                //ここまで
                                new BukkitRunnable() {
                                    int damageCharge = 0;
                                    int i = 0;
                                    @Override
                                    public void run() {
                                        if (!(Mana.checkMana(p))){
                                            DamageCharge.remove(p.getUniqueId());
                                            this.cancel();
                                        } else if (p.getHealth() == 1) {
                                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 10, 1);
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    if (DamageCharge.containsKey(p.getUniqueId())) {
                                                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SKELETON_DEATH, 10, 1);
                                                        p.sendMessage(ChatColor.DARK_RED + "ヴァンパイアブラッドの効果が切れた...");
                                                        DamageCharge.remove(p.getUniqueId());
                                                    }
                                                }
                                            }.runTaskLater(MagicStick.getPlugin(), 20 * 10);
                                            this.cancel();
                                        } else if (!(DamageCharge.containsKey(p.getUniqueId()))) {
                                            this.cancel();
                                        } else {
                                            if (i % 10 == 0) {
                                                damageCharge++;
                                                DamageCharge.put(p.getUniqueId(), damageCharge);
                                                p.setHealth(p.getHealth() - 1);
                                                p.playSound(p.getLocation(), Sound.BLOCK_CLOTH_BREAK, 1, 1);
                                                p.getWorld().spawnParticle(Particle.REDSTONE, p.getLocation(), 50, 0.5, 1, 0.5, 0);
                                            }
                                            i++;
                                        }
                                    }
                                }.runTaskTimer(MagicStick.getPlugin(), 0L, 1L);
                            } else {
                                p.sendMessage(ChatColor.RED + "体力が残り少ないため使えません。");
                                p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "マナが足りません。");
                            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onUseSkullSniper(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (a.equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.SPECTRAL_ARROW && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "スカルスナイパー")){
                if (checkUseMana(p, 15)) {
                    removeMana(p, 15);
                    Cooldown.setCooldown(p, getAbilityData().getInt("SkullSniper.cooltime"), "SkullSniper", p.getInventory().getHeldItemSlot());
                    // アビリティの処理
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 20, 0.5F);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 20, 0.6F);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 20, 0.6F);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2 * 20, 4, true));
                    final Location loc = p.getLocation();
                    final Vector direction = loc.getDirection();
                    outer:
                    for (double i = 0.5; i < 40; i += 0.5) {
                        double x = direction.getX() * i;
                        double y = direction.getY() * i + 1.5;
                        double z = direction.getZ() * i;
                        loc.add(x, y, z);
                        p.getWorld().spawnParticle(Particle.CRIT, loc, 2, 0, 0, 0, 0.05);
                        if (loc.getBlock().getType().isSolid()) {
                            break;
                        }
                        for (Entity entity : loc.getChunk().getEntities()) { //チャンク内のエンティティの数だけループする。エンティティをentity変数に入れる
                            if (entity.getLocation().distance(loc) < 1.5) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                if (entity != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                    if (entity.getType().isAlive()) { //エンティティが生きているかどうか
                                        Damageable d = (Damageable) entity;
                                        d.damage(12, p);
                                        p.getWorld().spawnParticle(Particle.CRIT, loc, 50, 0, 0, 0, 1);
                                        entity.getWorld().spawnParticle(Particle.BLOCK_CRACK, entity.getLocation(), 100, 0.5, 1, 0.5, 0, new MaterialData(Material.BONE_BLOCK));
                                        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 0);
                                        break outer;
                                    }
                                }
                            }
                        }
                        loc.subtract(x, y, z);
                    }
                    //ここまで
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onUseGrapplingArrow(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (a.equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.LEASH && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "グラップリングアロウ")) {
                if (checkUseMana(p, 7)) {
                    removeMana(p, 7);
                    Cooldown.setCooldown(p, getAbilityData().getInt("GrapplingArrow.cooltime"), "GrapplingArrow", p.getInventory().getHeldItemSlot());
                    // アビリティの処理
                    p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_THROW, 1, 0.7F);
                    p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_THROW, 1, 0.5F);
                    p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_THROW, 1, 0.3F);
                    Location loc = p.getLocation();
                    Vector direction = loc.getDirection();
                    double x = direction.getX() * 1;
                    double y = direction.getY() * 1 + 1.5;
                    double z = direction.getZ() * 1;
                    loc.add(x, y, z);
                    Arrow arrow = p.getWorld().spawnArrow(loc, direction, 1.5F, 1);
                    arrow.setShooter(p);
                    arrow.setCustomName("Grappling");
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onUseRefreshDebuff(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.INK_SACK && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "リフレッシュデバフ")){
                e.setCancelled(true);
                if (checkUseMana(p, 4)){
                    removeMana(p, 4);
                    Cooldown.setCooldown(p, getAbilityData().getInt("RefreshDebuff.cooltime"), "RefreshDebuff", p.getInventory().getHeldItemSlot());
                    p.removePotionEffect(PotionEffectType.SLOW);
                    p.removePotionEffect(PotionEffectType.WEAKNESS);
                    p.removePotionEffect(PotionEffectType.POISON);
                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                    p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_SPLASH, 1, 1);
                    p.spawnParticle(Particle.WATER_WAKE, p.getLocation(), 50, 0.2, 1, 0.2, 0.3F);
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    HashMap<Player, Boolean> BoneVeil = new HashMap<>();

    @EventHandler
    public void onUseBoneVeil(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.BONE && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "ボーンヴェール")){
                e.setCancelled(true);
                if (checkUseMana(p, 11)) {
                    removeMana(p, 11);
                    Cooldown.setCooldown(p, getAbilityData().getInt("BoneVeil.cooltime"), "BoneVeil", p.getInventory().getHeldItemSlot());
                    BoneVeil.put(p, true);
                    p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 10, 1);
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            BoneVeil.remove(p);
                            p.getWorld().playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 5, 1);
                            p.sendMessage(ChatColor.GRAY + "ボーンヴェールの効果が切れた");
                        }
                    }.runTaskLater(MagicStick.getPlugin(), 20 * 10);
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onUseEnderTeleport(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (a.equals(Action.RIGHT_CLICK_AIR)){
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.EYE_OF_ENDER && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_BLUE + "エンダーテレポート")){
                if (checkUseMana(p, 5)){
                    removeMana(p, 5);
                    e.setCancelled(true);
                    Cooldown.setCooldown(p, getAbilityData().getInt("EnderTeleport.cooltime"), "EnderTeleport", p.getInventory().getHeldItemSlot());
                    final Location loc = p.getLocation();
                    final Vector direction = loc.getDirection();
                    outer:
                    for (double i = 0.5; i < 20; i += 0.5) {
                        double x = direction.getX() * i;
                        double y = direction.getY() * i + 1;
                        double z = direction.getZ() * i;
                        loc.add(x, y, z);
                        p.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 2, 0.1, 0.1, 0.1, 0, new MaterialData(Material.COAL_BLOCK));
                        if (loc.getBlock().getType().isSolid()) {
                            double Tx = direction.getX() * 1;
                            double Ty = direction.getY() * 2;
                            double Tz = direction.getZ() * 1;
                            p.teleport(loc.subtract(Tx, Ty, Tz));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, true));
                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 5, 1);
                            break;
                        }
                        for (Entity entity : loc.getChunk().getEntities()){ //チャンク内のエンティティの数だけループする。エンティティをentity変数に入れる
                            if (entity.getLocation().distance(loc) < 2) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                if (entity != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                    if (entity.getType().isAlive()) { //エンティティが生きているかどうか
                                        Damageable d = (Damageable) entity;
                                        d.damage(3, p);
                                        double Tx = direction.getX() * 1;
                                        double Ty = direction.getY() * 2;
                                        double Tz = direction.getZ() * 1;
                                        p.teleport(loc.subtract(Tx, Ty, Tz));
                                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 5, 1);
                                        entity.getWorld().spawnParticle(Particle.BLOCK_CRACK, entity.getLocation().add(0, 1, 0), 50, 0.2, 0.2, 0.2, 0, new MaterialData(Material.COAL_BLOCK));
                                        entity.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_BLAST, 1, 1);
                                        if (entity instanceof LivingEntity) {
                                            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, true));
                                        }
                                        break outer;
                                    }
                                }
                            }
                        }
                        if (i > 19){
                            p.teleport(loc);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, true));
                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 5, 1);
                        }
                        loc.subtract(x, y, z);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onUseEnderSmoke(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.FIREWORK_CHARGE && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_BLUE + "エンダースモーク")){
                e.setCancelled(true);
                if (checkUseMana(p, 8)) {
                    removeMana(p, 8);
                    Cooldown.setCooldown(p, getAbilityData().getInt("EnderSmoke.cooltime"), "EnderSmoke", p.getInventory().getHeldItemSlot());
                    p.getWorld().spawnParticle(Particle.SMOKE_LARGE, p.getLocation(), 100, 2, 1, 2, 0.5);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 5, 0);
                    // アビリティの処理
                    for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                        if (other.getWorld() == p.getWorld() && other.getLocation().distanceSquared(p.getLocation()) <= 35) {
                            if (!(other == p)) {
                                if (other.getWorld() == p.getWorld()) {
                                    other.damage(3, p);
                                    other.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0, false));
                                    other.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 4, false));
                                    other.getWorld().spawnParticle(Particle.SMOKE_LARGE, other.getLocation().add(0, 1, 0), 50, 0.2, 0.5, 0.2, 0);
                                }
                            }
                        }
                    }
                    //ここまで
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onUseZombieShooter(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.ROTTEN_FLESH && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "ゾンビシューター")){
                e.setCancelled(true);
                if (checkUseMana(p, 2)) {
                    removeMana(p, 2);
                    Cooldown.setCooldown(p, getAbilityData().getInt("ZombieShooter.cooltime"), "ZombieShooter", p.getInventory().getHeldItemSlot());
                    // アビリティの処理
                    p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_HURT, 1, 1.2F);
                    Item dropItem = p.getWorld().dropItem(p.getLocation().add(0, 1, 0), new ItemStack(Material.ROTTEN_FLESH, 1));
                    dropItem.setVelocity(p.getLocation().getDirection().multiply(1.2));
                    dropItem.setCustomName("ZombieShooter");
                    dropItem.setPickupDelay(Integer.MAX_VALUE);
                    new BukkitRunnable(){
                        int delete = 0;
                        @Override
                        public void run(){
                            if (delete >= 30){
                                dropItem.remove();
                                this.cancel();
                            } else if (!(dropItem.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)){
                                dropItem.remove();
                                this.cancel();
                            } else {
                                for (Entity entity : dropItem.getLocation().getChunk().getEntities()){ //チャンク内のエンティティの数だけループする。エンティティをentity変数に入れる
                                    if (entity.getLocation().distance(dropItem.getLocation()) < 2) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                        if (entity != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                            if (entity.getType().isAlive()) { //エンティティが生きているかどうか
                                                Damageable d = (Damageable) entity;
                                                d.damage(3, p);
                                                if (entity instanceof Player victim){
                                                    if (checkUseMana(victim, 2)){
                                                        removeMana(victim, 2);
                                                        addMana(p, 2);
                                                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                        victim.playSound(victim.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 1, 1);
                                                    }
                                                }
                                                dropItem.remove();
                                                this.cancel();
                                            }
                                        }
                                    }
                                }
                            }
                            delete += 1;
                        }
                    }.runTaskTimer(MagicStick.getPlugin(), 0, 1);
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    HashMap<Player, Location> GraveLocation = new HashMap<>();

    @EventHandler
    public void onUseImmortalGrave(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.ENDER_PORTAL_FRAME && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "イモータルグレイブ")) {
                e.setCancelled(true);
                if (checkUseMana(p, 17)) {
                    if (!(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)){
                        if (p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2 < p.getHealth()) {
                            p.setHealth(p.getHealth() - (p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2));
                            removeMana(p, 17);
                            Cooldown.setCooldown(p, getAbilityData().getInt("ImmortalGrave.cooltime"), "ImmortalGrave", p.getInventory().getHeldItemSlot());
                            Item dropItem = p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.SKULL_ITEM, 1, (short) 2));
                            dropItem.setPickupDelay(Integer.MAX_VALUE);
                            dropItem.setVelocity(p.getLocation().getDirection().multiply(0));
                            GraveLocation.put(p, p.getLocation());
                            new BukkitRunnable(){
                                final Location loc = p.getLocation();
                                double angle = 0;
                                final double radius = 1;
                                @Override
                                public void run(){
                                    if (!(Mana.checkMana(p))){
                                        GraveLocation.remove(p);
                                        dropItem.remove();
                                        this.cancel();
                                    } else if (!(GraveLocation.containsKey(p))){
                                        dropItem.remove();
                                        this.cancel();
                                    } else if (angle * 10 >= 20 * 30){
                                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
                                        p.sendMessage(ChatColor.DARK_GREEN + "イモータルグレイブが消失した。");
                                        GraveLocation.remove(p);
                                        dropItem.remove();
                                        this.cancel();
                                    }
                                    double x = (radius * sin(angle));
                                    double z = (radius * cos(angle));
                                    loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.getX()+x, loc.getY(), loc.getZ()+z, 0, 0, 1, 0);
                                    dropItem.teleport(loc);
                                    angle += 0.1;
                                }
                            }.runTaskTimer(MagicStick.getPlugin(), 0, 1);
                        } else {
                            p.sendMessage(ChatColor.RED + "体力が足りません。");
                            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "空中では使用できません。");
                        p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onUseInfernoSpell(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.ENCHANTED_BOOK && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "インフェルノスペル")) {
                e.setCancelled(true);
                if (checkUseMana(p, 20)){
                    removeMana(p, 20);
                    Cooldown.setCooldown(p, getAbilityData().getInt("InfernoSpell.cooltime"), "InfernoSpell", p.getInventory().getHeldItemSlot());
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 10, 0);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 10, 1);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 10, 0.7f);
                    new BukkitRunnable(){
                        double t = Math.PI/4;
                        final Location loc = p.getLocation();
                        public void run(){
                            t = t + 0.1*Math.PI;
                            for (double theta = 0; theta <= 2*Math.PI; theta = theta + Math.PI/16){
                                double x = t*cos(theta);
                                double y = 2*Math.exp(-0.1*t) * sin(t) + 1.5;
                                double z = t*sin(theta);
                                loc.add(x,y,z);
                                loc.getWorld().spawnParticle(Particle.FLAME, loc, 0, 0, 0, 0, 1);
                                for (Entity entity : loc.getChunk().getEntities()){
                                    if (entity.getLocation().distance(loc) < 2) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                        if (entity != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                            if (entity.getType().isAlive()) { //エンティティが生きているかどうか
                                                Damageable d = (Damageable) entity;
                                                d.damage(8, p);
                                            }
                                        }
                                    }
                                }
                                loc.subtract(x,y,z);
                            }
                            if (t > 20){
                                this.cancel();
                            }
                        }

                    }.runTaskTimer(MagicStick.getPlugin(), 0, 1);
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void DamageByEntityEvent(EntityDamageByEntityEvent ev){
        if (ev.getDamager() instanceof Player attacker){
            if (DamageCharge.containsKey(attacker.getUniqueId())){
                if (DamageCharge.get(attacker.getUniqueId()) > 0){
                    attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1, 1);
                    ev.setDamage(ev.getDamage() + DamageCharge.get(attacker.getUniqueId()));
                    DamageCharge.remove(attacker.getUniqueId());
                }
            }
        }
        if (ev.getEntity() instanceof Player victim){
            if (Alive.contains(victim)){
                if (((victim.getHealth() - ev.getDamage()) <= 0)){
                    ev.setCancelled(true);
                    if (ShapeMob.containsKey(victim.getUniqueId())){
                        ShapeMob.remove(victim.getUniqueId());
                        BeforeHealth.remove(victim.getUniqueId());
                        victim.setOp(true);
                        victim.performCommand("undis");
                        victim.setOp(false);
                    }
                    if (GraveLocation.containsKey(victim)){
                        victim.teleport(GraveLocation.get(victim));
                        int maxhealth = 0;
                        for (String type : getPlayerData().getStringList(victim.getUniqueId().toString() + ".customkit." +
                                getPlayerData().getString(victim.getUniqueId().toString() + ".customkit.select") + ".type")) {
                            maxhealth += getTypeData().getInt(type + ".health");
                        }
                        victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxhealth);
                        victim.setHealth(1);
                        victim.getWorld().playSound(GraveLocation.get(victim), Sound.ITEM_TOTEM_USE, 20, 1);
                        victim.getWorld().spawnParticle(Particle.LAVA, victim.getLocation().add(0, 1, 0), 100, 1, 1, 1, 1);
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 220, 1, false));
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, true));
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 10, true));
                        GraveLocation.remove(victim);
                    } else {
                        Mana.mana.remove(victim.getUniqueId());
                        victim.setGameMode(GameMode.SPECTATOR);
                        Alive.remove(victim);
                        Spectator.add(victim);
                        victim.getWorld().spawnParticle(Particle.SPIT, victim.getLocation().add(0, 1, 0), 50, 0.2, 0.5, 0.2, 0.1);
                        victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_METAL_BREAK, 5, 1.3F);
                        victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_METAL_BREAK, 5, 1F);
                        victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_METAL_BREAK, 5, 0.7F);
                        if (Alive.size() == 1){
                            Join.GameEnd("Normal");
                        }
                    }
                } else if (BoneVeil.containsKey(victim)) {
                    if (ev.getDamager() instanceof Player attacker){
                        if (!(BoneVeil.containsKey(attacker))){
                            ev.setDamage(ev.getFinalDamage() / 2);
                            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_SKELETON_DEATH, 5, 1.5F);
                            for (int i = 0; i < 10; i++){
                                Item item = victim.getWorld().dropItem(victim.getLocation().add(0, 1, 0), new ItemStack(Material.BONE, 1));
                                try{
                                    item.setPickupDelay(Integer.MAX_VALUE);
                                    Field itemField = item.getClass().getDeclaredField("item");
                                    Field ageField;
                                    Object entityItem;
                                    itemField.setAccessible(true);
                                    entityItem = itemField.get(item);
                                    ageField = entityItem.getClass().getDeclaredField("age");
                                    ageField.setAccessible(true);
                                    ageField.set(entityItem, 6000 - (3 * 20));
                                } catch (NoSuchFieldException | IllegalAccessException e){
                                    e.printStackTrace();
                                }
                            }
                            Damageable d = (Damageable) ev.getDamager();
                            d.damage((ev.getDamage()), victim);
                        }
                    }
                } else {
                    String select = getPlayerData().getString(victim.getUniqueId().toString() + ".customkit.select");
                    List<String> type = getPlayerData().getStringList(victim.getUniqueId().toString() + ".customkit." + select + ".type");
                    for (String pt : type){
                        if (pt.equalsIgnoreCase("skeleton")){
                            if (ev.getFinalDamage() >= 8.0){
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5 * 20, 0, true));
                                victim.sendMessage(ChatColor.WHITE + "パッシブ発動: " + ChatColor.GRAY + "スケルトン");
                            }
                        }
                    }
                }
            }
            if (mana.containsKey(victim.getUniqueId())){
                int damage = (int) ev.getFinalDamage();
                addMana(victim, damage);
            }
        }
    }

    @EventHandler
    public void onShootArrowEvent(ProjectileHitEvent e){
        if (e.getEntity().getShooter() instanceof Player p){
            if (e.getEntity().getCustomName().equalsIgnoreCase("Grappling")){
                if (e.getHitBlock() == null){
                    Location PlayerLocation = p.getLocation();
                    Location ArrowLocation = e.getEntity().getLocation();
                    Location change = ArrowLocation.subtract(PlayerLocation);
                    p.setVelocity(change.toVector().multiply(0.15));
                    p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_RETRIEVE, 10, 0.7F);
                    p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_RETRIEVE, 10, 0.5F);
                    p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_RETRIEVE, 10, 0.3F);
                    e.getEntity().remove();
                } else {
                    if (e.getHitBlock().getType() == Material.BARRIER){
                        e.getEntity().remove();
                    } else {
                        Location PlayerLocation = p.getLocation();
                        Location ArrowLocation = e.getEntity().getLocation();
                        Location change = ArrowLocation.subtract(PlayerLocation);
                        p.setVelocity(change.toVector().multiply(0.15));
                        p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_RETRIEVE, 10, 0.7F);
                        p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_RETRIEVE, 10, 0.5F);
                        p.playSound(p.getLocation(), Sound.ENTITY_BOBBER_RETRIEVE, 10, 0.3F);
                        e.getEntity().remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerDoNotMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if (DoNotMove.containsKey(player)){
            player.teleport(DoNotMove.get(player));
        }
    }

    @EventHandler
    public void DropItemEvent(PlayerDropItemEvent e){
        if (!(e.getPlayer().isOp())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void PickUpItemEvent(EntityPickupItemEvent e){
        if (e.getEntity() instanceof Player p){
            if (!(p.isOp())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void NoOffHand(PlayerSwapHandItemsEvent e){
        Player p = e.getPlayer();
        if (!(p.isOp()) || GamePlayer.contains(p)) {
            e.setCancelled(true);
        }
    }
}
