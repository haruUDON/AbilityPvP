package haruudon.udon.magicstick.events;

import haruudon.udon.magicstick.GUIManager;
import haruudon.udon.magicstick.Join;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static haruudon.udon.magicstick.GUIManager.now;
import static haruudon.udon.magicstick.MagicStick.*;

public class GUIClickEvent implements Listener {

    @EventHandler
    public void onGUIClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String uuid = p.getUniqueId().toString();
        if (e.getClickedInventory() != null){
            if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + "カスタムキット")){
                if (e.getSlot() == 11){
                    now.put(p.getUniqueId(), "first");
                    GUIManager.SetAbilityMenu(p);
                } else if (e.getSlot() == 13){
                    now.put(p.getUniqueId(), "second");
                    GUIManager.SetAbilityMenu(p);
                } else if (e.getSlot() == 15){
                    now.put(p.getUniqueId(), "third");
                    GUIManager.SetAbilityMenu(p);
                } else if (e.getCurrentItem().getType() == Material.BARRIER){
                    p.closeInventory();
                }
                e.setCancelled(true);
            } else if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + "キット選択")){
                for (int i = 1; i < 4; i++){
                    if (e.getSlot() == (9 + (2 * i))){
                        List<String> th = new ArrayList<>();
                        th.add("first"); th.add("second"); th.add("third");
                        if (getPlayerData().getBoolean(uuid + ".customkit." + th.get(i - 1) + ".use")){
                            getPlayerData().set(uuid + ".customkit.select", th.get(i - 1));
                            savePlayerData();
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                            p.closeInventory();
                            Join.JoinPlayer(p);
                        } else {
                            p.closeInventory();
                            p.sendMessage(ChatColor.RED + "キットのセットアップが完了していません");
                            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                        }
                    }
                }
                if (e.getCurrentItem().getType() == Material.BARRIER){
                    p.closeInventory();
                }
                e.setCancelled(true);
            } else if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_RED + "本当に初期化しますか？")){
                if (e.getSlot() == 2){ //YES
                    String[] listOfType = {"NullType", "NullType"};
                    String[] listOfAbility = {"NullAbility", "NullAbility", "NullAbility", "NullAbility"};
                    int i = 0;
                    if (now.get(p.getUniqueId()).equals("first")) i += 1;
                    if (now.get(p.getUniqueId()).equals("second")) i += 2;
                    if (now.get(p.getUniqueId()).equals("third")) i += 3;
                    getPlayerData().set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".name", "§fカスタムキット" + i);
                    getPlayerData().set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                    getPlayerData().set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".cost", 0);
                    getPlayerData().set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".type", Arrays.asList(listOfType));
                    getPlayerData().set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".ability", Arrays.asList(listOfAbility));
                    getPlayerData().set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".weapon", "NullWeapon");
                    savePlayerData();
                    GUIManager.MainSetAbilityMenu(p);
                } else if (e.getSlot() == 6){
                    GUIManager.SetAbilityMenu(p);
                }
                e.setCancelled(true);
            }
            List<String> list1 = Arrays.asList("タイプ1", "タイプ2");
            for (String s : list1){
                if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + s + "をセットする")){
                    e.setCancelled(true);
                    if (e.getSlot() == 0){
                        List<String> type = getPlayerData().getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type");
                        List<String> ability = getPlayerData().getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                        int cost = getPlayerData().getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                        for (String a : ability){
                            if (!(a.equalsIgnoreCase("NullAbility"))){
                                if (getAbilityData().getString(a + ".rarity").equalsIgnoreCase("legendary"))
                                    cost -= 6;
                                if (getAbilityData().getString(a + ".rarity").equalsIgnoreCase("epic"))
                                    cost -= 4;
                                if (getAbilityData().getString(a + ".rarity").equalsIgnoreCase("rare"))
                                    cost -= 3;
                                if (getAbilityData().getString(a + ".rarity").equalsIgnoreCase("common"))
                                    cost -= 2;
                            }
                        }
                        type.set(list1.indexOf(s), "NullType");
                        String[] listOfAbility = {"NullAbility", "NullAbility", "NullAbility", "NullAbility"};
                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", Arrays.asList(listOfAbility));
                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type", type);
                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                        savePlayerData();
                        GUIManager.SetAbilityMenu(p);
                    } else if (e.getSlot() == 31){
                        GUIManager.SetAbilityMenu(p);
                    } else {
                        for (String alltype : getTypeData().getStringList("All")){
                            ItemStack item = getTypeData().getItemStack(alltype + ".item1");
                            String itemname = item.getItemMeta().getDisplayName();
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemname)){
                                List<String> type = getPlayerData().getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type");
                                type.set(list1.indexOf(s), alltype);
                                for (String t : type){
                                    if (!(alltype.equalsIgnoreCase(t))) {
                                        List<String> ability = getPlayerData().getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                                        int cost = getPlayerData().getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                                        for (String a : ability) {
                                            if (!(a.equalsIgnoreCase("NullAbility"))) {
                                                if (getAbilityData().getString(a + ".rarity").equalsIgnoreCase("legendary"))
                                                    cost -= 6;
                                                if (getAbilityData().getString(a + ".rarity").equalsIgnoreCase("epic"))
                                                    cost -= 4;
                                                if (getAbilityData().getString(a + ".rarity").equalsIgnoreCase("rare"))
                                                    cost -= 3;
                                                if (getAbilityData().getString(a + ".rarity").equalsIgnoreCase("common"))
                                                    cost -= 2;
                                            }
                                        }
                                        String[] listOfAbility = {"NullAbility", "NullAbility", "NullAbility", "NullAbility"};
                                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", Arrays.asList(listOfAbility));
                                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type", type);
                                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                                        savePlayerData();
                                        GUIManager.SetAbilityMenu(p);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            List<String> list2 = Arrays.asList("アビリティ1", "アビリティ2", "アビリティ3", "アビリティ4");
            for (String s : list2){
                if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + s + "をセットする")){
                    e.setCancelled(true);
                    if (e.getCurrentItem().getType() == Material.THIN_GLASS){
                        List<String> ability = getPlayerData().getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                        int cost = getPlayerData().getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                        if (getAbilityData().getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("legendary"))
                            cost -= 6;
                        if (getAbilityData().getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("epic"))
                            cost -= 4;
                        if (getAbilityData().getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("rare"))
                            cost -= 3;
                        if (getAbilityData().getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("common"))
                            cost -= 2;
                        ability.set(list2.indexOf(s), "NullAbility");
                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", ability);
                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                        getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                        savePlayerData();
                        GUIManager.SetAbilityMenu(p);
                    } else if (e.getSlot() == 8){
                        GUIManager.SetAbilityMenu(p);
                    } else {
                        for (String allAbility : getAbilityData().getStringList("All")){
                            ItemStack item = getAbilityData().getItemStack(allAbility + ".item1");
                            String itemName = item.getItemMeta().getDisplayName();
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemName)) {
                                int addCost = 0;
                                int cost = getPlayerData().getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                                List<String> ability = getPlayerData().getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                                if (getAbilityData().getString(allAbility + ".rarity").equalsIgnoreCase("legendary")) addCost += 6;
                                if (getAbilityData().getString(allAbility + ".rarity").equalsIgnoreCase("epic")) addCost += 4;
                                if (getAbilityData().getString(allAbility + ".rarity").equalsIgnoreCase("rare")) addCost += 3;
                                if (getAbilityData().getString(allAbility + ".rarity").equalsIgnoreCase("common")) addCost += 2;
                                if (!(ability.get(list2.indexOf(s)).equalsIgnoreCase("NullAbility"))){
                                    if (!(ability.contains(allAbility))){
                                        if (getAbilityData().getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("legendary"))
                                            cost -= 6;
                                        if (getAbilityData().getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("epic"))
                                            cost -= 4;
                                        if (getAbilityData().getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("rare"))
                                            cost -= 3;
                                        if (getAbilityData().getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("common"))
                                            cost -= 2;
                                        if (cost <= (18 - addCost)) { //コストが余ってるなら
                                            ability.set(list2.indexOf(s), allAbility);
                                            cost += addCost;
                                            getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", ability);
                                            getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                                            savePlayerData();
                                            GUIManager.SetAbilityMenu(p);
                                        }
                                    }
                                } else {
                                    if (!(ability.contains(allAbility))){
                                        if (cost <= (18 - addCost)) { //コストが余ってるなら
                                            ability.set(list2.indexOf(s), allAbility);
                                            cost += addCost;
                                            getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", ability);
                                            getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                                            savePlayerData();
                                            GUIManager.SetAbilityMenu(p);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + "武器をセットする")){
                e.setCancelled(true);
                if (e.getCurrentItem().getType() == Material.GLASS){
                    String weapon = getPlayerData().getString(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon");
                    int cost = getPlayerData().getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                    if (getWeaponData().getString(weapon + ".rarity").equalsIgnoreCase("legendary"))
                        cost -= 6;
                    if (getWeaponData().getString(weapon + ".rarity").equalsIgnoreCase("epic"))
                        cost -= 4;
                    if (getWeaponData().getString(weapon + ".rarity").equalsIgnoreCase("rare"))
                        cost -= 3;
                    if (getWeaponData().getString(weapon + ".rarity").equalsIgnoreCase("common"))
                        cost -= 2;
                    String newWeapon = "NullWeapon";
                    getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon", newWeapon);
                    getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                    getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                    savePlayerData();
                    GUIManager.SetAbilityMenu(p);
                } else if (e.getSlot() == 8){
                    GUIManager.SetAbilityMenu(p);
                } else {
                    for (String allweapon : getWeaponData().getStringList("All")){
                        ItemStack item = getWeaponData().getItemStack(allweapon + ".item1");
                        String itemname = item.getItemMeta().getDisplayName();
                        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemname)) {
                            int addcost = 0;
                            int cost = getPlayerData().getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                            String weapon = getPlayerData().getString(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon");
                            if (getWeaponData().getString(allweapon + ".rarity").equalsIgnoreCase("legendary")) addcost += 6;
                            if (getWeaponData().getString(allweapon + ".rarity").equalsIgnoreCase("epic")) addcost += 4;
                            if (getWeaponData().getString(allweapon + ".rarity").equalsIgnoreCase("rare")) addcost += 3;
                            if (getWeaponData().getString(allweapon + ".rarity").equalsIgnoreCase("common")) addcost += 2;
                            //コストが余ってるなら
                            if (!weapon.equalsIgnoreCase("NullWeapon")){
                                if (getWeaponData().getString(weapon + ".rarity").equalsIgnoreCase("legendary"))
                                    cost -= 6;
                                if (getWeaponData().getString(weapon + ".rarity").equalsIgnoreCase("epic"))
                                    cost -= 4;
                                if (getWeaponData().getString(weapon + ".rarity").equalsIgnoreCase("rare"))
                                    cost -= 3;
                                if (getWeaponData().getString(weapon + ".rarity").equalsIgnoreCase("common"))
                                    cost -= 2;
                            }
                            if (cost <= (18 - addcost)) { //コストが余ってるなら
                                cost += addcost;
                                getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon", allweapon);
                                getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                                savePlayerData();
                                GUIManager.SetAbilityMenu(p);
                            } else {
                                p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                            }
                        }
                    }
                }
            } else if (e.getClickedInventory().getTitle().equalsIgnoreCase(getPlayerData().getString(uuid + ".customkit." + now.get(p.getUniqueId()) + ".name"))){
                switch (e.getSlot()){
                    case 0: //リセット
                        GUIManager.CheckYesOrNo(p, "本当に初期化しますか？");
                        break;
                    case 3: //タイプA
                        GUIManager.SelectType(p, "タイプ1");
                        break;
                    case 5: //タイプB
                        GUIManager.SelectType(p, "タイプ2");
                        break;
                    case 13: //ウェポン
                        GUIManager.SelectWeapon(p);
                        break;
                    case 19: //アビリティ1
                        GUIManager.SelectAbility(p, "アビリティ1");
                        break;
                    case 21: //アビリティ2
                        GUIManager.SelectAbility(p, "アビリティ2");
                        break;
                    case 23: //アビリティ3
                        GUIManager.SelectAbility(p, "アビリティ3");
                        break;
                    case 25: //アビリティ4
                        GUIManager.SelectAbility(p, "アビリティ4");
                        break;
                    case 27: //名前変更
                        break;
                    case 31: //戻る
                        GUIManager.MainSetAbilityMenu(p);
                        break;
                    case 35: //保存
                        List<String> playerType = getPlayerData().getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type");
                        List<String> playerAbility = getPlayerData().getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                        String playerWeapon = getPlayerData().getString(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon");
                        if (!(playerType.get(0).equalsIgnoreCase("NullType"))
                                && !(playerType.get(1).equalsIgnoreCase("NullType"))
                                && !(playerAbility.get(0).equalsIgnoreCase("NullAbility"))
                                && !(playerAbility.get(1).equalsIgnoreCase("NullAbility"))
                                && !(playerAbility.get(2).equalsIgnoreCase("NullAbility"))
                                && !(playerAbility.get(3).equalsIgnoreCase("NullAbility"))
                                && !(playerWeapon.equalsIgnoreCase("NullWeapon"))){
                            getPlayerData().set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", true);
                            savePlayerData();
                            GUIManager.MainSetAbilityMenu(p);
                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                        } else {
                            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                        }
                        break;
                }
                e.setCancelled(true);
            } else {
                if (!(p.isOp())){
                    e.setCancelled(true);
                }
            }
        }
    }
}