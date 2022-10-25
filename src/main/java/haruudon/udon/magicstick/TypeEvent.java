package haruudon.udon.magicstick;

import org.bukkit.entity.Player;

import java.util.List;

import static haruudon.udon.magicstick.MagicStick.getData;

public class TypeEvent {
    public static boolean CheckType(Player p, String type){
        String select = getData("player").getString(p.getUniqueId().toString() + ".customkit.select");
        List<String> types = getData("player").getStringList(p.getUniqueId().toString() + ".customkit." + select + ".type");
        for (String pt : types) {
            if (pt.equalsIgnoreCase(type)){
                return true;
            }
        }
        return false;
    }
}
