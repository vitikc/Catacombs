package me.vitikc.catacombs;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import me.vitikc.catacombs.mobs.CCMobCreator;
import me.vitikc.catacombs.mobs.CCMobsManager;
import me.vitikc.catacombs.regions.CCRegion;
import me.vitikc.catacombs.regions.CCRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;;
import java.util.UUID;

public class CCCommandManager implements CommandExecutor {

    private WorldEdit worldEdit = CCMain.getWorldEdit();

    private CCMobCreator mobCreator = CCMain.getInstance().getMobCreator();
    private CCMobsManager mobsManager = CCMain.getInstance().getMobsManager();
    private CCConfigManager configManager = CCMain.getInstance().getConfigManager();
    private CCMessageManager messageManager = CCMain.getInstance().getMessageManager();

    private HashMap<String, String> incomplete = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //"cc" command executor
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can execute this command!");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("catacombs.main")){
            p.sendMessage(messageManager.getMessage("no_permissions"));
            return true;
        }
        if (args.length == 0){
            printHelp(p.getUniqueId());
            return true;
        }
        if (args.length == 1){
            switch (args[0].toLowerCase()){
                case "create":
                    //World Edit stuff.
                    //Check for selection (both vectors).
                    //Save vectors to file with region name.
                    if (!p.hasPermission("catacombs.create")){
                        p.sendMessage(messageManager.getMessage("no_permissions"));
                        return true;
                    }
                    if (!incomplete.containsKey(p.getName())){
                        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("create_before_spawn"));
                        return true;
                    }
                    if (isRegionSelected(p.getName())){
                        createRegion(p.getName(), incomplete.get(p.getName()));
                        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("create_success")
                                + incomplete.get(p.getName()));
                        incomplete.remove(p.getName());
                        mobsManager.restartCheckMobLocation();
                        return true;
                    }
                    break;
                case "list":
                    if (!p.hasPermission("catacombs.list")){
                        p.sendMessage(messageManager.getMessage("no_permissions"));
                        return true;
                    }
                    printRegionList(p.getUniqueId());
                    break;
                case "help":
                    if (!p.hasPermission("catacombs.help")){
                        p.sendMessage(messageManager.getMessage("no_permissions"));
                        return true;
                    }
                    printHelp(p.getUniqueId());
                    break;
                default:
                    p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_wrong_command"));
                    break;
            }
        }
        else if (args.length == 2){
            switch (args[0].toLowerCase()){
                case "remove":
                    if (!p.hasPermission("catacombs.remove")){
                        p.sendMessage(messageManager.getMessage("no_permissions"));
                        return true;
                    }
                    if (!CCRegionManager.getRegions().containsKey(args[1].toLowerCase())){
                        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_region_not_exists")
                                + args[1]);
                        return true;
                    }
                    if (!CCMobsManager.getMobAmount().containsKey(args[1].toLowerCase())){
                        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_spawn_not_exists")
                                + args[1]);
                        return true;
                    }
                    removeRegion(args[1].toLowerCase());
                    removeMobSpawn(args[1].toLowerCase());
                    mobsManager.restartCheckMobLocation();
                    break;
                case "respawn":
                    if (!p.hasPermission("catacombs.respawn")){
                        p.sendMessage(messageManager.getMessage("no_permissions"));
                        return true;
                    }
                    if (!CCRegionManager.getRegions().containsKey(args[1].toLowerCase())){
                        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_region_not_exists") + args[1]);
                        return true;
                    }
                    if (!CCMobsManager.getMobAmount().containsKey(args[1].toLowerCase())){
                        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_spawn_not_exists") + args[1]);
                        return true;
                    }
                    EntityType type = ((LivingEntity)CCMobsManager.getMobList().values().toArray()[0]).getType();
                    mobCreator.respawnMobsAt(args[1].toLowerCase(), type);
                    p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("mobs_respawn") + args[1].toLowerCase());
                    break;
                default:
                    break;
            }
        }
        /*else if (args.length == 3){
            switch (args[0].toLowerCase()){
                case "remove":
                    if (args[1].equalsIgnoreCase("region")){
                        if (!CCRegionManager.getRegions().containsKey(args[2].toLowerCase())){
                            p.sendMessage(messageManager.getMessage("error_region_not_exists") + args[1]);
                            return true;
                        }
                        removeRegion(args[2].toLowerCase());
                    }
                    else if (args[1].equalsIgnoreCase("spawn")){
                        if (!CCMobsManager.getMobAmount().containsKey(args[2].toLowerCase())){
                            p.sendMessage(messageManager.getMessage("error_spawn_not_exists") + args[1]);
                            return true;
                        }
                        removeMobSpawn(args[2].toLowerCase());
                    }
                    else{
                        p.sendMessage(messageManager.getMessage("error_unsupported_arg") + args[1]);
                        return true;
                    }
                    mobsManager.restartCheckMobLocation();
                    break;
                default:
                    p.sendMessage("Wrong command type help to see commands");
                    break;
            }
        }*/
        else if (args.length == 4){
            switch (args[0].toLowerCase()){
                case "spawn":
                    if (!p.hasPermission("catacombs.create")){
                        p.sendMessage(messageManager.getMessage("no_permissions"));
                        return true;
                    }
                    int count = Integer.parseInt(args[3]);
                    if (incomplete.containsKey(p.getName())){
                        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_incomplete"));
                        return true;
                    }
                    if (count == 0 || count < 0) {
                        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_mob_amount")
                                + args[3]);
                        return false;
                    }
                    if (isRegionExists(args[1].toLowerCase())){
                        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_region_already_exists")
                                + args[1]);
                        return true;
                    }
                    for (EntityType t : EntityType.values()){
                        if (t.name().equalsIgnoreCase(args[2])) {
                            EntityType type = EntityType.valueOf(args[2].toUpperCase());
                            if (type == EntityType.UNKNOWN) {
                                p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_mob_type"));
                                return true;
                            }
                            setMobSpawn(args[1].toLowerCase(), type, count, p.getLocation());
                            incomplete.put(p.getName(),args[1]);
                            p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("after_spawn"));
                            return true;
                        }
                    }
                    p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_mob_type"));
                    break;
                default:
                    p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_wrong_command"));
                    break;
            }
        }
        return true;
    }

    private void printHelp(UUID player){
        //Prints help message to player

        Player p = Bukkit.getPlayer(player);
        p.sendMessage(ChatColor.LIGHT_PURPLE + "Catacombs plugin");
        p.sendMessage(ChatColor.GOLD + "Author: " + CCMessageManager.author);
        p.sendMessage(ChatColor.GOLD + "Version: " + CCMessageManager.version);
        p.sendMessage(messageManager.getMessage("help_main"));
        p.sendMessage(messageManager.getMessage("help_create"));
        p.sendMessage(messageManager.getMessage("help_list"));
        p.sendMessage(messageManager.getMessage("help_help"));
        p.sendMessage(messageManager.getMessage("help_respawn"));
        p.sendMessage(messageManager.getMessage("help_remove"));
        //p.sendMessage(messageManager.getMessage("help_remove_spawn"));
        //p.sendMessage(messageManager.getMessage("help_remove_region"));
        p.sendMessage(messageManager.getMessage("help_spawn"));

        //Debug
        /*
        p.sendMessage("Help message here");
        for (CCRegion region : CCRegionManager.getRegions().values()){
            p.sendMessage("Region: " + region.getName());
            p.sendMessage("Min: " + region.getMinimum());
            p.sendMessage("Max: " + region.getMaximum());
        }
        for (String name : CCMobsManager.getMobAmount().keySet()){
            p.sendMessage("Name: " + name);
            p.sendMessage("Location: " + CCMobsManager.getMobAmount().get(name).keySet().toString());
            p.sendMessage("Amount: " + CCMobsManager.getMobAmount().get(name).values().toString());
            p.sendMessage("Type: " + CCMobsManager.getMobList().get(name).toArray()[0].toString());
        }*/
    }

    private void printRegionList(UUID player){
        Player p = Bukkit.getPlayer(player);
        p.sendMessage(CCMessageManager.announcer + messageManager.getMessage("region_list"));
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(String s : CCRegionManager.getRegions().keySet()){
            sb.append(s + ", ");
        }
        sb.replace(sb.length()-2, sb.length(), "]");
        p.sendMessage(sb.toString());
    }

    private void setMobSpawn(String name, EntityType type, int count, Location location){
        //Setting mob spawn location and saving it to file
        //And starting to spawn mobs
        mobCreator.spawnMob(name,count,location, type); //Zombie to test
    }

    private void removeRegion(String name){
        CCRegionManager.getRegions().remove(name);
        configManager.removeRegion(name);
    }

    private void removeMobSpawn(String name){
        mobsManager.killMobsAt(name);
        CCMobsManager.getMobList().remove(name);
        CCMobsManager.getMobAmount().remove(name);
        configManager.removeSpawn(name);
    }

    private void createRegion(String player, String name){
        LocalWorld world = worldEdit.getSession(player).getSelectionWorld();
        Vector minimum = new Vector();
        Vector maximum = new Vector();
        try {
            minimum = worldEdit.getSession(player).getSelection(world).getMinimumPoint();
            maximum = worldEdit.getSession(player).getSelection(world).getMaximumPoint();
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }
        CCRegion region = new CCRegion(name, minimum, maximum);
        CCRegionManager.getRegions().put(name, region);
        configManager.saveAllSpawners();
        configManager.saveAllRegions();
    }

    private boolean isRegionExists(String name){
        return CCRegionManager.getRegions().containsKey(name);
    }

    private boolean isRegionSelected(String player){
        LocalWorld world = worldEdit.getSession(player).getSelectionWorld();
        try {
            Vector minimum = worldEdit.getSession(player).getSelection(world).getMinimumPoint();
            Vector maximum = worldEdit.getSession(player).getSelection(world).getMaximumPoint();
            if(minimum == null || maximum == null) return false;

        } catch (IncompleteRegionException e) {
            Bukkit.getPlayer(player).sendMessage(CCMessageManager.announcer + messageManager.getMessage("error_no_selection"));
            return false;
        }
        return true;
    }
}


