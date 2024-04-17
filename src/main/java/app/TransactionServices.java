package app;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionServices extends Thread {

    private Random random = new Random();
    private List<Account> accounts;
    private static AtomicInteger transactionCount = new AtomicInteger(0);

    public TransactionServices(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public void run() {

        try {


            sleep();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean startTransaction() {

        System.out.println("Starting transaction in " + LocalTime.now() +
                " thread: " + Thread.currentThread().getName() +
                " count: " + getTransactionCount());

        incrementTransactionCount();


        return true;
    }

    private void sleep() throws InterruptedException {
        sleep(random.nextInt(1000) + 1000);
    }

    private static void incrementTransactionCount() {
        transactionCount.getAndIncrement();
    }

    private static int getTransactionCount() {
        return transactionCount.get();
    }
}
