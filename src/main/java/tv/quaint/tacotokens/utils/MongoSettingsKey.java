package tv.quaint.tacotokens.utils;

public enum MongoSettingsKey {
    UUID("uuid"),
    BALANCE("balance"),
    ;

    public String string;

    MongoSettingsKey(String string) {
        this.string = string;
    }
}
