package me.lokspel.spawnauth.helpers;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.common.players.Sessions;
import com.lenis0012.bukkit.loginsecurity.LoginSecurity;
import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.api.types.Identity;
import com.nickuc.openlogin.bukkit.OpenLoginBukkit;
import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.Predicate;

public final class AuthHelper {

    private static Predicate<Player> authCheck = player -> false;
    private static Predicate<Player> registeredCheck = player -> false;

    private AuthHelper() {
    }

    public static void init(String provider) {
        switch (provider) {
            case "nLogin" -> {
                authCheck = player ->
                        nLoginAPI.getApi().isAuthenticated(player.getName());

                registeredCheck = player ->
                        nLoginAPI.getApi()
                                .getAccount(Identity.ofKnownName(player.getName()))
                                .isPresent();
            }

            case "OpenLogin" -> {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("OpeNLogin");

                if (plugin instanceof OpenLoginBukkit openLogin) {
                    authCheck = player ->
                            openLogin.getLoginManagement()
                                    .isAuthenticated(player.getName());
                }

                registeredCheck = player ->
                        OpenLoginBukkit.getApi().isRegistered(player.getName());
            }

            case "LoginSecurity" -> {
                authCheck = player -> {
                    var session = LoginSecurity.getSessionManager()
                            .getPlayerSession(player);

                    return session != null && session.isLoggedIn();
                };

                registeredCheck = player -> {
                    var session = LoginSecurity.getSessionManager()
                            .getPlayerSession(player);

                    return session != null && session.isRegistered();
                };
            }

            case "AuthMe" -> {
                authCheck = player ->
                        AuthMeApi.getInstance().isAuthenticated(player);

                registeredCheck = player ->
                        AuthMeApi.getInstance().isRegistered(player.getName());
            }

            case "LoginTo" -> {
                authCheck = player ->
                        Sessions.isPlayerLogged(player.getUniqueId());

                registeredCheck = player -> {
                    var database = LoginTo.getDatabase();
                    return database != null
                            && database.databaseContainsPlayer(player.getUniqueId());
                };
            }
        }
    }

    public static boolean isAuthenticated(Player player) {
        return player != null && authCheck.test(player);
    }

    public static boolean isRegistered(Player player) {
        return player != null && registeredCheck.test(player);
    }
}