package haruudon.udon.magicstick;

import org.bukkit.entity.Player;

import java.util.List;

import static haruudon.udon.magicstick.MagicStick.getPlayerData;

public class TypeEvent {
    public static boolean CheckType(Player p, String type){
        String select = getPlayerData().getString(p.getUniqueId().toString() + ".customkit.select");
        List<String> types = getPlayerData().getStringList(p.getUniqueId().toString() + ".customkit." + select + ".type");
        for (String pt : types) {
            if (pt.equalsIgnoreCase(type)){
                return true;
            }
        }
        return false;
    }
}
