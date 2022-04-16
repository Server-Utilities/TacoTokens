package tv.quaint.tacotokens.config;

import de.leonhard.storage.sections.FlatFileSection;

public class ConfiguredMongo {
    public String uri;
    public String name;
    public String host;
    public String port;
    public String user;
    public String pass;

    public ConfiguredMongo() {
        this.uri = "mongodb://%user%:%pass%@%host%:%port%/%name%";
        this.name = "";
        this.host = "";
        this.port = "";
        this.user = "";
        this.pass = "";
    }

    public ConfiguredMongo parseFromConfigSection(FlatFileSection section) {
        this.setDBInfo(section.getString("name"), section.getString("host"), section.getString("port"));
        this.setUserInfo(section.getString("user"), section.getString("pass"));
        this.setUri(section.getString("uri"));

        return this;
    }

    public ConfiguredMongo setDBInfo(String name, String host, String port) {
        this.name = name;
        this.host = host;
        this.port = port;

        return this;
    }

    public ConfiguredMongo setUserInfo(String user, String pass) {
        this.user = user;
        this.pass = pass;

        return this;
    }

    public ConfiguredMongo setUri(String uri) {
        this.uri = uri;

        return this;
    }

    public String getParsedUri() {
        return this.uri
                .replace("%user%", this.user)
                .replace("%pass%", this.pass)
                .replace("%host%", this.host)
                .replace("%port%", this.port)
                .replace("%name%", this.name)
                ;
    }
}
