package tv.quaint.tacotokens.shops;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tv.quaint.tacotokens.balance.Balance;
import tv.quaint.tacotokens.balance.BalanceManager;
import tv.quaint.tacotokens.shops.items.ItemParser;
import tv.quaint.tacotokens.shops.items.ParsedItem;
import tv.quaint.tacotokens.utils.ItemUtils;

public class ShopTake {
    public TakeType type;
    public String value;

    public ShopTake(TakeType type, String value) {
        this.type = type;
        this.value = value;
    }

    public boolean predicate(ServerPlayerEntity player) {
        switch (this.type) {
            case CUSTOM_ITEM -> {
                ParsedItem item = ItemParser.parseItemValue(this.value);

                return ItemUtils.doesPlayerHaveSimilarItemWithAmount(item, player);
            }
            case BASIC_ITEM -> {
                String[] split = this.value.split(",");
                ItemStack item = ItemUtils.newItem(Registry.ITEM.get(new Identifier(split[0])), Integer.parseInt(split[1]));

                return ItemUtils.doesPlayerHaveSimilarItemWithAmount(item, player);
            }
            case BALANCE -> {
                double amount = 0d;
                try {
                    amount = Double.parseDouble(this.value);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Balance balance = BalanceManager.getBalance(player.getUuidAsString(), true);
                return (balance.balance >= amount);
            }
        }

        return false;
    }

    public boolean take(ServerPlayerEntity player) {
        switch (this.type) {
            case CUSTOM_ITEM -> {
                ParsedItem item = ItemParser.parseItemValue(this.value);

                if (ItemUtils.doesPlayerHaveSimilarItemWithAmount(item, player)) {
                    ItemUtils.removeSimilarItemFromPlayerInventoryByAmount(item, player, item.amount);
                    return true;
                }

                return false;
            }
            case BASIC_ITEM -> {
                String[] split = this.value.split(",");
                ItemStack item = ItemUtils.newItem(Registry.ITEM.get(new Identifier(split[0])), Integer.parseInt(split[1]));

                if (ItemUtils.doesPlayerHaveSimilarItemWithAmount(item, player)) {
                    ItemUtils.removeSimilarItemFromPlayerInventoryByAmount(item, player, item.getCount());
                    return true;
                }

                return false;
            }
            case BALANCE -> {
                double amount = 0d;
                try {
                    amount = Double.parseDouble(this.value);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Balance balance = BalanceManager.getBalance(player.getUuidAsString(), true);
                if (balance.balance < amount) return false;

                balance.removeAmount(amount);
                return true;
            }
        }

        return false;
    }
}
