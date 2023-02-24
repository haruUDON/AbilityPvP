package haruudon.udon.abilitypvp;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

import static haruudon.udon.abilitypvp.AbilityPvP.*;
import static haruudon.udon.abilitypvp.cooldown.Cooldown.Cooldowns;
import static haruudon.udon.abilitypvp.cooldown.Cooldown.DownAbility;

public class GameMain {
    public static HashMap<Player, Integer> KillCount;

    public static void setupJoinPlayer() {
        KillCount = new HashMap<>();
    }

    public static Boolean JoinPlayer(Player p, GameRoom g){
        if (!GameRoomManager.containsGamePlayer(p)){
            String select = getData("player").getString(p.getUniqueId().toString() + ".customkit.select");
            if (!(select == null) && getData("player").getBoolean(p.getUniqueId().toString() + ".customkit." + select + ".use")) {
                if (!g.getIsGaming()){
                    if (g.getPlayers().size() < 4){
                        g.addPlayer(p);
                        p.getInventory().clear();
                        p.getInventory().setItem(8, getData("item").getItemStack("Quit"));
                        for (Player join : g.getPlayers()) {
                            join.sendMessage(ChatColor.GRAY + p.getName() + ChatColor.YELLOW + "が参加しました (" + ChatColor.AQUA + g.getPlayers().size() + ChatColor.YELLOW + "/"
                                    + ChatColor.AQUA + "4" + ChatColor.YELLOW + ")");
                        }
                        if (g.getPlayers().size() == 2) {
                            new BukkitRunnable() {
                                int StartGameTimer = 20;

                                @Override
                                public void run() {
                                    if (g.getPlayers().size() < 2) {
                                        this.cancel();
                                    } else if (StartGameTimer == 0) {
                                        g.getPlayers().forEach(g::addAlive);
                                        GameStart(g);
                                        this.cancel();
                                    } else if (StartGameTimer == 20 || StartGameTimer == 15 || StartGameTimer == 10
                                            || StartGameTimer == 5 || StartGameTimer == 4 || StartGameTimer == 3 || StartGameTimer == 2 || StartGameTimer == 1) {
                                        for (Player join : g.getPlayers()) {
                                            join.sendMessage(ChatColor.YELLOW + "" + StartGameTimer + "秒後にゲームを開始します");
                                        }
                                    }
                                    StartGameTimer -= 1;
                                }
                            }.runTaskTimer(AbilityPvP.getPlugin(), 0, 20L);
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "部屋の人数が最大に達しているためゲームに参加できません");
                        p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                        return false;
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "その部屋は試合中のため参加できません");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                    return false;
                }
            } else {
                p.sendMessage(ChatColor.RED + "使用可能なキットを選択してください");
                p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                return false;
            }
        } else {
            p.sendMessage(ChatColor.RED + "あなたはすでにゲームに参加しています");
            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
            return false;
        }
        return true;
    }

    public static void QuitPlayer(Player p){
        GameRoom gameRoom = GameRoomManager.getRoom(p);
        if (gameRoom != null){
            for (Player join : gameRoom.getPlayers()) {
                join.sendMessage(ChatColor.GRAY + p.getName() + ChatColor.YELLOW + "が退出しました (" + ChatColor.AQUA + (gameRoom.getPlayers().size() - 1) + ChatColor.YELLOW + "/"
                        + ChatColor.AQUA + "4" + ChatColor.YELLOW + ")");
            }
            gameRoom.removePlayer(p);
            p.getInventory().clear();
            p.getInventory().setItem(0, getData("item").getItemStack("Custom"));
            p.getInventory().setItem(4, getData("item").getItemStack("Join"));
            p.getInventory().setItem(8, getData("item").getItemStack("Shop"));
        } else {
            p.sendMessage(ChatColor.RED + "あなたはゲームに参加していません");
            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
        }
    }

