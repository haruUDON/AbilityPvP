package haruudon.udon.magicstick;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

public class Scoreboard {
    public static void Create(Player p){
        new BukkitRunnable(){
            @Override
            public void run() {
                ScoreboardManager m = Bukkit.getScoreboardManager();
                org.bukkit.scoreboard.Scoreboard b = m.getNewScoreboard();
                Objective o = b.registerNewObjective("コイン", "");
                o.setDisplaySlot(DisplaySlot.SIDEBAR);
                o.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Ability" + ChatColor.GOLD + "" + ChatColor.BOLD + " PvP");
                Score line1 = o.getScore(ChatColor.YELLOW + "---------------");
                Score air1 = o.getScore("");
                Score coin = o.getScore(ChatColor.WHITE + "コイン: " + ChatColor.GOLD + CoinAndMagicOre.getCoin(p));
                Score magicore = o.getScore(ChatColor.WHITE + "魔法の鉱石: " + ChatColor.DARK_PURPLE + CoinAndMagicOre.getMagicOre(p));
                Score magicdust = o.getScore(ChatColor.WHITE + "魔法の粉: " + ChatColor.DARK_AQUA + CoinAndMagicOre.getMagicDust(p));
                Score air2 = o.getScore(" ");
                Score killCount = o.getScore(ChatColor.WHITE + "通算キル数: " + ChatColor.GREEN + KillAndWinCount.getKillCount(p));
                Score winCount = o.getScore(ChatColor.WHITE + "通算勝利数: " + ChatColor.GREEN + KillAndWinCount.getWinCount(p));
                Score air3 = o.getScore("  ");
                Score line2 = o.getScore(ChatColor.YELLOW + "--------------- ");
                line2.setScore(1);
                air3.setScore(2);
                magicdust.setScore(3);
                magicore.setScore(4);
                coin.setScore(5);
                air2.setScore(6);
                winCount.setScore(7);
                killCount.setScore(8);
                air1.setScore(9);
                line1.setScore(10);
                p.setScoreboard(b);
            }
        }.runTaskLater(MagicStick.getPlugin(), 1);
    }

    public static void Remove(Player p){
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
