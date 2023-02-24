package haruudon.udon.abilitypvp.events;

import haruudon.udon.abilitypvp.*;
import haruudon.udon.abilitypvp.gacha.Gacha;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static haruudon.udon.abilitypvp.GUIManager.now;
import static haruudon.udon.abilitypvp.AbilityPvP.*;

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
                        if (getData("player").getBoolean(uuid + ".customkit." + th.get(i - 1) + ".use")){
                            getData("player").set(uuid + ".customkit.select", th.get(i - 1));
                            savePlayerData();
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                            GUIManager.JoinRoomMenu(p);
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
            }else if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + "部屋")){
                if (e.getCurrentItem().getType() == Material.BARRIER){
                    GUIManager.SelectCustomKit(p);
                    e.setCancelled(true);
                    return;
                }
                GameRoom gameRoom = null;
                if (e.getSlot() == 9) gameRoom = GameRoomManager.getGameRooms().get(0);
                else if (e.getSlot() == 11) gameRoom = GameRoomManager.getGameRooms().get(1);
                else if (e.getSlot() == 13) gameRoom = GameRoomManager.getGameRooms().get(2);
                else if (e.getSlot() == 15) gameRoom = GameRoomManager.getGameRooms().get(3);
                else if (e.getSlot() == 17) gameRoom = GameRoomManager.getGameRooms().get(4);
                if (gameRoom != null){
                    if (GameMain.JoinPlayer(p, gameRoom)){
                        p.closeInventory();
                        return;
                    }
                }
                e.setCancelled(true);
            }else if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_RED + "本当に初期化しますか？")){
                if (e.getSlot() == 2){ //YES
                    String[] listOfType = {"NullType", "NullType"};
                    String[] listOfAbility = {"NullAbility", "NullAbility", "NullAbility", "NullAbility"};
                    int i = 0;
                    if (now.get(p.getUniqueId()).equals("first")) i += 1;
                    if (now.get(p.getUniqueId()).equals("second")) i += 2;
                    if (now.get(p.getUniqueId()).equals("third")) i += 3;
                    getData("player").set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".name", "§fカスタムキット" + i);
                    getData("player").set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                    getData("player").set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".cost", 0);
                    getData("player").set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".type", Arrays.asList(listOfType));
                    getData("player").set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".ability", Arrays.asList(listOfAbility));
                    getData("player").set(p.getUniqueId().toString() + ".customkit." + now.get(p.getUniqueId()) + ".weapon", "NullWeapon");
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
                        List<String> type = getData("player").getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type");
                        List<String> ability = getData("player").getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                        int cost = getData("player").getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                        for (String a : ability){
                            if (!(a.equalsIgnoreCase("NullAbility"))){
                                if (getData("ability").getString(a + ".rarity").equalsIgnoreCase("legendary"))
                                    cost -= 6;
                                if (getData("ability").getString(a + ".rarity").equalsIgnoreCase("epic"))
                                    cost -= 4;
                                if (getData("ability").getString(a + ".rarity").equalsIgnoreCase("rare"))
                                    cost -= 3;
                                if (getData("ability").getString(a + ".rarity").equalsIgnoreCase("common"))
                                    cost -= 2;
                            }
                        }
                        type.set(list1.indexOf(s), "NullType");
                        String[] listOfAbility = {"NullAbility", "NullAbility", "NullAbility", "NullAbility"};
                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", Arrays.asList(listOfAbility));
                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type", type);
                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                        savePlayerData();
                        GUIManager.SetAbilityMenu(p);
                    } else if (e.getSlot() == 31){
                        GUIManager.SetAbilityMenu(p);
                    } else {
                        for (String allType : getData("type").getStringList("All")){
                            ItemStack item = getData("type").getItemStack(allType + ".item1");
                            String itemname = item.getItemMeta().getDisplayName();
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemname)){
                                List<String> type = getData("player").getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type");
                                type.set(list1.indexOf(s), allType);
                                for (String t : type){
                                    if (!(allType.equalsIgnoreCase(t))) {
                                        List<String> ability = getData("player").getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                                        int cost = getData("player").getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                                        for (String a : ability) {
                                            if (!(a.equalsIgnoreCase("NullAbility"))) {
                                                if (getData("ability").getString(a + ".rarity").equalsIgnoreCase("legendary"))
                                                    cost -= 6;
                                                if (getData("ability").getString(a + ".rarity").equalsIgnoreCase("epic"))
                                                    cost -= 4;
                                                if (getData("ability").getString(a + ".rarity").equalsIgnoreCase("rare"))
                                                    cost -= 3;
                                                if (getData("ability").getString(a + ".rarity").equalsIgnoreCase("common"))
                                                    cost -= 2;
                                            }
                                        }
                                        String[] listOfAbility = {"NullAbility", "NullAbility", "NullAbility", "NullAbility"};
                                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", Arrays.asList(listOfAbility));
                                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type", type);
                                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                                        savePlayerData();
                                        GUIManager.SetAbilityMenu(p);
                                    }
                                }
                            }
                        }
                    }
                    return;
                }
            }
            List<String> list2 = Arrays.asList("アビリティ1", "アビリティ2", "アビリティ3", "アビリティ4");
            for (String s : list2){
                if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + s + "をセットする")){
                    e.setCancelled(true);
                    if (e.getCurrentItem().getType() == Material.THIN_GLASS){
                        List<String> ability = getData("player").getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                        int cost = getData("player").getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                        if (getData("ability").getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("legendary"))
                            cost -= 6;
                        if (getData("ability").getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("epic"))
                            cost -= 4;
                        if (getData("ability").getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("rare"))
                            cost -= 3;
                        if (getData("ability").getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("common"))
                            cost -= 2;
                        ability.set(list2.indexOf(s), "NullAbility");
                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", ability);
                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                        getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                        savePlayerData();
                        GUIManager.SetAbilityMenu(p);
                    } else if (e.getSlot() == 8){
                        GUIManager.SetAbilityMenu(p);
                    } else {
                        for (String allAbility : getData("ability").getStringList("All")){
                            ItemStack item = getData("ability").getItemStack(allAbility + ".item1");
                            String itemName = item.getItemMeta().getDisplayName();
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemName)) {
                                int addCost = 0;
                                int cost = getData("player").getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                                List<String> ability = getData("player").getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                                if (getData("ability").getString(allAbility + ".rarity").equalsIgnoreCase("legendary")) addCost += 6;
                                if (getData("ability").getString(allAbility + ".rarity").equalsIgnoreCase("epic")) addCost += 4;
                                if (getData("ability").getString(allAbility + ".rarity").equalsIgnoreCase("rare")) addCost += 3;
                                if (getData("ability").getString(allAbility + ".rarity").equalsIgnoreCase("common")) addCost += 2;
                                if (!(ability.get(list2.indexOf(s)).equalsIgnoreCase("NullAbility"))){
                                    if (!(ability.contains(allAbility))){
                                        if (getData("ability").getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("legendary"))
                                            cost -= 6;
                                        if (getData("ability").getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("epic"))
                                            cost -= 4;
                                        if (getData("ability").getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("rare"))
                                            cost -= 3;
                                        if (getData("ability").getString(ability.get(list2.indexOf(s)) + ".rarity").equalsIgnoreCase("common"))
                                            cost -= 2;
                                        if (cost <= (18 - addCost)) { //コストが余ってるなら
                                            ability.set(list2.indexOf(s), allAbility);
                                            cost += addCost;
                                            getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", ability);
                                            getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                                            savePlayerData();
                                            GUIManager.SetAbilityMenu(p);
                                        }
                                    }
                                } else {
                                    if (!(ability.contains(allAbility))){
                                        if (cost <= (18 - addCost)) { //コストが余ってるなら
                                            ability.set(list2.indexOf(s), allAbility);
                                            cost += addCost;
                                            getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability", ability);
                                            getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                                            savePlayerData();
                                            GUIManager.SetAbilityMenu(p);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return;
                }
            }
            for (String s : Arrays.asList("キルエフェクト", "キルメッセージ")){
                if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + s)){
                    String deco = null;
                    if (s.equals("キルエフェクト")) deco = "killeffect";
                    if (s.equals("キルメッセージ")) deco = "killmessage";
                    if (deco == null) return;
                    e.setCancelled(true);
                    if (e.getSlot() == 31){
                        GUIManager.DecorationMain(p);
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GRAY + "次へ")) {
                        Inventory inventory = e.getClickedInventory();
                        GUIManager.LoadSelectDecoration(inventory, p, "next");
                        p.updateInventory();
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GRAY + "前へ")) {
                        Inventory inventory = e.getClickedInventory();
                        GUIManager.LoadSelectDecoration(inventory, p, "prev");
                        p.updateInventory();
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                    } else {
                        for (String a : getData(deco).getStringList("All")) {
                            ItemStack item = getData(deco).getItemStack(a + ".item1");
                            String itemname = item.getItemMeta().getDisplayName();
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemname)){
                                if (getData("player").getStringList(uuid + ".have." + deco).contains(a)){
                                    getData("player").set(uuid + "." + deco, a);
                                    savePlayerData();
                                    p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                    Inventory inventory = e.getClickedInventory();
                                    GUIManager.LoadSelectDecoration(inventory, p, null);
                                    p.updateInventory();
                                } else {
                                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                                }
                            }
                        }
                    }
                    return;
                }
            }
            if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + "武器をセットする")){
                e.setCancelled(true);
                if (e.getCurrentItem().getType() == Material.GLASS){
                    String weapon = getData("player").getString(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon");
                    int cost = getData("player").getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                    if (getData("weapon").getString(weapon + ".rarity").equalsIgnoreCase("legendary"))
                        cost -= 6;
                    if (getData("weapon").getString(weapon + ".rarity").equalsIgnoreCase("epic"))
                        cost -= 4;
                    if (getData("weapon").getString(weapon + ".rarity").equalsIgnoreCase("rare"))
                        cost -= 3;
                    if (getData("weapon").getString(weapon + ".rarity").equalsIgnoreCase("common"))
                        cost -= 2;
                    String newWeapon = "NullWeapon";
                    getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon", newWeapon);
                    getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", false);
                    getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                    savePlayerData();
                    GUIManager.SetAbilityMenu(p);
                } else if (e.getSlot() == 8){
                    GUIManager.SetAbilityMenu(p);
                } else {
                    for (String allWeapon : getData("weapon").getStringList("All")){
                        ItemStack item = getData("weapon").getItemStack(allWeapon + ".item1");
                        String itemname = item.getItemMeta().getDisplayName();
                        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemname)){
                            int addCost = 0;
                            int cost = getData("player").getInt(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost");
                            String weapon = getData("player").getString(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon");
                            if (getData("weapon").getString(allWeapon + ".rarity").equalsIgnoreCase("legendary")) addCost += 6;
                            if (getData("weapon").getString(allWeapon + ".rarity").equalsIgnoreCase("epic")) addCost += 4;
                            if (getData("weapon").getString(allWeapon + ".rarity").equalsIgnoreCase("rare")) addCost += 3;
                            if (getData("weapon").getString(allWeapon + ".rarity").equalsIgnoreCase("common")) addCost += 2;
                            //コストが余ってるなら
                            if (!weapon.equalsIgnoreCase("NullWeapon")){
                                if (getData("weapon").getString(weapon + ".rarity").equalsIgnoreCase("legendary"))
                                    cost -= 6;
                                if (getData("weapon").getString(weapon + ".rarity").equalsIgnoreCase("epic"))
                                    cost -= 4;
                                if (getData("weapon").getString(weapon + ".rarity").equalsIgnoreCase("rare"))
                                    cost -= 3;
                                if (getData("weapon").getString(weapon + ".rarity").equalsIgnoreCase("common"))
                                    cost -= 2;
                            }
                            if (cost <= (18 - addCost)) { //コストが余ってるなら
                                cost += addCost;
                                getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon", allWeapon);
                                getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".cost", cost);
                                savePlayerData();
                                GUIManager.SetAbilityMenu(p);
                            } else {
                                p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                            }
                        }
                    }
                }
            } else if (e.getClickedInventory().getTitle().equalsIgnoreCase(getData("player").getString(uuid + ".customkit." + now.get(p.getUniqueId()) + ".name"))){
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
                        List<String> playerType = getData("player").getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".type");
                        List<String> playerAbility = getData("player").getStringList(uuid + ".customkit." + now.get(p.getUniqueId()) + ".ability");
                        String playerWeapon = getData("player").getString(uuid + ".customkit." + now.get(p.getUniqueId()) + ".weapon");
                        if (!(playerType.get(0).equalsIgnoreCase("NullType"))
                                && !(playerType.get(1).equalsIgnoreCase("NullType"))
                                && !(playerAbility.get(0).equalsIgnoreCase("NullAbility"))
                                && !(playerAbility.get(1).equalsIgnoreCase("NullAbility"))
                                && !(playerAbility.get(2).equalsIgnoreCase("NullAbility"))
                                && !(playerAbility.get(3).equalsIgnoreCase("NullAbility"))
                                && !(playerWeapon.equalsIgnoreCase("NullWeapon"))){
                            getData("player").set(uuid + ".customkit." + now.get(p.getUniqueId()) + ".use", true);
                            savePlayerData();
                            GUIManager.MainSetAbilityMenu(p);
                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                        } else {
                            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                        }
                        break;
                }
                e.setCancelled(true);
            } else if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + "装飾品")){
                e.setCancelled(true);
                if (e.getSlot() == 31){
                    p.closeInventory();
                } else if (e.getSlot() == 10){
                    GUIManager.SelectDecoration(p, "killeffect", "キルエフェクト");
                } else if (e.getSlot() == 12){
                    GUIManager.SelectDecoration(p, "killmessage", "キルメッセージ");
                }
            }  else if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + "ガチャ")){
                e.setCancelled(true);
                if (e.getSlot() == 31){
                    p.closeInventory();
                } else if (e.getSlot() == 11){
                    GUIManager.OpenCrateCount(p);
                    Gacha.NowOpenGUI.put(p, "decoration");
                }
            } else if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + "引く回数")){
                e.setCancelled(true);
                if (e.getCurrentItem().getType() == Material.CHEST){
                    if (CoinAndMagicOre.checkUseItems(p, "magicore", 10)){
                        Gacha.openGacha(p, 1);
                        CoinAndMagicOre.removeItems(p, "magicore", 10);
                    } else {
                        p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    }
                } else if (e.getCurrentItem().getType() == Material.ENDER_CHEST){
                    if (CoinAndMagicOre.checkUseItems(p, "magicore", 40)){
                        Gacha.openGacha(p, 5);
                        CoinAndMagicOre.removeItems(p, "magicore", 40);
                    } else {
                        p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    }
                } else if (e.getCurrentItem().getType() == Material.BARRIER){
                    GUIManager.OpenCrateMenu(p);
                }
            } else if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY + "結果")){
                e.setCancelled(true);
                List<Material> materialList = new ArrayList<>();
                materialList.add(Material.GRAY_GLAZED_TERRACOTTA);
                materialList.add(Material.BLUE_GLAZED_TERRACOTTA);
                materialList.add(Material.PURPLE_GLAZED_TERRACOTTA);
                materialList.add(Material.YELLOW_GLAZED_TERRACOTTA);
                for (Material m : materialList){
                    if (e.getCurrentItem().getType() == m){
                        ItemStack item = null;
                        ArrayList<Integer> slot = new ArrayList<>();
                        slot.add(13);
                        slot.add(3);
                        slot.add(5);
                        slot.add(11);
                        slot.add(15);
                        if (m == Material.GRAY_GLAZED_TERRACOTTA) p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 1, 1);
                        if (m == Material.BLUE_GLAZED_TERRACOTTA) p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1, 1);
                        if (m == Material.PURPLE_GLAZED_TERRACOTTA) p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
                        if (m == Material.YELLOW_GLAZED_TERRACOTTA) p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
                        for (int i : slot){
                            if (e.getSlot() == i){
                                String result = Gacha.GachaResultItem.get(p).get(slot.indexOf(i)).item();
                                String data = Gacha.GachaResultItem.get(p).get(slot.indexOf(i)).data();
                                item = switch (result) {
                                    case "common" -> getData("item").getItemStack("MagicDustCommon");
                                    case "rare" -> getData("item").getItemStack("MagicDustRare");
                                    case "epic" -> getData("item").getItemStack("MagicDustEpic");
                                    case "legendary" -> getData("item").getItemStack("BigMagicDust");
                                    default -> getData(data).getItemStack(result + ".item1");
                                };
                            }
                        }
                        Inventory inventory = e.getClickedInventory();
                        Gacha.loadGachaResult(inventory, Objects.requireNonNull(item), e.getSlot());
                        p.updateInventory();
                    }
                }
            } else {
                if (!(p.isOp())){
                    e.setCancelled(true);
                }
            }
        }
    }
}