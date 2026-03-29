package se.fusion1013.cobaltmagick.special.well;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.util.ContainerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WellManager extends Manager<CobaltMagick> {

    private static final FileLoadedRegistry<IWellRewardGroup> WELL_REWARD_GROUP = new FileLoadedRegistry<>(
            CobaltMagick.getInstance(),
            "well_reward_groups",
            WellRewardGroup::new,
            WellRewardGroup::new,
            (p, w) -> {
            }
    );
    private static final FileLoadedRegistry<IWellReward> WELL_REWARDS = new FileLoadedRegistry<>(
            CobaltMagick.getInstance(),
            "well_rewards",
            WellReward::new,
            WellReward::new,
            (p, w) -> {
            }
    );
    private static final Map<Location, Integer> WELL_TIMERS = new HashMap<>();

    public void tickWell(Location hopperLocation, Location centerLocation, Location rewardLocation, Location doorLocation, String rewardGroupName) {
        int contentAmount = ContainerUtil.getContentAmount(hopperLocation);
        if (contentAmount <= 5) return;

        int currentTime = WELL_TIMERS.computeIfAbsent(hopperLocation, k -> 0);

        if (currentTime == 0) {
            centerLocation.getWorld().playSound(centerLocation, Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
        }

        WellEffectUtil.display(centerLocation.toCenterLocation(), currentTime, 4);

        if (currentTime < 100) {
            WELL_TIMERS.put(hopperLocation, currentTime + 1);
            return;
        }

        List<IWellReward> rewards = getWellRewards(rewardGroupName);

        triggerWell(contentAmount - 5, centerLocation, rewardLocation, doorLocation, rewards);
        ContainerUtil.setAllItemsToAmount(hopperLocation, 1);
        WELL_TIMERS.remove(hopperLocation);
    }

    private List<IWellReward> getWellRewards(String rewardGroupName) {
        List<IWellReward> rewards = new ArrayList<>();
        IWellRewardGroup group = WELL_REWARD_GROUP.get(rewardGroupName);
        if (group == null) return rewards;

        for (String rewardName : group.getWellRewards()) {
            rewards.add(WELL_REWARDS.get(rewardName));
        }

        return rewards;
    }

    private void triggerWell(int contentAmount, Location centerLocation, Location rewardLocation, Location doorLocation, List<IWellReward> rewards) {
        World world = centerLocation.getWorld();

        List<Vector> vectors = ShapeUtils.generateCircle(3, 16, true);
        for (Vector vector : vectors) {
            world.spawnParticle(Particle.END_ROD, centerLocation.toCenterLocation().clone().add(vector), 1, 0, 0, 0, 0);
        }
        IWellReward result = getGreatest(contentAmount, rewards);
        if (result == null) return;

        Map<String, Object> context = new HashMap<>();
        context.put("center_location", centerLocation.toCenterLocation());
        context.put("center_top_location", centerLocation.toCenterLocation().clone().add(new Vector(0, 3, 0)));
        context.put("reward_location", rewardLocation.toCenterLocation());
        context.put("door_location", doorLocation.toCenterLocation());
        result.trigger(context);
    }

    public WellManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        WELL_REWARDS.reload();
        WELL_REWARD_GROUP.reload();
    }

    @Override
    public void disable() {

    }

    public IWellReward getGreatest(int limit, List<IWellReward> rewards) {
        IWellReward closestFound = null;
        int closestAmount = 0;
        for (IWellReward wellResult : rewards) {
            if (wellResult.getCoinAmount() > closestAmount && wellResult.getCoinAmount() <= limit) {
                closestFound = wellResult;
                closestAmount = wellResult.getCoinAmount();
            }
        }
        return closestFound;
    }

    public static String[] getRewardGroupNames() {
        return WELL_REWARD_GROUP.getNames();
    }

    private static WellManager INSTANCE;

    public static WellManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WellManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
