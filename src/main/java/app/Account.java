package app;


public class Account {

    String id;
    volatile int money;

    public synchronized void addMoney(int money) {
        System.out.println("add money " + money + " to " + this.id);
        this.money += money;
    }

    public synchronized boolean subtractMoney(int money) {
        System.out.println("subtract money " + money + " to " + this.id);

        if (this.money >= money) {
            this.money -= money;
            return true;
        }

        return false;
    }

    public Account(String id, int money) {
        this.id = id;
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
