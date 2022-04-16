package tv.quaint.tacotokens.balance;

public class Balance {
    public String belongsTo;
    public double balance;
    public boolean isChangeSafe; // true if belongsTo is UUID of a player.

    public Balance(String belongsTo, double balance, boolean isChangeSafe){
        this.belongsTo = belongsTo;
        this.balance = balance;
        this.isChangeSafe = isChangeSafe;
    }

    public Balance addAmount(double amount) {
        this.balance += amount;
        BalanceManager.saveBalance(this);

        return this;
    }

    public Balance removeAmount(double amount) {
        this.balance -= amount;
        BalanceManager.saveBalance(this);

        return this;
    }

    public Balance setAmount(double amount) {
        this.balance = amount;
        BalanceManager.saveBalance(this);

        return this;
    }
}
