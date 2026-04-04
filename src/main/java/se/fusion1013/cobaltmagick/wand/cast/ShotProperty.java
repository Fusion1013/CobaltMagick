package se.fusion1013.cobaltmagick.wand.cast;

import java.util.function.BiFunction;

public class ShotProperty<T> {

    public static final ShotProperty<Integer> MANA_COST = new ShotProperty<>("mana_cost", "Mana Cost", Integer::sum);
    public static final ShotProperty<Double> CAST_DELAY = new ShotProperty<>("cast_delay", "Cast Delay", Double::sum);
    public static final ShotProperty<Double> RECHARGE_TIME = new ShotProperty<>("recharge_time", "Recharge Time", Double::sum);
    public static final ShotProperty<Double> SPREAD = new ShotProperty<>("spread", "Spread", Double::sum);
    public static final ShotProperty<Double> RADIUS = new ShotProperty<>("radius", "Radius", Double::sum);
    public static final ShotProperty<Double> SPEED = new ShotProperty<>("speed", "Speed", Double::sum);
    public static final ShotProperty<Double> CRIT_CHANCE_BONUS = new ShotProperty<>("crit_chance_bonus", "Crit Chance Bonus", Double::sum);
    public static final ShotProperty<Double> DAMAGE = new ShotProperty<>("damage", "Damage", Double::sum);
    public static final ShotProperty<Boolean> HAS_PIERCING = new ShotProperty<>("piercing", "Piercing");
    public static final ShotProperty<Boolean> HAS_GRAVITY = new ShotProperty<>("gravity", "Gravity");
    public static final ShotProperty<Double> GRAVITY_MULTIPLIER = new ShotProperty<>("gravity_multiplier", "Gravity Multiplier", Double::sum);
    public static final ShotProperty<Boolean> HAS_AIR_RESISTANCE = new ShotProperty<>("air_resistance", "Air Resistance");
    public static final ShotProperty<Double> AIR_RESISTANCE_MULTIPLIER = new ShotProperty<>("air_resistance_multiplier", "Air Resistance Multiplier", Double::sum);
    public static final ShotProperty<Boolean> IS_BOUNCY = new ShotProperty<>("bouncy", "Bouncy");
    public static final ShotProperty<Integer> MAX_BOUNCES = new ShotProperty<>("max_bounces", "Max Bounces", Integer::sum);
    public static final ShotProperty<Integer> LIFESPAN = new ShotProperty<>("lifespan", "Lifespan", Integer::sum);

    public static final ShotProperty<?>[] values = new ShotProperty[]{
            MANA_COST, CAST_DELAY, RECHARGE_TIME,
            SPREAD, RADIUS, SPEED, CRIT_CHANCE_BONUS,
            DAMAGE, HAS_PIERCING, HAS_GRAVITY, GRAVITY_MULTIPLIER,
            HAS_AIR_RESISTANCE, AIR_RESISTANCE_MULTIPLIER,
            IS_BOUNCY, MAX_BOUNCES, LIFESPAN
    };

    private final String internalName;
    private final String displayName;
    private final BiFunction<T, T, T> add;

    private ShotProperty(String internalName, String displayName) {
        this(internalName, displayName, (a, b) -> b);
    }

    private ShotProperty(String internalName, String displayName, BiFunction<T, T, T> add) {
        this.internalName = internalName;
        this.displayName = displayName;
        this.add = add;
    }

    public String getDisplayName() {
        return displayName;
    }

    public T add(T a, T b) {
        if (a == null) return b;
        if (b == null) return a;
        return add.apply(a, b);
    }

    public String getInternalName() {
        return internalName;
    }

    public T cast(Object value) {
        return (T) value;
    }
}
