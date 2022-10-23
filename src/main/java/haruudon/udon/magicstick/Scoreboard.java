package haruudon.udon.magicstick;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

public class Scoreboard {
    public static void Create(Player p){
        ScoreboardManager m = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard b = m.getNewScoreboard();
        Objective o = b.registerNewObjective("コイン", "");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Ability" + ChatColor.GOLD + "" + ChatColor.BOLD + " PvP");
        Score air = o.getScore("");
        Score coin = o.getScore(ChatColor.WHITE + "コイン: " + ChatColor.GOLD + CoinAndMagicOre.getCoin(p));
        Score magicore = o.getScore(ChatColor.WHITE + "魔法の鉱石: " + ChatColor.DARK_PURPLE + CoinAndMagicOre.getMagicOre(p));
        Score killCount = o.getScore(ChatColor.WHITE + "通算キル数: " + ChatColor.GREEN + 0);
        Score winCount = o.getScore(ChatColor.WHITE + "通算勝利数: " + ChatColor.GREEN + 0);
        air.setScore(1);
        coin.setScore(2);
        magicore.setScore(4);
        air.setScore(5);
        winCount.setScore(6);
        killCount.setScore(7);
        air.setScore(8);
        p.setScoreboard(b);
    }

    public static void Remove(Player p){
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
