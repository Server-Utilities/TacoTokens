package tv.quaint.tacotokens.config;

import tv.quaint.tacotokens.utils.MainUtils;
import tv.quaint.tacotokens.utils.MessagingUtils;

public class BalanceSettings {
    public double startingAmount = 0;
    public double payingMin = 0.1;
    public double payingMax = 10000000;

    public BalanceSettings() {
        MessagingUtils.info("BalanceSettings loaded!");
    }

    public BalanceSettings setStartingAmount(double amount) {
        this.startingAmount = amount;

        return this;
    }

    public BalanceSettings setPayingMinAmount(double amount) {
        this.payingMin = amount;

        return this;
    }

    public BalanceSettings setPayingMaxAmount(double amount) {
        this.payingMax = amount;

        return this;
    }
}
