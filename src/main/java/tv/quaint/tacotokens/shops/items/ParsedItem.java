package tv.quaint.tacotokens.shops.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import tv.quaint.tacotokens.utils.ItemUtils;
import tv.quaint.tacotokens.utils.obj.SingleSet;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ParsedItem {
    public Item material;
    public int amount;
    public String name;
    public List<String> lore;
    public TreeMap<String, String> tags;
    public TreeMap<String, Integer> enchants;

    public ParsedItem() {
        this.material = Items.AIR;
        this.amount = 1;
        this.name = "";
        this.lore = new ArrayList<>();
        this.tags = new TreeMap<>();
        this.enchants = new TreeMap<>();
    }

    public ParsedItem(Item material, int amount, String name, List<String> lore, TreeMap<String, String> tags, TreeMap<String, Integer> enchants) {
        this.material = material;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.tags = tags;
        this.enchants = enchants;
    }

    public ParsedItem setMaterial(Item material) {
        this.material = material;

        return this;
    }

    public ParsedItem setAmount(int amount) {
        this.amount = amount;

        return this;
    }

    public ParsedItem setName(String name) {
        this.name = name;

        return this;
    }

    public ParsedItem setLore(List<String> lore) {
        this.lore = lore;

        return this;
    }

    public ParsedItem addLore(String string) {
        this.lore.add(string);

        return this;
    }

    public ParsedItem setTags(TreeMap<String, String> tags) {
        this.tags = tags;

        return this;
    }

    public ParsedItem addTag(SingleSet<String, String> set) {
        tags.put(set.key, set.value);

        return this;
    }

    public ParsedItem removeTag(String key) {
        tags.remove(key);

        return this;
    }

    public ParsedItem addEnchant(SingleSet<String, Integer> enchants) {
        this.enchants.put(enchants.key, enchants.value);

        return this;
    }

    public ItemStack asStack() {
        ItemStack stack = ItemUtils.newItem(this.material, this.amount);
        if (! this.name.equals("")) {
            ItemUtils.setName(stack, this.name);
        }
        if (this.lore.size() > 0) {
            ItemUtils.setLore(stack, this.lore);
        }
        if (this.tags.size() > 0) {
            ItemUtils.putStringedTags(stack, tags);
        }
        if (this.enchants.size() > 0) {
            ItemUtils.putEnchantments(stack, this.enchants);
        }

        return stack;
    }
}
