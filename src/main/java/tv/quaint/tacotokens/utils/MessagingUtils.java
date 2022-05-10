package tv.quaint.tacotokens.utils;

import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import tv.quaint.tacotokens.TacoTokens;

public class MessagingUtils {
    public static void info(String string) {
        TacoTokens.LOGGER.info(string);
    }

    public static void warn(String string) {
        TacoTokens.LOGGER.warn(string);
    }

    public static void severe(String string) {
        TacoTokens.LOGGER.error(string);
    }

    public static void sendMessageAs(Entity entity, String message) {
        for (ServerPlayerEntity p : MainUtils.getOnlinePlayers()) {
            p.sendMessage(TextUtils.newText(message), MessageType.CHAT);
        }
    }
}
