package se.fusion1013.plugin.cobaltmagick.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.*;
import se.fusion1013.plugin.cobaltcore.item.components.ComponentManager;
import se.fusion1013.plugin.cobaltcore.item.components.IComponentFactory;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;
import se.fusion1013.plugin.cobaltcore.item.system.ItemRarity;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.MagickAdvancementManager;
import se.fusion1013.plugin.cobaltmagick.item.components.MagickComponentFactory;
import se.fusion1013.plugin.cobaltmagick.item.create.*;
import se.fusion1013.plugin.cobaltmagick.util.constants.ItemConstants;
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

    // ----- COMPONENTS -----

    private static final IComponentFactory MAGICK_FACTORY = ComponentManager.registerFactory(new MagickComponentFactory());

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

    public static final ICustomItem SHINY_ORB = CreateShinyOrb.create();

    // ----- MATERIALS -----

    public static final ICustomItem ECHO_INGOT = register(new CobaltItem.Builder("echo_ingot")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&rEcho Ingot")).modelData(40).category(MagickItemCategory.MATERIAL).tags("dream_item")
            .build());

    public static final ICustomItem MANA_POWDER = register(new CobaltItem.Builder("mana_powder")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&fMana Powder")).modelData(8).category(MagickItemCategory.MATERIAL)
            .build());

    public static final ICustomItem AQUAMARINE = register(new CobaltItem.Builder("aquamarine")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&fAquamarine")).modelData(10).category(MagickItemCategory.MATERIAL)
            .build());

    // ----- CAULDRON THINGS -----

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

    // ----- TOOLS -----

    public static final ICustomItem DUNGEON_LOCATOR = register(new CobaltItem.Builder("dungeon_locator")
            .material(Material.COMPASS)
            .itemName(HexUtils.colorify("&fDungeon Locator"))
            .rarity(ItemRarity.UNCOMMON)
            .rarityLore(HexUtils.colorify("&7&oReveals the location of two hidden structures..."))
            .itemActivatorSync(ItemActivator.PLAYER_RIGHT_CLICK, ((iCustomItem, event, equipmentSlot) -> {
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
            .modelData(1)
            /*
            .addShapedRecipe(
                    "-e-",
                    "ece",
                    "-e-",
                    new AbstractCustomItem.ShapedIngredient('c', Material.COMPASS),
                    new AbstractCustomItem.ShapedIngredient('e', Material.NETHERITE_INGOT)
            )
             */
            .build());

    public static final ICustomItem EXPERIENCE_ORB = CreateExperienceOrb.createExperienceOrb();

    // Battleaxe
    public static final ICustomItem BATTLEAXE = register(new CobaltItem.Builder("battle_axe")
            .material(Material.NETHERITE_AXE)
            .itemName(ChatColor.RESET + "Battle Axe")
            .itemActivatorSync(ItemActivator.PLAYER_KILL_PLAYER, (item, event, slot) -> {
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
            .modelData(5).category(MagickItemCategory.TOOL)
            .build());

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

    /*
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

    public static final ICustomItem EMPTY_MARTINI_GLASS = register(new CobaltItem.Builder("empty_martini_glass")
            .material(Material.BUCKET)
            .itemName(HexUtils.colorify("&fEmpty Martini Glass")).modelData(7).addStoneCuttingRecipe(Material.GLASS).setItemCategory(MagickItemCategory.POTION)
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
     */

    // ----- POTIONS -----

    public static final ICustomItem POTION_JAR = register(new CobaltItem.Builder("potion_jar")
            .material(Material.POTION)
            .itemName(HexUtils.colorify("&fPotion Jar"))
            .modelData(1).category(MagickItemCategory.POTION)
            .itemActivatorAsync(ItemActivator.PLAYER_CONSUME, ((iCustomItem, event, equipmentSlot) -> {
                PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
                ItemStack item = consumeEvent.getPlayer().getEquipment().getItem(equipmentSlot);
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                if (meta.getCustomModelData() < 3) meta.setCustomModelData(meta.getCustomModelData() + 1);
                else consumeEvent.getPlayer().getEquipment().setItem(equipmentSlot, new ItemStack(Material.GLASS_BOTTLE)/*EMPTY_BOTTLE.getItemStack()*/);
                item.setItemMeta(meta);
            }))
            .build());

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());

        CreateTools.create();
        CreateSwords.create();
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
