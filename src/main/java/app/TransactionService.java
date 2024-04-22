package app;

import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionService extends Thread {

    private static final AtomicInteger transactionCount = new AtomicInteger(0);
    private static final AtomicInteger maxTransactionCount = new AtomicInteger(30);

    private final Random random = new Random();
    private final List<Account> accounts;
    private final Set<Observer> observers = new HashSet<>();
    private int maxSum = 10000;
    private int currentTransaction;
    private long startTime;

    public TransactionService(List<Account> accounts) {
        this.accounts = accounts;
    }

    public TransactionService(List<Account> accounts, int maxSum) {
        this.accounts = accounts;
        this.maxSum = maxSum;
    }

    private static void incrementTransactionCount() {
        transactionCount.getAndIncrement();
    }

    private static AtomicInteger getTransactionCount() {
        return transactionCount;
    }

    private static void setTransactionCount(int count) {
        transactionCount.set(count);
    }

    private static void clearTransactionCount() {
        transactionCount.set(0);
    }

    public static AtomicInteger getMaxTransactionCount() {
        return maxTransactionCount;
    }

    public static void setMaxTransactionCount(int maxTransactionCount) {
        TransactionService.maxTransactionCount.set(maxTransactionCount);
    }

    @Override
    public void run() {

        while (getTransactionCount().get() < getMaxTransactionCount().get() && !isInterrupted()) {

            startTime = System.currentTimeMillis();
            incrementTransactionCount();
            currentTransaction = getTransactionCount().get();

            sleep();

            Account accountOut = accounts.get(random.nextInt(accounts.size()));
            Account accountIn = accounts.get(random.nextInt(accounts.size()));
            int sum = random.nextInt(maxSum);

            startTransaction(accountOut, accountIn, sum);
        }
    }

    private void startTransaction(Account accountOut, Account accountIn, int sum) {

        if (accountOut.equals(accountIn)) {
            notifyObservers(Level.WARN,
                    String.format("It is prohibited to transfer money to your own account, transaction %s for %s is blocked", currentTransaction, accountOut.getId()));
            return;
        }

        String message = String.format("The transaction %s to transfer money in the amount of %s from %s to %s was",
                currentTransaction, sum, accountOut.getId(), accountIn.getId());

        if (accountOut.subtractMoney(sum)) {
            accountIn.addMoney(sum);
            notifyObservers(Level.INFO,
                    String.format("%s successfully completed in %s ms", message, (System.currentTimeMillis() - startTime)));
            return;
        }
        notifyObservers(Level.WARN,
                String.format("%s interrupted in %s ms", message, (System.currentTimeMillis() - startTime)));
    }

    private void sleep() {
        try {
            sleep(random.nextInt(1000) + 1000);
        } catch (InterruptedException e) {
            String message = String.format("An error occurred while sleeping thread %s: %s",
                    Thread.currentThread().getName(), e.getMessage());
            notifyObservers(Level.ERROR, message);
            notifyObservers(Level.ERROR, String.valueOf(e.fillInStackTrace()));
        }
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
}