    public static void GameStart(GameRoom g){
        g.setIsGaming(true);
        List<String> Maps = getData("map").getStringList("All");
        Random random = new Random();
        int randomMap = random.nextInt(Maps.size());
        String Map = Maps.get(randomMap);
        List<String> LocationList = new ArrayList<>(Arrays.asList(".location1", ".location2", ".location3", ".location4"));
        Collections.shuffle(LocationList);
        int list = 0;
        for (Player player : g.getPlayers()){
            KillCount.put(player, 0);
            haruudon.udon.abilitypvp.Scoreboard.Remove(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            Location loc = (Location) getData("map").get(Map + LocationList.get(list));
            Teleport.Teleport(player, loc);
            player.sendTitle(ChatColor.WHITE + "" + ChatColor.BOLD + Map, ChatColor.GREEN + "マップ", 0, 60, 20);
            list += 1;
        }
        GameStartTimer(g);
    }

    public static void GameStartTimer(GameRoom g) {
        new BukkitRunnable() {
            int StartGameTimer = 7;
            @Override
            public void run() {
                if (g.getPlayers().isEmpty()){
                    this.cancel();
                } else if (StartGameTimer == 0) {
                    Timer(g);
                    for (Player player : g.getPlayers()) {
                        player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Start!", "", 0, 60, 20);
                        player.sendMessage(ChatColor.GREEN + "アイテムを支給しました");
                        Mana.setInitialMana(player);
                        String uuid = player.getUniqueId().toString();
                        String select = getData("player").getString(uuid + ".customkit.select");
                        player.getInventory().setItem(0, getData("weapon")
                                .getItemStack(getData("player").getString(uuid + ".customkit." + select + ".weapon") + ".item1"));
                        int slot1 = 1;
                        for (String ability : getData("player").getStringList(uuid + ".customkit." + select + ".ability")) {
                            player.getInventory().setItem(slot1, getData("ability").getItemStack(ability + ".item1"));
                            slot1++;
                        }
                        int maxHealth = 0;
                        int slot2 = 7;
                        for (String type : getData("player").getStringList(uuid + ".customkit." + select + ".type")){
                            player.getInventory().setItem(slot2, getData("type").getItemStack(type + ".item1"));
                            maxHealth += getData("type").getInt(type + ".health");
                            slot2++;
                        }
                        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
                        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 2, true));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        this.cancel();
                    }
                } else if (StartGameTimer == 5 || StartGameTimer == 4) {
                    for (Player player : g.getPlayers()) {
                        player.sendTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + StartGameTimer, "", 0, 60, 20);
                    }
                } else if (StartGameTimer == 3 || StartGameTimer == 2 || StartGameTimer == 1) {
                    for (Player player : g.getPlayers()) {
                        player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + StartGameTimer, "", 0, 60, 20);
                    }
                }
                StartGameTimer -= 1;
            }
        }.runTaskTimer(AbilityPvP.getPlugin(), 0, 20);
    }

    public static void GameEnd(String result, GameRoom g) {
        ArrayList<Player> players = g.getPlayers();
        //スコアボード
        Scoreboard score = Bukkit.getScoreboardManager().getMainScoreboard();
        Team t = score.getTeam("hide");
        //ここまで
        Location lobby = (Location) getData("map").get("MainLobby.location");
        g.clearSpectator();
        Player Winner = null;
        switch (result) {
            case "Normal":
                for (Player player : players) {
                    Winner = g.getAlive().get(0);
                    player.sendTitle(ChatColor.WHITE + "" + ChatColor.BOLD + Winner.getName(), ChatColor.YELLOW + "勝者", 0, 60, 20);
                }
                break;
            case "TimeUp":
                for (Player player : players) {
                    player.sendTitle(ChatColor.WHITE + "" + ChatColor.BOLD + "なし", ChatColor.YELLOW + "勝者", 0, 60, 20);
                }
                break;
            case "ShutDown":
                break;
        }
        Player finalWinner = Winner;
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Player player : players){
                    haruudon.udon.abilitypvp.Scoreboard.Create(player);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(lobby);
                    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
                    player.getInventory().clear();
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.getInventory().setItem(0, getData("item").getItemStack("Custom"));
                    player.getInventory().setItem(4, getData("item").getItemStack("Join"));
                    player.getInventory().setItem(8, getData("item").getItemStack("Shop"));
                    Mana.mana.remove(player.getUniqueId());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
                    SendResult(player, KillCount.get(player), finalWinner);
                    KillAndWinCount.addKillCount(player, KillCount.get(player));
                    if (finalWinner == player) KillAndWinCount.addWinCount(player, 1);
                    KillCount.remove(player);
                    if (Cooldowns.containsKey(player.getUniqueId())){
                        DownAbility.remove(player.getUniqueId());
                        Cooldowns.remove(player.getUniqueId());
                    }
                    t.removeEntry(player.getName());
                }
                g.clearPlayers();
                g.clearAlive();
                g.setIsGaming(false);
