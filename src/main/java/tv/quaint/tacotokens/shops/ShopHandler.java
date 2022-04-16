package tv.quaint.tacotokens.shops;

import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.shops.config.ShopConfig;
import tv.quaint.tacotokens.utils.MessagingUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShopHandler {
    public static List<Shop> shops = new ArrayList<>();
    public static File shopFolder = new File(TacoTokens.getDataFolder(), "shops" + File.separator);

    public static boolean isRegistered(String identifier) {
        for (Shop shop : shops) {
            if (shop.identifier.equals(identifier)) return true;
        }

        return false;
    }

    public static boolean isRegistered(Shop shop) {
        return isRegistered(shop.identifier);
    }

    public static void registerShop(Shop shop) {
        if (isRegistered(shop)) return;

        shops.add(shop);

        MessagingUtils.info("Registered new shop with identifier '" + shop.identifier + "'!");
    }

    public static void unregisterShop(String identifier) {
        if (! isRegistered(identifier)) return;

        shops.removeIf(shop -> shop.identifier.equals(identifier));
    }

    public static Shop getShop(String identifier) {
        for (Shop shop : shops) {
            if (shop.identifier.equals(identifier)) return shop;
        }

        return null;
    }

    public static void flushShops() {
        shops = new ArrayList<>();
    }

    public static void refreshShops() {
        flushShops();

        for (File file : getShopFiles()) {
            ShopConfig config = new ShopConfig(file);
            config.registerThisAsShop();
        }
    }

    public static List<File> getShopFiles() {
        if (! shopFolder.exists()) {
            shopFolder.mkdirs();
        }

        File[] files = shopFolder.listFiles();
        List<File> toReturn = new ArrayList<>();

        for (File file : files) {
            if (! file.getName().endsWith(".yml")) continue;

            toReturn.add(file);
        }

        return toReturn;
    }

    public static List<String> getShopsAsStrings() {
        List<String> strings = new ArrayList<>();

        for (Shop shop : shops) {
            strings.add(shop.identifier);
        }

        return strings;
    }
}
