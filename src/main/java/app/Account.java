package app;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Account {

    private final Logger logger = LogManager.getLogger(Account.class);

    private final String id;
    private volatile int money;

    public Account(String id, int money) {
        this.id = id;
        this.money = isValid(money) ? money : 0;
    }

    public synchronized void addMoney(int money) {

        if (isValid(money)) {
            logger.debug("Topped up with {} to {}", money, this.id);
            this.money += money;
        }
    }

    public synchronized boolean subtractMoney(int money) {

        if (!isValid(money)) {
            return false;
        }

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
        if (isValid(money)) {
            this.money = money;
        }
    }

    private boolean isValid(int num) {
        if (num < 0) {
            logger.error("Number cannot be less than or equal to zero");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("id: %s - %s", this.id, this.money);
    }
}