//                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=item]");
            }
        }.runTaskLater(AbilityPvP.getPlugin(), 1);
    }

    public static void Timer(GameRoom g) {
        new BukkitRunnable() {
            int Timer = 600;
            @Override
            public void run() {
                if (g.getPlayers().isEmpty()){
                    this.cancel();
                } else if (Timer == 0) {
                    GameEnd("TimeUp", g);
                    this.cancel();
                } else if (Timer > 60) {
                    for (Player player : g.getPlayers()) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "残り時間: " + (Timer / 60) + " 分"));
                    }
                } else {
                    for (Player player : g.getPlayers()) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "残り時間: " + Timer + " 秒"));
                    }
                }
                Timer -= 1;
            }
        }.runTaskTimer(AbilityPvP.getPlugin(), 0, 20L);
    }

    public static void SendResult(Player p, int kill, Player winner){
        String uuidS = p.getUniqueId().toString();
        int level = getData("player").getInt(uuidS + ".levels.level");
        int exp = getData("player").getInt(uuidS + ".levels.exp");
        int reqExp = getData("player").getInt(uuidS + ".levels.req");
        int getExp = 10 + (10 * kill);
        int addCoin = 250 + (100 * kill);
        if (winner == p) {
            addCoin += 300;
            getExp += 15;
        }
        CoinAndMagicOre.addItems(p, "coin", addCoin);
        p.sendMessage(ChatColor.GOLD + "<" + ChatColor.YELLOW + "--------------------------------------------------" + ChatColor.GOLD  + ">");
        p.sendMessage("");
        if (winner == null) {
            p.sendMessage("                    " + ChatColor.YELLOW + "勝者: " + ChatColor.WHITE + "なし");
        } else p.sendMessage("                    " + ChatColor.YELLOW + "勝者: " + ChatColor.WHITE + winner.getName());
        p.sendMessage("");
        p.sendMessage("");
        p.sendMessage("                    " + ChatColor.DARK_RED + "キル数: " + ChatColor.WHITE + kill);
        p.sendMessage("");
        p.sendMessage("                    " + ChatColor.GOLD + "獲得コイン: " + ChatColor.WHITE + addCoin);
        p.sendMessage("                    " + ChatColor.GREEN + "獲得経験値: " + ChatColor.WHITE + getExp);
        p.sendMessage("");
        p.sendMessage(ChatColor.GOLD + "<" + ChatColor.YELLOW + "--------------------------------------------------" + ChatColor.GOLD  + ">");

        exp += getExp;
        if (exp >= reqExp){
            int getMagicOre = 0;
            level++;
            exp -= reqExp;
            reqExp *= 1.5;
            getData("player").set(uuidS + ".levels.level", level);
            getData("player").set(uuidS + ".levels.exp", exp);
            getData("player").set(uuidS + ".levels.req", reqExp);
            savePlayerData();
            getMagicOre += 5;
            if (level % 5 == 0) {
                getMagicOre += 10;
            }
            int finalGetMagicOre = getMagicOre;
            CoinAndMagicOre.addItems(p, "magicore", getMagicOre);
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    p.sendMessage(ChatColor.GOLD + "<" + ChatColor.YELLOW + "--------------------------------------------------" + ChatColor.GOLD  + ">");
                    p.sendMessage("");
                    p.sendMessage("                    " + ChatColor.GREEN + "" + ChatColor.BOLD + "レベルアップ");
                    p.sendMessage("");
                    p.sendMessage("                    " + ChatColor.DARK_GREEN + "現在レベル: " + ChatColor.WHITE + getData("player").getInt(uuidS + ".levels.level"));
                    p.sendMessage("                    " + ChatColor.DARK_GREEN + "必要経験値: " + ChatColor.WHITE + getData("player").getInt(uuidS + ".levels.exp") + "/" + getData("player").getInt(uuidS + ".levels.req"));
                    p.sendMessage("");
                    p.sendMessage("                    " + ChatColor.DARK_PURPLE + "獲得魔法の鉱石: " + ChatColor.WHITE + finalGetMagicOre);
                    p.sendMessage("");
                    p.sendMessage(ChatColor.GOLD + "<" + ChatColor.YELLOW + "--------------------------------------------------" + ChatColor.GOLD  + ">");
                }
            }.runTaskLater(AbilityPvP.getPlugin(), 30);
        } else {
            getData("player").set(uuidS + ".levels.level", level);
            getData("player").set(uuidS + ".levels.exp", exp);
            getData("player").set(uuidS + ".levels.req", reqExp);
            savePlayerData();
        }
    }
}