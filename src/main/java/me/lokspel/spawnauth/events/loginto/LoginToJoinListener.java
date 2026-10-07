package me.lokspel.spawnauth.events.loginto;

import com.github.yager400.loginto.common.players.Sessions;
import me.lokspel.spawnauth.SpawnAuth;
import me.lokspel.spawnauth.helpers.AuthHelper;
import me.lokspel.spawnauth.helpers.GameHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginToJoinListener implements Listener {
    // LoginTo marks the player as logged 5 ticks after it accepts a session
    private static final long SESSION_WAIT_TICKS = 10L;

    private final SpawnAuth plugin;
    private final GameHelper gameHelper;
    private final String loginMode;
    private final String registerMode;

    public LoginToJoinListener(SpawnAuth plugin, GameHelper gameHelper, String loginMode, String registerMode) {
        this.plugin = plugin;
        this.gameHelper = gameHelper;
        this.loginMode = loginMode;
        this.registerMode = registerMode;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getFoliaLib().getScheduler().runAtEntityLater(player, unused -> checkSession(player), SESSION_WAIT_TICKS);
    }

    private void checkSession(Player player) {
        if (!player.isOnline() || !Sessions.isPlayerLogged(player.getUniqueId())) {
            return;
        }

        String mode = AuthHelper.isRegistered(player) ? loginMode : registerMode;
        gameHelper.releaseFromLimbo(player, mode);
    }
}
