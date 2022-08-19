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
