package tv.quaint.tacotokens;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tv.quaint.tacotokens.commands.BalanceCommand;
import tv.quaint.tacotokens.commands.PayCommand;
import tv.quaint.tacotokens.commands.ReloadCommand;
import tv.quaint.tacotokens.commands.ShopCommand;
import tv.quaint.tacotokens.config.ConfigHandler;
import tv.quaint.tacotokens.config.StorageSettings;
import tv.quaint.tacotokens.shops.ShopHandler;
import tv.quaint.tacotokens.utils.MongoUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;

public class TacoTokens implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("TacoTokens");
    public static final String name = "taco-tokens";
    public static MinecraftServer SERVER;
    public static TacoTokens INSTANCE;
    public static ConfigHandler CONFIG;

    public static void reloadConfig() {
        CONFIG = CONFIG.handle();
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        CONFIG = new ConfigHandler();
        CONFIG = CONFIG.handle();

        ShopHandler.refreshShops();

        registerCommands();
    }

    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            new BalanceCommand().registerAs(dispatcher);
            new PayCommand().registerAs(dispatcher);

            new ShopCommand().registerAs(dispatcher);

            new ReloadCommand().register(dispatcher);
        });
    }

    public static File getWorkingDir() {
        return Paths.get("").toAbsolutePath().toFile();
    }

    public static File getDataFolder() {
        return new File(getWorkingDir(), name + File.separator);
    }

    public InputStream getResource(String fileName) {
        return this.getClass().getResourceAsStream(fileName);
    }
}
