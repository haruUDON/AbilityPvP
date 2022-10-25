package haruudon.udon.magicstick;

import haruudon.udon.magicstick.commands.Hub;
import haruudon.udon.magicstick.commands.SetCrateBlock;
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
    private static FileConfiguration kill;
    private static FileConfiguration item;
    private static FileConfiguration block;
    @Override
    public void onEnable() {
        plugin = this;
        data = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "data.yml"));
        ability = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "ability.yml"));
        weapon = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "weapon.yml"));
        type = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "type.yml"));
        map = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "map.yml"));
        kill = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "effect.yml"));
        item = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "item.yml"));
        block = YamlConfiguration.loadConfiguration(new File(MagicStick.getPlugin().getDataFolder(), "block.yml"));

        Mana.setupMana();
        GUIManager.setupNow();
        Cooldown.setupCooldown();
        Join.setupJoinPlayer();
        Gacha.setupGachaResult();

        getCommand("testability").setExecutor(new TestAbility());
        getCommand("test").setExecutor(new TestAbility());
        getCommand("crate").setExecutor(new SetCrateBlock());
        getCommand("hub").setExecutor(new Hub());

        getServer().getPluginManager().registerEvents(new AbilityEvent(), this);
        getServer().getPluginManager().registerEvents(new FoodLevel(), this);
        getServer().getPluginManager().registerEvents(new GenerateManaEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new GUIClickEvent(), this);
        getServer().getPluginManager().registerEvents(new LobbyItemEvent(), this);
        getServer().getPluginManager().registerEvents(new WeaponEvent(), this);
        getServer().getPluginManager().registerEvents(new CrateBlockEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MagicStick getPlugin(){
        return plugin;
    }

    public static FileConfiguration getData(String s){
        FileConfiguration re = null;
        if (s.equals("player")) re = data;
        if (s.equals("ability")) re = ability;
        if (s.equals("weapon")) re = weapon;
        if (s.equals("type")) re = type;
        if (s.equals("map")) re = map;
        if (s.equals("item")) re = item;
        if (s.equals("block")) re = block;
        if (s.equals("killeffect")) re = kill;
        return re;
    }

    public static void savePlayerData(){
        try {
            data.save(new File(MagicStick.getPlugin().getDataFolder(), "data.yml"));
        } catch (IOException e) {
            plugin.getLogger().info("データをセーブできませんでした。");
        }
    }

    public static void saveBlockData(){
        try {
            block.save(new File(MagicStick.getPlugin().getDataFolder(), "block.yml"));
        } catch (IOException e) {
            plugin.getLogger().info("データをセーブできませんでした。");
        }
    }
}
