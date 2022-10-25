package haruudon.udon.magicstick;

import org.bukkit.entity.Player;

import static haruudon.udon.magicstick.MagicStick.getData;
import static haruudon.udon.magicstick.MagicStick.savePlayerData;

public class CoinAndMagicOre {

    public static void addCoin(Player p, int add) {
        int coin = getData("player").getInt(p.getUniqueId().toString() + ".coin");
        getData("player").set(p.getUniqueId().toString() + ".coin", coin + add);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static void removeCoin(Player p, int remove) {
        int coin = getData("player").getInt(p.getUniqueId().toString() + ".coin");
        getData("player").set(p.getUniqueId().toString() + ".coin", coin - remove);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static boolean checkUseCoin(Player p, int check) {
        return getData("player").getInt(p.getUniqueId().toString() + ".coin") >= check;
    }

    public static int getCoin(Player p) {
        return (getData("player").getInt(p.getUniqueId().toString() + ".coin"));
    }

    public static void addMagicOre(Player p, int add) {
        int magicore = getData("player").getInt(p.getUniqueId().toString() + ".magicore");
        getData("player").set(p.getUniqueId().toString() + ".magicore", magicore + add);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static void removeMagicOre(Player p, int remove) {
        int magicore = getData("player").getInt(p.getUniqueId().toString() + ".magicore");
        getData("player").set(p.getUniqueId().toString() + ".magicore", magicore - remove);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static boolean checkUseMagicOre(Player p, int check) {
        return getData("player").getInt(p.getUniqueId().toString() + ".magicore") >= check;
    }

    public static int getMagicOre(Player p) {
        return getData("player").getInt(p.getUniqueId().toString() + ".magicore");
    }

    public static void addMagicDust(Player p, int add) {
        int magicdust = getData("player").getInt(p.getUniqueId().toString() + ".magicdust");
        getData("player").set(p.getUniqueId().toString() + ".magicdust", magicdust + add);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static void removeMagicDust(Player p, int remove) {
        int magicdust = getData("player").getInt(p.getUniqueId().toString() + ".magicdust");
        getData("player").set(p.getUniqueId().toString() + ".magicdust", magicdust - remove);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static boolean checkUseMagicDust(Player p, int check) {
        return getData("player").getInt(p.getUniqueId().toString() + ".magicdust") >= check;
    }

    public static int getMagicDust(Player p) {
        return (getData("player").getInt(p.getUniqueId().toString() + ".magicdust"));
    }
}
