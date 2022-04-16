package tv.quaint.tacotokens.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.balance.Balance;
import tv.quaint.tacotokens.balance.BalanceManager;
import tv.quaint.tacotokens.shops.Shop;
import tv.quaint.tacotokens.shops.ShopGive;
import tv.quaint.tacotokens.shops.ShopHandler;
import tv.quaint.tacotokens.shops.ShopTake;
import tv.quaint.tacotokens.utils.MainUtils;
import tv.quaint.tacotokens.utils.TextUtils;
import tv.quaint.tacotokens.utils.commands.SimpleCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;

public class ShopCommand extends SimpleCommand {
    public ShopCommand() {
        super("shop");

        info("Registered!");
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        player.sendMessage(TextUtils.newText("&c&lAvailable Shops&8:"), false);

        for (Shop shop : ShopHandler.shops) {
            player.sendMessage(TextUtils.newText(shop.buildListingMessage()
                    .replace("%command_base%", this.base)
            ), false);
        }

        return 0;
    }

    public int runShop(CommandContext<ServerCommandSource> context, String shop) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        Shop s = ShopHandler.getShop(shop);

        if (s == null) {
            player.sendMessage(TextUtils.newText("&cCould not find that shop!"), false);
            return 0;
        }

        for (String string : s.buildInfoMessage()) {
            player.sendMessage(TextUtils.newText(string
                    .replace("%command_base%", this.base)
            ), false);
        }

        return 0;
    }

    public int runShopConfirm(CommandContext<ServerCommandSource> context, String shop, boolean confirmed) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        Shop s = ShopHandler.getShop(shop);

        if (s == null) {
            player.sendMessage(TextUtils.newText("&cCould not find that shop!"), false);
            return 0;
        }

        if (! confirmed) {
            player.sendMessage(TextUtils.newText("&cDid not buy from shop &7'" + s.name + "&7'&c!"), false);
            return 0;
        }

        boolean canPass = true;
        for (ShopTake take : s.takes) {
            canPass = take.predicate(player);
        }
        if (! canPass) {
            player.sendMessage(TextUtils.newText("&cYou don't have enough payment!"), false);
            return 0;
        }

        for (ShopTake take : s.takes) {
            take.take(player);
        }
        for (ShopGive give : s.gives) {
            give.give(player);
        }


        return 1;
    }

    public int reload(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        ShopHandler.refreshShops();

        player.sendMessage(TextUtils.newText("&aReloaded the shops!"), false);

        return 1;
    }

    @Override
    public void registerAs(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> node = dispatcher.register(CommandManager
                .literal("shop")
                .then(argument("shop", StringArgumentType.word()).suggests(this)
                        .then(argument("confirm", BoolArgumentType.bool()).suggests(this)
                                .executes(context -> runShopConfirm(context, StringArgumentType.getString(context, "shop"), BoolArgumentType.getBool(context, "confirm")))
                        )
                        .executes(context -> runShop(context, StringArgumentType.getString(context, "shop")))
                )
                .executes(this))
                ;

        register(dispatcher, node);
    }

    @Override
    public TreeSet<String> getSuggestion(String[] args) {
        if (args.length == 1) {
            return new TreeSet<>(ShopHandler.getShopsAsStrings());
        }
        if (args.length == 2) {
            TreeSet<String> options = new TreeSet<>();
            options.add("true");
            options.add("false");

            return options;
        }

        return new TreeSet<>();
    }
}
