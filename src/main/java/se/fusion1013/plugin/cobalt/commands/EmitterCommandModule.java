package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.manager.ParticleManager;
import se.fusion1013.plugin.cobalt.particle.ParticleEffect;
import se.fusion1013.plugin.cobalt.particle.ParticleGroup;
import se.fusion1013.plugin.cobalt.particle.styles.*;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;

import java.util.*;
import java.util.stream.Collectors;

public class EmitterCommandModule implements CommandModule {
    @Override
    public void onCommandExecute(CommandSender sender, String[] args) {

        LocaleManager localeManager = Cobalt.getInstance().getManager(LocaleManager.class);

        if (args.length == 0){
            localeManager.sendMessage(sender, "command-usage-emitter-addparticle");
            localeManager.sendMessage(sender, "command-usage-emitter-create");
            localeManager.sendMessage(sender, "command-usage-emitter-delete");
            localeManager.sendMessage(sender, "command-usage-emitter-flag");
            localeManager.sendMessage(sender, "command-usage-emitter-info");
            localeManager.sendMessage(sender, "command-usage-emitter-list");
            localeManager.sendMessage(sender, "command-usage-emitter-move");
            localeManager.sendMessage(sender, "command-usage-emitter-removeparticle");
            return;
        }
        ParticleManager particleManager = Cobalt.getInstance().getManager(ParticleManager.class);

        if (sender instanceof Player){
            Player p = (Player)sender;

            switch (args[0]){
                case "create": // /emitter create <name> <z, y, z>
                    if (args.length < 4) {
                        localeManager.sendMessage(sender, "command-usage-emitter-create");
                        return;
                    } else {
                        String name = args[1];
                        Location location = new Location(p.getWorld(), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                        int id = particleManager.getParticleGroups().size();
                        ParticleGroup group = new ParticleGroup(location, name, id);
                        group.setName(name);
                        particleManager.addParticleGroup(group);

                        StringPlaceholders stringPlaceholders = StringPlaceholders.builder("name", name)
                                .addPlaceholder("x", location.getX())
                                .addPlaceholder("y", location.getY())
                                .addPlaceholder("z", location.getZ())
                                .build();
                        localeManager.sendMessage(sender, "command-success-emitter-create", stringPlaceholders);
                    }
                    break;
                case "delete": // /emitter delete <name|id>
                    localeManager.sendMessage(sender, "command-not-implemented");
                    break;
                case "addparticle": // /emitter addparticle <group> <particle> <style> [offset]
                    if (args.length < 4){
                        sender.sendMessage("Missing arguments");
                    } else {
                        String groupName = args[1];
                        Particle particle = ParticleEffect.fromName(args[2]).getSpigotEnum();
                        String style = args[3];
                        Vector offset;
                        if (args.length > 4) {
                            offset = new Vector(Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]));
                        } else {
                            offset = new Vector(0, 0, 0);
                        }
                        ParticleGroup group = particleManager.getParticleGroupByName(groupName);
                        group.addParticle(style, particle, offset);
                    }
                    sender.sendMessage("Not yet implemented");
                    break;
                case "removeparticle": // /emitter removeparticle <id>
                    localeManager.sendMessage(sender, "command-not-implemented");
                    break;
                case "list": // /emitter list
                    List<ParticleGroup> particleGroups = particleManager.getParticleGroups();
                    localeManager.sendMessage(sender, "list-emitter-groups");

                    for (int i = 0; i < particleGroups.size(); i++){
                        ParticleGroup group = particleGroups.get(i);
                        StringPlaceholders stringPlaceholders = StringPlaceholders.builder("id", i)
                                .addPlaceholder("name", group.getName())
                                .addPlaceholder("x", group.getLocation().getX())
                                .addPlaceholder("y", group.getLocation().getY())
                                .addPlaceholder("z", group.getLocation().getZ())
                                .build();
                        for (ParticleStyle style : group.getParticleStyleList()){
                            //stringPlaceholders.addPlaceholder("style-name", style.getName());
                        }
                        localeManager.sendMessage(sender, "list-emitter-group-output", stringPlaceholders);
                    }
                    break;
                case "info": // /emitter info <name|id>
                    if (args.length < 2){
                        localeManager.sendMessage(sender, "command-usage-emitter-info");
                        return;
                    } else {
                        String groupName = args[1];
                        ParticleGroup group = Cobalt.getInstance().getManager(ParticleManager.class).getParticleGroupByName(groupName);
                    }
                    localeManager.sendMessage(sender, "command-not-implemented");
                    break;
                case "move": // /emitter move <name|id> <position>
                    localeManager.sendMessage(sender, "command-not-implemented");
                    break;
                case "flag": // /emitter flag <name|id> [flag]
                    localeManager.sendMessage(sender, "command-not-implemented");
                    break;
                default:
                    break;
            }

            /*

            switch (args[0]){
                // Adds a new emitter (particle group)
                case "add":
                    // nic emitter add <x, y, z>
                    Location location = new Location(p.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    ParticleGroup group = new ParticleGroup(location);
                    Nicobalt.getInstance().getManager(ParticleManager.class).addParticleGroup(group);
                    break;
                // Removes an emitter (particle group)
                case "remove":
                    // nic emitter remove <index>
                    particleManager.getParticleGroups().remove(Integer.parseInt(args[1]));
                    break;
                case "modify":
                    // nic emitter modify <group> <name|add|remove|flag>
                    switch (args[2]){
                        // Modifies the name of the group
                        case "name":
                            particleManager.getParticleGroups().get(Integer.parseInt(args[1])).setName(args[3]);
                            break;
                        // Adds a new particle style to the group
                        case "add":
                            // nic emitter modify <group> <add> <particle> <style>

                            ParticleStyle style = null;
                            switch (args[4]){
                                case "sphere":
                                    style = new ParticleStyleSphere(Particle.valueOf(args[3].toUpperCase()));
                                    break;
                                case "cube":
                                    style = new ParticleStyleCube(Particle.valueOf(args[3].toUpperCase()));
                                    break;
                                case "icosphere":
                                    style = new ParticleStyleIcosphere(Particle.valueOf(args[3].toUpperCase()));
                                    break;
                            }

                            if (style != null) particleManager.getParticleGroups().get(Integer.parseInt(args[1])).addParticleStyle(style);
                            break;
                    }
                    break;
                // Lists all emitters (particle groups)
                case "list":
                    String response = ChatColor.GOLD + "There are " + ChatColor.DARK_GREEN + particleManager.getParticleGroups().size() + ChatColor.GOLD + " Emitters\n";

                    List<String> groupNames = new ArrayList<>();
                    for (int i = 0; i < particleManager.getParticleGroups().size(); i++){
                        groupNames.add(i + ": " + particleManager.getParticleGroups().get(i).getName());
                    }
                    response += String.join("\n", groupNames);
                    sender.sendMessage(response);
                    break;
            }
             */
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {

        String arg = args.length > 0 ? args[args.length -1] : "";

        if (args.length == 1){
            return Arrays.asList("create", "delete", "addparticle", "removeparticle", "list", "info", "move", "flag").stream()
                    .filter(s -> (arg.isEmpty() || s.startsWith(arg.toLowerCase(Locale.ENGLISH))))
                    .collect(Collectors.toList());
        } else if (args.length > 1){
            switch (args[0]){
                case "create":
                    break;
                case "delete":
                    // Return list of all active emitters
                    break;
                case "addparticle":
                    // Return list of particles
                    if (args.length == 2)
                        return ParticleEffect.getEnabledEffectNames().stream()
                            .filter(s -> (arg.isEmpty() || s.startsWith(arg.toLowerCase(Locale.ENGLISH))))
                            .collect(Collectors.toList());
                case "removeparticle":
                    // Return list of all active particles
                    break;
                case "list":
                    break;
                case "info":
                    // Return list of all active emitters
                    break;
                case "move":
                    // Return list of all active emitters
                    break;
                case "flag":
                    // Return list of all active emitters
                    break;
            }
        }

        return new ArrayList<>();
    }

    private Map<String, Class<? extends DefaultParticleStyles>> getStyles(){
        Map<String, Class<? extends DefaultParticleStyles>> styles = new LinkedHashMap<>();

        styles.put("sphere", ParticleStyleSphere.class);
        styles.put("cube", ParticleStyleCube.class);

        return styles;
    }

    @Override
    public String getName() {
        return "emitter";
    }

    @Override
    public String getDescriptionKey() {
        return "Creates a new emitter";
    }

    @Override
    public String getArguments() {
        return "<effect> <style> <entity|location>";
    }

    @Override
    public boolean requiresEffectsAndStyles() {
        return true;
    }

    @Override
    public boolean canConsoleExecute() {
        return false;
    }
}
