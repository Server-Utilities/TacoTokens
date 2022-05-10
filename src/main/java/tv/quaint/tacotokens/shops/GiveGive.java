package tv.quaint.tacotokens.shops;

import tv.quaint.tacotokens.utils.obj.AmountJustification;

public class GiveGive {
    public int amount;
    public String item;

    public GiveGive(String unparsed) {
        String[] split = unparsed.split(":", 2);
        if (split[0].startsWith("%random")) {
            amount = new AmountJustification(split[0]).roll();
        } else {
            amount = Integer.parseInt(split[0]);
        }
        item = split[1];
    }

    public boolean isValid() {
        return amount >= 1;
    }
}
