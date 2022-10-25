package haruudon.udon.magicstick;

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

import static haruudon.udon.magicstick.MagicStick.*;
import static haruudon.udon.magicstick.cooldown.Cooldown.Cooldowns;
import static haruudon.udon.magicstick.cooldown.Cooldown.DownAbility;

public class Join {

    public static ArrayList<Player> JoinPlayer;
    public static ArrayList<Player> GamePlayer;
    public static ArrayList<Player> Alive;
    public static ArrayList<Player> Spectator;

    public static void setupJoinPlayer() {
        JoinPlayer = new ArrayList<>();
        GamePlayer = new ArrayList<>();
        Alive = new ArrayList<>();
        Spectator = new ArrayList<>();
    }

    public static void JoinPlayer(Player p){
        if (!(JoinPlayer.contains(p)) && !(GamePlayer.contains(p))){
            String select = getData("player").getString(p.getUniqueId().toString() + ".customkit.select");
            if (!(select == null) && getData("player").getBoolean(p.getUniqueId().toString() + ".customkit." + select + ".use")) {
                if (JoinPlayer.size() < 4) {
                    JoinPlayer.add(p);
                    p.getInventory().clear();
                    p.getInventory().setItem(8, getData("item").getItemStack("Quit"));
                    for (Player join : JoinPlayer) {
                        join.sendMessage(ChatColor.GRAY + p.getName() + ChatColor.YELLOW + "が参加しました (" + ChatColor.AQUA + JoinPlayer.size() + ChatColor.YELLOW + "/"
                                + ChatColor.AQUA + "4" + ChatColor.YELLOW + ")");
                    }
                    if (JoinPlayer.size() == 2) {
                        new BukkitRunnable() {
                            int StartGameTimer = 20;

                            @Override
                            public void run() {
                                if (JoinPlayer.size() < 2) {
                                    this.cancel();
                                } else if (StartGameTimer == 0) {
                                    GamePlayer.addAll(JoinPlayer);
                                    Alive.addAll(JoinPlayer);
                                    JoinPlayer.clear();
                                    GameStart();
                                    this.cancel();
                                } else if (StartGameTimer == 20 || StartGameTimer == 15 || StartGameTimer == 10
                                        || StartGameTimer == 5 || StartGameTimer == 4 || StartGameTimer == 3 || StartGameTimer == 2 || StartGameTimer == 1) {
                                    for (Player join : JoinPlayer) {
                                        join.sendMessage(ChatColor.YELLOW + "" + StartGameTimer + "秒後にゲームを開始します");
                                    }
                                }
                                StartGameTimer -= 1;
                            }
                        }.runTaskTimer(MagicStick.getPlugin(), 0, 20L);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "ゲーム参加者が最大のためゲームに参加できません");
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
                }
            } else {
                p.sendMessage(ChatColor.RED + "使用可能なキットを選択してください");
                p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
            }
        } else {
            p.sendMessage(ChatColor.RED + "あなたはすでにゲームに参加しています");
            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
        }
    }

    public static void QuitPlayer(Player p){
        if (JoinPlayer.contains(p)){
            for (Player join : JoinPlayer) {
                join.sendMessage(ChatColor.GRAY + p.getName() + ChatColor.YELLOW + "が退出しました (" + ChatColor.AQUA + (JoinPlayer.size() - 1) + ChatColor.YELLOW + "/"
                        + ChatColor.AQUA + "4" + ChatColor.YELLOW + ")");
            }
            JoinPlayer.remove(p);
            p.getInventory().clear();
            p.getInventory().setItem(0, getData("item").getItemStack("Custom"));
            p.getInventory().setItem(4, getData("item").getItemStack("Join"));
            p.getInventory().setItem(8, getData("item").getItemStack("Shop"));
        } else {
            p.sendMessage(ChatColor.RED + "あなたはゲームに参加していません");
            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
        }
    }

