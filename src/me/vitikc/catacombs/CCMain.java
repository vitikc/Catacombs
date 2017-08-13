package me.vitikc.catacombs;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.vitikc.catacombs.mobs.CCMobCreator;
import me.vitikc.catacombs.mobs.CCMobsDieEvent;
import me.vitikc.catacombs.mobs.CCMobsManager;
import me.vitikc.catacombs.regions.CCRegion;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class CCMain extends JavaPlugin {

    private CCMobCreator mobCreator;
    private CCMobsManager mobsManager;
    private CCConfigManager configManager;
    private CCCommandManager commandManager;
    private CCMessageManager messageManager;

    private CCMobsDieEvent mobsDieEvent;

    private static CCMain instance;
    private static WorldEdit worldEdit;
    private static Economy economy;

    private static boolean isEconomyEnabled = false;

    @Override
    public void onEnable() {
        instance = this;
        worldEdit = WorldEdit.getInstance();
        RegisteredServiceProvider<Economy>  economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            isEconomyEnabled = true;
        } else {
            getLogger().info("Can't find Vault plugin!");
            isEconomyEnabled = false;
        }

        ConfigurationSerialization.registerClass(CCRegion.class);

        messageManager = new CCMessageManager();
        mobCreator = new CCMobCreator();
        mobsManager = new CCMobsManager();
        configManager = new CCConfigManager();
        commandManager = new CCCommandManager();

        mobsDieEvent = new CCMobsDieEvent();

        getServer().getPluginManager().registerEvents(mobsDieEvent, this);

        getServer().getPluginCommand("cc").setExecutor(commandManager);

        getServer().getLogger().info("Plugin loading");

        configManager.loadAllRegions();
        configManager.loadAllSpawners();
        mobsManager.startCheckMobLocation();
    }

    @Override
    public void onDisable() {
        //configManager.saveAllRegions();
        //configManager.saveAllSpawners();
        mobsManager.killAllMobs();
        getServer().getLogger().info("Plugin unloading");
    }


    public CCMobCreator getMobCreator() {
        return mobCreator;
    }

    public CCMobsManager getMobsManager() {
        return mobsManager;
    }

    public CCConfigManager getConfigManager() {
        return configManager;
    }

    public CCMessageManager getMessageManager() {
        return messageManager;
    }

    public static boolean isEconomyEnabled() {
        return isEconomyEnabled;
    }

    public static CCMain getInstance(){
        return instance;
    }

    public static WorldEdit getWorldEdit(){
        return worldEdit;
    }

    public static Economy getEconomy(){
        return economy;
    }
}
