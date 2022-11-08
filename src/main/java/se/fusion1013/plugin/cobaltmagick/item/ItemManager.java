package se.fusion1013.plugin.cobaltmagick.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.*;
import se.fusion1013.plugin.cobaltcore.item.category.ItemCategory;
import se.fusion1013.plugin.cobaltcore.item.enchantment.CobaltEnchantment;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentWrapper;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;
import se.fusion1013.plugin.cobaltcore.item.system.ItemRarity;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.MagickAdvancementManager;
import se.fusion1013.plugin.cobaltmagick.item.create.*;
import se.fusion1013.plugin.cobaltmagick.item.enchantments.MagickEnchantment;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.util.constants.ItemConstants;
import se.fusion1013.plugin.cobaltmagick.util.constants.MerchantRecipeConstants;
import se.fusion1013.plugin.cobaltmagick.world.structures.MagickStructureManager;

import java.util.ArrayList;
import java.util.List;

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

    // ----- REGISTER INTERNALS -----

    private static final Class<MagickItemCategory> MAGICK_ITEM_CATEGORY = CustomItemManager.registerCategory(MagickItemCategory.class);
    // TODO: Rarity

    // ----- MISCELLANEOUS -----

    public static final ICustomItem BROKEN_SPELL = register(new CobaltItem.Builder("broken_spell")
            .material(Material.CLOCK)
            .itemName(HexUtils.colorify("&dBroken Spell"))
            .modelData(61)
            .rarity(ItemRarity.MYSTIC)
            .rarityLore(Component.text("A malfunctioning spell").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))
            .category(MagickItemCategory.SPELL)
            .build());

    public static final ICustomItem BROKEN_WAND = register(new CobaltItem.Builder("broken_wand")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&dBroken Wand"))
            .modelData(15)
            .rarity(ItemRarity.MYSTIC)
            .rarityLore(
                    Component.text("This wand has snapped in half").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.text("but it still crackles with magical energy").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .category(MagickItemCategory.WAND)
            .build());

    public static final ICustomItem INVISIBLE_ITEM_FRAME = register(new CustomItem.CustomItemBuilder("invisible_item_frame", Material.ITEM_FRAME, 1)
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

    public static final ICustomItem INVISIBLE_GLOW_ITEM_FRAME = register(new CustomItem.CustomItemBuilder("invisible_glow_item_frame", Material.GLOW_ITEM_FRAME, 1)
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

    public static final ICustomItem GLOWING_ITEM_FRAME = register(new CustomItem.CustomItemBuilder("glowing_item_frame", Material.ITEM_FRAME, 1)
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

    public static final ICustomItem GLOWING_GLOW_ITEM_FRAME = register(new CustomItem.CustomItemBuilder("glowing_glow_item_frame", Material.GLOW_ITEM_FRAME, 1)
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

    public static final ICustomItem SHINY_ORB = CreateShinyOrb.create();

    // ----- COINS -----

    /*
    public static final ICustomItem IRON_COIN = register(new CustomItem.CustomItemBuilder("iron_coin", Material.IRON_NUGGET, 1)
            .setCustomName(HexUtils.colorify("&f&lIron Coin"))
            .addLoreLine(HexUtils.colorify("&r&7It glimmers"))
            .setCustomModel(1)
            .build());

    public static final ICustomItem GOLD_COIN = register(new CustomItem.CustomItemBuilder("gold_coin", Material.GOLD_NUGGET, 1)
            .setCustomName(HexUtils.colorify("&6&lGold Coin"))
            .addLoreLine(HexUtils.colorify("&r&eIt glimmers"))
            .setCustomModel(1)
            .build());
     */

    // ----- MATERIALS -----

    public static final ICustomItem ECHO_INGOT = register(new CobaltItem.Builder("echo_ingot")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&rEcho Ingot")).modelData(40).category(MagickItemCategory.MATERIAL).tags("dream_item")
            .build());

    /*
    public static final ICustomItem CRYSTAL_LENS = register(new CustomItem.CustomItemBuilder("crystal_lens", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&5Crystal Lens")).addLoreLine(HexUtils.colorify("&dLight shifts and distorts")).setCustomModel(4)
            .setItemCategory(MagickItemCategory.MATERIAL).build());

    public static final ICustomItem DRAGONSTONE = register(new CobaltItem.Builder("dragonstone")
            .material(Material.EMERALD).modelData(5)
            .itemName(HexUtils.colorify("&dDragonstone"))
            .rarity(ItemRarity.MYSTIC)
            .rarityLore(
                    Component.text("A mysterious glow dances").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.text("below the surface of this gem.").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .category(MagickItemCategory.UNKNOWN)
            .build());
     */

    /*
    public static final ICustomItem MANA_DIAMOND = register(new CustomItem.CustomItemBuilder("mana_diamond", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Diamond")).setCustomModel(6).setItemCategory(MagickItemCategory.MATERIAL).build());
     */

    public static final ICustomItem MANA_PEARL = register(new CobaltItem.Builder("mana_pearl")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&fMana Pearl")).modelData(7).category(MagickItemCategory.MATERIAL)
            .build());

    public static final ICustomItem MANA_POWDER = register(new CobaltItem.Builder("mana_powder")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&fMana Powder")).modelData(8).category(MagickItemCategory.MATERIAL)
            .build());

    /*
    public static final ICustomItem RAINBOW_ROD = register(new CustomItem.CustomItemBuilder("rainbow_rod", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<r:.5:1>Rainbow Rod")).setCustomModel(9).setItemCategory(MagickItemCategory.MATERIAL).build());
     */

    public static final ICustomItem AQUAMARINE = register(new CobaltItem.Builder("aquamarine")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&fAquamarine")).modelData(10).category(MagickItemCategory.MATERIAL)
            .build());

    public static final ICustomItem CRYSTAL_SHARDS = register(new CobaltItem.Builder("crystal_shards")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&dCrystal Shards")).modelData(42).category(MagickItemCategory.MATERIAL)
            .build());

    // ----- CAULDRON THINGS -----

    public static final ICustomItem SUNSEED = register(new CobaltItem.Builder("sunseed")
            .material(Material.EMERALD).modelData(1013)
            .itemName(HexUtils.colorify("<g:#b93b0b:#baba57>Sunseed"))
            .rarity(ItemRarity.MYSTIC)
            .rarityLore(
                    HexUtils.colorify("&8A seed of great promise...")
            )
            .category(MagickItemCategory.UNKNOWN)
            .build());

    public static final ICustomItem OUR_MATTER = register(new CobaltItem.Builder("our_matter")
            .material(Material.EMERALD).modelData(1001)
            .itemName(HexUtils.colorify("<g:#969696:#1a1a1a>Our Matter"))
            .rarity(ItemRarity.MYSTIC)
            .rarityLore(
                    HexUtils.colorify("&8Infinite possibilities pulse within...")
            )
            .category(MagickItemCategory.UNKNOWN)
            .build());

    public static final ICustomItem EVIL_EYE = register(new CobaltItem.Builder("evil_eye")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("<g:#172373:#2442a3>Evil Eye")).modelData(1012)
            .category(MagickItemCategory.MATERIAL)
            .build());

    public static final ICustomItem DEATH_BOUND_AMULET_DEACTIVATED = register(new CobaltItem.Builder("death_bound_amulet_deactivated")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("<g:#5e0000:#b80000>Death-Bound Amulet"))
            .extraLore(HexUtils.colorify("&7&oIt seems inactive for now...")).modelData(1008).category(MagickItemCategory.MATERIAL)
            .build());

    public static final ICustomItem DEATH_BOUND_AMULET = register(new CobaltItem.Builder("death_bound_amulet")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("<g:#5e0000:#b80000>Death-Bound Amulet")).modelData(1007).category(MagickItemCategory.MATERIAL)
            .extraLore(HexUtils.colorify("&7Bound Player: "))
            .itemActivatorSync(ItemActivator.PLAYER_RIGHT_CLICK, (((iCustomItem, event, equipmentSlot) -> {

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
                player.sendMessage(HexUtils.colorify("&7&oThe amulet binds to your soul..."));

            })))
            //TODO .addShapelessRecipe(new AbstractCustomItem.ShapelessIngredient(1, DEATH_BOUND_AMULET_DEACTIVATED.getItemStack()),
            //                    new AbstractCustomItem.ShapelessIngredient(1, EVIL_EYE.getItemStack()))
            .build());

    // ----- HATS -----

    public static final ICustomItem HAT_YODA = register(new CustomItem.CustomItemBuilder("hat_yoda", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rYoda")).setCustomModel(-107).setItemCategory(MagickItemCategory.HAT).build());

    public static final ICustomItem HAT_MONOCLE = register(new CustomItem.CustomItemBuilder("hat_monocle", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rMonocle")).setCustomModel(-97).setItemCategory(MagickItemCategory.HAT).build());

    public static final ICustomItem HAT_DEER_HEAD = register(new CustomItem.CustomItemBuilder("hat_deer_head", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rDeer Head")).setCustomModel(-93).setItemCategory(MagickItemCategory.HAT).build());

    public static final ICustomItem HAT_CIGAR = register(new CustomItem.CustomItemBuilder("hat_cigar", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rCigar")).setCustomModel(-92).setItemCategory(MagickItemCategory.HAT).build());

    public static final ICustomItem HAT_TINY_HAT = register(new CustomItem.CustomItemBuilder("hat_tiny_hat", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rTiny Hat")).setCustomModel(-91).setItemCategory(MagickItemCategory.HAT).build());

    public static final ICustomItem HAT_SPONGEBOB = register(new CustomItem.CustomItemBuilder("hat_spongebob", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rSpongebob")).setCustomModel(-88).setItemCategory(MagickItemCategory.HAT).build());

    public static final ICustomItem HAT_BIRD_MASTER = register(new CustomItem.CustomItemBuilder("hat_bird_master", Material.CLOCK, 1)
            .setCustomName(HexUtils.colorify("&rBird Master")).setCustomModel(-4).setItemCategory(MagickItemCategory.HAT).build());

    public static final ICustomItem VILLAGER_CONVERTER_HAT = register(new CustomItem.CustomItemBuilder("villager_converter_hat", Material.EMERALD, 1)
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
            .setItemCategory(MagickItemCategory.HAT)
            .build());

    // ----- TOOLS -----

    public static final ICustomItem DUNGEON_LOCATOR = register(new CustomItem.CustomItemBuilder("dungeon_locator", Material.COMPASS, 1)
            .setCustomName(HexUtils.colorify("&fDungeon Locator"))
            .addLoreLine(HexUtils.colorify("&7&oReveals the location of two hidden structures..."))
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

                    // Grant advancement
                    MagickAdvancementManager advancementManager = CobaltCore.getInstance().getSafeManager(CobaltMagick.getInstance(), MagickAdvancementManager.class);
                    if (advancementManager != null) advancementManager.grantAdvancement(interactEvent.getPlayer(), "progression", "locate_high_alchemist");

                    CobaltMagick.getInstance().getLogger().info("Set compass location to: " + MagickStructureManager.highAlchemistDungeonLocation + ", in world: " + MagickStructureManager.highAlchemistWorld.getName());
                }
            }))
            .setCustomModel(1)
            .addShapedRecipe(
                    "-e-",
                    "ece",
                    "-e-",
                    new AbstractCustomItem.ShapedIngredient('c', Material.COMPASS),
                    new AbstractCustomItem.ShapedIngredient('e', Material.NETHERITE_INGOT)
            )
            .build());

    public static final ICustomItem EXPERIENCE_ORB = CreateExperienceOrb.createExperienceOrb();

    // Battleaxe
    public static final ICustomItem BATTLEAXE = register(new CustomItem.CustomItemBuilder("battle_axe", Material.NETHERITE_AXE, 1)
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
            .setCustomModel(5).setItemCategory(MagickItemCategory.TOOL)
            .build());

    /*
    public static final ICustomItem POISON_BLADE = register(new CobaltItem.Builder("poison_blade")
            .material(Material.NETHERITE_SWORD).modelData(3)
            .itemName(HexUtils.colorify("&ePoison Blade"))
            .rarity(ItemRarity.RARE)
            .rarityLore(Component.text("Reeks of ancient poison").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))
            .category(MagickItemCategory.WEAPON)
            // Poison Effect
            .enchantments(new EnchantmentWrapper(CobaltEnchantment.POISON, 1, false))
            .build());
     */

    public static final ICustomItem CRYSTAL_KEY = register(new CobaltItem.Builder("crystal_key")
            .material(Material.EMERALD)
            .itemName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .extraLore(ChatColor.WHITE + "The key is voiceless")
            .modelData(3).category(MagickItemCategory.UNKNOWN)
            .build());

    public static final ICustomItem CRYSTAL_KEY_LIGHT_ACTIVE = register(new CobaltItem.Builder("crystal_key_light_active")
            .material(Material.EMERALD)
            .itemName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .extraLore(ChatColor.RESET + "" + ChatColor.WHITE + "The key is ready")
            .editMeta((meta -> {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                return meta;
            }))
            .modelData(3).category(MagickItemCategory.UNKNOWN)
            .build());

    public static final ICustomItem CRYSTAL_KEY_DARK_ACTIVE = register(new CobaltItem.Builder("crystal_key_dark_active")
            .material(Material.EMERALD)
            .itemName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .extraLore(ChatColor.RESET + "" + ChatColor.WHITE + "The key whispers secrets and promises; it is ready")
            .editMeta((meta -> {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                return meta;
            }))
            .modelData(3).category(MagickItemCategory.UNKNOWN)
            .build());

    public static final ICustomItem DUNGEON_COIN = register(new CobaltItem.Builder("dungeon_coin")
            .material(Material.EMERALD)
            .itemName(ChatColor.RESET + "" + ChatColor.GOLD + "Dungeon Coin")
            .modelData(1)
            .build());

    public static final ICustomItem DREAMGLASS = register(new CobaltItem.Builder("dreamglass")
            .material(Material.SPYGLASS)
            .itemName(HexUtils.colorify("<g:#20a9e8:#117cad>Dreamglass"))
            .extraLore(ChatColor.WHITE + "The lens distorts reality")
            .editMeta(itemMeta -> {
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
            .modelData(1).category(MagickItemCategory.TOOL)
            .tags("dream_item")
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

    public static final ShapedRecipe SLIMESTEEL_INGOT = CustomItemManager.addShapedRecipe("slimesteel_ingot_from_slime", CreateMaterialIngots.SLIMESTEEL_INGOT.getItemStack(),
            "---","-*-","---",
            new AbstractCustomItem.ShapedIngredient('-', Material.SLIME_BALL),
            new AbstractCustomItem.ShapedIngredient('*', Material.IRON_INGOT));

    /*
    public static final ShapedRecipe SLIMESTEEL_HELMET = CustomItemManager.addShapedRecipe("slimesteel_helmet", CreateArmor.SLIMESTEEL_HELMET.getItemStack(),
            "***", "*-*", "---",
            new AbstractCustomItem.ShapedIngredient('*', CreateMaterialIngots.SLIMESTEEL_INGOT.getItemStack()));

    public static final ShapedRecipe SLIMESTEEL_CHESTPLATE = CustomItemManager.addShapedRecipe("slimesteel_chestplate", CreateArmor.SLIMESTEEL_CHESTPLATE.getItemStack(),
            "*-*", "***", "***",
            new AbstractCustomItem.ShapedIngredient('*', CreateMaterialIngots.SLIMESTEEL_INGOT.getItemStack()));

    public static final ShapedRecipe SLIMESTEEL_LEGGINGS = CustomItemManager.addShapedRecipe("slimesteel_leggings", CreateArmor.SLIMESTEEL_LEGGINGS.getItemStack(),
            "***", "*-*", "*-*",
            new AbstractCustomItem.ShapedIngredient('*', CreateMaterialIngots.SLIMESTEEL_INGOT.getItemStack()));

    public static final ShapedRecipe SLIMESTEEL_BOOTS = CustomItemManager.addShapedRecipe("slimesteel_boots", CreateArmor.SLIMESTEEL_BOOTS.getItemStack(),
            "---", "*-*", "*-*",
            new AbstractCustomItem.ShapedIngredient('*', CreateMaterialIngots.SLIMESTEEL_INGOT.getItemStack()));
     */

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());

        // Create external items
        CreateMaterialIngots.create();
        // CreateKeyItems.create();
        CreateTools.create();
        CreateSwords.create();
        // CreateEssenceStones.create();
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
