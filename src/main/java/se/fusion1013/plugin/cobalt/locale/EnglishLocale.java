package se.fusion1013.plugin.cobalt.locale;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {
    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Fusion1013";
    }

    @Override
    public Map<String, String> getDefaultLocaleString() {
        return new LinkedHashMap<String, String>(){
            {
                this.put("#0", "Plugin Message Prefix");
                this.put("prefix", "&7[<g:#00aaaa:#0066aa>Cobalt&7] ");

                this.put("#1", "Emitter Command Usage Messages");
                this.put("command-usage-emitter-addparticle", "&e/emitter addparticle <group> <particle> <style> [offset]");
                this.put("command-usage-emitter-create", "&e/emitter create <name> <<x> <y> <z>>");
                this.put("command-usage-emitter-delete", "&e/emitter delete <name|id>");
                this.put("command-usage-emitter-flag", "&e/emitter flag <name|id> <flag>");
                this.put("command-usage-emitter-info", "&e/emitter info <name|id>");
                this.put("command-usage-emitter-list", "&e/emitter list");
                this.put("command-usage-emitter-move", "&e/emitter move <name|id> <position>");
                this.put("command-usage-emitter-removeparticle", "&e/emitter removeparticle <id>");

                this.put("#2", "Emitter Command Success Messages");
                this.put("command-success-emitter-create", "&eCreated new emitter. &eName: &b%name% &eLocation: &b%x%&ex &b%y%&ey &b%z%&ez");

                this.put("#3", "Scenario Command Usage Messages");
                this.put("command-usage-scenario-create", "/scenario create <name> [data]");
                this.put("command-usage-scenario-start", "/scenario start <event>");

                this.put("#10", "Misc");
                this.put("command-not-implemented", "&eCommand not yet implemented");
                this.put("command-unknown", "&eUnknown command");

                this.put("#11", "List Messages");
                this.put("list-header", "<g:#00aaaa:#0066aa>------ %header% ------");
                this.put("list-item-name", "&eName: &b%name%");
                this.put("list-item-location", "&eLocation: &b%x%&ex &b%y%&ey &b%z%&ez &eWorld: &b%world%");
                this.put("list-item-id-name-location", "&e[&b%id%&e] &eName: &b%name% &eLocation: &b%x%&ex &b%y%&ey &b%z%&ez");

                // DEPRECATED --
                this.put("list-emitter-groups", "<g:#00aaaa:#0066aa>------ Available Emitters ------");
                this.put("list-emitter-group-output", "&e[&b%id%&e] &eName: &b%name% &eLocation: &b%x%&ex &b%y%&ey &b%z%&ez");
                this.put("list-scenarios", "<g:#00aaaa:#0066aa>------ Available Scenarios ------");
                this.put("list-scenarios-output", "&e[&b%id%&e] &eName: &b%name% &eLocation: &b%x%&ex &b%y%&ey &b%z%&ez &eWorld: &b%world%");
                // --DEPRECATED


                this.put("gradient", "<g:#ff1100:#00ff1e>GRADIENT");
            }
        };
    }
}
