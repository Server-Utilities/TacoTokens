package tv.quaint.tacotokens.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import tv.quaint.tacotokens.shops.items.ParsedItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ItemUtils {
    public static ItemStack newItem(Item material, int amount) {
        return new ItemStack(material, amount);
    }

    public static ItemStack setName(ItemStack stack, String name) {
        stack.setCustomName(TextUtils.newText(name));
        return stack;
    }

    public static ItemStack setLore(ItemStack stack, List<String> lore) {
        NbtCompound l = stack.getOrCreateNbt();
        NbtCompound tag = l.getCompound("display");
        NbtList list = tag.getList("Lore", NbtString.STRING_TYPE);
        list.clear();

        for (String st : lore) {
            list.add(NbtString.of(st));
        }
        tag.put("Lore", list);
        stack.setNbt(l);

        return stack;
    }

    public static ItemStack putStringedTag(ItemStack stack, String key, String value) {
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putString(key, value);
        stack.setNbt(tag);

        return stack;
    }

    public static ItemStack putStringedTags(ItemStack stack, TreeMap<String, String> tags) {
        for (String key : tags.keySet()) {
            ItemUtils.putStringedTag(stack, key, tags.get(key));
        }

        return stack;
    }

    public static ItemStack putEnchantment(ItemStack stack, String enchant, int value) {
        Enchantment enchantment = getEnchantmentByName(enchant);

        stack.addEnchantment(enchantment, value);

        return stack;
    }

    public static ItemStack putEnchantments(ItemStack stack, TreeMap<String, Integer> enchants) {
        for (String key : enchants.keySet()) {
            putEnchantment(stack, key, enchants.get(key));
        }

        return stack;
    }

    public static Enchantment getEnchantmentByName(String string) {
        for (Enchantment enchantment : getEnchantments()) {
            if (getType(enchantment).equals(string)) return enchantment;
        }

        return Enchantments.UNBREAKING;
    }

    public static List<Enchantment> getEnchantments() {
        List<Enchantment> all = new ArrayList<>();

        all.add(Enchantments.AQUA_AFFINITY);
        all.add(Enchantments.BINDING_CURSE);
        all.add(Enchantments.BANE_OF_ARTHROPODS);
        all.add(Enchantments.BLAST_PROTECTION);
        all.add(Enchantments.CHANNELING);
        all.add(Enchantments.DEPTH_STRIDER);
        all.add(Enchantments.EFFICIENCY);
        all.add(Enchantments.FLAME);
        all.add(Enchantments.FORTUNE);
        all.add(Enchantments.FEATHER_FALLING);
        all.add(Enchantments.FIRE_ASPECT);
        all.add(Enchantments.FIRE_PROTECTION);
        all.add(Enchantments.FROST_WALKER);
        all.add(Enchantments.IMPALING);
        all.add(Enchantments.INFINITY);
        all.add(Enchantments.KNOCKBACK);
        all.add(Enchantments.LURE);
        all.add(Enchantments.LOOTING);
        all.add(Enchantments.LOYALTY);
        all.add(Enchantments.LUCK_OF_THE_SEA);
        all.add(Enchantments.MENDING);
        all.add(Enchantments.MULTISHOT);
        all.add(Enchantments.PIERCING);
        all.add(Enchantments.POWER);
        all.add(Enchantments.PROJECTILE_PROTECTION);
        all.add(Enchantments.PROTECTION);
        all.add(Enchantments.PUNCH);
        all.add(Enchantments.QUICK_CHARGE);
        all.add(Enchantments.RIPTIDE);
        all.add(Enchantments.RESPIRATION);
        all.add(Enchantments.SMITE);
        all.add(Enchantments.SHARPNESS);
        all.add(Enchantments.SILK_TOUCH);
        all.add(Enchantments.SOUL_SPEED);
        all.add(Enchantments.SWEEPING);
        all.add(Enchantments.SWIFT_SNEAK);
        all.add(Enchantments.THORNS);
        all.add(Enchantments.UNBREAKING);
        all.add(Enchantments.VANISHING_CURSE);

        return all;
    }

    public static boolean hasTag(ItemStack stack, String key) {
        NbtCompound tag = stack.getOrCreateNbt();
        return tag.contains(key);
    }

    public static String getStringedTagValue(ItemStack stack, String key) {
        NbtCompound tag = stack.getOrCreateNbt();
        return tag.getString(key);
    }

    public static boolean hasStringedTagValue(ItemStack stack, String key, String value) {
        return getStringedTagValue(stack, key).equals(value);
    }

    public static String getType(ItemStack stack) {
        String[] split = stack.getTranslationKey().split(":", 2);
        if (split.length < 2) {
            return split[0].substring(split[0].lastIndexOf(".") + 1);
        } else {
            return split[1].substring(split[1].lastIndexOf(".") + 1);
        }
    }

    public static String getType(Enchantment enchantment) {
        String[] split = enchantment.getTranslationKey().split(":", 2);
        if (split.length < 2) {
            return split[0].substring(split[0].lastIndexOf(".") + 1);
        } else {
            return split[1].substring(split[1].lastIndexOf(".") + 1);
        }
    }

    public static boolean isSameAnyAmount(ItemStack toCheck, ItemStack original) {
        if (toCheck == null) return false;
        if (original == null) return false;

        ItemStack temp = toCheck;
        temp.setCount(original.getCount());

        return temp.equals(original);
    }

    public static HashMap<Integer, ItemStack> getInventory(PlayerEntity player) {
        HashMap<Integer, ItemStack> inventory = new HashMap<>();

        for (int i = 0; i < 27; i ++) {
            inventory.put(i, player.getInventory().getStack(i));
        }

        return inventory;
    }

    public static HashMap<Integer, ItemStack> getSimilarInInventory(PlayerEntity player, ItemStack stack) {
        HashMap<Integer, ItemStack> similar = new HashMap<>();
        HashMap<Integer, ItemStack> inventory = getInventory(player);

        for (int key : inventory.keySet()) {
            ItemStack s = inventory.get(key);

            if (isSameAnyAmount(s, stack)) {
                similar.put(key, s);
            }
        }

        return similar;
    }
    
    public static void removeOneOf(ItemStack stack, int slot, PlayerEntity player) {
        if (getType(stack).equals("air")) return;
        if (stack.getCount() <= 1) player.getInventory().setStack(slot, newItem(Items.AIR, 1));

        stack.setCount(stack.getCount() - 1);
    }

    public static boolean hasEnoughToRemoveOf(PlayerEntity player, ItemStack stack, int amount) {
        ItemStack inv = player.getMainHandStack();
        if (! isSameAnyAmount(inv, stack)) return false;

        return inv.getCount() >= amount;
    }

    public static boolean removeAmountOf(PlayerEntity player, ItemStack stack, int amount) {
        if (amount - stack.getCount() != 0) return false;
        if (stack != player.getMainHandStack()) return false;

        removeItemInHand(player);
        return true;
    }

    public static void removeItemInHand(PlayerEntity player) {
//        player.getInventory().setStack(player.getInventory().selectedSlot, newItem(Items.AIR));
        player.getInventory().removeStack(player.getInventory().selectedSlot);
    }

    public static boolean isShopItem(ItemStack stack) {
        return hasStringedTagValue(stack, "shopItem", "yes");
    }

    public static boolean doesPlayerHaveSimilarItemWithAmount(ParsedItem item, PlayerEntity player) {
        for (ItemStack stack : getInventory(player).values()) {
            if (stack.getName().equals(TextUtils.newText(item.name)) &&
                    stack.getCount() >= item.amount &&
                    (isShopItem(stack) && isShopItem(item.asStack())) &&
                    getType(stack).equals(getType(item.asStack()))
            ) return true;
        }

        return false;
    }

    public static void removeSimilarItemFromPlayerInventoryByAmount(ParsedItem item, PlayerEntity player, int amount) {
        if (doesPlayerHaveSimilarItemWithAmount(item, player)) {
            player.getInventory().remove(stack -> stack.getName().equals(TextUtils.newText(item.name)) &&
                    stack.getCount() >= item.amount &&
                    (isShopItem(stack) && isShopItem(item.asStack())) &&
                    getType(stack).equals(getType(item.asStack())
                    ), amount, player.getInventory());
        }
    }

    public static boolean doesPlayerHaveSimilarItemWithAmount(ItemStack item, PlayerEntity player) {
        for (ItemStack stack : getInventory(player).values()) {
            if (getType(stack).equals(getType(item)) && stack.getCount() >= item.getCount()) return true;
        }

        return false;
    }

    public static void removeSimilarItemFromPlayerInventoryByAmount(ItemStack item, PlayerEntity player, int amount) {
        if (doesPlayerHaveSimilarItemWithAmount(item, player)) {
            player.getInventory().remove(stack -> (
                    stack.getTranslationKey().equals(item.getTranslationKey()) && stack.getCount() >= item.getCount()
            ), amount, player.getInventory());
        }
    }

//    public static boolean doesPlayerHaveItemWithAmount(ParsedItem item, PlayerEntity player) {
//        for (ItemStack stack : getInventory(player).values()) {
//            if (stack.getName().equals(TextUtils.newText(item.name)) &&
//                    stack.getCount() == item.amount &&
//                    stack.
//            ) return true;
//        }
//    }
}
