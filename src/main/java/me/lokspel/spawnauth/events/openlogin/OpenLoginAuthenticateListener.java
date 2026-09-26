package me.lokspel.spawnauth.events.openlogin;

import com.nickuc.openlogin.bukkit.api.events.AsyncLoginEvent;
import com.nickuc.openlogin.bukkit.api.events.AsyncRegisterEvent;
import me.lokspel.spawnauth.helpers.GameHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OpenLoginAuthenticateListener implements Listener {
    private final GameHelper gameHelper;
    private final String loginMode;
    private final String registerMode;

    public OpenLoginAuthenticateListener(GameHelper gameHelper, String loginMode, String registerMode) {
        this.gameHelper = gameHelper;
        this.loginMode = loginMode;
        this.registerMode = registerMode;
    }

    @EventHandler
    private void onAsyncLogin(AsyncLoginEvent event) {
        gameHelper.releaseFromLimboAsync(event.getPlayer(), loginMode);
    }

    @EventHandler
    private void onAsyncRegister(AsyncRegisterEvent event) {
        gameHelper.releaseFromLimboAsync(event.getPlayer(), registerMode);
    }
}
