package app;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    int startAmount = 1000;

    @Test
    void getAndSetMaxTransactionCount() {
        TransactionService.setMaxTransactionCount(20);
        assertEquals(20, TransactionService.getMaxTransactionCount().get());
    }

    @Test
    void run() {

        TransactionService.setMaxTransactionCount(100);

        List<Account> accounts = genericAccauntList();
        List<TransactionService> threads = genericThreadList(accounts);
        waitingForThreadsToExecute(threads);

        int totalAmount = accounts.stream()
                .mapToInt(Account::getMoney)
                .sum();

        assertEquals(5000, totalAmount);

        accounts.forEach(account -> {
            assertNotEquals(1000, account.getMoney());
            assertTrue(account.getMoney() > 0);
        });
    }

    private void waitingForThreadsToExecute(List<TransactionService> threads) {
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.fillInStackTrace();
            }
        });
    }

    private List<TransactionService> genericThreadList(List<Account> accounts) {
        return IntStream.range(0, 20)
                .mapToObj(i -> {
                    TransactionService transaction = new TransactionService(accounts, startAmount);
                    transaction.start();
                    return transaction;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Account> genericAccauntList() {
        return IntStream.range(0, 5)
                .mapToObj(i -> new Account(String.valueOf(i), startAmount))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}