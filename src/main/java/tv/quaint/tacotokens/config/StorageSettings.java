package tv.quaint.tacotokens.config;

import tv.quaint.tacotokens.utils.MessagingUtils;

public class StorageSettings {
    public enum Type {
        MONGO,
        STORAGE,
        ;
    }

    public Type type;

    public StorageSettings() {
        MessagingUtils.info("StorageConfiguration loaded!");
    }

    public StorageSettings setType(String unparsed) {
        switch (unparsed){
            case "mongodb", "mongo" -> {
                this.type = Type.MONGO;
            }
            default -> {
                this.type = Type.STORAGE;
            }
        }

        return this;
    }
}
