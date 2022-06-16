package se.fusion1013.plugin.cobaltmagick.item;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.*;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.create.ShinyOrb;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.util.constants.MerchantRecipeConstants;

import java.util.Objects;

import static se.fusion1013.plugin.cobaltcore.item.CustomItemManager.register;

/**
 * Holds all <code>CustomItems</code> registered by the <code>CobaltMagick</code> plugin.
 * Items are registered through <code>CobaltCore</code>.
 */
public class ItemManager extends Manager {

    // ----- CONSTRUCTORS -----

    public ItemManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- KEYS -----

    public static final CustomItem DUNGEON_KEY = register(new CustomItem.CustomItemBuilder("dungeon_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Dungeon Key")
            .setCustomModel(2)
            .build());

    public static final CustomItem RUSTY_KEY = register(new CustomItem.CustomItemBuilder("rusty_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GRAY + "Rusty Key")
            .setCustomModel(11)
            .build());

    public static final CustomItem RED_KEY = register(new CustomItem.CustomItemBuilder("red_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.RED + "Red Key")
            .setCustomModel(12)
            .build());

    public static final CustomItem GREEN_KEY = register(new CustomItem.CustomItemBuilder("green_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Green Key")
            .setCustomModel(13)
            .build());

    public static final CustomItem BLUE_KEY = register(new CustomItem.CustomItemBuilder("blue_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.BLUE + "Blue Key")
            .setCustomModel(14)
            .build());

    // ----- MISCELLANEOUS -----

    public static final CustomItem BROKEN_SPELL = register(new CustomItem.CustomItemBuilder("broken_spell", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "Broken Spell")
            .addLoreLine("A malfunctioning spell")
            .setCustomModel(61)
            .build());

    public static final CustomItem BROKEN_WAND = register(new CustomItem.CustomItemBuilder("broken_wand", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "Broken Wand")
            .addLoreLine("This wand has snapped in half")
            .addLoreLine("but it still crackles with magical energy")
            .setCustomModel(15)
            .build());

    public static final CustomItem INVISIBLE_ITEM_FRAME = register(new CustomItem.CustomItemBuilder("invisible_item_frame", Material.ITEM_FRAME, 1)
            .setCustomName(HexUtils.colorify("&rInvisible Item Frame"))
            .addItemActivator(ItemActivator.HANGING_PLACE, (item, event, slot) -> {
                HangingPlaceEvent hangingPlaceEvent = (HangingPlaceEvent) event;
                ItemFrame itemFrame = (ItemFrame) hangingPlaceEvent.getEntity();
                itemFrame.setVisible(false);
            })
            .addShapedRecipe("***", "*%*", "***",
                    new AbstractCustomItem.ShapedIngredient('%', new ItemStack(Material.ITEM_FRAME)),
                    new AbstractCustomItem.ShapedIngredient('*', new ItemStack(Material.GLASS_PANE)))
            .build());

    public static final CustomItem INVISIBLE_GLOW_ITEM_FRAME = register(new CustomItem.CustomItemBuilder("invisible_glow_item_frame", Material.GLOW_ITEM_FRAME, 1)
            .setCustomName(HexUtils.colorify("&rInvisible Glow Item Frame"))
            .addItemActivator(ItemActivator.HANGING_PLACE, (item, event, slot) -> {
                HangingPlaceEvent hangingPlaceEvent = (HangingPlaceEvent) event;
                ItemFrame itemFrame = (ItemFrame) hangingPlaceEvent.getEntity();
                itemFrame.setVisible(false);
            })
            .addShapedRecipe("***", "*%*", "***",
                    new AbstractCustomItem.ShapedIngredient('%', new ItemStack(Material.GLOW_ITEM_FRAME)),
                    new AbstractCustomItem.ShapedIngredient('*', new ItemStack(Material.GLASS_PANE)))
            .build());

    public static final CustomItem GLOWING_ITEM_FRAME = register(new CustomItem.CustomItemBuilder("glowing_item_frame", Material.ITEM_FRAME, 1)
            .setCustomName(HexUtils.colorify("&rGlowing Item Frame"))
            .addItemActivator(ItemActivator.HANGING_PLACE, (item, event, slot) -> {
                HangingPlaceEvent hangingPlaceEvent = (HangingPlaceEvent) event;
                ItemFrame itemFrame = (ItemFrame) hangingPlaceEvent.getEntity();
                itemFrame.setGlowing(true);
            })
            .addShapedRecipe("***", "*%*", "***",
                    new AbstractCustomItem.ShapedIngredient('%', new ItemStack(Material.ITEM_FRAME)),
                    new AbstractCustomItem.ShapedIngredient('*', new ItemStack(Material.GLOWSTONE_DUST)))
            .build());

    public static final CustomItem GLOWING_GLOW_ITEM_FRAME = register(new CustomItem.CustomItemBuilder("glowing_glow_item_frame", Material.GLOW_ITEM_FRAME, 1)
            .setCustomName(HexUtils.colorify("&rGlowing Glow Item Frame"))
            .addItemActivator(ItemActivator.HANGING_PLACE, (item, event, slot) -> {
                HangingPlaceEvent hangingPlaceEvent = (HangingPlaceEvent) event;
                ItemFrame itemFrame = (ItemFrame) hangingPlaceEvent.getEntity();
                itemFrame.setGlowing(true);
            })
            .addShapedRecipe("***", "*%*", "***",
                    new AbstractCustomItem.ShapedIngredient('%', new ItemStack(Material.GLOW_ITEM_FRAME)),
                    new AbstractCustomItem.ShapedIngredient('*', new ItemStack(Material.GLOWSTONE_DUST)))
            .build());

    public static final CustomItem SHINY_ORB = ShinyOrb.create();

    // ----- COINS -----

    public static final CustomItem IRON_COIN = register(new CustomItem.CustomItemBuilder("iron_coin", Material.IRON_NUGGET, 1)
            .setCustomName(HexUtils.colorify("&f&lIron Coin"))
            .addLoreLine(HexUtils.colorify("&r&7It glimmers"))
            .setCustomModel(1)
            .build());

    public static final CustomItem GOLD_COIN = register(new CustomItem.CustomItemBuilder("gold_coin", Material.GOLD_NUGGET, 1)
            .setCustomName(HexUtils.colorify("&6&lGold Coin"))
            .addLoreLine(HexUtils.colorify("&r&eIt glimmers"))
            .setCustomModel(1)
            .build());

    // ----- MATERIALS -----

    public static final CustomItem ECHO_INGOT = register(new CustomItem.CustomItemBuilder("echo_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&rEcho Ingot")).setCustomModel(40).build());

    public static final CustomItem CRYSTAL_LENS = register(new CustomItem.CustomItemBuilder("crystal_lens", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&5Crystal Lens")).addLoreLine(HexUtils.colorify("&dLight shifts and distorts")).setCustomModel(4).build());

    public static final CustomItem DRAGONSTONE = register(new CustomItem.CustomItemBuilder("dragonstone", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&dDragonstone")).setCustomModel(5).build());

    public static final CustomItem MANA_DIAMOND = register(new CustomItem.CustomItemBuilder("mana_diamond", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Diamond")).setCustomModel(6).build());

    public static final CustomItem MANA_PEARL = register(new CustomItem.CustomItemBuilder("mana_pearl", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Pearl")).setCustomModel(7).build());

    public static final CustomItem MANA_POWDER = register(new CustomItem.CustomItemBuilder("mana_powder", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Powder")).setCustomModel(8).build());

    public static final CustomItem RAINBOW_ROD = register(new CustomItem.CustomItemBuilder("rainbow_rod", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<r:.5:1>Rainbow Rod")).setCustomModel(9).build());

    public static final CustomItem AQUAMARINE = register(new CustomItem.CustomItemBuilder("aquamarine", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fAquamarine")).setCustomModel(10).build());

    public static final CustomItem CRYSTAL_SHARDS = register(new CustomItem.CustomItemBuilder("crystal_shards", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&dCrystal Shards")).setCustomModel(42).build());

    // ----- RUNES -----

    public static final CustomItem RUNE_OF_AIR = register(new CustomItem.CustomItemBuilder("rune_of_air", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.AQUA + "Rune of Air").setCustomModel(1001).addTag("rune").build());

    public static final CustomItem RUNE_OF_AUTUMN = register(new CustomItem.CustomItemBuilder("rune_of_autumn", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Rune of Autumn").setCustomModel(1002).addTag("rune").build());

    public static final CustomItem RUNE_OF_EARTH = register(new CustomItem.CustomItemBuilder("rune_of_earth", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_GREEN + "Rune of Earth").setCustomModel(1003).addTag("rune").build());

    public static final CustomItem RUNE_OF_ENVY = register(new CustomItem.CustomItemBuilder("rune_of_envy", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "Rune of Envy").setCustomModel(1004).addTag("rune").build());

    public static final CustomItem RUNE_OF_FIRE = register(new CustomItem.CustomItemBuilder("rune_of_fire", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_RED + "Rune of Fire").setCustomModel(1005).addTag("rune").build());

    public static final CustomItem RUNE_OF_GLUTTONY = register(new CustomItem.CustomItemBuilder("rune_of_gluttony", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.RED + "Rune of Gluttony").setCustomModel(1006).addTag("rune").build());

    public static final CustomItem RUNE_OF_GREED = register(new CustomItem.CustomItemBuilder("rune_of_greed", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Rune of Greed").setCustomModel(1007).addTag("rune").build());

    public static final CustomItem RUNE_OF_LUST = register(new CustomItem.CustomItemBuilder("rune_of_lust", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "Rune of Lust").setCustomModel(1008).addTag("rune").build());

    public static final CustomItem RUNE_OF_MANA = register(new CustomItem.CustomItemBuilder("rune_of_mana", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_AQUA + "Rune of Mana").setCustomModel(1009).addTag("rune").build());

    public static final CustomItem RUNE_OF_PRIDE = register(new CustomItem.CustomItemBuilder("rune_of_pride", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Rune of Pride").setCustomModel(1010).addTag("rune").build());

    public static final CustomItem RUNE_OF_SLOTH = register(new CustomItem.CustomItemBuilder("rune_of_sloth", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.BLUE + "Rune of Sloth").setCustomModel(1011).addTag("rune").build());

    public static final CustomItem RUNE_OF_SPRING = register(new CustomItem.CustomItemBuilder("rune_of_spring", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_GREEN + "Rune of Spring").setCustomModel(1012).addTag("rune").build());

    public static final CustomItem RUNE_OF_SUMMER = register(new CustomItem.CustomItemBuilder("rune_of_summer", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.YELLOW + "Rune of Summer").setCustomModel(1013).addTag("rune").build());

    public static final CustomItem RUNE_OF_WATER = register(new CustomItem.CustomItemBuilder("rune_of_water", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_BLUE + "Rune of Water").setCustomModel(1014).addTag("rune").build());

    public static final CustomItem RUNE_OF_WINTER = register(new CustomItem.CustomItemBuilder("rune_of_winter", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.WHITE + "Rune of Winter").setCustomModel(1015).addTag("rune").build());

    public static final CustomItem RUNE_OF_WRATH = register(new CustomItem.CustomItemBuilder("rune_of_wrath", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.RED + "Rune of Wrath").setCustomModel(1016).addTag("rune").build());

    // ----- HATS -----

    public static final CustomItem HAT_YODA = register(new CustomItem.CustomItemBuilder("hat_yoda", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rYoda")).setCustomModel(-107).build());

    public static final CustomItem HAT_MONOCLE = register(new CustomItem.CustomItemBuilder("hat_monocle", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rMonocle")).setCustomModel(-97).build());

    public static final CustomItem HAT_DEER_HEAD = register(new CustomItem.CustomItemBuilder("hat_deer_head", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rDeer Head")).setCustomModel(-93).build());

    public static final CustomItem HAT_CIGAR = register(new CustomItem.CustomItemBuilder("hat_cigar", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rCigar")).setCustomModel(-92).build());

    public static final CustomItem HAT_TINY_HAT = register(new CustomItem.CustomItemBuilder("hat_tiny_hat", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rTiny Hat")).setCustomModel(-91).build());

    public static final CustomItem HAT_SPONGEBOB = register(new CustomItem.CustomItemBuilder("hat_spongebob", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rSpongebob")).setCustomModel(-88).build());

    public static final CustomItem HAT_BIRD_MASTER = register(new CustomItem.CustomItemBuilder("hat_bird_master", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rBird Master")).setCustomModel(-4).build());

    public static final CustomItem VILLAGER_CONVERTER_HAT = register(new CustomItem.CustomItemBuilder("villager_converter_hat", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&rVillager Converter"))
            .addLoreLine(HexUtils.colorify("&r&7Type: &2Hat"))
            .setCustomModel(43)
            .addItemActivator(ItemActivator.PLAYER_CLICK_AT_ENTITY, (item, event, slot) -> {
                PlayerInteractAtEntityEvent interactAtEntityEvent = (PlayerInteractAtEntityEvent) event;

                if (interactAtEntityEvent.getRightClicked() instanceof Villager villager) {

                    // If villager does not have a profession, set the new profession
                    if (villager.getProfession() == Villager.Profession.NONE) {

                        CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> {
                            // Set the new profession
                            villager.setProfession(Villager.Profession.MASON);
                            villager.setRecipes(MerchantRecipeConstants.createHatRecipes()); // TODO: Select random recipes with weights
                            villager.setVillagerLevel(5);

                            // Set equipment & play effects
                            villager.getEquipment().setItem(EquipmentSlot.HEAD, HAT_BIRD_MASTER.getItemStack());
                            World world = villager.getWorld();
                            Location location = villager.getLocation().clone().add(new Vector(.5, 1, .5));

                            world.playSound(location, "cobalt.poof", SoundCategory.NEUTRAL, 1, 1);
                            world.spawnParticle(Particle.DUST_COLOR_TRANSITION, location, 30, .5, 1, .5, 0, new Particle.DustTransition(Color.WHITE, Color.GRAY, 1));

                            PlayerUtil.reduceHeldItemStack(interactAtEntityEvent.getPlayer(), 1);
                        });
                    }
                }
            })
            .addShapedRecipe("-*-", "*%*", "-*-",
                    new AbstractCustomItem.ShapedIngredient('%', MANA_PEARL.getItemStack()),
                    new AbstractCustomItem.ShapedIngredient('*', CRYSTAL_SHARDS.getItemStack()),
                    new AbstractCustomItem.ShapedIngredient('-', AQUAMARINE.getItemStack()))
            .build());

    // ----- TOOLS -----

    public static final CustomItem ECHO_TOTEM = register(new CustomItem.CustomItemBuilder("echo_totem", Material.ENCHANTED_BOOK, 1)
            .setCustomName(HexUtils.colorify("&eEcho Totem"))
            .addLoreLine(HexUtils.colorify("&o&7Teleports you to your spawnpoint, though it comes at a cost..."))
            .addItemActivator(ItemActivator.PLAYER_RIGHT_CLICK, (item, event, slot) -> {
                PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
                if (!item.compareTo(interactEvent.getPlayer().getInventory().getItemInMainHand())) return;

                Player player = interactEvent.getPlayer();
                Location spawnLocation = player.getBedSpawnLocation();
                player.playEffect(EntityEffect.TELEPORT_ENDER);
                PlayerUtil.reduceHeldItemStack(player, 1);
                CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltCore.getInstance(), () -> {
                    PlayerUtil.dropPercentageOfInventory(player, .5);
                    player.teleport(Objects.requireNonNullElseGet(spawnLocation, () -> player.getWorld().getSpawnLocation()));
                    player.playEffect(EntityEffect.TOTEM_RESURRECT);
                });
            })
            .setCustomModel(1001)
            .build());

    // Item Used to Reactivate Sculk Shrieker
    public static final CustomItem SHRIEKER_CATALYST = register(new CustomItem.CustomItemBuilder("shrieker_catalyst", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&eShrieker Catalyst"))
            .addShapedRecipe("-*-", "*%*", "-*-", new AbstractCustomItem.ShapedIngredient('*', ECHO_INGOT.getItemStack()), new AbstractCustomItem.ShapedIngredient('%', Material.SCULK_CATALYST))
            .addItemActivator(ItemActivator.PLAYER_RIGHT_CLICK_BLOCK, (item, event, slot) -> {
                PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;

                ICustomItem interactedCustom = CustomItemManager.getCustomItem(interactEvent.getItem());
                if (interactedCustom == null) return;
                if (!interactedCustom.compareTo(item.getItemStack())) return;

                Block block = interactEvent.getClickedBlock();
                if (block == null) return;
                if (block.getType() == Material.SCULK_SHRIEKER) {
                    CobaltCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltCore.getInstance(), () -> {
                        // Create the block data
                        BlockData data = CobaltCore.getInstance().getServer().createBlockData("minecraft:sculk_shrieker[can_summon=true,shrieking=false,waterlogged=false]");
                        if (block.getBlockData().equals(data)) return;

                        // Set the block data
                        block.setBlockData(data);
                        PlayerUtil.reduceHeldItemStack(interactEvent.getPlayer(), 1); // TODO: The item is potentially in the offhand

                        // Display particles and play sound
                        block.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, block.getLocation().clone().add(new Vector(.5, .5, .5)), 20, .3, .3, .3, 0, null);
                        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_PLACE, SoundCategory.BLOCKS, 1, 1);
                        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_WARDEN_NEARBY_CLOSEST, SoundCategory.BLOCKS, 1, 1);
                    });
                }
            })
            .setCustomModel(2001)
            .build());

    // Battleaxe
    public static final CustomItem BATTLEAXE = register(new CustomItem.CustomItemBuilder("battle_axe", Material.NETHERITE_AXE, 1)
            .setCustomName(ChatColor.RESET + "Battle Axe")
            .addShapelessRecipe(new AbstractCustomItem.ShapelessIngredient(1, Material.NETHERITE_AXE), new AbstractCustomItem.ShapelessIngredient(1, Material.NETHERITE_AXE))
            .addItemActivator(ItemActivator.PLAYER_KILL_PLAYER, (item, event, slot) -> {
                EntityDamageByEntityEvent killEvent = (EntityDamageByEntityEvent) event;
                ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();

                // Set item meta
                skullMeta.setOwningPlayer((Player) killEvent.getEntity());
                skullMeta.setDisplayName(killEvent.getEntity().getName()); // TODO: Replace with component

                skullItem.setItemMeta(skullMeta);
            })
            .setCustomModel(5)
            .build());

    public static final CustomItem POISON_BLADE = register(new CustomItem.CustomItemBuilder("poison_blade", Material.NETHERITE_SWORD, 1)
            .setCustomName(HexUtils.colorify("&dPoison Blade"))
            .addItemActivator(ItemActivator.PLAYER_HIT_ENTITY, (item, event, slot) -> {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                if (damageEvent.getDamager() instanceof Player player && damageEvent.getEntity() instanceof LivingEntity living) {
                    if (item.compareTo(player.getInventory().getItemInMainHand())) {
                        CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> {
                            living.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*4, 0));
                        });
                    }
                }
            })
            .setCustomModel(3)
            .build());

    public static final CustomItem CRYSTAL_KEY = register(new CustomItem.CustomItemBuilder("crystal_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .addLoreLine(ChatColor.WHITE + "The key is voiceless")
            .setCustomModel(3)
            .build());

    public static final CustomItem CRYSTAL_KEY_LIGHT_ACTIVE = register(new CustomItem.CustomItemBuilder("crystal_key_light_active", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .addLoreLine(ChatColor.RESET + "" + ChatColor.WHITE + "The key is ready")
            .setItemMetaEditor((meta -> {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                return meta;
            }))
            .setCustomModel(3)
            .build());

    public static final CustomItem CRYSTAL_KEY_DARK_ACTIVE = register(new CustomItem.CustomItemBuilder("crystal_key_dark_active", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .addLoreLine(ChatColor.RESET + "" + ChatColor.WHITE + "The key whispers secrets and promises; it is ready")
            .setItemMetaEditor((meta -> {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                return meta;
            }))
            .setCustomModel(3)
            .build());

    public static final CustomItem DUNGEON_COIN = register(new CustomItem.CustomItemBuilder("dungeon_coin", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Dungeon Coin")
            .setCustomModel(1)
            .build());

    public static final CustomItem DREAMGLASS = register(new CustomItem.CustomItemBuilder("dreamglass", Material.SPYGLASS, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_AQUA + "Dreamglass")
            .addLoreLine(ChatColor.WHITE + "The lens distorts reality")
            .setCustomModel(1)
            .build());

    // ----- CUSTOM RECIPES -----

    public static final ShapedRecipe BUNDLE_RECIPE = CustomItemManager.addShapedRecipe(new ItemStack(Material.BUNDLE),
            "%*%","*-*","***",
            new AbstractCustomItem.ShapedIngredient('%', Material.STRING),
            new AbstractCustomItem.ShapedIngredient('*', Material.RABBIT_HIDE));

    public static final ShapedRecipe SPARKBOLT_RECIPE = CustomItemManager.addShapedRecipe(SpellManager.SPARK_BOLT.getSpellItem(),
            "-*-", "*%*", "-*-",
            new AbstractCustomItem.ShapedIngredient('%', MANA_PEARL.getItemStack()),
            new AbstractCustomItem.ShapedIngredient('*', Material.AMETHYST_SHARD),
            new AbstractCustomItem.ShapedIngredient('-', AQUAMARINE.getItemStack()));

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static ItemManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CustomItemManager</code>.
     *
     * @return The object of this class
     */
    public static ItemManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ItemManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
