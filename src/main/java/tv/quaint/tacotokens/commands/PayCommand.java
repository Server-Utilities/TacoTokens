package tv.quaint.tacotokens.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
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
import tv.quaint.tacotokens.utils.MainUtils;
import tv.quaint.tacotokens.utils.MessagingUtils;
import tv.quaint.tacotokens.utils.TextUtils;
import tv.quaint.tacotokens.utils.UUIDUtils;
import tv.quaint.tacotokens.utils.commands.SimpleCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;

public class PayCommand extends SimpleCommand {
    public PayCommand() {
        super("pay", "sendmoney", "givemoney");

        info("Registered!");
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        Balance balance = BalanceManager.getBalance(player.getUuidAsString(), true);

        player.sendMessage(TextUtils.newText("&aYour current balance is&8: &6" + balance.balance), false);

        return (int) balance.balance;
    }

    public int runOperationOther(CommandContext<ServerCommandSource> source, String otherName, double amount) throws CommandSyntaxException {
        ServerPlayerEntity sender = source.getSource().getPlayer();
        ServerPlayerEntity other = MainUtils.getServerPlayerEntityByName(otherName);

        String uuid = "";
        if (other == null) {
            uuid = UUIDUtils.getCachedUUID(otherName);
            sender.sendMessage(TextUtils.newText("&cCould not find player by name of '" + otherName + "' using UUID of '" + uuid + "' instead..."), false);
        } else {
            uuid = other.getUuidAsString();
        }

        if (sender.getUuidAsString().equals(uuid)) {
            sender.sendMessage(TextUtils.newText("&cYou cannot pay yourself!"), false);
            return 0;
        }

        Balance balance = BalanceManager.getBalance(sender.getUuidAsString(), true);

        if (balance.balance < amount) {
            sender.sendMessage(TextUtils.newText("&cYou do not have enough balance to pay this person!"), false);
            return 0;
        } else {
            Balance otherBal = BalanceManager.getBalance(uuid, true);
            double senderBefore = balance.balance;
            double otherBefore = otherBal.balance;
            sender.sendMessage(TextUtils.newText("&aPaying &d" + otherName + " &6" + amount + " &abalance&8... &aTransaction processing..."), false);
            if (other != null) other.sendMessage(TextUtils.newText("&aPayment from &d" + sender.getEntityName() + " &aof &6" + amount + " &abalance&8... &aTransaction processing..."), false);
            balance.removeAmount(amount);
            otherBal.addAmount(amount);
            sender.sendMessage(TextUtils.newText("&aCompleted&8! &aJust paid &d" + otherName + " &6" + amount + " &abalance&8! &aYour new balance&8: &6" + balance.balance + " &7(&aWas &6" + senderBefore + "&7)"), false);
            if (other != null) other.sendMessage(TextUtils.newText("&aCompleted&8! &d" + sender.getEntityName() + " &ajust paid you &6" + amount + " &abalance&8! &aYour new balance&8: &6" + otherBal.balance + " &7(&aWas &6" + otherBefore + "&7)"), false);
            return (int) amount;
        }
    }

    @Override
    public void registerAs(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode node = dispatcher.register(CommandManager
                .literal(base)
                        .then(argument("online", StringArgumentType.word()).suggests(this)
                                .then(argument("amount", DoubleArgumentType.doubleArg(TacoTokens.CONFIG.balanceSettings.payingMin, TacoTokens.CONFIG.balanceSettings.payingMax))
                                        .executes(context -> runOperationOther(context, StringArgumentType.getString(context, "online"), DoubleArgumentType.getDouble(context, "amount")))
                                )
                                .executes(context -> runOperationOther(context, StringArgumentType.getString(context, "online"), 1))
                        )
                .executes(this))
        ;

        register(dispatcher, node);
    }

    @Override
    public TreeSet<String> getSuggestion(String[] args) {
        if (args.length == 1) {
            return getOnlineSuggestion();
        }
        if (args.length == 2) {
            TreeSet<String> options = new TreeSet<>();
            double min = TacoTokens.CONFIG.balanceSettings.payingMin;
            double max = TacoTokens.CONFIG.balanceSettings.payingMax;
            if (min < 1) min = 0;
            if (max > 10) max = 10;
            for (double i = min; i <= max; i += 0.5) {
                options.add(String.valueOf(i));
            }

            return options;
        }

        return new TreeSet<>();
    }
}
