package tv.quaint.tacotokens.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.messages.CommandMessages;
import tv.quaint.tacotokens.shops.ShopHandler;
import tv.quaint.tacotokens.utils.MessagingUtils;

public class ReloadCommand implements Command<ServerCommandSource> {
    public ReloadCommand() {
        info("Loading!");
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        TacoTokens.reloadConfig();
        player.sendMessage(MutableText.Serializer.fromJson(CommandMessages.RELOAD_CONFIG_RELOADED.parse()), false);

        ShopHandler.refreshShops();
        player.sendMessage(MutableText.Serializer.fromJson(CommandMessages.RELOAD_SHOPS_RELOADED.parse()), false);

        return 0;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager
                .literal("tacotokens:reload")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(this));
        info("Registered!");
    }

    public void info(String message) {
        MessagingUtils.info(getConsolePrefix() + message);
    }

    public String getConsolePrefix() {
        return this.getClass().getSimpleName() + " ><> ";
    }
}
