package se.fusion1013.plugin.cobaltmagick.item.enchantments;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.enchantment.CobaltEnchantment;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentManager;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentWrapper;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.event.SpellCastEvent;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;

import java.sql.PreparedStatement;
import java.util.*;

public class MagickEnchantmentManager extends Manager implements Listener, Runnable {

    // ----- REGISTER -----

    private static Class<MagickEnchantment> MAGICK_ENCHANTMENTS = EnchantmentManager.registerEnchantment(MagickEnchantment.class);

    // ----- EVENTS -----

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
        if (executeEvasion(event.getEntity())) event.setDamage(0);
    }

    @EventHandler
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity hitEntity && event.getDamager() instanceof LivingEntity damager) {
            executeFrostbite(hitEntity, damager);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();
        if (damager instanceof LivingEntity livingDamager) {
            EntityEquipment eq = livingDamager.getEquipment();
            if (eq == null) return;
            ItemStack heldItem = eq.getItemInMainHand();

            // -- Check enchants that trigger on hit

            if (damaged instanceof LivingEntity livingDamaged) {

                // -- Check enchants that trigger on entity death
                if (livingDamaged.getHealth() - event.getFinalDamage() <= 0) { // TODO: Check totem

                    // Adrenaline
                    executeAdrenaline(livingDamager);

                }
            }
        }
    }

    @EventHandler
    public void onSpelLCast(SpellCastEvent event) {
        LivingEntity caster = event.getSpell().getCaster();
        if (caster == null) return;

        executeCurseOfNeutralizedSpells(caster, event);
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        executeAutosmelt(event);
    }

    @EventHandler
    public void playerItemHeldEvent(PlayerItemHeldEvent event) {
        executeSlipperinessCurse(event.getPlayer(), event.getNewSlot());
    }

    // ----- ENCHANTMENT HANDLING -----

    private static void executeSlipperinessCurse(Player player, int slot) {
        ItemStack heldItem = player.getInventory().getItem(slot);
        if (heldItem == null) return;
        int slipperinessLevel = MagickEnchantment.CURSE_OF_SLIPPERINESS.getLevel(heldItem);
        Random r = new Random();
        if (slipperinessLevel > 0 && r.nextDouble() < .05) { // 5% risk of dropping item
            player.getInventory().setItem(slot, new ItemStack(Material.AIR));

            // Drop item
            Item item = player.getWorld().dropItem(player.getEyeLocation(), heldItem);
            item.setVelocity(player.getEyeLocation().getDirection().clone().multiply(.25));
            player.playSound(player.getLocation(), "cobalt.poof", SoundCategory.PLAYERS, 1, 1);
        }
    }

    private static void executeAutosmelt(BlockBreakEvent event) {
        int autosmeltLevel = EnchantmentManager.getTotalEnchantLevel(MagickEnchantment.AUTOSMELT, event.getPlayer());
        if (autosmeltLevel > 0) {
            // Util variables
            Location location = event.getBlock().getLocation().toCenterLocation();

            // Get items that would be dropped by the block
            Collection<ItemStack> items = event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer());
            event.setDropItems(false);

            // Convert drop items to smelted item drops
            List<ItemStack> itemsToDrop = new ArrayList<>();
            for (ItemStack item : items) {

                Iterator<Recipe> recipes = Bukkit.recipeIterator();
                boolean recipeFound = false;
                while (recipes.hasNext()) {
                    if (recipes.next() instanceof FurnaceRecipe furnaceRecipe) {

                        if (furnaceRecipe.getInput().getType() == item.getType()) {
                            ItemStack newItem = furnaceRecipe.getResult();
                            newItem.setAmount(item.getAmount());
                            itemsToDrop.add(newItem);
                            recipeFound = true;
                            break;
                        }
                    }
                }

                if (!recipeFound) itemsToDrop.add(item);
            }

            // Drop items
            for (ItemStack item : itemsToDrop) {
                location.getWorld().dropItem(location, item);
            }

            // Particles
            location.getWorld().spawnParticle(Particle.FLAME, location, 5, .3, .3, .3, .05);
            location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, .1f, 1);
        }
    }

    private static void executeCurseOfNeutralizedSpells(LivingEntity entity, SpellCastEvent event) {
        int curseLevel = EnchantmentManager.getTotalEnchantLevel(MagickEnchantment.CURSE_OF_NEUTRALIZED_SPELLS, entity);
        if (curseLevel > 0) {
            event.setCancelled(true);

            // TODO: Effects
        }
    }

    private static final Map<UUID, Float> adrenalineBonuses = new HashMap<>();
    private static final Map<UUID, Integer> adrenalineLevels = new HashMap<>();
    private static final Map<UUID, Double> moveSpeedBase = new HashMap<>();

    private static void tickAdrenaline() {
        List<UUID> toRemove = new ArrayList<>();
        for (UUID uuid : adrenalineBonuses.keySet()) {
            float currentValue = Math.max(adrenalineBonuses.get(uuid) - 0.05f, 0);
            adrenalineBonuses.put(uuid, currentValue);
            if (currentValue <= 0) toRemove.add(uuid);

            // Update adrenaline bonuses
            LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
            if (entity == null) continue;
            int level = adrenalineLevels.get(uuid);

            AttributeInstance instance = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (instance == null) continue;

            double baseSpeed = moveSpeedBase.computeIfAbsent(uuid, k -> instance.getBaseValue());
            double speedPercent = 1 + ((level * 0.2) * (currentValue / 10));
            instance.setBaseValue(baseSpeed * speedPercent);

            // Particles
            entity.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, entity.getLocation().toCenterLocation(), 2, .1, .01, .1, new Particle.DustTransition(Color.AQUA, Color.SILVER, 1));
        }

        for (UUID uuid : toRemove) adrenalineBonuses.remove(uuid);
    }

    private static void executeAdrenaline(LivingEntity entity) {

        int adrenalineLevel = EnchantmentManager.getTotalEnchantLevel(MagickEnchantment.ADRENALINE, entity);
        if (adrenalineLevel > 0) {
            adrenalineBonuses.put(entity.getUniqueId(), 10f);
            adrenalineLevels.put(entity.getUniqueId(), adrenalineLevel);

            // Effects
            if (entity instanceof Player player) player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, SoundCategory.PLAYERS, 1, 1);
        }

    }

    private static final Map<Player, Float> regenCooldowns = new HashMap<>();

    private static void executeRegeneration(Player player) {
        float currentCooldown = regenCooldowns.computeIfAbsent(player, k -> 0F);
        int regenLevel = EnchantmentManager.getTotalEnchantLevel(MagickEnchantment.REGENERATION, player);
        if (regenLevel > 0) {
            float cooldownThreshold = 4 - ((regenLevel-1)/5f);
            if (currentCooldown > cooldownThreshold) {
                regenCooldowns.put(player, 0f);
                currentCooldown = 0;
                double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                if (maxHealth > player.getHealth() + 1) {
                    player.setHealth(player.getHealth() + 1);

                    // Play effects
                    World world = player.getWorld();
                    Location location = player.getLocation();

                    world.spawnParticle(Particle.HEART, location, 2, .2, .1, .2, 0);
                    player.playSound(location, Sound.ENTITY_WITCH_DRINK, SoundCategory.PLAYERS, 1, 1);
                }
            }
            regenCooldowns.put(player, currentCooldown+0.05f);
        }
    }

    private static void executeFrostbite(LivingEntity hitEntity, LivingEntity damager) {
        if (damager.getEquipment() == null) return;
        if (hitEntity.getFreezeTicks() > 0) return;
        int frostbiteLevel = MagickEnchantment.FROSTBITE.getLevel(damager.getEquipment().getItemInMainHand());

        if (frostbiteLevel > 0) {
            int freezeTicks = 20 * frostbiteLevel * 3;
            hitEntity.setFreezeTicks(freezeTicks);

            // Freeze effects
            hitEntity.getWorld().spawnParticle(Particle.SNOWFLAKE, hitEntity.getLocation().clone().add(0, .5, 0), 20, .3, .3, .3, .01);
            if (hitEntity instanceof Player player) {
                player.playSound(hitEntity.getLocation(), Sound.BLOCK_SNOW_BREAK, SoundCategory.PLAYERS, 1, 1);
            }
        }
    }

    private static boolean executeEvasion(Entity entity) {

        int evasionLevel = EnchantmentManager.getTotalEnchantLevel(MagickEnchantment.EVASION, entity);

        if (evasionLevel > 0) {
            Random r = new Random();
            if (r.nextDouble() <= (evasionLevel * 4 / 100.0)) {

                // Play dodge effects
                entity.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, entity.getLocation().clone().add(0, .3, 0), 3, .1, .2, .2, .1);
                if (entity instanceof Player player) {
                    player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 1, 1);
                }

                return true;
            }
        }

        return false;
    }

    private static void executeAgility(LivingEntity livingEntity) {
        int agilityLevel = EnchantmentManager.getTotalEnchantLevel(MagickEnchantment.AGILITY, livingEntity);

        if (agilityLevel > 0) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, agilityLevel-1, true, false));
        }
    }

    private static final Map<Player, Double> actualMaxHealth = new HashMap<>();
    private static final Map<Player, Integer> previousWeakeningLevel = new HashMap<>();

    private static void executeWeakeningCurse(Player player) {
        int weakeningLevel = EnchantmentManager.getTotalEnchantLevel(MagickEnchantment.CURSE_OF_WEAKENING, player);
        int previousLevel = previousWeakeningLevel.computeIfAbsent(player, k -> weakeningLevel);

        if (weakeningLevel > 0) {
            // Set new max health
            double healthModification = Math.pow(0.5, weakeningLevel);
            AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (instance == null) return;
            double newMax = actualMaxHealth.get(player) * healthModification;
            instance.setBaseValue(newMax);

            // Set health to max health if above max health
            if (player.getHealth() > newMax) player.setHealth(newMax);
        } else {
            AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (instance == null) return;
            instance.setBaseValue(actualMaxHealth.get(player));
        }

        // If level changed, play effects
        if (previousLevel != weakeningLevel) {
            player.playSound(player.getLocation(), Sound.ENTITY_WITCH_DRINK, SoundCategory.PLAYERS, 1, 1);
            player.spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation(), 5, .3, .3, .3, 0);
        }

        previousWeakeningLevel.put(player, weakeningLevel);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AttributeInstance instance = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance == null) return;
        actualMaxHealth.put(event.getPlayer(), instance.getBaseValue());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        AttributeInstance instance = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance == null) return;
        instance.setBaseValue(actualMaxHealth.get(event.getPlayer()));
    }

    // ----- TICKING EVENTS -----

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            executeAgility(p);
            executeRegeneration(p);
            executeWeakeningCurse(p);
        }

        tickAdrenaline();
    }

    // ----- CONSTRUCTORS -----

    public MagickEnchantmentManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), this, 0, 1);
    }

    @Override
    public void disable() {
    }
}
