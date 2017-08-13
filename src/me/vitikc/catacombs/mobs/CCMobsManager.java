package me.vitikc.catacombs.mobs;


import com.sk89q.worldedit.Vector;
import me.vitikc.catacombs.CCConfigManager;
import me.vitikc.catacombs.CCMain;
import me.vitikc.catacombs.regions.CCRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class CCMobsManager {
    //TODO: Mob respawning Completed 11.08.17 02:42

    private static HashMap<String, HashMap<Location, Integer>> mobAmount = new HashMap<>();
    private static HashMap<String, HashSet<LivingEntity>> mobList = new HashMap<>();

    private int taskId = -1;

    public CCMobsManager(){
        startCheckMobLocation();
    }

    public static HashMap<String, HashMap<Location, Integer>> getMobAmount(){
        return mobAmount;
    }

    public static HashMap<String, HashSet<LivingEntity>> getMobList(){
        return mobList;
    }

    private void killNext(final LivingEntity e){
        Bukkit.getScheduler().scheduleSyncDelayedTask(CCMain.getInstance(), new Runnable(){

            @Override
            public void run() {
                e.damage(1000f);
            }
        }, 10L);
    }

    public void startCheckMobLocation(){
        final ArrayList<String> names = new ArrayList<>();
        names.addAll(CCRegionManager.getRegions().keySet());
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CCMain.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (String name : names) {
                    if (mobList.isEmpty())
                        return;
                    if (mobList.get(name) == null)
                        return;
                    for (Iterator<LivingEntity> iterator = mobList.get(name).iterator(); iterator.hasNext(); ){
                        //Debug
                        /*CCMain.getInstance().getLogger().info("Mob location:" +
                                "\nx:" + e.getLocation().getBlockX() +
                                "\ny:" + e.getLocation().getBlockY());
                        */
                        LivingEntity e = iterator.next();
                        Vector vector = new Vector(e.getLocation().getBlockX(), e.getLocation().getBlockY(), e.getLocation().getBlockZ());
                        if (!CCRegionManager.getRegions().get(name).contains(vector)) {
                            killNext(e);
                            //CCMain.getInstance().getLogger().info("One mob killed one spawned");
                        }
                    }
                }
            }
        }, 5L, CCConfigManager.checkMobLocation);
    }

    public void stopCheckMobLocation(){
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public void restartCheckMobLocation(){
        stopCheckMobLocation();
        startCheckMobLocation();
    }

    public void killAllMobs(){
        ArrayList<String> names = new ArrayList<>();
        names.addAll(mobList.keySet());
        for (String name : names){
            for (Iterator<LivingEntity> iterator = mobList.get(name).iterator(); iterator.hasNext();){
                LivingEntity entity = iterator.next();
                iterator.remove();
                entity.remove();
            }
        }
    }

    public void killMobsAt(String name){
        for (Iterator<LivingEntity> iterator = mobList.get(name).iterator(); iterator.hasNext();){
            LivingEntity entity = iterator.next();
            iterator.remove();
            entity.remove();
        }
    }

}
