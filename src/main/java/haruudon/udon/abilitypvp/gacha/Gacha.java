package haruudon.udon.abilitypvp.gacha;

import haruudon.udon.abilitypvp.CoinAndMagicOre;
import haruudon.udon.abilitypvp.GUIManager;
import haruudon.udon.abilitypvp.AbilityPvP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static haruudon.udon.abilitypvp.AbilityPvP.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Gacha {
    public static Map<Player, ArrayList<String>> GachaResultRarity;
    public static Map<Player, ArrayList<GachaItem>> GachaResultItem;
    public static Map<Player, String> NowOpenGUI;

    public static void setupGachaResult(){
        GachaResultRarity = new HashMap<>();
        GachaResultItem = new HashMap<>();
        NowOpenGUI = new HashMap<>();
        Location location = (Location) getData("block").get("Gacha");
        new BukkitRunnable(){
            double angle = 0;
            final double radius = 1;
            @Override
            public void run() {
                location.add(0.5, 0.3, 0.5);
                double x = (radius * sin(angle));
                double z = (radius * cos(angle));
                location.getWorld().spawnParticle(Particle.SPELL_WITCH, location.getX()+x, location.getY(), location.getZ()+z, 0, 0, 1, 0);
                location.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, location.getX(), location.getY()+1.5, location.getZ(), 0, 0, 1.3, 2.3);
                angle += 0.1;
                location.subtract(0.5, 0.3, 0.5);
            }
        }.runTaskTimer(AbilityPvP.getPlugin(), 0, 1);
    }
    public static void openGacha(Player p, int count){
        String uuidS = p.getUniqueId().toString();
        GachaResultRarity.remove(p);
        GachaResultItem.remove(p);
        ArrayList<String> resultRarity = new ArrayList<>();
        ArrayList<GachaItem> items = new ArrayList<>();
        ArrayList<GachaItem> notHave = new ArrayList<>();
        ArrayList<GachaItem> resultItem = new ArrayList<>();
        for (int i = 0; i < count; i++){
            Random random = new Random();
            int chance = random.nextInt(100) + 1;
            if (chance < 58) {
                resultRarity.add("common");
            } else if (chance < 86) {
                resultRarity.add("rare");
            } else if (chance < 96) {
                resultRarity.add("epic");
            } else {
                resultRarity.add("legendary");
            }
        }
        if (NowOpenGUI.get(p).equalsIgnoreCase("decoration")) {
            String[] dataList = {"killeffect", "killmessage"};
            for (String data : dataList){
                for (String s : getData(data).getStringList("All")){
                    if (s.equals("NullEffect") || s.equals("NullMessage")) continue;
                    items.add(new GachaItem(s, data));
                    if (getData("player").getStringList(uuidS + ".have." + data).contains(s)) continue;
                    notHave.add(new GachaItem(s, data));
                }
            }
        }
        for (String k : resultRarity){
            ArrayList<GachaItem> rarity = new ArrayList<>();
            for (GachaItem i : items){
                String item = i.item();
                String data = i.data();
                if (getData(data).getString(item + ".rarity").equalsIgnoreCase(k)) rarity.add(new GachaItem(item, data));
            }
            Collections.shuffle(rarity);
            GachaItem get = rarity.get(0);
            if (notHave.contains(get)){
                resultItem.add(get);
                List<String> have = getData("player").getStringList(uuidS + ".have." + get.data());
                have.add(get.item());
                getData("player").set(uuidS + ".have." + get.data(), have);
                savePlayerData();
            } else {
                resultItem.add(new GachaItem(k, "magicdust"));
                if (k.equalsIgnoreCase("common")) CoinAndMagicOre.addItems(p, "magicdust", 5);
                if (k.equalsIgnoreCase("rare")) CoinAndMagicOre.addItems(p, "magicdust", 10);
                if (k.equalsIgnoreCase("epic")) CoinAndMagicOre.addItems(p, "magicdust", 50);
                if (k.equalsIgnoreCase("legendary")) CoinAndMagicOre.addItems(p, "magicdust", 100);
            }
        }
        GachaResultRarity.put(p, resultRarity);
        GachaResultItem.put(p, resultItem);
        GUIManager.OpenGacha(p, count);
    }



    public static Inventory createGachaResult(){
        return Bukkit.createInventory(null, 18, ChatColor.DARK_GRAY + "結果");
    }

    public static void loadGachaResult(Inventory inv, ItemStack item, int slot){
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set((lore.size() - 1), ChatColor.WHITE + "入手");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
}
