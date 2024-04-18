package app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionService extends Thread {

    private static final AtomicInteger transactionCount = new AtomicInteger(0);
    private static final AtomicInteger maxTransactionCount = new AtomicInteger(30);

    private final Logger logger = LogManager.getLogger(TransactionService.class);
    private final Random random = new Random();
    private final List<Account> accounts;
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

    public static AtomicInteger getMaxTransactionCount() {
        return maxTransactionCount;
    }

    public static void setMaxTransactionCount(int maxTransactionCount) {
        TransactionService.maxTransactionCount.set(maxTransactionCount);
    }

    @Override
    public void run() {

        String message = "{} stream was {} in {}";
        logger.debug(message, Thread.currentThread().getName(), "launched", LocalTime.now());

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

        logger.debug(message, Thread.currentThread().getName(), "completed", LocalTime.now());
    }

    private void startTransaction(Account accountOut, Account accountIn, int sum) {

        String message = String.format("The transaction %s to transfer money in the amount of %s from %s to %s was {} {} ms",
                currentTransaction, sum, accountOut.getId(), accountIn.getId());

        if (accountOut.subtractMoney(sum)) {
            accountIn.addMoney(sum);
            logger.info(message, "successfully completed in", System.currentTimeMillis() - startTime);
            return;
        }
        logger.warn(message, "interrupted in", System.currentTimeMillis() - startTime);
    }

    private void sleep() {
        try {
            sleep(random.nextInt(1000) + 1000);
        } catch (InterruptedException e) {
            logger.error("An error occurred while sleeping thread {}: {}", Thread.currentThread().getName(), e.getMessage());
            logger.error(e.fillInStackTrace());
        }
    }
}
