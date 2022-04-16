package tv.quaint.tacotokens.balance;

import com.mongodb.client.MongoCollection;
import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.balance.storage.StorageConfig;
import tv.quaint.tacotokens.config.StorageSettings;
import tv.quaint.tacotokens.utils.MongoUtils;

public class BalanceManager {
    public static Balance getBalance(String of, boolean ofPlayer) {
        StorageSettings storageSettings = TacoTokens.CONFIG.storageSettings;
        if (storageSettings.type.equals(StorageSettings.Type.MONGO)) {
            return new Balance(of, MongoUtils.getBalance(of, ofPlayer), ofPlayer);
        }
        if (storageSettings.type.equals(StorageSettings.Type.STORAGE)) {
            return new Balance(of, TacoTokens.CONFIG.storageConfig.getBalance(of, ofPlayer), ofPlayer);
        }

        return new Balance(of, 0d, ofPlayer);
    }

    public static void saveBalance(Balance balance) {
        StorageSettings storageSettings = TacoTokens.CONFIG.storageSettings;
        if (storageSettings.type.equals(StorageSettings.Type.MONGO)) {
            MongoUtils.saveBalance(balance);
        }
        if (storageSettings.type.equals(StorageSettings.Type.STORAGE)) {
            TacoTokens.CONFIG.storageConfig.saveBalance(balance);
        }
    }
}
