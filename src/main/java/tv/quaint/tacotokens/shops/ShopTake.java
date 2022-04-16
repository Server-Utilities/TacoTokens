package tv.quaint.tacotokens.shops;

import net.minecraft.server.network.ServerPlayerEntity;
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
            case ITEM -> {
                ParsedItem item = ItemParser.parseItemValue(this.value);

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
            case ITEM -> {
                ParsedItem item = ItemParser.parseItemValue(this.value);

                if (ItemUtils.doesPlayerHaveSimilarItemWithAmount(item, player)) {
                    ItemUtils.removeSimilarItemFromPlayerInventoryByAmount(item, player, item.amount);
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
