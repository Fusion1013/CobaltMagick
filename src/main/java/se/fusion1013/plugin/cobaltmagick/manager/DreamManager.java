package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;

import java.util.*;

public class DreamManager extends Manager implements Runnable, Listener {

    private static DreamManager INSTANCE = null;
    /**
     * Returns the object representing this <code>DreamManager</code>.
     *
     * @return The object of this class
     */
    public static DreamManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new DreamManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    private final Map<UUID, Integer> blindPlayersTick = new HashMap<>();
    private final Map<UUID, LivingEntity> spyglassDetectionEntity = new HashMap<>();

    public void removeDreamingPlayer(Player player){
        blindPlayersTick.remove(player.getUniqueId());
    }

    public void addDreamingPlayer(Player player){
        blindPlayersTick.put(player.getUniqueId(), 0);
        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
            player.playSound(player.getLocation(), "cobalt.eternal_halls", SoundCategory.MASTER, 1000000, 1);
        }, 60);
    }

    public DreamManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    @Override
    public void run() {
        List<UUID> uuids = new ArrayList<>(blindPlayersTick.keySet());

        for (UUID uuid : uuids){
            int tick = blindPlayersTick.get(uuid);
            Player player = Bukkit.getPlayer(uuid);

            if (player != null){
                // Spyglass detection entity
                ItemStack item = player.getInventory().getItemInMainHand();

                LivingEntity detectionEntity = spyglassDetectionEntity.get(uuid);
                if (ItemManager.DREAMGLASS.compareTo(item)) {

                    if (detectionEntity != null){

                        // Teleport spyglass detection parrot
                        Vector lookingDirection = player.getEyeLocation().getDirection();
                        Location newDetectionEntityLocation = player.getEyeLocation().clone().add(lookingDirection);
                        detectionEntity.teleport(newDetectionEntityLocation);
                        detectionEntity.setFireTicks(0);

                    } else {
                        // Summon spyglass detection parrot
                        World world = player.getWorld();

                        LivingEntity entity = (LivingEntity)world.spawnEntity(player.getLocation(), EntityType.PARROT);
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0, true, false));
                        entity.setInvulnerable(true);
                        entity.setSilent(true);
                        entity.setAI(false);
                        entity.setGravity(false);

                        spyglassDetectionEntity.put(uuid, entity);
                    }
                } else if (detectionEntity != null) {
                    LivingEntity entity = spyglassDetectionEntity.get(uuid);
                    entity.teleport(new Location(entity.getWorld(), 0, -1000, 0));
                    entity.setHealth(0);
                    spyglassDetectionEntity.remove(uuid);
                }
            }

            if (tick == 0 && player != null){
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                player.removePotionEffect(PotionEffectType.SLOW);
                player.removePotionEffect(PotionEffectType.JUMP);
            } else if (tick > 0 && player != null) {
                blindPlayersTick.put(uuid, tick-1);
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 5));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 250));
            }
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event){
        Advancement advancement = event.getAdvancement();

        if (advancement.getKey().getKey().equalsIgnoreCase("adventure/spyglass_at_parrot")) {
            Player p = event.getPlayer();

            ItemStack is = p.getInventory().getItemInMainHand();
            if (is.getType() == Material.AIR) return;

            if (ItemManager.DREAMGLASS.compareTo(is)) {

                if (blindPlayersTick.get(p.getUniqueId()) != null) {
                    blindPlayersTick.put(p.getUniqueId(), 1);
                }
            }
            AdvancementProgress pr = p.getAdvancementProgress(advancement);
            for (String s : pr.getAwardedCriteria()){
                pr.revokeCriteria(s);
            }
        }
    }

    @Override
    public void reload() {
        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CobaltMagick.getInstance(), this, 0, 1);
    }

    @Override
    public void disable() {

    }
}
