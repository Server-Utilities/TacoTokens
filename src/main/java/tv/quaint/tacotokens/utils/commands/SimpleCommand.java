package tv.quaint.tacotokens.utils.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import tv.quaint.tacotokens.utils.MainUtils;
import tv.quaint.tacotokens.utils.MessagingUtils;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public abstract class SimpleCommand implements Command<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {
    public String baseBase = "tacotokens:";
    public String base;
    public String[] aliases;

    public SimpleCommand(String base, String... aliases) {
        this.base = base;
        this.aliases = aliases;
    }

    public abstract void registerAs(CommandDispatcher<ServerCommandSource> dispatcher);

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, LiteralCommandNode as) {
        for (String alias : aliases) {
            dispatcher.register(literal(alias).redirect(as));
        }
        dispatcher.register(literal(baseBase + base).redirect(as));
        for (String alias : aliases) {
            dispatcher.register(literal(baseBase + alias).redirect(as));
        }

        info("Registered " + getClass().getSimpleName() + "!");
    }

    public void info(String message) {
        MessagingUtils.info(getConsolePrefix() + message);
    }

    public String getConsolePrefix() {
        return this.getClass().getSimpleName() + " ><> ";
    }

//    public RequiredArgumentBuilder<ServerCommandSource, String> getSuggestions(final String label) throws CommandSyntaxException {
//        return argument(label, StringArgumentType.string()).suggests()
//    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String input = context.getInput();
        String[] args = input.split(" ");

        TreeSet<String> suggestions = getSuggestion(args);

        return CommandSource.suggestMatching(suggestions, builder);
    }

    public TreeSet<String> getOnlineSuggestion() {
        return new TreeSet<>(MainUtils.getOnlinePlayerNames());

//        return MainUtils.getCompletion(playerNames, at);
    }

    public abstract TreeSet<String> getSuggestion(String[] args);
}

