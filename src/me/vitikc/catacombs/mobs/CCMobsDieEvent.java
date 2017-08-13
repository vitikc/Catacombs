package me.vitikc.catacombs.mobs;

import me.vitikc.catacombs.CCConfigManager;
import me.vitikc.catacombs.CCMain;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class CCMobsDieEvent implements Listener {
    private CCConfigManager configManager = CCMain.getInstance().getConfigManager();

    @EventHandler
    public void onMobDeathEvent(EntityDeathEvent event){
        LivingEntity e = event.getEntity();
        //If mob is our than minus one from amount
        if (!CCMobsManager.getMobAmount().containsKey(e.getCustomName()))
            return;
        String name = e.getCustomName();
        EntityType type = e.getType();
        Location location = (Location) CCMobsManager.getMobAmount().get(name).keySet().toArray()[0];
        CCMobsManager.getMobList().get(name).remove(e);
        CCMain.getInstance().getMobCreator().spawnMob(name,location,type);
        event.getDrops().clear();
        if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player){
            Player p = event.getEntity().getKiller();
            if (CCMain.isEconomyEnabled()) {
                double money = configManager.getMobPrice(type);
                CCMain.getEconomy().depositPlayer(p, money);
                p.sendMessage(CCMain.getInstance().getMessageManager().getMessage("mob_kill_reward") + money);
            }
            if (isDropChance(configManager.getDropChance(type)))
                event.getDrops().addAll(configManager.getMobDrop(type));
        }

    }
    private boolean isDropChance(int chance){
        double r = Math.random();
        if (r*100 <= chance) return true;
        return false;
    }
}
