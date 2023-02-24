package haruudon.udon.abilitypvp;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GameRoom {
    private Boolean isGaming;
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> alive = new ArrayList<>();
    private ArrayList<Player> spectator = new ArrayList<>();
    private String worldName;

    public GameRoom(String worldName){
        this.worldName = worldName;
        isGaming = false;
    }

    public Boolean getIsGaming(){
        return isGaming;
    }

    public void setIsGaming(Boolean b){
        isGaming = b;
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public void addPlayer(Player p){
        players.add(p);
    }

    public void removePlayer(Player p){
        players.remove(p);
    }

    public void clearPlayers(){
        players.clear();
    }

    public ArrayList<Player> getAlive(){
        return alive;
    }

    public void addAlive(Player p){
        alive.add(p);
    }

    public void removeAlive(Player p){
        alive.remove(p);
    }

    public void clearAlive(){
        alive.clear();
    }

    public ArrayList<Player> getSpectator(){
        return spectator;
    }

    public void addSpectator(Player p){
        spectator.add(p);
    }

    public void removeSpectator(Player p){
        spectator.remove(p);
    }

    public void clearSpectator(){
        spectator.clear();
    }

    public String getWorldName(){
        return worldName;
    }
}
