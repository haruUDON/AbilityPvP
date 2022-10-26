package haruudon.udon.magicstick.events;

import haruudon.udon.magicstick.*;
import haruudon.udon.magicstick.cooldown.Cooldown;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
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
import java.util.UUID;

import static haruudon.udon.magicstick.GameMain.*;
import static haruudon.udon.magicstick.MagicStick.*;
import static haruudon.udon.magicstick.Mana.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class AbilityEvent implements Listener {
    HashMap<Player, Location> lastLocation = new HashMap<>();

    @EventHandler
    public void onUseRewindTime(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.CHORUS_FRUIT_POPPED && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "リワインドタイム")) {
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 7)) {
                    removeMana(p, 7);
                    lastLocation.put(p, p.getLocation());
                    p.playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1, 0);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), null);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), getData("ability").getItemStack("RewindTime.item2"));
                        }
                    }.runTaskLater(MagicStick.getPlugin(), 1);
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            } else if (item.getType() == Material.WATCH && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "マジックウォッチ")) {
                p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 50, 0.5, 1.5, 0.5, 0.5);
                p.teleport(lastLocation.get(p));
                Cooldown.setCooldown(p, getData("ability").getInt("RewindTime.cooltime"), "RewindTime", p.getInventory().getHeldItemSlot()); //クールタイムセット
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

    public static HashMap<UUID, String> ShapeMob = new HashMap<>();
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 4)) {
                    removeMana(p, 3);
                    ShapeMob.put(p.getUniqueId(), "Bat");
                    BeforeHealth.put(p.getUniqueId(), p.getHealth());
                    int slot = p.getInventory().getHeldItemSlot();
                    String cmd1 = "lp user %player% permission set idisguise.*";
                    cmd1 = cmd1.replace("%player%", p.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd1); //disコマンドを使用するための権限を与える
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 10, true));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_LOOP, 10, 1);
                    p.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, p.getLocation(), 50, 0.5, 1.5, 0.5, 0.5);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), null);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2);
                    p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), getData("ability").getItemStack("VampireWing.item2"));
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
                                for (String type : getData("player").getStringList(p.getUniqueId().toString() + ".customkit." +
                                        getData("player").getString(p.getUniqueId().toString() + ".customkit.select") + ".type")) {
                                    maxhealth += getData("type").getInt(type + ".health");
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
                                        Cooldown.setCooldown(p, getData("ability").getInt("VampireWing.cooltime"), "VampireWing", slot);
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
                for (String type : getData("player").getStringList(p.getUniqueId().toString() + ".customkit." +
                        getData("player").getString(p.getUniqueId().toString() + ".customkit.select") + ".type")) {
                    maxhealth += getData("type").getInt(type + ".health");
                }
                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxhealth);
                p.setHealth(BeforeHealth.get(p.getUniqueId()));
                BeforeHealth.remove(p.getUniqueId());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Cooldown.setCooldown(p, getData("ability").getInt("VampireWing.cooltime"), "VampireWing", p.getInventory().getHeldItemSlot());
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 5)) {
                    removeMana(p, 5);
                    Cooldown.setCooldown(p, getData("ability").getInt("VampireHeart.cooltime"), "VampireHeart", p.getInventory().getHeldItemSlot());
                    // アビリティの処理
                    for (Player other : Alive) {
                        if (other.getWorld() == p.getWorld() && other.getLocation().distanceSquared(p.getLocation()) <= 16) {
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                if (checkUseMana(p, 3)) {
                    removeMana(p, 3);
                    Cooldown.setCooldown(p, getData("ability").getInt("FriezeSpell.cooltime"), "FriezeSpell", p.getInventory().getHeldItemSlot());
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
                            for (Player player : loc.getChunk().getWorld().getPlayers()) { //チャンク内のエンティティの数だけループする。エンティティをentity変数に入れる
                                if (player.getLocation().distance(loc) < 2) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                    if (player != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                        if (Alive.contains(player)) { //エンティティが生きているかどうか
                                            p.getWorld().spawnParticle(Particle.SNOWBALL, loc, 50, 0, 0, 0, 1);
                                            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0, 1, 0), 100, 0.5, 1, 0.5, 0, new MaterialData(Material.ICE));
                                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 0);
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 9, true));
                                            player.damage(5, p);
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 4)) {
                    if (p.getHealth() < p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()){
                        removeMana(p, 4);
                        Cooldown.setCooldown(p, getData("ability").getInt("HealSpell.cooltime"), "HealSpell", p.getInventory().getHeldItemSlot());
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                MaterialData data = item.getData();
                if (data instanceof Dye dye) {
                    if (dye.getColor() == DyeColor.RED) {
                        if (checkUseMana(p, 10)) {
                            if (p.getHealth() > 1) {
                                removeMana(p, 10);
                                p.getInventory().setItem(p.getInventory().getHeldItemSlot(), null);
                                Cooldown.setCooldown(p, getData("ability").getInt("VampireBlood.cooltime"), "VampireBlood", p.getInventory().getHeldItemSlot());
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                if (checkUseMana(p, 17)) {
                    removeMana(p, 17);
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
                        p.getWorld().spawnParticle(Particle.CRIT, loc, 1, 0, 0, 0, 0.05);
                        if (loc.getBlock().getType().isSolid()) {
                            Cooldown.setCooldown(p, getData("ability").getInt("SkullSniper.cooltime"), "SkullSniper", p.getInventory().getHeldItemSlot());
                            break;
                        }
                        for (Player player : loc.getChunk().getWorld().getPlayers()) { //チャンク内のエンティティの数だけループする。エンティティをentity変数に入れる
                            if (player.getLocation().distance(loc) < 1.5) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                if (player != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                    if (Alive.contains(player)) { //エンティティが生きているかどうか
                                        Cooldown.setCooldown(p, (getData("ability").getInt("SkullSniper.cooltime") - 20), "SkullSniper", p.getInventory().getHeldItemSlot());
                                        p.getWorld().spawnParticle(Particle.CRIT, loc, 50, 0, 0, 0, 1);
                                        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation(), 100, 0.5, 1, 0.5, 0, new MaterialData(Material.BONE_BLOCK));
                                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 0);
                                        player.damage(12, p);
                                        break outer;
                                    }
                                }
                            }
                        }
                        loc.subtract(x, y, z);
                        if (i == 39){
                            Cooldown.setCooldown(p, getData("ability").getInt("SkullSniper.cooltime"), "SkullSniper", p.getInventory().getHeldItemSlot());
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
    public void onUseGrapplingArrow(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (a.equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.LEASH && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "グラップリングアロウ")) {
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                if (checkUseMana(p, 7)) {
                    removeMana(p, 7);
                    Cooldown.setCooldown(p, getData("ability").getInt("GrapplingArrow.cooltime"), "GrapplingArrow", p.getInventory().getHeldItemSlot());
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 4)){
                    removeMana(p, 4);
                    Cooldown.setCooldown(p, getData("ability").getInt("RefreshDebuff.cooltime"), "RefreshDebuff", p.getInventory().getHeldItemSlot());
                    p.removePotionEffect(PotionEffectType.SLOW);
                    p.removePotionEffect(PotionEffectType.WEAKNESS);
                    p.removePotionEffect(PotionEffectType.POISON);
                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                    p.removePotionEffect(PotionEffectType.CONFUSION);
                    p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    p.removePotionEffect(PotionEffectType.GLOWING);
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 11)) {
                    removeMana(p, 11);
                    Cooldown.setCooldown(p, getData("ability").getInt("BoneVeil.cooltime"), "BoneVeil", p.getInventory().getHeldItemSlot());
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                if (checkUseMana(p, 5)){
                    removeMana(p, 5);
                    e.setCancelled(true);
                    Cooldown.setCooldown(p, getData("ability").getInt("EnderTeleport.cooltime"), "EnderTeleport", p.getInventory().getHeldItemSlot());
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
                        for (Player player : loc.getChunk().getWorld().getPlayers()){ //チャンク内のエンティティの数だけループする。エンティティをentity変数に入れる
                            if (player.getLocation().distance(loc) < 2) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                if (player != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                    if (Alive.contains(player)) { //エンティティが生きているかどうか
                                        double Tx = direction.getX() * 1;
                                        double Ty = direction.getY() * 2;
                                        double Tz = direction.getZ() * 1;
                                        p.teleport(loc.subtract(Tx, Ty, Tz));
                                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 5, 1);
                                        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0, 1, 0), 50, 0.2, 0.2, 0.2, 0, new MaterialData(Material.COAL_BLOCK));
                                        player.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_BLAST, 1, 1);
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, true));
                                        player.damage(3, p);
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 8)) {
                    removeMana(p, 8);
                    Cooldown.setCooldown(p, getData("ability").getInt("EnderSmoke.cooltime"), "EnderSmoke", p.getInventory().getHeldItemSlot());
                    p.getWorld().spawnParticle(Particle.SMOKE_LARGE, p.getLocation(), 100, 2, 1, 2, 0.5);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 5, 0);
                    // アビリティの処理
                    for (Player other : Alive) {
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 2)) {
                    removeMana(p, 2);
                    Cooldown.setCooldown(p, getData("ability").getInt("ZombieShooter.cooltime"), "ZombieShooter", p.getInventory().getHeldItemSlot());
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
                            } else {
                                for (Player player : dropItem.getLocation().getChunk().getWorld().getPlayers()){ //チャンク内のエンティティの数だけループする。エンティティをentity変数に入れる
                                    if (player.getLocation().distance(dropItem.getLocation()) < 2) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                        if (player != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                            if (Alive.contains(player)) { //エンティティが生きているかどうか
                                                if (checkUseMana(player, 2)){
                                                    removeMana(player, 2);
                                                    addMana(p, 2);
                                                    p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                    player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 1, 1);
                                                    player.damage(2, p);
                                                    dropItem.remove();
                                                    this.cancel();
                                                }
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 10)) {
                    if (!(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)){
                        if (p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 3 < p.getHealth()) {
                            p.setHealth(p.getHealth() - (p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 3));
                            removeMana(p, 10);
                            Cooldown.setCooldown(p, getData("ability").getInt("ImmortalGrave.cooltime"), "ImmortalGrave", p.getInventory().getHeldItemSlot());
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
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 20)){
                    removeMana(p, 20);
                    Cooldown.setCooldown(p, getData("ability").getInt("InfernoSpell.cooltime"), "InfernoSpell", p.getInventory().getHeldItemSlot());
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
                                for (Player player : loc.getChunk().getWorld().getPlayers()){
                                    if (player.getLocation().distance(loc) < 2) { //エンティティの半径1.5の範囲内にlocがあるか確認
                                        if (player != (p)) { //エンティティがクリックしたプレイヤーじゃないか
                                            if (Alive.contains(player)) { //エンティティが生きているかどうか
                                                player.damage(8, p);
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
    public void onUseZombiePower(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.BLAZE_POWDER && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "ゾンビパワー")) {
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 7)) {
                    if (!(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)){
                        removeMana(p, 7);
                        Cooldown.setCooldown(p, getData("ability").getInt("ZombiePower.cooltime"), "ZombiePower", p.getInventory().getHeldItemSlot());
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, 5, 0);
                        Location loc = p.getLocation();
                        loc.setPitch(90);
                        p.teleport(loc);
                        Block middleBlock = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
                        int radius = 5;
                        for (int x = radius; x >= -radius; x--) {
                            for (int y = 1; y >= -1; y--) {
                                for (int z = radius; z >= -radius; z--) {
                                    Block block = middleBlock.getRelative(x, y, z);
                                    if (block.getRelative(BlockFace.UP).getType() == Material.AIR){
                                        FallingBlock fallingBlock = p.getWorld().spawnFallingBlock(block.getLocation().add(0, 1, 0), block.getType(), block.getData());
                                        Location change = fallingBlock.getLocation().add(0, 2, 0).subtract(fallingBlock.getLocation());
                                        fallingBlock.setVelocity(change.toVector().multiply(0.15));
                                        fallingBlock.setDropItem(false);
                                    }
                                }
                            }
                        }
                        for (Player other : Alive) {
                            if (other.getWorld() == p.getWorld() && other.getLocation().distanceSquared(p.getLocation()) <= 25){
                                if (!(other == p)) {
                                    if (other.getWorld() == p.getWorld()) {
                                        other.damage(7, p);
                                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                        Location PlayerLocation = p.getLocation();
                                        Location OtherLocation = other.getLocation();
                                        Location change = OtherLocation.add(0, 1, 0).subtract(PlayerLocation);
                                        other.setVelocity(change.toVector().multiply(1));
                                    }
                                }
                            }
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
    public void onUseCreeperGrenade(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.TNT && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "クリーパーグレネード")){
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 7)) {
                    removeMana(p, 7);
                    Cooldown.setCooldown(p, getData("ability").getInt("CreeperGrenade.cooltime"), "CreeperGrenade", p.getInventory().getHeldItemSlot());
                    // アビリティの処理
                    p.playSound(p.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1, 1);
                    Item dropItem = p.getWorld().dropItem(p.getLocation().add(0, 1, 0), new ItemStack(Material.TNT, 1));
                    dropItem.setVelocity(p.getLocation().getDirection().multiply(1.3));
                    dropItem.setCustomName("CreeperGrenade");
                    dropItem.setPickupDelay(Integer.MAX_VALUE);
                    new BukkitRunnable(){
                        int delete = 0;
                        @Override
                        public void run(){
                            if (delete >= 80){
                                dropItem.remove();
                                p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, dropItem.getLocation(), 1, 0, 0, 0, 0.1);
                                p.getWorld().playSound(dropItem.getLocation(), Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 3, 2);
                                for (Player other : Alive) {
                                    if (other.getWorld() == dropItem.getWorld() && other.getLocation().distanceSquared(dropItem.getLocation()) <= 9){
                                        if (!(other == p)) {
                                            if (other.getWorld() == dropItem.getWorld()) {
                                                other.damage(6, p);
                                                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                Location change = other.getLocation().add(0, 2, 0).subtract(other.getLocation());
                                                other.setVelocity(change.toVector().multiply(0.15));
                                            }
                                        }
                                    }
                                }
                                this.cancel();
                            } else if (delete >= 30){
                                p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, dropItem.getLocation().add(0, 0.5, 0), 1, 0, 0, 0, 0);
                                for (Player other : Alive) {
                                    if (other.getWorld() == dropItem.getWorld() && other.getLocation().distanceSquared(dropItem.getLocation()) <= 4) {
                                        if (!(other == p)) {
                                            if (other.getWorld() == dropItem.getWorld()) {
                                                p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, dropItem.getLocation(), 1, 0, 0, 0, 0.1);
                                                p.getWorld().playSound(dropItem.getLocation(), Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 3, 2);
                                                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                Location change = other.getLocation().add(0, 2, 0).subtract(other.getLocation());
                                                other.setVelocity(change.toVector().multiply(0.15));
                                                other.damage(6, p);
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

    @EventHandler
    public void onUseBombJump(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.SULPHUR && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "ボムジャンプ")) {
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 8)){
                    removeMana(p, 8);
                    Cooldown.setCooldown(p, getData("ability").getInt("BombJump.cooltime"), "BombJump", p.getInventory().getHeldItemSlot());
                    p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation(), 5, 0, 0, 0, 2);
                    p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 50, 0.2, 0.2, 0.2, 0.2);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 1);
                    Location PlayerLocation = p.getLocation();
                    Location PlayerAddLocation = p.getLocation().add(0, 5, 0);
                    Location change = PlayerAddLocation.subtract(PlayerLocation);
                    p.setVelocity(change.toVector().multiply(0.5));
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onUseExplodePunch(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player attacker){
            if (e.getEntity() instanceof Player victim){
                ItemStack item = attacker.getInventory().getItemInMainHand();
                ItemMeta itemMeta = item.getItemMeta();
                if (item.getType() == Material.COAL && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "エクスプロードパンチ")){
                    if (ShapeMob.containsKey(attacker.getUniqueId())){
                        attacker.sendMessage(ChatColor.RED + "変身中は使用できません。");
                        attacker.playSound(attacker.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                        return;
                    }
                    if (Alive.contains(victim)) {
                        if (((victim.getHealth() - e.getDamage()) > 0)) {
                            if (checkUseMana(attacker, 11)){
                                removeMana(attacker, 11);
                                Cooldown.setCooldown(attacker, getData("ability").getInt("ExplodePunch.cooltime"), "ExplodePunch", attacker.getInventory().getHeldItemSlot());
                                Location AttackerLocation = attacker.getLocation();
                                Location VictimLocation = victim.getLocation();
                                victim.getWorld().spawnParticle(Particle.CRIT_MAGIC, VictimLocation.add(0, 1.5, 0), 50, 0.2, 0.2, 0.2, 0.5);
                                victim.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, VictimLocation.add(0, 1.5, 0), 1, 0, 0, 0, 0.1);
                                victim.getWorld().playSound(VictimLocation.add(0, 1.5, 0), Sound.ENTITY_FIREWORK_LARGE_BLAST, 1, 0);
                                victim.getWorld().playSound(VictimLocation.add(0, 1.5, 0), Sound.ENTITY_FIREWORK_LARGE_BLAST, 1, 0.7F);
                                victim.getWorld().playSound(VictimLocation.add(0, 1.5, 0), Sound.ENTITY_FIREWORK_LARGE_BLAST, 1, 1);
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 3, true));
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 6, true));
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 0, true));
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, true));
                                Location change = VictimLocation.add(0, 1, 0).subtract(AttackerLocation);
                                victim.setVelocity(change.toVector().multiply(1));
                            } else {
                                attacker.sendMessage(ChatColor.RED + "マナが足りません。");
                                attacker.playSound(attacker.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onUseCreeperExplosion(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (!(e.getHand() == EquipmentSlot.HAND)) return;
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getType() == Material.SKULL_ITEM && itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "クリーパーエクスプロージョン")){
                if (ShapeMob.containsKey(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "変身中は使用できません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return;
                }
                e.setCancelled(true);
                if (checkUseMana(p, 14)){
                    removeMana(p, 14);
                    Cooldown.setCooldown(p, getData("ability").getInt("CreeperExplosion.cooltime"), "CreeperExplosion", p.getInventory().getHeldItemSlot());
                    ShapeMob.put(p.getUniqueId(), "creeper");
                    p.removePotionEffect(PotionEffectType.SPEED);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2, true));
                    p.setOp(true);
                    p.performCommand("dis creeper");
                    p.setOp(false);
                    new BukkitRunnable(){
                        int ExplodeTimer = 4;
                        @Override
                        public void run() {
                            if (!(ShapeMob.containsKey(p.getUniqueId()))){
                                this.cancel();
                            } else if (ExplodeTimer == 0){
                                p.removePotionEffect(PotionEffectType.SPEED);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true));
                                ShapeMob.remove(p.getUniqueId());
                                p.setOp(true);
                                p.performCommand("undis");
                                p.setOp(false);
                                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 0);
                                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 0.7f);
                                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
                                p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation().add(0, 1, 0), 10, 1, 1, 1, 1);
                                for (Player other : Alive){
                                    if (other.getWorld() == p.getWorld() && other.getLocation().distanceSquared(p.getLocation()) <= 16){
                                        if (other != p) {
                                            if (other.getWorld() == p.getWorld()) {
                                                Location PlayerLocation = p.getLocation();
                                                Location OtherLocation = other.getLocation();
                                                Location change = OtherLocation.add(0, 1, 0).subtract(PlayerLocation);
                                                other.setVelocity(change.toVector().multiply(2));
                                                other.damage(18, p);
                                            }
                                        }
                                    }
                                }
                                this.cancel();
                            } else {
                                p.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, p.getLocation(), 50, 0.5, 0.5, 0.5, 1);
                                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5, 1);
                                ExplodeTimer--;
                            }
                        }
                    }.runTaskTimer(MagicStick.getPlugin(), 0, 20);
                } else {
                    p.sendMessage(ChatColor.RED + "マナが足りません。");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void BlockPlaceEvent(EntityChangeBlockEvent e){
        if (e.getEntityType() == EntityType.FALLING_BLOCK) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void DamageByEntityEvent(EntityDamageByEntityEvent ev){
        if (ev.getDamager() instanceof Player attacker){
            if (ev.getEntity() instanceof Player victim){
                if (DamageCharge.containsKey(attacker.getUniqueId())){
                    if (DamageCharge.get(attacker.getUniqueId()) > 0){
                        attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1, 1);
                        ev.setDamage(ev.getDamage() + DamageCharge.get(attacker.getUniqueId()));
                        DamageCharge.remove(attacker.getUniqueId());
                    }
                }
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
                            for (String type : getData("player").getStringList(victim.getUniqueId().toString() + ".customkit." +
                                    getData("player").getString(victim.getUniqueId().toString() + ".customkit.select") + ".type")) {
                                maxhealth += getData("type").getInt(type + ".health");
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
                            Decoration.KillEffect(attacker, victim);
                            Decoration.KillMassage(attacker, victim);
                            victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_METAL_BREAK, 5, 1.3F);
                            victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_METAL_BREAK, 5, 1F);
                            victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_METAL_BREAK, 5, 0.7F);
                            KillCount.put(attacker, KillCount.get(attacker) + 1);
                            if (Alive.size() == 1){
                                GameMain.GameEnd("Normal");
                            }
                        }
                    } else if (BoneVeil.containsKey(victim)) {
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
                    } else {
                        if (TypeEvent.CheckType(victim, "skeleton")){
                            if (ev.getFinalDamage() >= 8.0){
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5 * 20, 0, true));
                                victim.sendMessage(ChatColor.WHITE + "パッシブ発動: " + ChatColor.GRAY + "スケルトン");
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
