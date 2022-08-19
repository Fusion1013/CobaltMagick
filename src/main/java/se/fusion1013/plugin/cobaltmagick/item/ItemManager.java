package se.fusion1013.plugin.cobaltmagick.item;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
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
import se.fusion1013.plugin.cobaltmagick.item.brewing.BrewingRecipe;
import se.fusion1013.plugin.cobaltmagick.item.create.ShinyOrb;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.util.constants.MerchantRecipeConstants;

import java.util.Objects;

import static se.fusion1013.plugin.cobaltcore.item.CustomItemManager.register;

/**
 * Holds all <code>CustomItems</code> registered by the <code>CobaltMagick</code> plugin.
 * Items are registered through <code>CobaltCore</code>.
 */
public class ItemManager extends Manager implements Listener {

    // ----- CONSTRUCTORS -----

    public ItemManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

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
    // ----- CAULDRON THINGS -----

    public static final CustomItem OUR_MATTER = register(new CustomItem.CustomItemBuilder("our_matter", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#969696:#1a1a1a>Our Matter")).setCustomModel(1001).setItemCategory(MagickItemCategory.MATERIAL).build());

    public static final CustomItem EVIL_EYE = register(new CustomItem.CustomItemBuilder("evil_eye", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#172373:#2442a3>Evil Eye")).setCustomModel(1012).setItemCategory(MagickItemCategory.MATERIAL).build());

    public static final CustomItem DEATH_BOUND_AMULET_DEACTIVATED = register(new CustomItem.CustomItemBuilder("death_bound_amulet_deactivated", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#5e0000:#b80000>Death-Bound Amulet"))
            .addLoreLine(HexUtils.colorify("&7&oIt seems inactive for now...")).setCustomModel(1008).setItemCategory(MagickItemCategory.MATERIAL).build());

    public static final CustomItem DEATH_BOUND_AMULET = register(new CustomItem.CustomItemBuilder("death_bound_amulet", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#5e0000:#b80000>Death-Bound Amulet")).setCustomModel(1007).setItemCategory(MagickItemCategory.MATERIAL)
            .addLoreLine(HexUtils.colorify("&7Bound Player: "))
            .addItemActivatorSync(ItemActivator.PLAYER_RIGHT_CLICK, (((iCustomItem, event, equipmentSlot) -> {

                PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
                Player player = interactEvent.getPlayer();
                ItemStack item = player.getInventory().getItem(equipmentSlot);
                ItemMeta itemMeta = item.getItemMeta();

                // Set persistent data
                itemMeta.getPersistentDataContainer().set(ItemConstants.DEATH_BOUND_ORB_OWNER, PersistentDataType.STRING, player.getUniqueId().toString());

                // Set new lore
                List<String> lore = new ArrayList<>();
                lore.add(HexUtils.colorify("&7Bound Player: " + player.getName()));
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);

                // Play effects
                player.playSound(player.getLocation(), "cobalt.brain", 1000000, 1);

            })))
            .addShapelessRecipe(new AbstractCustomItem.ShapelessIngredient(1, DEATH_BOUND_AMULET_DEACTIVATED.getItemStack()),
                                new AbstractCustomItem.ShapelessIngredient(1, EVIL_EYE.getItemStack()))
            .build());

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

    public static final CustomItem DUNGEON_LOCATOR = register(new CustomItem.CustomItemBuilder("dungeon_locator", Material.COMPASS, 1)
            .setCustomName(HexUtils.colorify("&fDungeon Locator"))
            .addItemActivatorSync(ItemActivator.PLAYER_RIGHT_CLICK, ((iCustomItem, event, equipmentSlot) -> {
                PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;

                if (interactEvent.getItem().getItemMeta() instanceof CompassMeta compassMeta) {

                    // Set dungeon location
                    compassMeta.setLodestoneTracked(false);
                    compassMeta.setLodestone(new Location(interactEvent.getPlayer().getWorld(), MagickStructureManager.highAlchemistDungeonLocation.getX(), MagickStructureManager.highAlchemistDungeonLocation.getY(), MagickStructureManager.highAlchemistDungeonLocation.getZ()));

                    // Add enchant
                    compassMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                    compassMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    interactEvent.getItem().setItemMeta(compassMeta);

                    CobaltMagick.getInstance().getLogger().info("Set compass location to: " + MagickStructureManager.highAlchemistDungeonLocation + ", in world: " + MagickStructureManager.highAlchemistWorld.getName());
                }
            }))
            .addShapedRecipe(
                    "-e-",
                    "ece",
                    "-e-",
                    new AbstractCustomItem.ShapedIngredient('c', Material.RECOVERY_COMPASS),
                    new AbstractCustomItem.ShapedIngredient('e', Material.ECHO_SHARD)
            )
            .build());

    public static final CustomItem EXPERIENCE_ORB = CreateExperienceOrb.createExperienceOrb();

    // Battleaxe
    public static final CustomItem BATTLEAXE = register(new CustomItem.CustomItemBuilder("battle_axe", Material.NETHERITE_AXE, 1)
            .setCustomName(ChatColor.RESET + "Battle Axe")
            .addShapelessRecipe(new AbstractCustomItem.ShapelessIngredient(1, Material.NETHERITE_AXE), new AbstractCustomItem.ShapelessIngredient(1, Material.NETHERITE_AXE))
            .addItemActivatorSync(ItemActivator.PLAYER_KILL_PLAYER, (item, event, slot) -> {
                EntityDamageByEntityEvent killEvent = (EntityDamageByEntityEvent) event;
                ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();

                // Set item meta
                skullMeta.setOwningPlayer((Player) killEvent.getEntity());
                skullMeta.displayName(Component.text(killEvent.getEntity().getName()).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));

                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Killed by: " + killEvent.getDamager().getName()).color(NamedTextColor.DARK_PURPLE));
                skullMeta.lore(lore);

                skullItem.setItemMeta(skullMeta);
                killEvent.getEntity().getWorld().dropItemNaturally(killEvent.getEntity().getLocation(), skullItem);
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
            .setItemMetaEditor(itemMeta -> {
                List<Component> currentLore = itemMeta.lore();
                if (currentLore != null) {
                    currentLore.addAll(
                            List.of(new Component[]{
                                    Component.text("----------").color(NamedTextColor.BLACK),
                                    Component.text("[")
                                            .color(NamedTextColor.GRAY)
                                            .append(Component.keybind("key.sneak")
                                                    .color(NamedTextColor.GOLD))
                                            .append(Component.text("]: Focus")
                                                    .color(NamedTextColor.GRAY))
                                            .decoration(TextDecoration.ITALIC, false),
                                    Component.text("[")
                                            .color(NamedTextColor.GRAY)
                                            .append(Component.keybind("key.use")
                                                    .color(NamedTextColor.GOLD))
                                            .append(Component.text("]: Interact")
                                                    .color(NamedTextColor.GRAY))
                                            .decoration(TextDecoration.ITALIC, false)
                            })
                    );
                    itemMeta.lore(currentLore);
                }
                return itemMeta;
            })
            .setCustomModel(1).setItemCategory(MagickItemCategory.TOOL)
            .addTag("dream_item")
            .build());

    // ----- BOTTLES / GLASSES / FLASKS -----

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        ICustomItem[] items = CustomItemManager.getPlayerHeldCustomItem(event.getPlayer());

        for (ICustomItem item : items) {
            if (item != null) {
                if (item.compareTo(EMPTY_MARTINI_GLASS.getItemStack())) event.setItemStack(MARTINI_GLASS.getItemStack());
                if (item.compareTo(EMPTY_DIAMOND_BOTTLE.getItemStack())) event.setItemStack(DIAMOND_BOTTLE.getItemStack());
                if (item.compareTo(EMPTY_JAR.getItemStack())) event.setItemStack(JAR.getItemStack());
                if (item.compareTo(EMPTY_ROCKS_GLASS_2.getItemStack())) event.setItemStack(ROCKS_GLASS_2.getItemStack());
                if (item.compareTo(EMPTY_ROCKS_GLASS.getItemStack())) event.setItemStack(ROCKS_GLASS.getItemStack());
                if (item.compareTo(EMPTY_PERFUME_BOTTLE.getItemStack())) event.setItemStack(PERFUME_BOTTLE.getItemStack());
                if (item.compareTo(EMPTY_WINE_GLASS.getItemStack())) event.setItemStack(WINE_GLASS.getItemStack());
                if (item.compareTo(EMPTY_ROUND_BOTTLE.getItemStack())) event.setItemStack(ROUND_BOTTLE.getItemStack());
                if (item.compareTo(EMPTY_SHOT_GLASS.getItemStack())) event.setItemStack(SHOT_GLASS.getItemStack());
                if (item.compareTo(EMPTY_FLASK.getItemStack())) event.setItemStack(FLASK.getItemStack());
                if (item.compareTo(EMPTY_TRIANGLE_BOTTLE.getItemStack())) event.setItemStack(TRIANGLE_BOTTLE.getItemStack());
                if (item.compareTo(EMPTY_CHAMPAGNE_GLASS.getItemStack())) event.setItemStack(CHAMPAGNE_GLASS.getItemStack());
                if (item.compareTo(EMPTY_BOTTLE.getItemStack())) event.setItemStack(BOTTLE.getItemStack());
                if (item.compareTo(EMPTY_LABELED_BOTTLE.getItemStack())) event.setItemStack(LABELED_BOTTLE.getItemStack());
                if (item.compareTo(EMPTY_SKULL_BOTTLE.getItemStack())) event.setItemStack(SKULL_BOTTLE.getItemStack());
            }
        }
    }

    public static final ICustomItem EMPTY_MARTINI_GLASS = register(new CustomItem.CustomItemBuilder("empty_martini_glass", Material.BUCKET, 4)
            .setCustomName(HexUtils.colorify("&fEmpty Martini Glass")).setCustomModel(7).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem MARTINI_GLASS = register(new CustomItem.CustomItemBuilder("martini_glass", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fMartini Glass")).setCustomModel(7).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_MARTINI_GLASS.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_DIAMOND_BOTTLE = register(new CustomItem.CustomItemBuilder("empty_diamond_bottle", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Diamond Bottle")).setCustomModel(3).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem DIAMOND_BOTTLE = register(new CustomItem.CustomItemBuilder("diamond_bottle", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fDiamond Bottle")).setCustomModel(17).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_DIAMOND_BOTTLE.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_JAR = register(new CustomItem.CustomItemBuilder("empty_jar", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Jar")).setCustomModel(5).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem JAR = register(new CustomItem.CustomItemBuilder("jar", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fJar")).setCustomModel(5).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_JAR.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_ROCKS_GLASS_2 = register(new CustomItem.CustomItemBuilder("empty_rocks_glass_2", Material.BUCKET, 4)
            .setCustomName(HexUtils.colorify("&fEmpty Rocks Glass")).setCustomModel(10).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem ROCKS_GLASS_2 = register(new CustomItem.CustomItemBuilder("rocks_glass_2", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fRocks Glass")).setCustomModel(10).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_ROCKS_GLASS_2.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_ROCKS_GLASS = register(new CustomItem.CustomItemBuilder("empty_rocks_glass", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Rocks Glass")).setCustomModel(9).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem ROCKS_GLASS = register(new CustomItem.CustomItemBuilder("rocks_glass", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fRocks Glass")).setCustomModel(9).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_ROCKS_GLASS.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_PERFUME_BOTTLE = register(new CustomItem.CustomItemBuilder("empty_perfume_bottle", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Perfume Bottle")).setCustomModel(8).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem PERFUME_BOTTLE = register(new CustomItem.CustomItemBuilder("perfume_bottle", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fPerfume Bottle")).setCustomModel(8).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_PERFUME_BOTTLE.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_WINE_GLASS = register(new CustomItem.CustomItemBuilder("empty_wine_glass", Material.BUCKET, 4)
            .setCustomName(HexUtils.colorify("&fEmpty Wine Glass")).setCustomModel(15).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem WINE_GLASS = register(new CustomItem.CustomItemBuilder("wine_glass", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fWine Glass")).setCustomModel(15).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_WINE_GLASS.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_ROUND_BOTTLE = register(new CustomItem.CustomItemBuilder("empty_round_bottle", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Round Bottle")).setCustomModel(11).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem ROUND_BOTTLE = register(new CustomItem.CustomItemBuilder("round_bottle", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fRound Bottle")).setCustomModel(11).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_ROUND_BOTTLE.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_SHOT_GLASS = register(new CustomItem.CustomItemBuilder("empty_shot_glass", Material.BUCKET, 8)
            .setCustomName(HexUtils.colorify("&fEmpty Shot Glass")).setCustomModel(12).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem SHOT_GLASS = register(new CustomItem.CustomItemBuilder("shot_glass", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fShot Glass")).setCustomModel(12).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_SHOT_GLASS.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_FLASK = register(new CustomItem.CustomItemBuilder("empty_flask", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Flask")).setCustomModel(4).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem FLASK = register(new CustomItem.CustomItemBuilder("flask", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fFlask")).setCustomModel(4).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_FLASK.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_TRIANGLE_BOTTLE = register(new CustomItem.CustomItemBuilder("empty_triangle_bottle", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Triangle Bottle")).setCustomModel(14).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem TRIANGLE_BOTTLE = register(new CustomItem.CustomItemBuilder("triangle_bottle", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fTriangle Bottle")).setCustomModel(14).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_TRIANGLE_BOTTLE.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_CHAMPAGNE_GLASS = register(new CustomItem.CustomItemBuilder("empty_champagne_glass", Material.BUCKET, 4)
            .setCustomName(HexUtils.colorify("&fEmpty Champagne Glass")).setCustomModel(2).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem CHAMPAGNE_GLASS = register(new CustomItem.CustomItemBuilder("champagne_glass", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fChampagne Glass")).setCustomModel(16).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_CHAMPAGNE_GLASS.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_BOTTLE = register(new CustomItem.CustomItemBuilder("empty_bottle", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Jar")).setCustomModel(1).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem BOTTLE = register(new CustomItem.CustomItemBuilder("bottle", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fBottle")).setCustomModel(1).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_BOTTLE.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final ICustomItem EMPTY_LABELED_BOTTLE = register(new CustomItem.CustomItemBuilder("empty_labeled_bottle", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Labeled Bottle")).setCustomModel(6).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem LABELED_BOTTLE = register(new CustomItem.CustomItemBuilder("labeled_bottle", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fLabeled Bottle")).setCustomModel(6).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_LABELED_BOTTLE.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    public static final StonecuttingRecipe GLASS_BOTTLE_RECIPE = CustomItemManager.addStoneCuttingRecipe(
            new StonecuttingRecipe(new NamespacedKey(CobaltMagick.getInstance(), "cobaltmagick.custom.stonecutting.glass_bottle"), new ItemStack(Material.GLASS_BOTTLE), Material.GLASS)
    );

    public static final ICustomItem EMPTY_SKULL_BOTTLE = register(new CustomItem.CustomItemBuilder("empty_skull_bottle", Material.BUCKET, 1)
            .setCustomName(HexUtils.colorify("&fEmpty Skull Bottle")).setCustomModel(13).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
            .build());

    public static final ICustomItem SKULL_BOTTLE = register(new CustomItem.CustomItemBuilder("skull_bottle", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fSkull Bottle")).setCustomModel(13).setItemCategory(MagickItemCategory.POTION)
            .setItemMetaEditor(itemMeta -> {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                return potionMeta;
            })
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = EMPTY_SKULL_BOTTLE.getItemStack();
                item.setAmount(1);
                consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, item);
            }))
            .build());

    // ----- POTIONS -----

    public static final ICustomItem POTION_JAR = register(new CustomItem.CustomItemBuilder("potion_jar", Material.POTION, 1)
            .setCustomName(HexUtils.colorify("&fPotion Jar"))
            .setCustomModel(1).setItemCategory(MagickItemCategory.POTION)
            .addItemActivator(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = consumeEvent.getPlayer().getEquipment().getItem(equipmentSlot);
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                if (meta.getCustomModelData() < 3) meta.setCustomModelData(meta.getCustomModelData() + 1);
                else consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, EMPTY_BOTTLE.getItemStack());
                item.setItemMeta(meta);
            }))
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
        Bukkit.getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());
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