    public static void GameStart(){
        List<String> Maps = getData("map").getStringList("All");
        Random random = new Random();
        int randomMap = random.nextInt(Maps.size());
        String Map = Maps.get(randomMap);
        List<String> LocationList = new ArrayList<>(Arrays.asList(".location1", ".location2", ".location3", ".location4"));
        Collections.shuffle(LocationList);
        int list = 0;
        for (Player player : GamePlayer){
            haruudon.udon.magicstick.Scoreboard.Remove(player);
            player.getInventory().clear();
            Location loc = (Location) getData("map").get(Map + LocationList.get(list));
            player.teleport(loc);
            player.sendTitle(ChatColor.WHITE + "" + ChatColor.BOLD + Map, ChatColor.GREEN + "マップ", 0, 60, 20);
            list += 1;
        }
        GameStartTimer();
    }

    public static void GameStartTimer() {
        new BukkitRunnable() {
            int StartGameTimer = 7;
            @Override
            public void run() {
                if (GamePlayer.isEmpty()){
                    this.cancel();
                } else if (StartGameTimer == 0) {
                    Timer();
                    //スコアボード作成
                    Scoreboard score = Bukkit.getScoreboardManager().getMainScoreboard();
                    Team t = score.getTeam("hide");
                    if (t == null){
                        t = score.registerNewTeam("hide");
                        t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                        t.setCanSeeFriendlyInvisibles(false);
                    }
                    //ここまで
                    for (Player player : GamePlayer) {
                        player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Start!", "", 0, 60, 20);
                        player.sendMessage(ChatColor.GREEN + "アイテムを支給しました");
                        t.addEntry(player.getName());
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
                    for (Player player : GamePlayer) {
                        player.sendTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + StartGameTimer, "", 0, 60, 20);
                    }
                } else if (StartGameTimer == 3 || StartGameTimer == 2 || StartGameTimer == 1) {
                    for (Player player : GamePlayer) {
                        player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + StartGameTimer, "", 0, 60, 20);
                    }
                }
                StartGameTimer -= 1;
            }
        }.runTaskTimer(MagicStick.getPlugin(), 0, 20);
    }

    public static void GameEnd(String result) {
        //スコアボード
        Scoreboard score = Bukkit.getScoreboardManager().getMainScoreboard();
        Team t = score.getTeam("hide");
        //ここまで
        Location lobby = (Location) getData("map").get("MainLobby.location");
        Spectator.clear();
        for (Player player : GamePlayer){
            haruudon.udon.magicstick.Scoreboard.Create(player);
            t.removeEntry(player.getName());
            player.setGameMode(GameMode.SURVIVAL);
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
            if (Cooldowns.containsKey(player.getUniqueId())){
                DownAbility.remove(player.getUniqueId());
                Cooldowns.remove(player.getUniqueId());
            }
        }
        switch (result) {
            case "Normal":
                Player Winner = Alive.get(0);
                for (Player player : GamePlayer) {
                    player.sendTitle(ChatColor.WHITE + "" + ChatColor.BOLD + Winner.getName(), ChatColor.YELLOW + "勝者", 0, 60, 20);
                }
                GamePlayer.clear();
                Alive.clear();
                break;
            case "TimeUp":
                for (Player player : GamePlayer) {
                    player.sendTitle(ChatColor.WHITE + "" + ChatColor.BOLD + "なし", ChatColor.YELLOW + "勝者", 0, 60, 20);
                }
                GamePlayer.clear();
                Alive.clear();
                break;
            case "ShutDown":
                break;
        }
    }

    public static void Timer() {
        new BukkitRunnable() {
            int Timer = 600;
            @Override
            public void run() {
                if (GamePlayer.isEmpty()){
                    this.cancel();
                } else if (Timer == 0) {
                    GameEnd("TimeUp");
                    this.cancel();
                } else if (Timer > 60) {
                    for (Player player : GamePlayer) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "残り時間: " + (Timer / 60) + " 分"));
                    }
                } else {
                    for (Player player : GamePlayer) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "残り時間: " + Timer + " 秒"));
                    }
                }
                Timer -= 1;
            }
        }.runTaskTimer(MagicStick.getPlugin(), 0, 20L);
    }
}
//    public void SendResult(Player p){
//        p.sendMessage(ChatColor.GOLD + "<" + ChatColor.YELLOW + "--------------------------------------------------" + ChatColor.GOLD  + ">");
//        p.sendMessage("");
//        p.sendMessage(ChatColor.GOLD + "<" + ChatColor.YELLOW + "--------------------------------------------------" + ChatColor.GOLD  + ">");
//    }
