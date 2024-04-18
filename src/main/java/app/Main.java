package app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final Random random = new Random();
    private static final int startAmount = 10000;
    private static final int accountCount = 14;
    private static final int threadCount = 20;
    private static final int maxTransactionCount = 300;
    private static final String idFormat = "%06d";

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        TransactionService.setMaxTransactionCount(maxTransactionCount);

        List<Account> accounts = genericAccauntList();
        List<TransactionService> threads = genericThreadList(accounts);

        waitingForThreadsToExecute(threads);

        int totalAmount = accounts.stream()
                .mapToInt(Account::getMoney)
                .sum();

        logger.debug("Total before {} after {}", startAmount * accountCount, totalAmount);
        logger.debug(System.lineSeparator());
        accounts.forEach(logger::debug);

        logger.info("Program completed in {}", System.currentTimeMillis() - startTime);
    }

    private static void waitingForThreadsToExecute(List<TransactionService> threads) {
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("An error occurred while waiting for threads to execute: {}", e.getMessage());
                logger.error(e.fillInStackTrace());
            }
        });
    }

    private static List<TransactionService> genericThreadList(List<Account> accounts) {
        return IntStream.range(0, threadCount)
                .mapToObj(i -> {
                    TransactionService transaction = new TransactionService(accounts, startAmount);
                    transaction.start();
                    return transaction;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<Account> genericAccauntList() {
        return IntStream.range(0, accountCount)
                .mapToObj(i -> {
                    String id = String.format(idFormat, random.nextInt(100));
                    return new Account(id, startAmount);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}