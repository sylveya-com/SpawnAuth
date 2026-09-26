package me.lokspel.spawnauth.events.loginsecurity;

import com.lenis0012.bukkit.loginsecurity.events.AuthActionEvent;
import com.lenis0012.bukkit.loginsecurity.session.AuthActionType;
import me.lokspel.spawnauth.helpers.GameHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LoginSecurityLoginListener implements Listener {
    private final GameHelper gameHelper;
    private final String loginMode;
    private final String registerMode;

    public LoginSecurityLoginListener(GameHelper gameHelper, String loginMode, String registerMode) {
        this.gameHelper = gameHelper;
        this.loginMode = loginMode;
        this.registerMode = registerMode;
    }

    @EventHandler
    private void onLogin(AuthActionEvent event) {
        if (event.getType() != AuthActionType.LOGIN) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        gameHelper.releaseFromLimbo(player, loginMode);
    }

    @EventHandler
    private void onRegister(AuthActionEvent event) {
        if (event.getType() != AuthActionType.REGISTER) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        gameHelper.releaseFromLimbo(player, registerMode);
    }
}
