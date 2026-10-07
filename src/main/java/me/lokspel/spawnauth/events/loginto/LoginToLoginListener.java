package me.lokspel.spawnauth.events.loginto;

import com.github.yager400.loginto.api.LoginTo;
import com.github.yager400.loginto.api.events.PlayerLoginEvent;
import com.github.yager400.loginto.api.events.PlayerRegistrationEvent;
import com.github.yager400.loginto.api.utils.EventManager;
import me.lokspel.spawnauth.helpers.GameHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class LoginToLoginListener {

    private LoginToLoginListener() {
    }

    public static void register(GameHelper gameHelper, String loginMode, String registerMode) {
        EventManager eventManager = LoginTo.getEventManager();

        eventManager.register(PlayerLoginEvent.class,
                event -> release(gameHelper, event.getPlayerUUID(), loginMode));

        eventManager.register(PlayerRegistrationEvent.class,
                event -> release(gameHelper, event.getPlayerUUID(), registerMode));
    }

    private static void release(GameHelper gameHelper, UUID playerUUID, String mode) {
        Player player = Bukkit.getPlayer(playerUUID);

        if (player != null) {
            gameHelper.releaseFromLimbo(player, mode);
        }
    }
}