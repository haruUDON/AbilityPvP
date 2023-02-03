package haruudon.udon.abilitypvp;

import org.bukkit.entity.Player;

import static haruudon.udon.abilitypvp.AbilityPvP.getData;
import static haruudon.udon.abilitypvp.AbilityPvP.savePlayerData;

public class CoinAndMagicOre {

    public static void addItems(Player p, String item, int add) {
        int amount = getData("player").getInt(p.getUniqueId().toString() + "." + item);
        getData("player").set(p.getUniqueId().toString() + "." + item, amount + add);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static void removeItems(Player p, String item, int remove) {
        int amount = getData("player").getInt(p.getUniqueId().toString() + "." + item);
        getData("player").set(p.getUniqueId().toString() + "." + item, amount - remove);
        savePlayerData();
        Scoreboard.Create(p);
    }

    public static boolean checkUseItems(Player p, String item, int check) {
        return getData("player").getInt(p.getUniqueId().toString() + "." + item) >= check;
    }

    public static int getItems(Player p, String item) {
        return (getData("player").getInt(p.getUniqueId().toString() + "." + item));
    }
}
