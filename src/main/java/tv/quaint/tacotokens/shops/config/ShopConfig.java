package tv.quaint.tacotokens.shops.config;

import de.leonhard.storage.Config;
import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.shops.*;
import tv.quaint.tacotokens.utils.MainUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShopConfig {
    public Config config;
    public File cfile;

    public ShopConfig(File file) {
        cfile = file;
        reloadConfig();
    }

    public Config loadConfig() {
        return MainUtils.loadConfigNoDefault(cfile);
    }

    public void reloadConfig() {
        config = loadConfig();
    }

    public void registerThisAsShop() {
        String identifier = getIdentifier();
        String name = getName();
        List<ShopTake> takes = getShopTakes();
        List<ShopGive> gives = getShopGives();
        List<String> description = getDescription();

        Shop shop = new Shop(identifier);
        shop = shop.setName(name);

        for (String line : description) {
            shop.addDescriptionLine(line);
        }

        for (ShopTake take : takes) {
            shop = shop.addTake(take);
        }
        for (ShopGive give : gives) {
            shop = shop.addGive(give);
        }

        shop.register();
    }

    public String getIdentifier() {
        reloadConfig();

        return config.getString("identifier");
    }

    public String getName() {
        reloadConfig();

        return config.getString("name");
    }

    public List<String> getDescription() {
        reloadConfig();

        return config.getStringList("description");
    }

    public List<ShopTake> getShopTakes() {
        reloadConfig();

        List<ShopTake> takes = new ArrayList<>();

        for (String key : config.singleLayerKeySet("takes")) {
            TakeType type = config.getEnum("takes." + key + ".type", TakeType.class);
            String value = config.getString("takes." + key + ".value");

            ShopTake shopTake = new ShopTake(type, value);

            takes.add(shopTake);
        }

        return takes;
    }

    public List<ShopGive> getShopGives() {
        reloadConfig();

        List<ShopGive> gives = new ArrayList<>();

        for (String key : config.singleLayerKeySet("gives")) {
            GiveType type = config.getEnum("gives." + key + ".type", GiveType.class);
            String value = config.getString("gives." + key + ".value");

            ShopGive shopTake = new ShopGive(type, value);

            gives.add(shopTake);
        }

        return gives;
    }
}
