package haruudon.udon.magicstick;

import haruudon.udon.magicstick.commands.TestAbility;
import haruudon.udon.magicstick.cooldown.Cooldown;
import haruudon.udon.magicstick.events.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class MagicStick extends JavaPlugin {

    private static MagicStick plugin;
    private static FileConfiguration data;
    private static FileConfiguration ability;
    private static FileConfiguration weapon;
    private static FileConfiguration type;
    private static FileConfiguration map;
    private static FileConfiguration item;
    @Override
    public void onEnable() {
        plugin = this;
        data = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "data.yml"));
        ability = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "ability.yml"));
        weapon = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "weapon.yml"));
        type = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "type.yml"));
        map = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "map.yml"));
        item = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "item.yml"));

        Mana.setupMana();
        GUIManager.setupNow();
        Cooldown.setupCooldown();
        Join.setupJoinPlayer();

        getCommand("testability").setExecutor(new TestAbility());
        getCommand("test").setExecutor(new TestAbility());

        getServer().getPluginManager().registerEvents(new AbilityEvent(), this);
        getServer().getPluginManager().registerEvents(new FoodLevel(), this);
        getServer().getPluginManager().registerEvents(new GenerateManaEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new GUIClickEvent(), this);
        getServer().getPluginManager().registerEvents(new LobbyItemEvent(), this);
        getServer().getPluginManager().registerEvents(new WeaponEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MagicStick getPlugin(){
        return plugin;
    }

    public static FileConfiguration getPlayerData(){
        return data;
    }

    public static void savePlayerData(){
        try {
            data.save(new File(MagicStick.getPlugin().getDataFolder(), "data.yml"));
        } catch (IOException e) {
            plugin.getLogger().info("データをセーブできませんでした。");
        }
    }

//    public static void saveAbilityData(){
//        try {
//            ability.save(new File(MagicStick.getPlugin().getDataFolder(), "ability.yml"));
//        } catch (IOException e) {
//            plugin.getLogger().info("データをセーブできませんでした。");
//        }
//    }
//
//    public static void saveMapData(){
//        try {
//            map.save(new File(MagicStick.getPlugin().getDataFolder(), "map.yml"));
//        } catch (IOException e) {
//            plugin.getLogger().info("データをセーブできませんでした。");
//        }
//    }

    public static FileConfiguration getAbilityData(){
        return ability;
    }

    public static FileConfiguration getWeaponData(){
        return weapon;
    }

    public static FileConfiguration getTypeData(){
        return type;
    }

    public static FileConfiguration getMapData(){
        return map;
    }

    public static FileConfiguration getItem(){
        return item;
    }
}
