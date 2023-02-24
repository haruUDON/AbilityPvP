package haruudon.udon.abilitypvp;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class GameRoomManager {
    private static final ArrayList<GameRoom> gameRooms = new ArrayList<>();

    public static void setupGameRoomManager(){
        gameRooms.add(new GameRoom(""));
        gameRooms.add(new GameRoom(""));
        gameRooms.add(new GameRoom(""));
        gameRooms.add(new GameRoom(""));
        gameRooms.add(new GameRoom(""));
    }

    public static ArrayList<GameRoom> getGameRooms(){
        return gameRooms;
    }

    public static GameRoom getRoom(Player p){
        for (GameRoom g : gameRooms){
            if (g.getPlayers().contains(p)) return g;
        }
        return null;
    }

    public static Boolean containsGamePlayer(Player p){
        return getRoom(p) != null;
    }
}
