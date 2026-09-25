package me.lokspel.spawnauth.world;

import me.lokspel.spawnauth.util.ReflectionUtil;
import org.bukkit.World;

public final class LimboWorldConfigurator {
    private static final Object ADVANCE_TIME_RULE = ReflectionUtil.gameRuleOrNull("ADVANCE_TIME", "DO_DAYLIGHT_CYCLE");
    private static final Object SPAWN_PHANTOMS_RULE = ReflectionUtil.gameRuleOrNull("SPAWN_PHANTOMS", "DO_INSOMNIA");
    private static final Object ADVANCE_WEATHER_RULE = ReflectionUtil.gameRuleOrNull("ADVANCE_WEATHER", "DO_WEATHER_CYCLE");
    private static final Object SPAWN_MOBS_RULE = ReflectionUtil.gameRuleOrNull("SPAWN_MOBS", "DO_MOB_SPAWNING");
    private static final Object FALL_DAMAGE_RULE = ReflectionUtil.gameRuleOrNull("FALL_DAMAGE");
    private static final Object KEEP_INVENTORY_RULE = ReflectionUtil.gameRuleOrNull("KEEP_INVENTORY");

    private LimboWorldConfigurator() {
    }

    public static void configure(World world) {
        ReflectionUtil.setGameRule(world, ADVANCE_TIME_RULE, "doDaylightCycle", false);
        ReflectionUtil.setGameRule(world, SPAWN_PHANTOMS_RULE, "doInsomnia", false);
        ReflectionUtil.setGameRule(world, ADVANCE_WEATHER_RULE, "doWeatherCycle", false);
        ReflectionUtil.setGameRule(world, SPAWN_MOBS_RULE, "doMobSpawning", false);
        ReflectionUtil.setGameRule(world, FALL_DAMAGE_RULE, "fallDamage", false);
        ReflectionUtil.setGameRule(world, KEEP_INVENTORY_RULE, "keepInventory", true);
        world.setTime(6000L);
        world.setStorm(false);
        world.setThundering(false);
    }
}