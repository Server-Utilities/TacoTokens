package tv.quaint.tacotokens.config;

import de.leonhard.storage.Config;
import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.balance.storage.StorageConfig;
import tv.quaint.tacotokens.utils.MainUtils;
import tv.quaint.tacotokens.utils.MongoUtils;

import java.io.File;

public class ConfigHandler {
    public Config config;
    public String assetsString = "assets" + File.separator;
    public String cstring = "config.yml";
    public File cfile = new File(TacoTokens.getDataFolder(), cstring);
    public StorageSettings storageSettings;
    public BalanceSettings balanceSettings;

    public ConfiguredMongo configuredMongo;

    public StorageConfig storageConfig;

    public ConfigHandler() {
        reloadConfig();
    }

    public Config loadConfig() {
        return MainUtils.loadConfigFromSelf(cfile, assetsString + cstring);
    }

    public void reloadConfig() {
        config = loadConfig();
    }

    public ConfigHandler handle() {
        reloadConfig();

        storageSettings = new StorageSettings().setType(config.getString("storage.type"));

        if (storageSettings.type.equals(StorageSettings.Type.MONGO)) {
            configuredMongo = new ConfiguredMongo();
            configuredMongo = configuredMongo.parseFromConfigSection(config.getSection("storage.database"));
        }
        if (storageSettings.type.equals(StorageSettings.Type.STORAGE)) {
            storageConfig = new StorageConfig();
        }

        balanceSettings = new BalanceSettings().setStartingAmount(config.getDouble("balance.starting-amount"));
        balanceSettings = balanceSettings.setPayingMinAmount(config.getDouble("balance.paying.min"));
        balanceSettings = balanceSettings.setPayingMaxAmount(config.getDouble("balance.paying.max"));

        if (storageSettings.type.equals(StorageSettings.Type.MONGO)) {
            MongoUtils.database = MongoUtils.loadDatabase();
        }

        return this;
    }
}
