package haruudon.udon.magicstick;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static haruudon.udon.magicstick.MagicStick.*;

public class GUIManager {
    public static HashMap<UUID, String> now;

    public static void setupNow(){
        now = new HashMap<>();
    }
    public static void SetAbilityMenu(Player p){
        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        String th = now.get(p.getUniqueId());
        List<String> type = getData("player").getStringList(p.getUniqueId().toString() + ".customkit." + th + ".type");
        List<String> ability = getData("player").getStringList(p.getUniqueId().toString() + ".customkit." + th + ".ability");
        Inventory inventory = Bukkit.createInventory(p,54, getData("player").getString(p.getUniqueId().toString() + ".customkit." + th + ".name"));
        ItemStack reset = new ItemStack(Material.LAVA_BUCKET);
        ItemStack type1 = getData("type").getItemStack(type.get(0) + ".item1");
        ItemStack type2 = getData("type").getItemStack(type.get(1) + ".item1");
        ItemStack weapon = getData("weapon").getItemStack(getData("player").getString(p.getUniqueId().toString() + ".customkit." + th + ".weapon") + ".item1");
        ItemStack ability1 = getData("ability").getItemStack(ability.get(0) + ".item1");
        ItemStack ability2 = getData("ability").getItemStack(ability.get(1) + ".item1");
        ItemStack ability3 = getData("ability").getItemStack(ability.get(2) + ".item1");
        ItemStack ability4 = getData("ability").getItemStack(ability.get(3) + ".item1");
        ItemStack rename = new ItemStack(Material.NAME_TAG);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemStack create = new ItemStack(Material.ANVIL);
        ItemStack cost1 = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
        ItemStack cost2 = new ItemStack(Material.STAINED_GLASS, 1, (short) 0);
        ItemMeta reset_meta = reset.getItemMeta();
        reset_meta.setDisplayName(ChatColor.DARK_RED + "初期値に戻す");
        reset.setItemMeta(reset_meta);
        ItemMeta rename_meta = rename.getItemMeta();
        rename_meta.setDisplayName(ChatColor.GRAY + "名前を変更する");
        rename.setItemMeta(rename_meta);
        ItemMeta back_meta = back.getItemMeta();
        back_meta.setDisplayName(ChatColor.RED + "戻る");
        back.setItemMeta(back_meta);
        ItemMeta create_meta = create.getItemMeta();
        create_meta.setDisplayName(ChatColor.YELLOW + "保存する");
        create.setItemMeta(create_meta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 53; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(0, reset);
        inventory.setItem(3, type1);
        inventory.setItem(5, type2);
        inventory.setItem(13, weapon);
        inventory.setItem(19, ability1);
        inventory.setItem(21, ability2);
        inventory.setItem(23, ability3);
        inventory.setItem(25, ability4);
        inventory.setItem(27, rename);
        inventory.setItem(31, back);
        inventory.setItem(35, create);
        int i = 36;
        if (!(getData("player").getInt(p.getUniqueId().toString() + ".customkit." + th + ".cost") == 0)){
            while (i < 36 + getData("player").getInt(p.getUniqueId().toString() + ".customkit." + th + ".cost")) {
                inventory.setItem(i, cost1);
                i++;
            }
            if (!(i > 53)){
                while (i <= 53){
                    inventory.setItem(i, cost2);
                    i++;
                }
            }
        } else {
            while (i <= 53){
                inventory.setItem(i, cost2);
                i++;
            }
        }
        p.openInventory(inventory);
    }

    public static void MainSetAbilityMenu(Player p){
        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        Inventory inventory = Bukkit.createInventory(p,36, ChatColor.DARK_GRAY + "カスタムキット");
        ItemStack item1 = new ItemStack(Material.WORKBENCH);
        ItemStack item2 = new ItemStack(Material.WORKBENCH);
        ItemStack item3 = new ItemStack(Material.WORKBENCH);
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta item1_meta = item1.getItemMeta();
        item1_meta.setDisplayName(getData("player").getString(p.getUniqueId().toString() + ".customkit.first.name"));
        List<String> lore1 = new ArrayList<>();
        if (getData("player").getBoolean(p.getUniqueId().toString() + ".customkit.first.use")) {
            lore1.add(ChatColor.GREEN + "使用可能");
        } else lore1.add(ChatColor.RED + "使用不可能");
        item1_meta.setLore(lore1);
        item1.setItemMeta(item1_meta);
        ItemMeta item2_meta = item2.getItemMeta();
        item2_meta.setDisplayName(getData("player").getString(p.getUniqueId().toString() + ".customkit.second.name"));
        List<String> lore2 = new ArrayList<>();
        if (getData("player").getBoolean(p.getUniqueId().toString() + ".customkit.second.use")) {
            lore2.add(ChatColor.GREEN + "使用可能");
        } else lore2.add(ChatColor.RED + "使用不可能");
        item2_meta.setLore(lore2);
        item2.setItemMeta(item2_meta);
        ItemMeta item3_meta = item3.getItemMeta();
        item3_meta.setDisplayName(getData("player").getString(p.getUniqueId().toString() + ".customkit.third.name"));
        List<String> lore3 = new ArrayList<>();
        if (getData("player").getBoolean(p.getUniqueId().toString() + ".customkit.third.use")) {
            lore3.add(ChatColor.GREEN + "使用可能");
        } else lore3.add(ChatColor.RED + "使用不可能");
        item3_meta.setLore(lore3);
        item3.setItemMeta(item3_meta);
        ItemMeta close_meta = close.getItemMeta();
        close_meta.setDisplayName(ChatColor.RED + "閉じる");
        close.setItemMeta(close_meta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 35; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(11, item1);
        inventory.setItem(13, item2);
        inventory.setItem(15, item3);
        inventory.setItem(31, close);
        p.openInventory(inventory);
    }

    public static void SelectCustomKit(Player p){
        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        Inventory inventory = Bukkit.createInventory(p,36, ChatColor.DARK_GRAY + "キット選択");
        ItemStack item1 = new ItemStack(Material.CHEST);
        ItemStack item2 = new ItemStack(Material.CHEST);
        ItemStack item3 = new ItemStack(Material.CHEST);
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta item1_meta = item1.getItemMeta();
        item1_meta.setDisplayName(getData("player").getString(p.getUniqueId().toString() + ".customkit.first.name"));
        List<String> lore1 = new ArrayList<>();
        if (getData("player").getBoolean(p.getUniqueId().toString() + ".customkit.first.use")) {
            lore1.add(ChatColor.GREEN + "使用可能");
        } else lore1.add(ChatColor.RED + "使用不可能");
        item1_meta.setLore(lore1);
        item1.setItemMeta(item1_meta);
        ItemMeta item2_meta = item2.getItemMeta();
        item2_meta.setDisplayName(getData("player").getString(p.getUniqueId().toString() + ".customkit.second.name"));
        List<String> lore2 = new ArrayList<>();
        if (getData("player").getBoolean(p.getUniqueId().toString() + ".customkit.second.use")) {
            lore2.add(ChatColor.GREEN + "使用可能");
        } else lore2.add(ChatColor.RED + "使用不可能");
        item2_meta.setLore(lore2);
        item2.setItemMeta(item2_meta);
        ItemMeta item3_meta = item3.getItemMeta();
        item3_meta.setDisplayName(getData("player").getString(p.getUniqueId().toString() + ".customkit.third.name"));
        List<String> lore3 = new ArrayList<>();
        if (getData("player").getBoolean(p.getUniqueId().toString() + ".customkit.third.use")) {
            lore3.add(ChatColor.GREEN + "使用可能");
        } else lore3.add(ChatColor.RED + "使用不可能");
        item3_meta.setLore(lore3);
        item3.setItemMeta(item3_meta);
        ItemMeta close_meta = close.getItemMeta();
        close_meta.setDisplayName(ChatColor.RED + "閉じる");
        close.setItemMeta(close_meta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 35; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(11, item1);
        inventory.setItem(13, item2);
        inventory.setItem(15, item3);
        inventory.setItem(31, close);
        p.openInventory(inventory);
    }

    public static void CheckYesOrNo(Player p, String checkMassage){
        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        Inventory inventory = Bukkit.createInventory(p,9, ChatColor.DARK_RED + checkMassage);
        ItemStack item1 = new ItemStack(Material.CONCRETE, 1, (short) 5);
        ItemStack item2 = new ItemStack(Material.CONCRETE, 1, (short) 14);
        ItemMeta item1_meta = item1.getItemMeta();
        item1_meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "はい");
        item1.setItemMeta(item1_meta);
        ItemMeta item2_meta = item2.getItemMeta();
        item2_meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "いいえ");
        item2.setItemMeta(item2_meta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 8; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(2, item1);
        inventory.setItem(6, item2);
        p.openInventory(inventory);
    }

    public static void SelectType(Player p, String typeNumber){
        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        Inventory inventory = Bukkit.createInventory(p,36, ChatColor.DARK_GRAY + typeNumber + "をセットする");
        ItemStack air = new ItemStack(Material.MONSTER_EGG);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta airMeta = air.getItemMeta();
        airMeta.setDisplayName(ChatColor.WHITE + "何もセットしない");
        air.setItemMeta(airMeta);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "戻る");
        back.setItemMeta(backMeta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 35; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(0, air);
        inventory.setItem(31, back);
        List<String> allType = getData("type").getStringList("All");
        int slot = 1;
        for (String s : allType){
            ItemStack type = getData("type").getItemStack(s + ".item1");
            inventory.setItem(slot, type);
            slot++;
        }
        p.openInventory(inventory);
    }

    public static void SelectAbility(Player p, String abilityNumber){
        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        Inventory inventory = Bukkit.createInventory(p,54, ChatColor.DARK_GRAY + abilityNumber + "をセットする");
        ItemStack legendary = new ItemStack(Material.CONCRETE, 6, (short) 4);
        ItemStack epic = new ItemStack(Material.CONCRETE, 4, (short) 10);
        ItemStack rare = new ItemStack(Material.CONCRETE, 3, (short) 11);
        ItemStack common = new ItemStack(Material.CONCRETE, 2, (short) 7);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemStack air = new ItemStack(Material.THIN_GLASS);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemStack cost1 = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
        ItemStack cost2 = new ItemStack(Material.STAINED_GLASS, 1, (short) 0);
        ItemMeta legendaryMeta = legendary.getItemMeta();
        legendaryMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "レジェンダリー");
        legendary.setItemMeta(legendaryMeta);
        ItemMeta epicMeta = epic.getItemMeta();
        epicMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "エピック");
        epic.setItemMeta(epicMeta);
        ItemMeta rareMeta = rare.getItemMeta();
        rareMeta.setDisplayName(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "レア");
        rare.setItemMeta(rareMeta);
        ItemMeta commonMeta = common.getItemMeta();
        commonMeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "コモン");
        common.setItemMeta(commonMeta);
        ItemMeta airMeta = air.getItemMeta();
        airMeta.setDisplayName(ChatColor.WHITE + "何もセットしない");
        air.setItemMeta(airMeta);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "戻る");
        back.setItemMeta(backMeta);
        for (int i = 0; i <= 35; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(0, legendary);
        inventory.setItem(9, epic);
        inventory.setItem(18, rare);
        inventory.setItem(27, common);
        inventory.setItem(1, air);
        inventory.setItem(10, air);
        inventory.setItem(19, air);
        inventory.setItem(28, air);
        inventory.setItem(8, back);
        List<String> allAbility = getData("ability").getStringList("All");
        List<String> typeAbility = new ArrayList<>();
        int slotLegendary = 2;
        int slotEpic = 11;
        int slotRare = 20;
        int slotCommon = 29;
        for (String s : allAbility){
            List<String> playerType = getData("player").getStringList(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".type");
            for (String pt : playerType){
                if (getData("ability").getString(s + ".type").equals(pt)){
                    typeAbility.add(s);
                }
            }
        }
        for (String ta : typeAbility){
            ItemStack item = getData("ability").getItemStack(ta + ".item1");
            if (getData("ability").getString(ta + ".rarity").equals("legendary")){
                inventory.setItem(slotLegendary, item);
                slotLegendary ++;
            } else if (getData("ability").getString(ta + ".rarity").equals("epic")){
                inventory.setItem(slotEpic, item);
                slotEpic ++;
            } else if (getData("ability").getString(ta + ".rarity").equals("rare")){
                inventory.setItem(slotRare, item);
                slotRare ++;
            } else if (getData("ability").getString(ta + ".rarity").equals("common")){
                inventory.setItem(slotCommon, item);
                slotCommon ++;
            }
        }
        int i = 36;
        if (!(getData("player").getInt(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".cost") == 0)){
            while (i < 36 + getData("player").getInt(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".cost")) {
                inventory.setItem(i, cost1);
                i++;
            }
            if (!(i > 53)){
                while (i <= 53){
                    inventory.setItem(i, cost2);
                    i++;
                }
            }
        } else {
            while (i <= 53){
                inventory.setItem(i, cost2);
                i++;
            }
        }
        p.openInventory(inventory);
    }

    public static void SelectWeapon(Player p){
        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        Inventory inventory = Bukkit.createInventory(p,54, ChatColor.DARK_GRAY + "武器をセットする");
        ItemStack legendary = new ItemStack(Material.CONCRETE, 6, (short) 4);
        ItemStack epic = new ItemStack(Material.CONCRETE, 4, (short) 10);
        ItemStack rare = new ItemStack(Material.CONCRETE, 3, (short) 11);
        ItemStack common = new ItemStack(Material.CONCRETE, 2, (short) 7);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemStack air = new ItemStack(Material.GLASS);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemStack cost1 = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
        ItemStack cost2 = new ItemStack(Material.STAINED_GLASS, 1, (short) 0);
        ItemMeta legendaryMeta = legendary.getItemMeta();
        legendaryMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "レジェンダリー");
        legendary.setItemMeta(legendaryMeta);
        ItemMeta epicMeta = epic.getItemMeta();
        epicMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "エピック");
        epic.setItemMeta(epicMeta);
        ItemMeta rareMeta = rare.getItemMeta();
        rareMeta.setDisplayName(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "レア");
        rare.setItemMeta(rareMeta);
        ItemMeta commonMeta = common.getItemMeta();
        commonMeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "コモン");
        common.setItemMeta(commonMeta);
        ItemMeta airMeta = air.getItemMeta();
        airMeta.setDisplayName(ChatColor.WHITE + "何もセットしない");
        air.setItemMeta(airMeta);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "戻る");
        back.setItemMeta(backMeta);
        for (int i = 0; i <= 35; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(0, legendary);
        inventory.setItem(9, epic);
        inventory.setItem(18, rare);
        inventory.setItem(27, common);
        inventory.setItem(1, air);
        inventory.setItem(10, air);
        inventory.setItem(19, air);
        inventory.setItem(28, air);
        inventory.setItem(8, back);
        List<String> allWeapon = getData("weapon").getStringList("All");
        int slotLegendary = 2;
        int slotEpic = 11;
        int slotRare = 20;
        int slotCommon = 29;
        for (String aw : allWeapon){
            ItemStack item = getData("weapon").getItemStack(aw + ".item1");
            if (getData("weapon").getString(aw + ".rarity").equals("legendary")){
                inventory.setItem(slotLegendary, item);
                slotLegendary ++;
            } else if (getData("weapon").getString(aw + ".rarity").equals("epic")){
                inventory.setItem(slotEpic, item);
                slotEpic ++;
            } else if (getData("weapon").getString(aw + ".rarity").equals("rare")){
                inventory.setItem(slotRare, item);
                slotRare ++;
            } else if (getData("weapon").getString(aw + ".rarity").equals("common")){
                inventory.setItem(slotCommon, item);
                slotCommon ++;
            }
        }
        int i = 36;
        if (!(getData("player").getInt(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".cost") == 0)){
            while (i < 36 + getData("player").getInt(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".cost")) {
                inventory.setItem(i, cost1);
                i++;
            }
            if (!(i > 53)){
                while (i <= 53){
                    inventory.setItem(i, cost2);
                    i++;
                }
            }
        } else {
            while (i <= 53){
                inventory.setItem(i, cost2);
                i++;
            }
        }
        p.openInventory(inventory);
    }

    public static void SelectDecoration(Player p){
        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        Inventory inventory = Bukkit.createInventory(p,36, ChatColor.DARK_GRAY + "装飾品");
        ItemStack kill = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta killMeta = kill.getItemMeta();
        ItemMeta backMeta = back.getItemMeta();
        killMeta.setDisplayName(ChatColor.DARK_AQUA + "キルエフェクト");
        killMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemStack item = getData("killeffect").getItemStack(getData("player").getString(p.getUniqueId().toString() + ".killeffect") + ".item1");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "使用中: " + item.getItemMeta().getDisplayName());
        killMeta.setLore(lore);
        backMeta.setDisplayName(ChatColor.RED + "戻る");
        kill.setItemMeta(killMeta);
        back.setItemMeta(backMeta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 35; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(10, kill);
        inventory.setItem(31, back);
        p.openInventory(inventory);
    }

    public static void SelectKillEffect(Player p){
        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        Inventory inventory = Bukkit.createInventory(p,36, ChatColor.DARK_GRAY + "キルエフェクト");
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "戻る");
        back.setItemMeta(backMeta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 35; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(31, back);
        int slot = 0;
        List<String> allKillEffect = getData("killeffect").getStringList("All");
        for (String s : allKillEffect){
            ItemStack killeffect = getData("killeffect").getItemStack(s + ".item1");
            ItemMeta meta = killeffect.getItemMeta();
            List<String> lore = meta.getLore();
            if (getData("player").getString(p.getUniqueId().toString() + ".killeffect").equalsIgnoreCase(s)){
                lore.set((lore.size() - 1), ChatColor.YELLOW + "使用中");
            } else if (getData("player").getStringList(p.getUniqueId().toString() + ".have.killeffect").contains(s)){
                lore.set((lore.size() - 1), ChatColor.GREEN + "所持済み");
            } else {
                lore.set((lore.size() - 1), ChatColor.RED + "未所持");
            }
            meta.setLore(lore);
            killeffect.setItemMeta(meta);
            inventory.setItem(slot, killeffect);
            slot++;
        }
        p.openInventory(inventory);
    }

    public static void LoadKillEffectMenu(Inventory inv, Player p){
        int slot = 0;
        List<String> allKillEffect = getData("killeffect").getStringList("All");
        for (String s : allKillEffect){
            ItemStack killeffect = getData("killeffect").getItemStack(s + ".item1");
            ItemMeta meta = killeffect.getItemMeta();
            List<String> lore = meta.getLore();
            if (getData("player").getString(p.getUniqueId().toString() + ".killeffect").equalsIgnoreCase(s)){
                lore.set((lore.size() - 1), ChatColor.YELLOW + "使用中");
            } else if (getData("player").getStringList(p.getUniqueId().toString() + ".have.killeffect").contains(s)){
                lore.set((lore.size() - 1), ChatColor.GREEN + "所持済み");
            } else {
                lore.set((lore.size() - 1), ChatColor.RED + "未所持");
            }
            meta.setLore(lore);
            killeffect.setItemMeta(meta);
            inv.setItem(slot, killeffect);
            slot++;
        }
    }

    public static void OpenCrateMenu(Player p){
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 0.7f);
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1f);
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1.5f);
        Location loc = (Location) MagicStick.getData("block").get("Crate");
        loc.add(0.5, 0, 0.5);
        p.spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 100, 0.3, 0.3, 0.3, 1);
        p.spawnParticle(Particle.SPELL_WITCH, loc, 100, 0.3, 0.3, 0.3, 1);
        p.spawnParticle(Particle.END_ROD, loc, 100, 0.3, 0.3, 0.3, 0.1);
        loc.subtract(0.5, 0, 0.5);
        Inventory inventory = Bukkit.createInventory(p,36, ChatColor.DARK_GRAY + "ガチャ");
        ItemStack kill = new ItemStack(Material.GOLD_SWORD);
        ItemStack ability = new ItemStack(Material.BOOK);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta killMeta = kill.getItemMeta();
        ItemMeta abilityMeta = ability.getItemMeta();
        ItemMeta backMeta = back.getItemMeta();
        killMeta.setDisplayName(ChatColor.DARK_RED + "キルエフェクトガチャ");
        killMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        abilityMeta.setDisplayName(ChatColor.DARK_PURPLE + "アビリティガチャ");
        backMeta.setDisplayName(ChatColor.RED + "閉じる");
        kill.setItemMeta(killMeta);
        ability.setItemMeta(abilityMeta);
        back.setItemMeta(backMeta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 35; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(11, kill);
        inventory.setItem(15, ability);
        inventory.setItem(31, back);
        p.openInventory(inventory);
    }

    public static void OpenCrateCount(Player p){
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 0.7f);
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1f);
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1.5f);
        Inventory inventory = Bukkit.createInventory(p,18, ChatColor.DARK_GRAY + "引く回数");
        ItemStack one = new ItemStack(Material.CHEST);
        ItemStack eleven = new ItemStack(Material.ENDER_CHEST);
        ItemStack magicore = new ItemStack(Material.REDSTONE_ORE);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta oneMeta = one.getItemMeta();
        ItemMeta elevenMeta = eleven.getItemMeta();
        ItemMeta magicoreMeta = magicore.getItemMeta();
        ItemMeta backMeta = back.getItemMeta();
        oneMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "1" + ChatColor.WHITE + "回引く");
        elevenMeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "5" + ChatColor.WHITE + "回引く");
        magicoreMeta.setDisplayName(ChatColor.DARK_PURPLE + "魔法の鉱石");
        magicoreMeta.addEnchant(Enchantment.LUCK, 1, false);
        magicoreMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> oneLore = new ArrayList<>();
        ArrayList<String> elevenLore = new ArrayList<>();
        ArrayList<String> magicoreLore = new ArrayList<>();
        oneLore.add(ChatColor.WHITE + "消費鉱石: " + ChatColor.LIGHT_PURPLE + 10);
        elevenLore.add(ChatColor.WHITE + "消費鉱石: " + ChatColor.LIGHT_PURPLE + 40);
        magicoreLore.add(ChatColor.WHITE + "所持数: " + ChatColor.DARK_PURPLE + CoinAndMagicOre.getMagicOre(p));
        oneMeta.setLore(oneLore);
        elevenMeta.setLore(elevenLore);
        magicoreMeta.setLore(magicoreLore);
        backMeta.setDisplayName(ChatColor.RED + "戻る");
        one.setItemMeta(oneMeta);
        eleven.setItemMeta(elevenMeta);
        magicore.setItemMeta(magicoreMeta);
        back.setItemMeta(backMeta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 17; i++){
            inventory.setItem(i, background);
        }
        inventory.setItem(2, one);
        inventory.setItem(4, magicore);
        inventory.setItem(6, eleven);
        inventory.setItem(13, back);
        p.openInventory(inventory);
    }

    public static void OpenGacha(Player p, int count){
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 0.7f);
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1f);
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1.5f);
        Inventory inventory = Gacha.createGachaResult();
        ItemStack common = new ItemStack(Material.GRAY_GLAZED_TERRACOTTA);
        ItemMeta commonMeta = common.getItemMeta();
        commonMeta.setDisplayName(ChatColor.DARK_GRAY + "コモン");
        commonMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        commonMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        commonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        common.setItemMeta(commonMeta);
        ItemStack rare = new ItemStack(Material.BLUE_GLAZED_TERRACOTTA);
        ItemMeta rareMeta = rare.getItemMeta();
        rareMeta.setDisplayName(ChatColor.DARK_BLUE + "レア");
        rareMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        rareMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        rareMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        rare.setItemMeta(rareMeta);
        ItemStack epic = new ItemStack(Material.PURPLE_GLAZED_TERRACOTTA);
        ItemMeta epicMeta = epic.getItemMeta();
        epicMeta.setDisplayName(ChatColor.DARK_PURPLE + "エピック");
        epicMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        epicMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        epicMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        epic.setItemMeta(epicMeta);
        ItemStack legendary = new ItemStack(Material.YELLOW_GLAZED_TERRACOTTA);
        ItemMeta legendaryMeta = legendary.getItemMeta();
        legendaryMeta.setDisplayName(ChatColor.GOLD + "レジェンダリー");
        legendaryMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        legendaryMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        legendaryMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        legendary.setItemMeta(legendaryMeta);
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i <= 17; i++){
            inventory.setItem(i, background);
        }
        ArrayList<Integer> slot = new ArrayList<>();
        slot.add(13);
        slot.add(3);
        slot.add(5);
        slot.add(11);
        slot.add(15);
        slot.forEach(integer -> inventory.setItem(integer, null));
        p.openInventory(inventory);
        new BukkitRunnable(){
            int c = 0;
            @Override
            public void run() {
                if (c == count) this.cancel();
                //もっといい書き方が絶対あるとおもう1↓
                String rarity = Gacha.GachaResultRarity.get(p).get(0);
                Gacha.GachaResultRarity.get(p).remove(0);
                if (rarity.equalsIgnoreCase("common")) inventory.setItem(slot.get(c), common);
                if (rarity.equalsIgnoreCase("rare")) inventory.setItem(slot.get(c), rare);
                if (rarity.equalsIgnoreCase("epic")) inventory.setItem(slot.get(c), epic);
                if (rarity.equalsIgnoreCase("legendary")) inventory.setItem(slot.get(c), legendary);
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                c++;
            }
        }.runTaskTimer(MagicStick.getPlugin(), 0, 20);
    }
}
