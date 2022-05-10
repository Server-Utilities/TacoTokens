package tv.quaint.tacotokens.shops;

import net.minecraft.server.network.ServerPlayerEntity;
import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.balance.Balance;
import tv.quaint.tacotokens.balance.BalanceManager;
import tv.quaint.tacotokens.utils.obj.AmountJustification;

public class ShopGive {
    public GiveType type;
    public String value;

    public ShopGive(GiveType type, String value) {
        this.type = type;
        this.value = value;
    }

    public void give(ServerPlayerEntity player) {
        switch (this.type) {
            case COMMAND -> {
                TacoTokens.SERVER.getCommandManager().execute(TacoTokens.SERVER.getCommandSource(), this.value
                        .replace("%player%", player.getEntityName())
                        .replace("%uuid%", player.getUuidAsString())
                        .replace("%ping%", String.valueOf(player.pingMilliseconds))
                );
            }
            case GIVE -> {
                GiveGive give = new GiveGive(this.value);
                if (! give.isValid()) return;

                TacoTokens.SERVER.getCommandManager().execute(TacoTokens.SERVER.getCommandSource(),
                        "give " + player.getEntityName() + " " + give.item
                                .replace("%player%", player.getEntityName())
                                .replace("%uuid%", player.getUuidAsString())
                                .replace("%ping%", String.valueOf(player.pingMilliseconds))
                                + " " + give.amount
                );
            }
            case BALANCE -> {
                if (value.startsWith("+")) {
                    try {
                        int add = 0;
                        if (value.substring(1).startsWith("%random")) {
                            add = new AmountJustification(value.substring(1)).roll();
                        } else {
                            add = Integer.parseInt(value.substring(1));
                        }
                        BalanceManager.getBalance(player.getUuidAsString(), true).addAmount(add);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (value.startsWith("-")) {
                    try {
                        int remove = 0;
                        if (value.substring(1).startsWith("%random")) {
                            remove = new AmountJustification(value.substring(1)).roll();
                        } else {
                            remove = Integer.parseInt(value.substring(1));
                        }
                        BalanceManager.getBalance(player.getUuidAsString(), true).removeAmount(remove);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    int set = 0;
                    if (value.startsWith("%random")) {
                        set = new AmountJustification(value).roll();
                    } else {
                        set = Integer.parseInt(value);
                    }
                    BalanceManager.getBalance(player.getUuidAsString(), true).setAmount(set);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
