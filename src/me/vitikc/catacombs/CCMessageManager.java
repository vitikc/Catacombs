package me.vitikc.catacombs;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class CCMessageManager {
    //private static HashMap<String, String> messages;
    private File file;
    private YamlConfiguration config;
    private HashMap<String,String> messages;

    private boolean isNeedToStore = false;

    public static String announcer = ChatColor.LIGHT_PURPLE + "[CC]";
    public static String author = ChatColor.GOLD + CCMain.getInstance().getDescription().getAuthors().get(0);
    public static String version = ChatColor.AQUA + CCMain.getInstance().getDescription().getVersion();

    public CCMessageManager(){
        messages = new HashMap<>();
        file = new File(CCMain.getInstance().getDataFolder(), "messages.yml");
        config = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            saveMessagesFile();
        }
        setDefaultMessages();
        saveMessagesFile();
    }


    private void setDefaultMessages(){
        setMessage("after_spawn","&aSpawn created! Select region and type /cc create to finish creating catacomb!");
        setMessage("create_before_spawn", "&cType /cc spawn <name> <type> <amount> first!");
        setMessage("create_success","&aSuccessfully created catacomb with name:");
        setMessage("error_mob_amount","&cCan't spawn this amount of mobs:");
        setMessage("error_incomplete","&cYou need to end creation of previous catacomb!");
        setMessage("error_region_already_exists","&cRegion with same name already exists:");
        setMessage("error_region_not_exists", "&cRegion not exists:");
        setMessage("error_no_selection","&cSelect region with //wand first!");
        setMessage("error_spawn_not_exists", "&cSpawn not exists:");
        setMessage("error_mob_type","&cUnknown mob type! Enter valid mob type!");
        setMessage("error_unsupported_arg", "&cUnsupported argument:");
        setMessage("error_wrong_command","&cWrong command type help to see commands!");
        setMessage("mob_kill_reward","&bYou got some money for killing mob:");
        setMessage("region_list","&aList of regions:");
        setMessage("mobs_respawn", "&aMobs respawned in:");
        setMessage("no_permissions", "&cYou don't have permissions!");
        setMessage("help_main", "/cc - Main command. Shows help message.");
        setMessage("help_create", "/cc create - Creates region with mob spawner's name.");
        setMessage("help_list", "/cc list - Shows created region names.");
        setMessage("help_help", "/cc help - Shows this message");
        setMessage("help_respawn", "/cc respawn <name> - Respawn mobs in <name> region.");
        setMessage("help_remove", "/cc remove <name> - Removes region and spawn with <name> name");
        //setMessage("help_remove_spawn", "/cc remove spawn <spawn_name> - Removes only spawn with <spawn_name> name.");
        //setMessage("help_remove_region", "/cc remove region <region_name> - Removes only region with <region_name> name.");
        setMessage("help_spawn","/cc spawn <name> <mob_type> <amount> - Set's spawner with <name>. Spawn's <amount> of <mob_type> mobs.");
    }

    private void setMessage(String key, String value){
        if (!config.contains(key)) {
            config.set(key,value);
            messages.put(key,value);
            return;
        }
        messages.put(key,config.getString(key));
    }

    public String getMessage(String key){
        if (!config.contains(key))
            return key + " message not found";
        return ChatColor.translateAlternateColorCodes('&',messages.get(key));
    }

    private void saveMessagesFile(){
        try {
            config.save(file);
        } catch (IOException e){
            CCMain.getInstance().getLogger().info("Can't save messages.yml");
            e.printStackTrace();
        }
    }
}
