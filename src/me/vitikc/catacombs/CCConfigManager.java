package me.vitikc.catacombs;

import me.vitikc.catacombs.mobs.CCMobCreator;
import me.vitikc.catacombs.mobs.CCMobsManager;
import me.vitikc.catacombs.regions.CCRegion;
import me.vitikc.catacombs.regions.CCRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class CCConfigManager {
    private File configFile;
    private File spawnersFile;
    private File regionsFile;

    private YamlConfiguration config;
    private YamlConfiguration spawners;
    private YamlConfiguration regions;

    private CCMobCreator mobCreator;

    public static long checkMobLocation = 20L; //Every second

    CCConfigManager(){
        createFiles();
    }

    private void createFiles(){
        configFile = new File(CCMain.getInstance().getDataFolder(), "config.yml");
        regionsFile = new File(CCMain.getInstance().getDataFolder(), "regions.yml");
        spawnersFile = new File(CCMain.getInstance().getDataFolder(), "spawners.yml");

        config = YamlConfiguration.loadConfiguration(configFile);
        regions = YamlConfiguration.loadConfiguration(regionsFile);
        spawners = YamlConfiguration.loadConfiguration(spawnersFile);

        mobCreator = CCMain.getInstance().getMobCreator();

        if (!configFile.exists()){
            saveConfigFile();
        }
        if (!regionsFile.exists()){
            saveRegionsFile();
        }
        if (!spawnersFile.exists()){
            saveSpawnersFile();
        }
        setDefaultDrops();
        setDefaultPrices();
        setDefaultLocationCheck();

        int time = config.getInt("config.location_check");
        if (time < 1) time = 1;
        checkMobLocation = 20L * time;

    }

    private void setDefaultPrices(){
        for (EntityType t : EntityType.values()){
            if (t.getEntityClass() == null) {
                continue;
            }
            if (Creature.class.isAssignableFrom(t.getEntityClass())) {
                setMobPrice(t, 0d);
            }
        }
    }

    public ArrayList<ItemStack> getMobDrop(EntityType type){
        String drops = (String) config.get("mobs." + type.name().toLowerCase() + ".drop.item");
        ArrayList<String> strings = new ArrayList<>();
        strings.addAll(Arrays.asList(drops.split(", ")));
        ArrayList<ItemStack> items = new ArrayList<>();
        for (String s : strings){
            Material material = Material.getMaterial(s.toUpperCase());
            if(material == null){
                CCMain.getInstance().getLogger().info("Bad item!");
                return null;
            }
            items.add(new ItemStack(material,1));
        }
        return  items;
    }

    public double getMobPrice(EntityType type){
        return (double) config.get("mobs." + type.name().toLowerCase() + ".price");
    }

    public int getDropChance(EntityType type){
        return (int) config.get("mobs." + type.name().toLowerCase()+ ".drop.chance");
    }

    private void setDefaultLocationCheck(){
        if (config.isSet("config.location_check"))
            return;
        config.set("config.location_check", 1);
        saveConfigFile();
    }

    private void setDefaultDrops(){
        for (EntityType t : EntityType.values()){
            if (t.getEntityClass() == null) {
                continue;
            }
            if (!Creature.class.isAssignableFrom(t.getEntityClass()))
                continue;
            setMobDrop(t, "Stick, Stick");
            setDropChance(t, 10);
        }
    }

    private void setMobPrice(EntityType type, double price){
        String path = "mobs." + type.name().toLowerCase() + ".price";
        if (config.isSet(path))
            return;
        config.set(path, price);
        saveConfigFile();
    }
    private void setMobDrop(EntityType type, String drop){
        String path = "mobs." + type.name().toLowerCase() + ".drop.item";
        if (config.isSet(path))
            return;
        config.set(path, drop);
        saveConfigFile();
    }
    private void setDropChance(EntityType type, int chance){
        String path = "mobs." + type.name().toLowerCase() + ".drop.chance";
        if (config.isSet(path))
            return;
        config.set(path, chance);
        saveConfigFile();
    }

    public void removeRegion(String name){
        regions.set(name, null);
        saveAllRegions();
    }

    public void removeSpawn(String name){
        spawners.set(name, null);
        saveAllSpawners();
    }

    public void saveAllRegions(){
        ArrayList<String> names = new ArrayList<>();
        names.addAll(CCRegionManager.getRegions().keySet());
        for (String region : names){
            saveRegion(CCRegionManager.getRegions().get(region));
        }
        saveRegionsFile();
    }

    private void saveRegion(CCRegion region){
        regions.set(region.getName(), region);
    }

    public void saveAllSpawners(){
        ArrayList<String> names = new ArrayList<>();
        names.addAll(CCMobsManager.getMobAmount().keySet());
        for (String spawner : names){
            saveSpawner(spawner);
        }
        saveSpawnersFile();
    }

    private void saveSpawner(String name){
        EntityType type = ((LivingEntity)CCMobsManager.getMobList().get(name).toArray()[0]).getType();
        Location location = (Location) CCMobsManager.getMobAmount().get(name).keySet().toArray()[0];
        spawners.set(name + ".name",name);
        spawners.set(name + ".type", type.toString());
        spawners.set(name + ".amount", CCMobsManager.getMobAmount().get(name).values().toArray()[0]);
        spawners.set(name + ".location.world", location.getWorld().getName());
        spawners.set(name + ".location.x", location.getBlockX());
        spawners.set(name + ".location.y", location.getBlockY());
        spawners.set(name + ".location.z", location.getBlockZ());

    }

    public void loadAllSpawners(){
        ArrayList<String> names = new ArrayList<>();
        names.addAll(spawners.getKeys(false));
        for (String name : names){
            loadSpawner(name);
        }
    }

    private void loadSpawner(String name){
        EntityType type;
        int amount;
        double x;
        double y;
        double z;
        String world;
        type = EntityType.valueOf((String)spawners.get(name + ".type"));
        amount = (int) spawners.get(name + ".amount");
        x = (int) spawners.get(name + ".location.x");
        y = (int) spawners.get(name + ".location.y");
        z = (int) spawners.get(name + ".location.z");
        world = (String) spawners.get(name + ".location.world");
        Location location = new Location(Bukkit.getWorld(world),x,y,z);
        HashMap<Location, Integer> map = new HashMap<>();
        map.put(location, amount);
        HashSet<LivingEntity> set = new HashSet<>();
        CCMobsManager.getMobAmount().put(name,map);
        CCMobsManager.getMobList().put(name, set);
        if (amount < 0 || world == null || type == EntityType.UNKNOWN) {
            return;
        }
        for (int i = 0; i < amount; i++){
            mobCreator.spawnMob(name,location,type);
        }
    }

    public void loadAllRegions(){
        ArrayList<String> names = new ArrayList<>();
        names.addAll(regions.getKeys(false));
        for(String region : names){
            CCRegionManager.getRegions().put(region, loadRegion(region));
        }
    }

    private CCRegion loadRegion(String name){
        return (CCRegion) regions.get(name);
    }

    private void saveConfigFile(){
        try {
            config.save(configFile);
        } catch (IOException e) {
            CCMain.getInstance().getLogger().info("Can't save config.yml");
            e.printStackTrace();
        }
    }
    private void saveRegionsFile(){
        try {
            regions.save(regionsFile);
        } catch (IOException e) {
            CCMain.getInstance().getLogger().info("Can't save regions.yml");
            e.printStackTrace();
        }
    }
    private void saveSpawnersFile(){
        try {
            spawners.save(spawnersFile);
        } catch (IOException e) {
            CCMain.getInstance().getLogger().info("Can't save spawners.yml");
            e.printStackTrace();
        }
    }

}
