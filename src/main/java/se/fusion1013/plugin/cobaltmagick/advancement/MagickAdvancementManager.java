package se.fusion1013.plugin.cobaltmagick.advancement;

import eu.endercentral.crazy_advancements.JSONMessage;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.advancement.CobaltAdvancementManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.event.SpellCastEvent;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.spells.SpellType;

import java.util.*;

public class MagickAdvancementManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private AdvancementManager SPELL_ADVANCEMENT_MANAGER;
    private final Map<String, Advancement> SPELL_ADVANCEMENTS = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public MagickAdvancementManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- CREATING -----

    private AdvancementManager createProgressionAdvancements() {

        // Create the advancement manager
        AdvancementManager manager = new AdvancementManager(new NameKey("cobalt_magick.progression", "progression"));
        this.PROGRESSION_ADVANCEMENT_MANAGER = manager;

        AdvancementVisibility visibility = AdvancementVisibility.PARENT_GRANTED;

        // Create advancement window display
        ItemStack rootIcon = ItemManager.AQUAMARINE.getItemStack();
        JSONMessage rootTitle = new JSONMessage(new TextComponent("Magick"));
        JSONMessage rootDescription = new JSONMessage(new TextComponent("Begin your journey into Magick"));
        AdvancementDisplay.AdvancementFrame rootFrame = AdvancementDisplay.AdvancementFrame.TASK;

        AdvancementDisplay rootDisplay = new AdvancementDisplay(rootIcon, rootTitle, rootDescription, rootFrame, visibility);
        rootDisplay.setBackgroundTexture("textures/cobalt/block/smooth_lead_block.png");
        rootDisplay.setX(0);
        rootDisplay.setY(0);

        Advancement rootAdvancement = new Advancement(new NameKey("cobalt_magick.progression.root", "start_magick"), rootDisplay);
        manager.addAdvancement(rootAdvancement);
        PROGRESSION_ADVANCEMENTS.put("root", rootAdvancement);

        // ----- Crystal Key Quest Advancements -----

        // Dungeon Locator
        AdvancementDisplay locatorDisplay = createDisplay(ItemManager.DUNGEON_LOCATOR.getItemStack(), "Not a Stronghold...", "Use a Dungeon Locator", AdvancementDisplay.AdvancementFrame.TASK, AdvancementVisibility.PARENT_GRANTED);
        locatorDisplay.setPositionOrigin(rootAdvancement);
        locatorDisplay.setX(1);
        Advancement locateHighAlchemistAdvancement = new Advancement(rootAdvancement, new NameKey("cobalt_magick.progression.locate_high_alchemist", "locate_high_alchemist"), locatorDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(locateHighAlchemistAdvancement);
        PROGRESSION_ADVANCEMENTS.put("locate_high_alchemist", locateHighAlchemistAdvancement);

        // Kill High Alchemist
        AdvancementDisplay highAlchemistDisplay = createDisplay(ItemManager.CRYSTAL_KEY.getItemStack(), "Forgotten Foe", "Clear the Ancient Laboratory", AdvancementDisplay.AdvancementFrame.TASK, AdvancementVisibility.PARENT_GRANTED);
        highAlchemistDisplay.setPositionOrigin(locateHighAlchemistAdvancement);
        highAlchemistDisplay.setX(1);
        Advancement killHighAlchemistAdvancement = new Advancement(locateHighAlchemistAdvancement, new NameKey("cobalt.magick.progression.kill_high_alchemist", "kill_high_alchemist"), highAlchemistDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(killHighAlchemistAdvancement);
        PROGRESSION_ADVANCEMENTS.put("kill_high_alchemist", killHighAlchemistAdvancement);

        // Attune Key (Light & Dark)
        AdvancementDisplay keyAttuneLightDisplay = createDisplay(new ItemStack(Material.NOTE_BLOCK), "The Key Takes in the Music", "It is ready", AdvancementDisplay.AdvancementFrame.TASK, AdvancementVisibility.HIDDEN);
        keyAttuneLightDisplay.setPositionOrigin(killHighAlchemistAdvancement);
        keyAttuneLightDisplay.setX(1);
        keyAttuneLightDisplay.setY(-.5f);
        Advancement attuneKeyLightAdvancement = new Advancement(killHighAlchemistAdvancement, new NameKey("cobalt.magick.progression.attune_key_light", "attune_key_light"), keyAttuneLightDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(attuneKeyLightAdvancement);
        PROGRESSION_ADVANCEMENTS.put("attune_key_light", attuneKeyLightAdvancement);

        AdvancementDisplay keyAttuneDarkDisplay = createDisplay(SpellManager.OCARINA_A.getSpellItem(), "The Key Begins to Whisper!", "I can give you so much in exchange for...", AdvancementDisplay.AdvancementFrame.TASK, AdvancementVisibility.HIDDEN);
        keyAttuneDarkDisplay.setPositionOrigin(killHighAlchemistAdvancement);
        keyAttuneDarkDisplay.setX(1);
        keyAttuneDarkDisplay.setY(.5f);
        Advancement attuneKeyDarkAdvancement = new Advancement(killHighAlchemistAdvancement, new NameKey("cobalt.magick.progression.attune_key_dark", "attune_key_dark"), keyAttuneDarkDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(attuneKeyDarkAdvancement);
        PROGRESSION_ADVANCEMENTS.put("attune_key_dark", attuneKeyDarkAdvancement);

        // Open Chest(s)
        AdvancementDisplay lightChestOpenDisplay = createDisplay(SpellManager.OMEGA.getSpellItem(), "The Chest Opens...", "But the key might have other stories to tell...", AdvancementDisplay.AdvancementFrame.GOAL, AdvancementVisibility.HIDDEN);
        lightChestOpenDisplay.setPositionOrigin(attuneKeyLightAdvancement);
        lightChestOpenDisplay.setX(1);
        Advancement lightChestOpenAdvancement = new Advancement(attuneKeyLightAdvancement, new NameKey("cobalt.magick.progression.open_light_chest", "open_light_chest"), lightChestOpenDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(lightChestOpenAdvancement);
        PROGRESSION_ADVANCEMENTS.put("light_chest_open", lightChestOpenAdvancement);

        AdvancementDisplay darkChestOpenDisplay = createDisplay(SpellManager.RANDOM_SPELL.getSpellItem(), "The glass key speaks!", "The chest listens", AdvancementDisplay.AdvancementFrame.GOAL, AdvancementVisibility.HIDDEN);
        darkChestOpenDisplay.setPositionOrigin(attuneKeyDarkAdvancement);
        darkChestOpenDisplay.setX(1);
        Advancement darkChestOpenAdvancement = new Advancement(attuneKeyDarkAdvancement, new NameKey("cobalt.magick.progression.open_dark_chest", "open_dark_chest"), darkChestOpenDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(darkChestOpenAdvancement);
        PROGRESSION_ADVANCEMENTS.put("dark_chest_open", darkChestOpenAdvancement);

        // ----- SECRET PROGRESSION ADVANCEMENTS -----

        TextComponent meditationCubeTitle = new TextComponent("Meditate");
        meditationCubeTitle.setFont("minecraft:alt");
        TextComponent meditationCubeDescription = new TextComponent("Cube glorious cube");
        meditationCubeDescription.setFont("minecraft:alt");
        AdvancementDisplay meditationCubeDisplay = createDisplay(new ItemStack(Material.LODESTONE), meditationCubeTitle, meditationCubeDescription, AdvancementDisplay.AdvancementFrame.CHALLENGE, AdvancementVisibility.HIDDEN);
        meditationCubeDisplay.setPositionOrigin(rootAdvancement);
        meditationCubeDisplay.setX(1);
        meditationCubeDisplay.setY(2);
        Advancement meditationCubeAdvancement = new Advancement(rootAdvancement, new NameKey("cobalt.magick.progression.secret.meditation_cube", "meditation_cube"), meditationCubeDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(meditationCubeAdvancement);
        PROGRESSION_ADVANCEMENTS.put("meditation_cube", meditationCubeAdvancement);

        // ----- CAULDRON PROGRESSION ADVANCEMENTS -----

        // Nigredo
        TextComponent cauldronNigredoTitle = new TextComponent("nigredo");
        cauldronNigredoTitle.setFont("minecraft:fin_small");
        TextComponent cauldronNigredoDescription = new TextComponent("the shadow within");
        cauldronNigredoDescription.setFont("minecraft:fin_small");
        AdvancementDisplay cauldronNigredoDisplay = createDisplay(ItemManager.EVIL_EYE.getItemStack(), cauldronNigredoTitle, cauldronNigredoDescription, AdvancementDisplay.AdvancementFrame.TASK, AdvancementVisibility.HIDDEN);
        cauldronNigredoDisplay.setPositionOrigin(rootAdvancement);
        cauldronNigredoDisplay.setX(1);
        cauldronNigredoDisplay.setY(-2);
        Advancement cauldronNigredoAdvancement = new Advancement(rootAdvancement, new NameKey("cobalt.magick.progression.cauldron.nigredo", "nigredo"), cauldronNigredoDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(cauldronNigredoAdvancement);
        PROGRESSION_ADVANCEMENTS.put("nigredo", cauldronNigredoAdvancement);

        // Albedo
        TextComponent cauldronAlbedoTitle = new TextComponent("albedo");
        cauldronAlbedoTitle.setFont("minecraft:fin_small");
        TextComponent cauldronAlbedoDescription = new TextComponent("prima materia, cleansed from impurities");
        cauldronAlbedoDescription.setFont("minecraft:fin_small");
        AdvancementDisplay cauldronAlbedoDisplay = createDisplay(ItemManager.OUR_MATTER.getItemStack(), cauldronAlbedoTitle, cauldronAlbedoDescription, AdvancementDisplay.AdvancementFrame.TASK, AdvancementVisibility.HIDDEN);
        cauldronAlbedoDisplay.setPositionOrigin(rootAdvancement);
        cauldronAlbedoDisplay.setX(2);
        cauldronAlbedoDisplay.setY(-2);
        Advancement cauldronAlbedoAdvancement = new Advancement(rootAdvancement, new NameKey("cobalt.magick.progression.cauldron.albedo", "albedo"), cauldronAlbedoDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(cauldronAlbedoAdvancement);
        PROGRESSION_ADVANCEMENTS.put("albedo", cauldronAlbedoAdvancement);

        // Citrinitas
        TextComponent cauldronCitrinitasTitle = new TextComponent("citrinitas");
        cauldronCitrinitasTitle.setFont("minecraft:fin_small");
        TextComponent cauldronCitrinitasDescription = new TextComponent("dawn of solar light");
        cauldronCitrinitasDescription.setFont("minecraft:fin_small");
        AdvancementDisplay cauldronCitrinitasDisplay = createDisplay(ItemManager.SUNSEED.getItemStack(), cauldronCitrinitasTitle, cauldronCitrinitasDescription, AdvancementDisplay.AdvancementFrame.TASK, AdvancementVisibility.HIDDEN);
        cauldronCitrinitasDisplay.setPositionOrigin(rootAdvancement);
        cauldronCitrinitasDisplay.setX(3);
        cauldronCitrinitasDisplay.setY(-2);
        Advancement cauldronCitrinitasAdvancement = new Advancement(rootAdvancement, new NameKey("cobalt.magick.progression.cauldron.citrinitas", "citrinitas"), cauldronCitrinitasDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(cauldronCitrinitasAdvancement);
        PROGRESSION_ADVANCEMENTS.put("citrinitas", cauldronCitrinitasAdvancement);

        // Rubedo
        TextComponent cauldronRubedoTitle = new TextComponent("rubedo");
        cauldronRubedoTitle.setFont("minecraft:fin_small");
        TextComponent cauldronRubedoDescription = new TextComponent("a promise made, a promise to keep");
        cauldronRubedoDescription.setFont("minecraft:fin_small");
        AdvancementDisplay cauldronRubedoDisplay = createDisplay(new ItemStack(Material.GOLD_BLOCK), cauldronRubedoTitle, cauldronRubedoDescription, AdvancementDisplay.AdvancementFrame.TASK, AdvancementVisibility.HIDDEN);
        cauldronRubedoDisplay.setPositionOrigin(rootAdvancement);
        cauldronRubedoDisplay.setX(4);
        cauldronRubedoDisplay.setY(-2);
        Advancement cauldronRubedoAdvancement = new Advancement(rootAdvancement, new NameKey("cobalt.magick.progression.cauldron.rubedo", "rubedo"), cauldronRubedoDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(cauldronRubedoAdvancement);
        PROGRESSION_ADVANCEMENTS.put("rubedo", cauldronRubedoAdvancement);

        return manager;
    }

    private AdvancementManager createSpellAdvancements() {

        int spellSectionHeight = 4;

        // Create the advancement manager
        AdvancementManager manager = new AdvancementManager(new NameKey("cobalt_magick.spells", "spells"));
        this.SPELL_ADVANCEMENT_MANAGER = manager;

        AdvancementVisibility visibility = AdvancementVisibility.HIDDEN;

        // Create advancement window display
        ItemStack rootIcon = SpellManager.SPARK_BOLT.getSpellItem();
        JSONMessage rootTitle = new JSONMessage(new TextComponent("Spells"));
        JSONMessage rootDescription = new JSONMessage(new TextComponent("Cast your first spell"));
        AdvancementDisplay.AdvancementFrame rootFrame = AdvancementDisplay.AdvancementFrame.CHALLENGE;

        AdvancementDisplay rootDisplay = new AdvancementDisplay(rootIcon, rootTitle, rootDescription, rootFrame, visibility);
        rootDisplay.setBackgroundTexture("textures/block/amethyst_block.png");
        rootDisplay.setX(0);
        rootDisplay.setY(SpellType.values().length / 2f);

        Advancement rootAdvancement = new Advancement(new NameKey("cobalt_magick.spells.root", "first_spell"), rootDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
        manager.addAdvancement(rootAdvancement);

        for (int i = 0; i < SpellType.values().length; i++) {
            SpellType type = (SpellType) Arrays.stream(SpellType.values()).toArray()[i];
            ISpell[] spells = SpellManager.getSpellsOfType(type);

            // Create type root advancement
            ItemStack typeIcon = type.getDisplayItem();
            JSONMessage typeTitle = new JSONMessage(new TextComponent(type.getLocalizedName()));
            JSONMessage typeDescription = new JSONMessage(new TextComponent("Unlocked " + type.getLocalizedName() + " spells"));
            AdvancementDisplay.AdvancementFrame typeFrame = AdvancementDisplay.AdvancementFrame.GOAL;

            AdvancementDisplay typeDisplay = new AdvancementDisplay(typeIcon, typeTitle, typeDescription, typeFrame, visibility);
            typeDisplay.setPositionOrigin(rootAdvancement);
            typeDisplay.setX(1);
            typeDisplay.setY((i * spellSectionHeight) - (spellSectionHeight * (SpellType.values().length / 2f)));

            Advancement typeAdvancement = new Advancement(rootAdvancement, new NameKey("cobalt_magick.spells.type." + type, type.toString()), typeDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
            manager.addAdvancement(typeAdvancement);

            // Create advancements for all spells of that type
            for (int s = 0; s < spells.length; s++) {
                ISpell spell = spells[s];

                ItemStack spellIcon = spell.getSpellItem();
                JSONMessage spellTitle = new JSONMessage(new TextComponent(spell.getSpellName()));

                JSONMessage spellDescription;
                if (spell.getDescription() == null) spellDescription = new JSONMessage(new TextComponent("[NULL]"));
                else spellDescription = new JSONMessage(new TextComponent(spell.getDescription()));

                AdvancementDisplay.AdvancementFrame spellFrame = AdvancementDisplay.AdvancementFrame.TASK;

                AdvancementDisplay spellDisplay = new AdvancementDisplay(spellIcon, spellTitle, spellDescription, spellFrame, visibility);
                spellDisplay.setPositionOrigin(typeAdvancement);

                int width = (spells.length / spellSectionHeight) + 1;
                int x = s % width;
                int y = s / width;

                spellDisplay.setX(1 + x);
                spellDisplay.setY((-spellSectionHeight/2f) + .5f + y);

                Advancement spellAdvancement = new Advancement(typeAdvancement, new NameKey("cobalt_magick.spells." + type + "." + spell.getInternalSpellName().replaceAll("#", "hash"), "spell." + spell.getInternalSpellName().replaceAll("#", "hash")), spellDisplay, AdvancementFlag.TOAST_AND_MESSAGE);
                manager.addAdvancement(spellAdvancement);
                SPELL_ADVANCEMENTS.put(spell.getInternalSpellName(), spellAdvancement);
            }
        }

        return manager;
    }

    // ----- EVENTS -----

    @EventHandler
    public void onSpellCast(SpellCastEvent event) {
        String internalSpellName = event.getSpell().getInternalSpellName();
        Advancement spellAdvancement = SPELL_ADVANCEMENTS.get(internalSpellName);
        if (spellAdvancement == null) return;
        if (event.getSpell().getCaster() instanceof Player player) {
            if (player.isOp()) return;
            SPELL_ADVANCEMENT_MANAGER.addPlayer(player);
            SPELL_ADVANCEMENT_MANAGER.grantAdvancement(player, spellAdvancement.getRootAdvancement());
            grantThrough(SPELL_ADVANCEMENT_MANAGER, spellAdvancement, player);
        }
    }

    // ----- ADVANCEMENT GIVING -----

    /**
     * Grant an advancement to a <code>Player</code>.
     *
     * @param player the <code>Player</code> to give the advancement to.
     * @param advancementName the name of the advancement.
     * @return true if the advancement was successfully granted.
     */
    public boolean grantAdvancement(Player player, String advancementName) {
        Advancement advancement = SPELL_ADVANCEMENTS.get(advancementName);
        if (advancement == null) return false;

        SPELL_ADVANCEMENT_MANAGER.grantAdvancement(player, advancement);
        return true;
    }

    public void grantAll(Player player) {
        SPELL_ADVANCEMENT_MANAGER.addPlayer(player);
        for (Advancement advancement : SPELL_ADVANCEMENT_MANAGER.getAdvancements()) {
            SPELL_ADVANCEMENT_MANAGER.grantAdvancement(player, advancement);
        }
    }

    private void grantThrough(AdvancementManager manager, Advancement advancement, Player player) {
        if (advancement.getParent() != null) grantThrough(manager, advancement.getParent(), player);

        manager.grantAdvancement(player, advancement);
    }

    // ----- GETTERS / SETTERS -----

    /**
     * Get all advancement names.
     *
     * @return an array of advancement names.
     */
    public String[] getAdvancementNames() {
        List<String> advancements = new ArrayList<>(SPELL_ADVANCEMENTS.keySet());
        return advancements.toArray(new String[0]);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), CobaltAdvancementManager.class).addAdvancementManager(createSpellAdvancements());
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static MagickAdvancementManager INSTANCE = null;
    /**
     * Returns the object representing this <code>AdvancementManager</code>.
     *
     * @return The object of this class
     */
    public static MagickAdvancementManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new MagickAdvancementManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
