import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Inventory {
    private int stock;

    public Inventory(int initialStock) {
        this.stock = initialStock;
    }

    public synchronized boolean processOrder(int quantity) {
        if (quantity <= stock) {
            stock -= quantity;
            System.out.println("Processed order of quantity: " + quantity + ". Remaining stock: " + stock);
            return true;
        } else {
            System.out.println("Order of quantity: " + quantity + " cannot be processed. Insufficient stock.");
            return false;
        }
    }
}

class Order {
    private final int id;
    private final int quantity;

    public Order(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }
}

class OrderProcessor implements Runnable {
    private final Inventory inventory;
    private final Order order;

    public OrderProcessor(Inventory inventory, Order order) {
        this.inventory = inventory;
        this.order = order;
    }

    @Override
    public void run() {
        // Simulate processing time
        try {
            Thread.sleep(new Random().nextInt(1000)); // Simulate variable processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        inventory.processOrder(order.getQuantity());
    }
}

public class MultiThreadedOrderProcessingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get initial stock and number of orders from user input
        System.out.print("Enter the initial stock: ");
        int initialStock = scanner.nextInt();

        System.out.print("Enter the number of orders to process: ");
        int numberOfOrders = scanner.nextInt();

        Inventory inventory = new Inventory(initialStock); // Initialize inventory with user-defined stock
        List<Thread> threads = new ArrayList<>();

        // Create and start order processing threads
        for (int i = 1; i <= numberOfOrders; i++) {
            int quantity = new Random().nextInt(15) + 1; // Random order quantity between 1 and 15
            Order order = new Order(i, quantity);
            Thread thread = new Thread(new OrderProcessor(inventory, order));
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("All orders processed.");
        scanner.close(); // Close the scanner
    }
}