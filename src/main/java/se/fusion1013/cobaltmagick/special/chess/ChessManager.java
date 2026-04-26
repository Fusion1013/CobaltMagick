package se.fusion1013.cobaltmagick.special.chess;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static se.fusion1013.cobaltmagick.special.chess.ChessPieceUtil.*;

public class ChessManager extends Manager<CobaltMagick> implements Listener {

    public void setupBoard(Location location) {
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                Location currentLocation = location.clone().add(x, 0, z);
                int zMod = z % 2;
                int type = (x + zMod) % 2;

                // Set board blocks
                if (type == 0) {
                    currentLocation.getBlock().setBlockData(Material.BLACK_CONCRETE.createBlockData());
                    currentLocation.clone().subtract(0, 1, 0).getBlock().setBlockData(Material.STRUCTURE_BLOCK.createBlockData());
                } else {
                    currentLocation.getBlock().setBlockData(Material.WHITE_CONCRETE.createBlockData());
                    currentLocation.clone().subtract(0, 1, 0).getBlock().setBlockData(Material.STRUCTURE_BLOCK.createBlockData());
                }

                // Kill pieces
                ChessPieceState chessPiece = getChessPiece(currentLocation.clone().add(0, 1, 0));
                if (chessPiece != null) chessPiece.remove();
            }
        }

        Location pieceLocation = location.clone().add(0, 1, 0);

        spawnPiece(pieceLocation, ChessPieceType.Rook, ChessPieceColor.White);
        spawnPiece(pieceLocation.clone().add(0, 0, 1), ChessPieceType.Knight, ChessPieceColor.White);
        spawnPiece(pieceLocation.clone().add(0, 0, 2), ChessPieceType.Bishop, ChessPieceColor.White);
        spawnPiece(pieceLocation.clone().add(0, 0, 3), ChessPieceType.Queen, ChessPieceColor.White);
        spawnPiece(pieceLocation.clone().add(0, 0, 4), ChessPieceType.King, ChessPieceColor.White);
        spawnPiece(pieceLocation.clone().add(0, 0, 5), ChessPieceType.Bishop, ChessPieceColor.White);
        spawnPiece(pieceLocation.clone().add(0, 0, 6), ChessPieceType.Knight, ChessPieceColor.White);
        spawnPiece(pieceLocation.clone().add(0, 0, 7), ChessPieceType.Rook, ChessPieceColor.White);

        for (int i = 0; i < 8; i++) {
            spawnPiece(pieceLocation.clone().add(1, 0, i), ChessPieceType.Pawn, ChessPieceColor.White);
        }
    }

    public void spawnPiece(Location location, ChessPieceType type, ChessPieceColor color) {
        Location spawnLocation = location.toCenterLocation().subtract(0, 0.5, 0);
        World world = location.getWorld();

        world.spawn(spawnLocation, ItemDisplay.class, (entity) -> {
            ItemStack itemStack = getItemStack(type, color);
            entity.setItemStack(itemStack);
            entity.setTransformation(new Transformation(new Vector3f(0, 0.5f, 0), new AxisAngle4f(0, 0, 0, 0), new Vector3f(1f, 1f, 1f), new AxisAngle4f(0, 0, 0, 0)));
            entity.setTeleportDuration(10);
            PersistentDataContainer persistent = entity.getPersistentDataContainer();
            persistent.set(CHESS_PIECE_KEY, PersistentDataType.STRING, type.getTypeString());
            persistent.set(CHESS_PIECE_COLOR_KEY, PersistentDataType.STRING, color.getColorString());
            persistent.set(type.getKey(), PersistentDataType.BOOLEAN, true);
        });
        world.spawn(spawnLocation, Interaction.class, (entity) -> {
            entity.setInteractionHeight(type.getHeight());
            entity.setInteractionWidth(0.5f);
            entity.setResponsive(true);
            PersistentDataContainer persistent = entity.getPersistentDataContainer();
            persistent.set(CHESS_PIECE_KEY, PersistentDataType.STRING, type.getTypeString());
            persistent.set(CHESS_PIECE_COLOR_KEY, PersistentDataType.STRING, color.getColorString());
            persistent.set(type.getKey(), PersistentDataType.BOOLEAN, true);
        });
    }

    public ItemStack getItemStack(ChessPieceType type, ChessPieceColor color) {
        String colorString = color.getColorString();
        String typeString = type.getTypeString();

        String itemModel = "chess/chess_" + colorString + "_" + typeString;
        ItemStack itemStack = new ItemStack(Material.CLOCK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setItemModel(new NamespacedKey("thegreatwork", itemModel));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @EventHandler
    public void onInteraction(EntityDamageByEntityEvent event) {
        Entity hitEntity = event.getEntity();
        Location location = hitEntity.getLocation();
        World world = location.getWorld();

        PersistentDataContainer persistent = hitEntity.getPersistentDataContainer();
        tryMoveChessPiece(persistent, location, world, hitEntity);
        tryCompleteChessMove(persistent, location, world, hitEntity);
    }

    private void tryCompleteChessMove(PersistentDataContainer persistent, Location location, World world, Entity entity) {
        String uuid = persistent.getOrDefault(CHESS_TARGET_KEY, PersistentDataType.STRING, "NONE");
        if (uuid.equalsIgnoreCase("NONE")) return;

        UUID targetEntityUUID = UUID.fromString(uuid);
        Entity chessEntity = world.getEntity(targetEntityUUID);
        CobaltMagick.getInstance().getLogger().info("Chess Entity: " + chessEntity);
        if (chessEntity == null) return;

        ChessPieceState state = getChessPiece(chessEntity.getLocation());
        CobaltMagick.getInstance().getLogger().info("State: " + state);
        if (state == null) return;

        Collection<Entity> entities = world.getNearbyEntities(location, 8, 1, 8, e -> e.getPersistentDataContainer().has(CHESS_TARGET_KEY));
        entities.forEach(Entity::remove);

        state.teleport(location);
    }

    private void tryMoveChessPiece(PersistentDataContainer persistent, Location location, World world, Entity entity) {
        String typeString = persistent.getOrDefault(CHESS_PIECE_KEY, PersistentDataType.STRING, "NONE");
        if (typeString.equalsIgnoreCase("NONE")) return;

        ChessPieceType type = EnumUtils.findEnumInsensitiveCase(ChessPieceType.class, typeString);
        List<Vector> offsets = type.getOffsets();

        for (Vector offset : offsets) {
            trySpawnChessMoveMarker(location, world, entity, offset);
        }
    }

    private static void trySpawnChessMoveMarker(Location location, World world, Entity entity, Vector offset) {
        Location spawnLocation = location.toCenterLocation().clone().add(offset);

        Location validateBlockLocation = spawnLocation.clone().subtract(0, 2, 0);
        Block block = validateBlockLocation.getBlock();
        if (block.getType() != Material.STRUCTURE_BLOCK) return;

        ChessPieceState chessPiece = getChessPiece(spawnLocation);
        if (chessPiece != null) return;

        world.spawn(spawnLocation, BlockDisplay.class, e -> {
            e.setBlock(Material.RED_STAINED_GLASS.createBlockData());
            e.setGlowing(true);
            e.setTransformation(new Transformation(new Vector3f(-0.25f, -0.25f, -0.25f), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f(0, 0, 0, 0)));

            e.getPersistentDataContainer().set(CHESS_TARGET_KEY, PersistentDataType.STRING, entity.getUniqueId().toString());
        });
        world.spawn(spawnLocation.subtract(0, 0.25, 0), Interaction.class, e -> {
            e.getPersistentDataContainer().set(CHESS_TARGET_KEY, PersistentDataType.STRING, entity.getUniqueId().toString());
            e.setInteractionWidth(0.5f);
            e.setInteractionHeight(0.5f);
            e.setResponsive(true);
        });
    }

    public ChessManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }

    public static ChessPieceState getChessPiece(Location location) {
        Location chessPieceLocation = location.toCenterLocation().subtract(0, 0.5, 0);

        ItemDisplay display = ChessPieceUtil.getNearbyChessDisplay(chessPieceLocation);
        Interaction interaction = ChessPieceUtil.getNearbyChessInteraction(chessPieceLocation);

        CobaltMagick.getInstance().getLogger().info("Display: " + display);
        CobaltMagick.getInstance().getLogger().info("Interaction: " + interaction);

        if (display == null || interaction == null) return null;

        PersistentDataContainer persistent = display.getPersistentDataContainer();
        ChessPieceType type = ChessPieceUtil.getType(persistent);
        ChessPieceColor color = ChessPieceUtil.getColor(persistent);

        CobaltMagick.getInstance().getLogger().info("Type: " + type);
        CobaltMagick.getInstance().getLogger().info("Color: " + color);

        if (type == null || color == null) return null;

        return new ChessPieceState(chessPieceLocation, display, interaction, type, color);
    }


    private static ChessManager INSTANCE;

    public static ChessManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChessManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
