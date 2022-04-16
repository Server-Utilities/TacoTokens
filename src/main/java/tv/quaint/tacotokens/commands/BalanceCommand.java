package tv.quaint.tacotokens.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.server.command.CommandManager.*;

public class BalanceCommand extends SimpleCommand {
    public BalanceCommand() {
        super("balance", "bal", "money");

        info("Registered!");
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        Balance balance = BalanceManager.getBalance(player.getUuidAsString(), true);

        player.sendMessage(TextUtils.newText("&aYour current balance is&8: &6" + balance.balance), false);

        return (int) balance.balance;
    }

    public int runOperationOther(CommandContext<ServerCommandSource> source, String operation, String other, int amount) throws CommandSyntaxException {
        ServerCommandSource commandSource = source.getSource();
        if (commandSource.equals(TacoTokens.SERVER.getCommandSource())) {
            return runOperationOtherConsole(commandSource, operation, other, amount);
        } else {
            return runOperationOtherPlayer(commandSource, operation, other, amount);
        }
    }

    public int runOperationOtherPlayer(ServerCommandSource source, String operation, String otherName, int amount) throws CommandSyntaxException {
        ServerPlayerEntity sender = source.getPlayer();
        ServerPlayerEntity other = MainUtils.getServerPlayerEntityByName(otherName);

        String uuid = "";
        if (other == null) {
            uuid = UUIDUtils.getCachedUUID(otherName);
            sender.sendMessage(TextUtils.newText("&cCould not find player by name of '" + otherName + "' using UUID of '" + uuid + "' instead..."), false);
        } else {
            uuid = other.getUuidAsString();
        }

        Balance balance = BalanceManager.getBalance(uuid, true);

        switch (operation) {
            case "check" -> {
                sender.sendMessage(TextUtils.newText(otherName + "&a's current balance is&8: &6" + balance.balance), false);

                return (int) balance.balance;
            }
            case "set" -> {
                int before = (int) balance.balance;

                balance = balance.setAmount(amount);

                sender.sendMessage(TextUtils.newText(otherName + "&a's current balance is now&8: &6" + balance.balance + " &a(Was&8: &6" + before + "&a)"), false);

                return (int) balance.balance;
            }
            case "add" -> {
                int before = (int) balance.balance;

                balance = balance.addAmount(amount);

                sender.sendMessage(TextUtils.newText(otherName + "&a's current balance is now&8: &6" + balance.balance + " &a(Was&8: &6" + before + "&a)"), false);

                return (int) balance.balance;
            }
            case "remove" -> {
                int before = (int) balance.balance;

                balance = balance.removeAmount(amount);

                sender.sendMessage(TextUtils.newText(otherName + "&a's current balance is now&8: &6" + balance.balance + " &a(Was&8: &6" + before + "&a)"), false);

                return (int) balance.balance;
            }
            default -> {
                sender.sendMessage(TextUtils.newText("&cYou did not specify the correct operation. You specified '" + otherName + "'..."), false);
                return 0;
            }
        }
    }

    public int runOperationOtherConsole(ServerCommandSource source, String operation, String otherName, int amount) throws CommandSyntaxException {
        ServerPlayerEntity other = MainUtils.getServerPlayerEntityByName(otherName);

        String uuid = "";
        if (other == null) {
            uuid = UUIDUtils.getCachedUUID(otherName);
            MessagingUtils.info("Could not find player by name of '" + otherName + "' using UUID of '" + uuid + "' instead...");
        } else {
            uuid = other.getUuidAsString();
        }

        Balance balance = BalanceManager.getBalance(uuid, true);

        switch (operation) {
            case "check" -> {
                MessagingUtils.info(otherName + "'s current balance is: " + balance.balance);

                return (int) balance.balance;
            }
            case "set" -> {
                int before = (int) balance.balance;

                balance = balance.setAmount(amount);

                MessagingUtils.info(otherName + "'s current balance is now: " + balance.balance + " (Was: " + before + ")");

                return (int) balance.balance;
            }
            case "add" -> {
                int before = (int) balance.balance;

                balance = balance.addAmount(amount);

                MessagingUtils.info(otherName + "'s current balance is now: " + balance.balance + " (Was: " + before + ")");

                return (int) balance.balance;
            }
            case "remove" -> {
                int before = (int) balance.balance;

                balance = balance.removeAmount(amount);

                MessagingUtils.info(otherName + "'s current balance is now: " + balance.balance + " (Was: " + before + ")");

                return (int) balance.balance;
            }
            default -> {
                MessagingUtils.info("You did not specify the correct operation. You specified '" + otherName + "'...");
                return 0;
            }
        }
    }

    @Override
    public void registerAs(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> node = dispatcher.register(CommandManager
                .literal("balance")
                .then(argument("operation", StringArgumentType.word()).suggests(this)
                        .then(argument("online", StringArgumentType.word()).suggests(this)
                                .then(argument("amount", IntegerArgumentType.integer()).suggests(this)
                                        .requires(source -> {
                                            return source.hasPermissionLevel(4) || source.equals(TacoTokens.SERVER.getCommandSource());
                                        })
                                        .executes(context -> runOperationOther(context, StringArgumentType.getString(context, "operation"), StringArgumentType.getString(context, "online"), IntegerArgumentType.getInteger(context, "amount")))
                                )
                                .requires(source -> {
                                    return source.hasPermissionLevel(4) || source.equals(TacoTokens.SERVER.getCommandSource());
                                })
                                .executes(context -> runOperationOther(context, "check", StringArgumentType.getString(context, "online"), 0))
                        )
                )
                .executes(this))
        ;

        register(dispatcher, node);
    }

    @Override
    public TreeSet<String> getSuggestion(String[] args) {
        if (args.length == 1) {
            TreeSet<String> options = new TreeSet<>();
            options.add("check");
            options.add("set");
            options.add("add");
            options.add("remove");

            return options;
        }
        if (args.length == 2) {
            return getOnlineSuggestion();
        }
        if (args.length == 3) {
            TreeSet<String> options = new TreeSet<>();
            double min = -5;
            double max = 5;
            for (double i = min; i <= max; i += 0.5) {
                options.add(String.valueOf(i));
            }

            return options;
        }
        
        return new TreeSet<>();
    }
}
