package haruudon.udon.magicstick;

import org.bukkit.entity.Player;


import static haruudon.udon.magicstick.MagicStick.getPlayerData;
import static haruudon.udon.magicstick.MagicStick.savePlayerData;

public class CoinAndMagicOre {

    public static void addCoin(Player p, Integer add) {
        int coin = getPlayerData().getInt(p.getUniqueId().toString() + ".coin");
        getPlayerData().set(p.getUniqueId().toString() + ".coin", coin + add);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static void removeCoin(Player p, Integer remove) {
        int coin = getPlayerData().getInt(p.getUniqueId().toString() + ".coin");
        getPlayerData().set(p.getUniqueId().toString() + ".coin", coin - remove);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static boolean checkUseCoin(Player p, Integer check) {
        return getPlayerData().getInt(p.getUniqueId().toString() + ".coin") >= check;
    }

    public static int getCoin(Player p) {
        return (getPlayerData().getInt(p.getUniqueId().toString() + ".coin"));
    }

    public static void addMagicOre(Player p, Integer add) {
        int magicore = getPlayerData().getInt(p.getUniqueId().toString() + ".magicore");
        getPlayerData().set(p.getUniqueId().toString() + ".magicore", magicore + add);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static void removeMagicOre(Player p, Integer remove) {
        int magicore = getPlayerData().getInt(p.getUniqueId().toString() + ".magicore");
        getPlayerData().set(p.getUniqueId().toString() + ".magicore", magicore - remove);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static boolean checkUseMagicOre(Player p, Integer check) {
        return getPlayerData().getInt(p.getUniqueId().toString() + ".magicore") >= check;
    }

    public static int getMagicOre(Player p) {
        return getPlayerData().getInt(p.getUniqueId().toString() + ".magicore");
    }
}
//    public static void setCustomKit(Player p, Integer th,){
//        int magicore = MagicStick.getData().getInt(p.getUniqueId().toString() + ".customkit.first.type");
//        MagicStick.getData().set(p.getUniqueId().toString() + ".customkit.first.type", );
//        saveData();
//}
