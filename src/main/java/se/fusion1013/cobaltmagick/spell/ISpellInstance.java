package se.fusion1013.cobaltmagick.spell;

public interface ISpellInstance extends ISpellTemplate {

    String state();

    void setState(String state);

    int slot();

    ISpellTemplate spellTemplate();

}
