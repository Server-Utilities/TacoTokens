package tv.quaint.tacotokens.utils.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.CommandRegistryWrapper;
import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.utils.MainUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ArgumentSuggestions {
    public static CompletableFuture<Suggestions> minMax(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder, double min, double max) {
        List<String> options = new ArrayList<>();
        for (double i = (min < -10 ? -10 : min); i <= (max > 10 ? 10 : max); i += 0.5) {
            if (i > max - 1) i = max;
            options.add(String.valueOf(i));
            if (i < min - 1) i = min;
        }

        return CommandSource.suggestMatching(options, builder);
    }
}
