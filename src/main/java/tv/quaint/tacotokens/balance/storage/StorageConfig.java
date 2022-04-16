package tv.quaint.tacotokens.balance.storage;

import de.leonhard.storage.Config;
import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.balance.Balance;
import tv.quaint.tacotokens.utils.MainUtils;

import java.io.File;

public class StorageConfig {
    public Config config;
    public String cstring = "storage.yml";
    public File cfile = new File(TacoTokens.getDataFolder(), cstring);

    public StorageConfig() {
        reloadConfig();
    }

    public Config loadConfig() {
        return MainUtils.loadConfigNoDefault(cfile);
    }

    public void reloadConfig() {
        config = loadConfig();
    }

    public void saveBalance(Balance balance) {
        config.set("balances." + balance.belongsTo, balance.balance);
    }

    public double getBalance(String belongsTo, boolean ofPlayer) {
        reloadConfig();

        if (! config.getSection("balances").singleLayerKeySet().contains(belongsTo)) {
            saveBalance(new Balance(belongsTo, TacoTokens.CONFIG.balanceSettings.startingAmount, ofPlayer));
        }

        return config.getDouble("balances." + belongsTo);
    }
}
