package tv.quaint.tacotokens.shops.items;

import com.google.re2j.Matcher;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tv.quaint.tacotokens.utils.MainUtils;
import tv.quaint.tacotokens.utils.MatcherUtils;
import tv.quaint.tacotokens.utils.obj.AmountJustification;
import tv.quaint.tacotokens.utils.obj.SingleSet;

public class ItemParser {
    public static ParsedItem parseItemValue(String from) {
        Matcher matcher = MatcherUtils.setupMatcher("((.*?)[:](.*?)[;])", from);

        ParsedItem ParsedItem = new ParsedItem();

        while (matcher.find()) {
            String unparsed = matcher.group(1);
            String varIdentifier = matcher.group(2);
            String varContent = matcher.group(3);

            if (varIdentifier.equals("material")) {
                Identifier identifier = new Identifier(varContent);
                ParsedItem = ParsedItem.setMaterial(Registry.ITEM.get(identifier));
            }
            if (varIdentifier.equals("amount")) {
                try {
                    int amount = Integer.parseInt(varContent);
                    ParsedItem = ParsedItem.setAmount(amount);
                } catch (Exception e) {
                    try {
                        int amount = new AmountJustification(varContent).roll();
                        ParsedItem = ParsedItem.setAmount(amount);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (varIdentifier.equals("name")) {
                ParsedItem = ParsedItem.setName(varContent);
            }
            if (varIdentifier.equals("lore")) {
                Matcher m = MatcherUtils.setupMatcher("([<](.*?)[>])", varContent);

                while (m.find()) {
                    ParsedItem.addLore(m.group(2));
                }
            }
            if (varIdentifier.equals("tags")) {
                String[] pairs = varContent.split(",");
                for (String p : pairs) {
                    String[] pair = p.split(":", 2);
                    ParsedItem = ParsedItem.addTag(new SingleSet<>(pair[0], pair[1]));
                }
            }
            if (varIdentifier.equals("enchantments")) {
                String[] pairs = varContent.split(",");
                for (String p : pairs) {
                    String[] pair = p.split(":", 2);

                    int value = 1;
                    try {
                        value = Integer.parseInt(pair[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ParsedItem = ParsedItem.addEnchant(new SingleSet<>(pair[0], value));
                }
            }

        }

        return ParsedItem;
    }
}
