package app;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Account {

    private final Logger logger = LogManager.getLogger(Account.class);

    private final String id;
    private volatile int money;

    public Account(String id, int money) {
        this.id = id;
        this.money = money;
    }

    public synchronized void addMoney(int money) {
        logger.debug("Topped up with {} to {}", money, this.id);
        this.money += money;
    }

    public synchronized boolean subtractMoney(int money) {

        if (this.money >= money) {
            this.money -= money;
            logger.debug("Debited {} to {}", money, this.id);
            return true;
        }

        logger.warn("An attempt to withdraw more funds than {} has in your account", this.id);
        return false;
    }

    public String getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return String.format("id: %s - %s", this.id, this.money);
    }
}
