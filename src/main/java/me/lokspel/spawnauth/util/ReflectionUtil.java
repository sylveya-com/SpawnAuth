package me.lokspel.spawnauth.util;

import me.lokspel.spawnauth.helpers.LogHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public final class ReflectionUtil {

    private static final String MODERN_GAME_RULES = "org.bukkit.GameRules";
    private static final String LEGACY_GAME_RULE = "org.bukkit.GameRule";

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = Map.of(
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            char.class, Character.class,
            double.class, Double.class,
            float.class, Float.class,
            int.class, Integer.class,
            long.class, Long.class,
            short.class, Short.class
    );

    private ReflectionUtil() {
    }

    public static Object gameRuleOrNull(String name, String... legacyNames) {
        Object rule = staticFieldOrNull(MODERN_GAME_RULES, name);

        if (rule != null) {
            return rule;
        }

        for (String legacyName : legacyNames) {
            rule = staticFieldOrNull(LEGACY_GAME_RULE, legacyName);

            if (rule != null) {
                return rule;
            }
        }

        return null;
    }

    public static void setGameRule(
            World world,
            Object rule,
            String name,
            boolean value
    ) {
        if (rule != null && invoke(world, "setGameRule", rule, value) != null) {
            return;
        }

        invoke(world, "setGameRule", name, String.valueOf(value));
        invoke(world, "setGameRuleValue", name, String.valueOf(value));
    }

    public static int getIntGameRule(
            World world,
            Object rule,
            String name,
            int fallback
    ) {
        if (rule != null) {
            Object value = invoke(world, "getGameRuleValue", rule);

            if (value instanceof Number number) {
                return number.intValue();
            }
        }

        Object value = invoke(world, "getGameRuleValue", name);

        if (value == null) {
            return fallback;
        }

        String text = value.toString();

        if (text.isBlank()) {
            return fallback;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            LogHelper.LOGGER.warning(() ->
                    "Unparseable game rule value '" + text
                            + "' for " + name
                            + " in world '" + world.getName() + "'"
            );

            return fallback;
        }
    }

    public static boolean isPrimaryThread() {
        Object result = invoke(Bukkit.getServer(), "isPrimaryThread");

        return result instanceof Boolean
                ? (Boolean) result
                : true;
    }

    public static void setCollidable(Player player, boolean value) {
        invoke(player, "setCollidable", value);
    }

    public static void setSpawnLocation(World world, Location location) {
        invoke(world, "setSpawnLocation", location);
        invoke(world, "setSpawnLocation", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Class<?> classOrNull(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException exception) {
            return null;
        }
    }

    public static Object staticFieldOrNull(
            String className,
            String fieldName
    ) {
        Class<?> type = classOrNull(className);

        if (type == null) {
            return null;
        }

        return staticFieldOrNull(type, fieldName);
    }

    public static Object staticFieldOrNull(
            Class<?> type,
            String fieldName
    ) {
        try {
            Field field = type.getField(fieldName);
            return field.get(null);
        } catch (NoSuchFieldException exception) {
            return null;
        } catch (IllegalAccessException | SecurityException exception) {
            LogHelper.LOGGER.warning(() ->
                    "Failed to read field '"
                            + type.getName()
                            + "."
                            + fieldName
                            + "': "
                            + exception
            );

            return null;
        }
    }

    private static Object invoke(
            Object target,
            String name,
            Object... args
    ) {
        Method method = findMethod(target.getClass(), name, args);

        if (method == null) {
            return null;
        }

        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalAccessException exception) {
            LogHelper.LOGGER.warning(() ->
                    "Failed to access method "
                            + target.getClass().getName()
                            + "."
                            + method.getName()
                            + ": "
                            + exception
            );
        } catch (InvocationTargetException exception) {
            LogHelper.LOGGER.warning(() ->
                    "Method "
                            + target.getClass().getName()
                            + "."
                            + method.getName()
                            + " threw "
                            + exception.getCause()
            );
        }

        return null;
    }

    private static Method findMethod(
            Class<?> type,
            String name,
            Object... args
    ) {
        for (Method method : type.getMethods()) {
            if (!method.getName().equals(name)) {
                continue;
            }

            Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length != args.length) {
                continue;
            }

            boolean matches = true;

            for (int i = 0; i < parameters.length; i++) {
                if (!isAssignable(parameters[i], args[i])) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                return method;
            }
        }

        return null;
    }

    private static boolean isAssignable(
            Class<?> parameter,
            Object argument
    ) {
        if (argument == null) {
            return !parameter.isPrimitive();
        }

        if (!parameter.isPrimitive()) {
            return parameter.isAssignableFrom(argument.getClass());
        }

        Class<?> wrapper = PRIMITIVE_WRAPPERS.get(parameter);

        return wrapper != null && wrapper.isInstance(argument);
    }
}