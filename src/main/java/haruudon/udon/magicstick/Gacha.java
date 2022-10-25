package haruudon.udon.magicstick;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static haruudon.udon.magicstick.MagicStick.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Gacha {
    public static Map<Player, ArrayList<String>> GachaResultRarity;
    public static Map<Player, ArrayList<String>> GachaResultItem;
    public static Map<Player, String> NowOpenGUI;

    public static void setupGachaResult(){
        GachaResultRarity = new HashMap<>();
        GachaResultItem = new HashMap<>();
        NowOpenGUI = new HashMap<>();
        Location location = (Location) getData("block").get("Crate");
        new BukkitRunnable(){
            double angle = 0;
            final double radius = 1;
            final Location loc = location.add(0.5, 1, 0.5);
            @Override
            public void run() {
                double x = (radius * sin(angle));
                double z = (radius * cos(angle));
                loc.getWorld().spawnParticle(Particle.SPELL_WITCH, loc.getX()+x, loc.getY(), loc.getZ()+z, 0, 0, 1, 0);
                loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 0, 0, 1, 0);
                angle += 0.1;
            }
        }.runTaskTimer(MagicStick.getPlugin(), 0, 1);
    }
    public static void openGacha(Player p, int count){
        GachaResultRarity.remove(p);
        GachaResultItem.remove(p);
        ArrayList<String> kekka1 = new ArrayList<>();
        ArrayList<String> kekka2 = new ArrayList<>();
        List<String> nakami1 = new ArrayList<>();
        List<String> have = getData("player").getStringList(p.getUniqueId().toString() + ".have.killeffect");
        for (int i = 0; i < count; i++){
            Random random = new Random();
            int chance = random.nextInt(100) + 1;
            if (chance < 58) {
                kekka1.add("common");
            } else if (chance < 86) {
                kekka1.add("rare");
            } else if (chance < 96) {
                kekka1.add("epic");
            } else {
                kekka1.add("legendary");
            }
        }
        if (NowOpenGUI.get(p).equalsIgnoreCase("killeffect")) {
            nakami1 = getData("killeffect").getStringList("All");
            nakami1.remove("NullEffect");
            have.remove("NullEffect");
        } else if (NowOpenGUI.get(p).equalsIgnoreCase("ability")){
            nakami1 = getData("ability").getStringList("All");
            have.remove("NullEffect");
        }
        for (String k : kekka1){
            ArrayList<String> rarity = new ArrayList<>();
            for (String a : nakami1){
                if (getData("killeffect").getString(a + ".rarity").equalsIgnoreCase(k)){
                    rarity.add(a);
                }
            }
            Collections.shuffle(rarity);
            if (!(have.contains(rarity.get(0)))){
                kekka2.add(rarity.get(0));
                have.add(rarity.get(0));
            } else {
                kekka2.add(k);
                if (k.equalsIgnoreCase("common")) CoinAndMagicOre.addMagicDust(p, 5);
                if (k.equalsIgnoreCase("rare")) CoinAndMagicOre.addMagicDust(p, 10);
                if (k.equalsIgnoreCase("epic")) CoinAndMagicOre.addMagicDust(p, 50);
                if (k.equalsIgnoreCase("legendary")) CoinAndMagicOre.addMagicDust(p, 100);
            }
        }
        have.add("NullEffect");
        getData("player").set(p.getUniqueId().toString() + ".have.killeffect", have);
        savePlayerData();
        GachaResultRarity.put(p, new ArrayList<>());
        GachaResultItem.put(p, new ArrayList<>());
        GachaResultRarity.get(p).addAll(kekka1);
        GachaResultItem.get(p).addAll(kekka2);
        GUIManager.OpenGacha(p, count);
    }

    public static Inventory createGachaResult(){
        return Bukkit.createInventory(null, 18, ChatColor.DARK_GRAY + "結果");
    }

    public static void loadGachaResult(Inventory inv, ItemStack item, int slot){
        inv.setItem(slot, item);
    }
}
