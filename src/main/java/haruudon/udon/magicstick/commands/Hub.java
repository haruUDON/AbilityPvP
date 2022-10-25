package haruudon.udon.magicstick.commands;

import haruudon.udon.magicstick.Join;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static haruudon.udon.magicstick.MagicStick.getData;

public class Hub implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p){
            if (Join.GamePlayer.contains(p)) return false;
            if (Join.Spectator.contains(p)) return false;
            Location loc = (Location) getData("map").get("MainLobby.location");
            p.teleport(loc);
        }
        return false;
    }
}
