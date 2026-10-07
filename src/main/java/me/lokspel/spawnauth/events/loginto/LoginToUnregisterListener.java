package me.lokspel.spawnauth.events.loginto;

import com.github.yager400.loginto.api.LoginTo;
import com.github.yager400.loginto.api.events.PlayerAccountDeletionEvent;
import me.lokspel.spawnauth.helpers.SaveHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class LoginToUnregisterListener {

    private LoginToUnregisterListener() {
    }

    public static void register(SaveHelper saveHelper) {
        LoginTo.getEventManager().register(PlayerAccountDeletionEvent.class,
                event -> save(saveHelper, event.getPlayerUUID()));
    }

    private static void save(SaveHelper saveHelper, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !saveHelper.usePersistence(player)) {
            return;
        }

        saveHelper.saveLocation(player.getName(), player.getLocation());
    }
}