
import ClientService.Client;
import ClientService.Server;
import Configuration.GlobalConfiguration;
import Log.Logging;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyTest {
    // Simulate 100 concurrent clients
    private static final int THREAD_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        // 1. start server
        new Thread(() -> {
            try {
                Server.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 2. waiting RMI start...
        Logging.info("TEST      | Waiting for RMI server to start (5000ms) ...");
        Thread.sleep(5000);

        // initialize executor
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startGate = new CountDownLatch(1); // 用于同时起跑
        CountDownLatch endGate = new CountDownLatch(THREAD_COUNT); // 用于等待全部结束

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Logging.info("TEST      | --- Starting High Concurrency Test with {} threads ---", THREAD_COUNT);

        // 3. submit task
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    // create unique client for each thread
                    Client client = new Client();

                    // waiting to start simultaneously
                    startGate.await();

                    // execute
                    client.getTicket();
                    // just simply assume that everyone vote to NO.1 candidate.
                    client.vote(1);

                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    Logging.error("TEST      | Thread task failed: {}", e.getMessage());
                } finally {
                    endGate.countDown();
                }
            });
        }

        long startTime = System.currentTimeMillis();
        // run!
        startGate.countDown();

        // waiting all thread finish
        endGate.await();
        long endTime = System.currentTimeMillis();

        // verification
        Logging.info("TEST      | --- Test Completed ---");
        Logging.info("TEST      | Time taken: {} ms", (endTime - startTime));
        Logging.info("TEST      | Successful votes: {}", successCount.get());
        Logging.info("TEST      | Failed attempts: {}", failCount.get());

        // verify server's final count
        try {
            Client checker = new Client();
            checker.getTicket();
            checker.vote(1); // we have to vote 1 more to see the result
            Map<Integer, Integer> results = checker.fetchVotingResults();

            // check candidate 1's votes
            int finalVotesForOption1 = results.get(1);
            Logging.info("TEST      | Final server count for Option 1: {}", finalVotesForOption1);

            if (finalVotesForOption1 == successCount.get() + 1) { // +1 because checker vote 1 as well.
                Logging.info("TEST      | SUCCESS: Data consistency verified. No race conditions detected.");
            } else {
                Logging.error("TEST      | FAILURE: Data inconsistency! Possible race condition.");
            }
        } catch (Exception e) {
            Logging.error("TEST      | Result verification failed: {}", e.getMessage());
        }

        // 7. shutdown
        executor.shutdown();
        Server.terminate();
        System.exit(0);
    }
}