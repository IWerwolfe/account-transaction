package app;


import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Account implements Comparable<Account> {

    private final Set<Observer> observers = new HashSet<>();

    private final String id;
    private volatile int money;

    public Account(String id, int money) {
        this.id = id;
        addMoney(money);
    }

    public synchronized void addMoney(int money) {

        if (isValid(money)) {
            this.money += money;
            notifyObservers(Level.DEBUG, String.format("Topped up with %s to %s", money, this.id));
        }
    }

    public synchronized boolean subtractMoney(int money) {

        if (!isValid(money)) {
            return false;
        }

        if (this.money >= money) {
            this.money -= money;
            notifyObservers(Level.DEBUG,
                    String.format("Debited %s to %s", money, this.id));
            return true;
        }

        notifyObservers(Level.WARN,
                String.format("An attempt to withdraw more funds than %s has in your account", this.id));
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
            notifyObservers(Level.ERROR, "Number cannot be less than or equal to zero");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("id: %s - %s", this.id, this.money);
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    void notifyObservers(Level level, String message) {
        observers.forEach(observer -> observer.logging(level, message));
    }

    @Override
    public int compareTo(Account o) {
        return Integer.compare(Integer.parseInt(o.id), Integer.parseInt(id));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Account other = (Account) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
