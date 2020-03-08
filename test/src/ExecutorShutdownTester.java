import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorShutdownTester {
    static void pauseCurrentThread(int timeoutInMillis) {
        try {
            Thread.sleep(timeoutInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void testShutdownOnExecutor() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        int i = 1;
        Runnable runnable1 = getRunnable(i++);

        Runnable runnable2 = getRunnable(i);

        System.out.println("Submitting both the tasks");
        executorService.submit(runnable1);
        executorService.submit(runnable2);
        int timeoutInMillis = 10;
        pauseCurrentThread(timeoutInMillis);

        System.out.println("shutting the executor");
        executorService.shutdown();
        System.out.println(String.format("[%d] executor shut down. Starting wait...",System.currentTimeMillis()));
        try {
            executorService.awaitTermination(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("[%d] termination wait finished",System.currentTimeMillis()));
    }

    private static Runnable getRunnable(int i) {
        return new Runnable() {
            @Override
            public void run() {

                System.out.println(String.format("[%d]runnable_%d: sleeping the thread.", System.currentTimeMillis(), i));
                pauseCurrentThread(10000);
                System.out.println(String.format("[%d]runnable_%d: freeing the thread.", System.currentTimeMillis(), i));
            }
        };
    }
}