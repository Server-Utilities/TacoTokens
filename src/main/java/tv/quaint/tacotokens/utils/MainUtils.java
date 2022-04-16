package tv.quaint.tacotokens.utils;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import tv.quaint.tacotokens.TacoTokens;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MainUtils {
    public static List<ServerPlayerEntity> getOnlinePlayers() {
        return TacoTokens.SERVER.getPlayerManager().getPlayerList();
    }

    public static List<String> getOnlinePlayerNames() {
        List<String> strings = new ArrayList<>();

        for (ServerPlayerEntity player : getOnlinePlayers()) {
            strings.add(player.getEntityName());
        }

        return strings;
    }

    public static ServerPlayerEntity getServerPlayerEntityByName(String name) {
        for (ServerPlayerEntity player : getOnlinePlayers()) {
            if (player.getEntityName().equals(name)) return player;
        }

        return null;
    }

    public static Config loadConfigFromSelf(File file, String fileString) {
        if (! file.exists()) {
            try {
                TacoTokens.getDataFolder().mkdirs();
                try (InputStream in = TacoTokens.INSTANCE.getResource(fileString)) {
                    assert in != null;
                    Files.copy(in, file.toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(file).createConfig();
    }

    public static Config loadConfigNoDefault(File file) {
        if (! file.exists()) {
            try {
                TacoTokens.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(file).createConfig();
    }

    public static String resize(String text, int digits) {
        try {
            digits = getDigits(digits, text.length());
            return text.substring(0, digits);
        } catch (Exception e) {
            return text;
        }
    }

    public static String truncate(String text, int digits) {
        if (! text.contains(".")) return text;

        try {
            digits = getDigits(text.indexOf(".") + digits + 1, text.length());
            return text.substring(0, digits);
        } catch (Exception e) {
            return text;
        }
    }

    public static int getDigits(int start, int otherSize){
        return Math.min(start, otherSize);
    }

    public static TreeSet<String> getCompletion(List<String> of, String param){
        return of.stream()
                .filter(completion -> completion.toLowerCase(Locale.ROOT).startsWith(param.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public static TreeSet<String> getCompletion(TreeSet<String> of, String param){
        return of.stream()
                .filter(completion -> completion.toLowerCase(Locale.ROOT).startsWith(param.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
