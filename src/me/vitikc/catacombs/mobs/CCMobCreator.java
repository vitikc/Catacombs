package me.vitikc.catacombs.mobs;

import me.vitikc.catacombs.CCMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class CCMobCreator {

    public void spawnMob(String name, int amount, Location location, EntityType type){
        //Spawn's mob at location with custom name and type
        HashSet<LivingEntity> entities = new HashSet<>();
        for(int i = 0; i < amount; i++) {
            //Debug information
            /*
            CCMain.getInstance().getLogger().info("Spawning " + type.name() + " in world " +
                    location.getWorld().getName() + " with name " + name +
                    " at \nposition " +
                    "\nx:" + location.getBlockX() +
                    "\ny:" + location.getBlockY() +
                    "\nz:" + location.getBlockZ());
            */
            LivingEntity entity = (LivingEntity) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location, type);
            entity.setCustomName(name);
            entity.setCustomNameVisible(false);
            entities.add(entity);
        }
        CCMobsManager.getMobList().put(name, entities);
        HashMap<Location, Integer> map = new HashMap<>();
        map.put(location, amount);
        CCMobsManager.getMobAmount().put(name,map);
    }
    public void spawnMob(String name, Location location, EntityType type){
        LivingEntity entity = (LivingEntity) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location, type);
        entity.setCustomName(name);
        entity.setCustomNameVisible(false);

        CCMobsManager.getMobList().get(name).add(entity);
    }
    public void respawnMobsAt(String region, EntityType type){
        int amount = CCMobsManager.getMobAmount().get(region).get(0);
        Location location = (Location) CCMobsManager.getMobAmount().get(region).keySet().toArray()[0];
        for (Iterator<LivingEntity> iterator = CCMobsManager.getMobList().get(region).iterator(); iterator.hasNext();){
            LivingEntity entity = iterator.next();
            entity.remove();
            iterator.remove();
        }
        for (int i = 0; i < amount; i++)
            spawnMob(region,location,type);
    }
}
