package haruudon.udon.abilitypvp;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import haruudon.udon.abilitypvp.commands.Coin;
import haruudon.udon.abilitypvp.gacha.Gacha;
import haruudon.udon.abilitypvp.commands.Hub;
import haruudon.udon.abilitypvp.gacha.GachaBlockEvent;
import haruudon.udon.abilitypvp.gacha.SetGachaBlock;
import haruudon.udon.abilitypvp.commands.TestAbility;
import haruudon.udon.abilitypvp.cooldown.Cooldown;
import haruudon.udon.abilitypvp.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

public final class AbilityPvP extends JavaPlugin {

    private static AbilityPvP plugin;
    private static FileConfiguration data;
    private static FileConfiguration ability;
    private static FileConfiguration weapon;
    private static FileConfiguration type;
    private static FileConfiguration map;
    private static FileConfiguration kill;
    private static FileConfiguration item;
    private static FileConfiguration block;
    private static FileConfiguration message;
    private static FileConfiguration template;
    @Override
    public void onEnable() {
        plugin = this;
        data = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "data.yml"));
        ability = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "ability.yml"));
        weapon = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "weapon.yml"));
        type = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "type.yml"));
        map = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "map.yml"));
        kill = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "effect.yml"));
        item = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "item.yml"));
        block = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "block.yml"));
        message = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "message.yml"));
        template = YamlConfiguration.loadConfiguration(new File(AbilityPvP.getPlugin().getDataFolder(), "template.yml"));

        Mana.setupMana();
        GUIManager.setupNow();
        Cooldown.setupCooldown();
        GameMain.setupJoinPlayer();
        Gacha.setupGachaResult();
        TickLoop.StartLoop();
        GameRoomManager.setupGameRoomManager();

        getCommand("testability").setExecutor(new TestAbility());
        getCommand("test").setExecutor(new TestAbility());
        getCommand("gacha").setExecutor(new SetGachaBlock());
        getCommand("hub").setExecutor(new Hub());
        getCommand("coin").setExecutor(new Coin());
        getCommand("magicore").setExecutor(new Coin());
        getCommand("magicdust").setExecutor(new Coin());

        getServer().getPluginManager().registerEvents(new AbilityEvent(), this);
        getServer().getPluginManager().registerEvents(new FoodLevel(), this);
        getServer().getPluginManager().registerEvents(new GenerateManaEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new GUIClickEvent(), this);
        getServer().getPluginManager().registerEvents(new LobbyItemEvent(), this);
        getServer().getPluginManager().registerEvents(new WeaponEvent(), this);
        getServer().getPluginManager().registerEvents(new GachaBlockEvent(), this);
        getServer().getPluginManager().registerEvents(new TypeEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AbilityPvP getPlugin(){
        return plugin;
    }

    public static FileConfiguration getData(String s){
        FileConfiguration returnData = null;
        if (s.equals("player")) returnData = data;
        if (s.equals("ability")) returnData = ability;
        if (s.equals("weapon")) returnData = weapon;
        if (s.equals("type")) returnData = type;
        if (s.equals("map")) returnData = map;
        if (s.equals("item")) returnData = item;
        if (s.equals("block")) returnData = block;
        if (s.equals("killeffect")) returnData = kill;
        if (s.equals("killmessage")) returnData = message;
        if (s.equals("template")) returnData = template;
        return returnData;
    }

    public static void savePlayerData(){
        try {
            data.save(new File(AbilityPvP.getPlugin().getDataFolder(), "data.yml"));
        } catch (IOException e) {
            plugin.getLogger().info("データをセーブできませんでした。");
        }
    }

    public static void saveBlockData(){
        try {
            block.save(new File(AbilityPvP.getPlugin().getDataFolder(), "block.yml"));
        } catch (IOException e) {
            plugin.getLogger().info("データをセーブできませんでした。");
        }
    }

    public static ItemStack getSkull(String value){ //not use
        String signature = "IjXI2pWnYJZThpc6Cu5JrASOX03qj/vOrm+3mSr8ElTDu5f1jIEdyEnwbCMun5yimJRLOtOJ9NtgJTgD+3FzvEHQ23YNaQOHa2UQDXxcOUPBNbzbwnsDsyJy5BofgvK9yTz9yn2COZjI4w6xpTTfY8JAFu3Rupvo43JI9tZNClTgnH94kSnjqNpc49U4ySc9wpOX4e7n0wx+qiynFnuebiw+TSZTyM3dt+39Fz0q7rSYjroiX5kJ61h8KDGEqEp0lntwS5Dw980ypE2wqj2qAtEnCfA6dEFQNTwHdZ38Pa1dnTa+yeZYYiwswrDpLzQJMNsReG/Gv7PM6Srw6J1kBOb9JNqAkGIksdG6jw8rpE32oc5NCrCJeHKnIV6baN4S8tn+SzB7MyxH/dAeaCG1eWAFKtka96WDDlSJ0b3q0yz3QDLj8OfrrmUahjeGzcTyCIFQRZtP2i2LtoMd7GP2qgP19yqP5n5QrMKKhhOrNUNS6Gs6bgYR2aJ0vAoA18JMIVpcgbkFnD0CG1tFnp1TFdErfNstKXeJFgrWMtL6dvJArrIsKM0X2GZD4V8xnhEFzUXvD92McvYo/o3Hf1QyTgSYzgcuLNzjqcglSHtToUIE9lujUPB839LSIy3lfybNFw4ifL+xBeHN43brNdf//RXKVZ2ZIMDaJaKaEa8DxIA=";
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static World MultiverseWorld(String worldname){
        for(World w : Bukkit.getWorlds()){
            if(w.getName().equals(worldname)){
                return w;
            }
        }
        return null;
    }
}
