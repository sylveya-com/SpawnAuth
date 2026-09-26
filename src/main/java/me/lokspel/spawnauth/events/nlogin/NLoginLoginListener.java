package me.lokspel.spawnauth.events.nlogin;

import com.nickuc.login.api.event.bukkit.auth.LoginEvent;
import com.nickuc.login.api.event.bukkit.auth.RegisterEvent;
import me.lokspel.spawnauth.helpers.GameHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NLoginLoginListener implements Listener {
    private final GameHelper gameHelper;
    private final String loginMode;
    private final String registerMode;

    public NLoginLoginListener(GameHelper gameHelper, String loginMode, String registerMode) {
        this.gameHelper = gameHelper;
        this.loginMode = loginMode;
        this.registerMode = registerMode;
    }

    @EventHandler
    private void onPlayerLogin(LoginEvent event) {
        gameHelper.releaseFromLimbo(event.getPlayer(), loginMode);
    }

    @EventHandler
    private void onPlayerRegister(RegisterEvent event) {
        gameHelper.releaseFromLimbo(event.getPlayer(), registerMode);
    }
}
