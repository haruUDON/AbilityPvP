package haruudon.udon.abilitypvp;

import org.bukkit.entity.Player;

import static haruudon.udon.abilitypvp.AbilityPvP.getData;
import static haruudon.udon.abilitypvp.AbilityPvP.savePlayerData;

public class KillAndWinCount {
    public static void addKillCount(Player p, int add) {
        int killcount = getData("player").getInt(p.getUniqueId().toString() + ".killcount");
        getData("player").set(p.getUniqueId().toString() + ".killcount", killcount + add);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static int getKillCount(Player p) {
        return (getData("player").getInt(p.getUniqueId().toString() + ".killcount"));
    }

    public static void addWinCount(Player p, int add) {
        int wincount = getData("player").getInt(p.getUniqueId().toString() + ".wincount");
        getData("player").set(p.getUniqueId().toString() + ".wincount", wincount + add);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static int getWinCount(Player p) {
        return getData("player").getInt(p.getUniqueId().toString() + ".wincount");
    }
}
