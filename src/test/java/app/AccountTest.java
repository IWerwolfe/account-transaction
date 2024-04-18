package app;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountTest {

    private final Account account = new Account("1", 0);
    private final List<Integer> listAmount = List.of(10, 20, 30);
    private final List<Integer> bigListAmount = List.of(10, 20, 30, 10, 20, 30);
    private final List<Integer> listIncorrectAmount = List.of(10, -20, 30, 0);

    @Test
    void addMoney_correct() {

        account.setMoney(0);

        account.addMoney(10);
        assertEquals(10, account.getMoney());

        listAmount.forEach(account::addMoney);
        assertEquals(70, account.getMoney());
    }

    @Test
    void addMoney_MultiThreads() {

        account.setMoney(0);

        List<Thread> threads = IntStream.range(0, 10)
                .mapToObj(i -> {
                    Thread thread = new Thread(() -> bigListAmount.forEach(account::addMoney));
                    thread.start();
                    return thread;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(1200, account.getMoney());
    }

    @Test
    void addMoney_MultiThreads2() {

        account.setMoney(1000);

        List<Thread> threads = IntStream.range(0, 10)
                .mapToObj(i -> {
                    Thread thread = new Thread(() ->
                            bigListAmount.forEach(amount -> {
                                if (i % 2 == 0) {
                                    account.addMoney(amount);
                                } else {
                                    account.subtractMoney(amount);
                                }
                            }));
                    thread.start();
                    return thread;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(1000, account.getMoney());
    }

    @Test
    void addMoney_incorrect() {

        account.setMoney(0);

        account.addMoney(-10);
        assertEquals(0, account.getMoney());

        listIncorrectAmount.forEach(account::addMoney);
        assertEquals(40, account.getMoney());
    }

    @Test
    void subtractMoney_correct() {

        account.setMoney(100);

        account.subtractMoney(10);
        assertEquals(90, account.getMoney());

        listAmount.forEach(account::subtractMoney);
        assertEquals(30, account.getMoney());

        account.subtractMoney(30);
        assertEquals(0, account.getMoney());
    }

    @Test
    void subtractMoney_incorrect() {

        account.setMoney(100);

        account.subtractMoney(-10);
        assertEquals(100, account.getMoney());

        account.subtractMoney(-1000);
        assertEquals(100, account.getMoney());

        listIncorrectAmount.forEach(account::subtractMoney);
        assertEquals(60, account.getMoney());
    }

    @Test
    void getMoney() {
        account.setMoney(100);
        assertEquals(100, account.getMoney());
    }

    @Test
    void setMoney() {

        account.setMoney(100);
        assertEquals(100, account.getMoney());

        account.setMoney(-100);
        assertEquals(100, account.getMoney());
    }

    @Test
    void testToString() {
        account.setMoney(100);
        assertEquals("id: 1 - 100", account.toString());
    }
}