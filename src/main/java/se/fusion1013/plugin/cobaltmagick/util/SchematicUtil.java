package se.fusion1013.plugin.cobaltmagick.util;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.NBTSchematicReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.eventbus.EventBus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.FileUtil;
import org.bukkit.util.Vector;
import org.flywaydb.core.internal.util.FileCopyUtils;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.io.*;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class SchematicUtil {

    public static void pasteSchematic(String name, Location location){
        Clipboard clipboard = loadSchematic(name);
        World world = location.getWorld();
        BukkitWorld bWorld = new BukkitWorld(world);
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(bWorld, -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e){
            e.printStackTrace();
        }
    }

    // DO NOT TOUCH THIS FOR THE LOVE OF GOD IT FINALLY WORKS
    // IF YOU HAVE THE SAME ISSUE LOADING SCHEMATICS AGAIN, MOVE FILTERED RESOURCES TO SEPARATE FOLDER
    public static Clipboard loadSchematic(String name){
        if (!name.endsWith(".schem")) name += ".schem";

        String path = "/schematics/";

        InputStream stream = CobaltMagick.getInstance().getClass().getResourceAsStream(path + name);
        ClipboardFormat format = ClipboardFormats.findByAlias("schem");
        try (ClipboardReader reader = format.getReader(stream)){
            Clipboard clipboard = reader.read();
            return clipboard;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
