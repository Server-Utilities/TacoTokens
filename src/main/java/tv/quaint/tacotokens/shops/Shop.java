package tv.quaint.tacotokens.shops;

import tv.quaint.tacotokens.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Shop {
    public String identifier;
    public String name = "UNDEFINED";
    public TreeMap<Integer, String> description = new TreeMap<>();
    public List<ShopTake> takes = new ArrayList<>();
    public List<ShopGive> gives = new ArrayList<>();

    public Shop(String identifier) {
        this.identifier = identifier;
    }

    public Shop setName(String name) {
        this.name = name;

        return this;
    }

    public Shop addDescriptionLine(String line) {
        this.description.put(this.description.size(), line);

        return this;
    }

    public String getDescriptionLine(int line) {
        return this.description.get(line);
    }

    public String getDescriptionLineEasy(int line) {
        return getDescriptionLine(line - 1);
    }

    public Shop removeDescriptionLineAt(int line) {
        this.description.remove(line);

        return this;
    }

    public Shop addTake(ShopTake take) {
        takes.add(take);

        return this;
    }

    public Shop removeTake(ShopTake take) {
        takes.remove(take);

        return this;
    }

    public Shop addGive(ShopGive give) {
        gives.add(give);

        return this;
    }

    public Shop removeGive(ShopGive give) {
        gives.remove(give);

        return this;
    }

    public void register() {
        ShopHandler.registerShop(this);
    }

    public String buildListingMessage() {
        return this.name + " &8>> &dfor more info&8, &dtype&8: &b/%command_base% " + this.identifier;
    }

    public List<String> buildInfoMessage() {
        List<String> strings = new ArrayList<>();

        strings.add(this.name);

        for (int line : this.description.keySet()) {
            strings.add(this.getDescriptionLine(line));
        }

        strings.add("&eIf you would like to buy this&8, &eplease type&8: &b/%command_base% " + this.identifier + " true");

        return strings;
    }
}
