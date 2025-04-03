import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Task implements Runnable {
    private final int id;
    private final CountDownLatch latch;

    public Task(int id, CountDownLatch latch) {
        this.id = id;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            // Simulate some work with a random sleep time
            int workTime = new Random().nextInt(3000) + 1000; // 1 to 3 seconds
            System.out.println("Task " + id + " is starting. It will take " + workTime + " milliseconds.");
            Thread.sleep(workTime);
            System.out.println("Task " + id + " is completed.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Task " + id + " was interrupted.");
        } finally {
            latch.countDown(); // Decrement the count of the latch
        }
    }
}

public class CountdownTimerExample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get the number of tasks and threads from user input
        System.out.print("Enter the number of tasks: ");
        int numberOfTasks = scanner.nextInt();

        System.out.print("Enter the number of threads in the pool: ");
        int numberOfThreads = scanner.nextInt();

        CountDownLatch latch = new CountDownLatch(numberOfTasks);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads); // Pool of user-defined threads

        // Submit tasks to the executor service
        for (int i = 1; i <= numberOfTasks; i++) {
            executorService.submit(new Task(i, latch));
        }

        try {
            latch.await(); // Wait for all tasks to complete
            System.out.println("All tasks are completed. Proceeding with the next steps.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread was interrupted.");
        } finally {
            executorService.shutdown(); // Shutdown the executor service
            scanner.close(); // Close the scanner
        }
    }
}