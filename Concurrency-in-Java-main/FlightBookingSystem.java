import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class Flight {
    private final String flightNumber;
    private int availableSeats;

    public Flight(String flightNumber, int availableSeats) {
        this.flightNumber = flightNumber;
        this.availableSeats = availableSeats;
    }

    public synchronized boolean bookSeat() {
        if (availableSeats > 0) {
            availableSeats--;
            return true;
        }
        return false;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }
}

class BookingTask implements Callable<String> {
    private final Flight flight;

    public BookingTask(Flight flight) {
        this.flight = flight;
    }

    @Override
    public String call() {
        if (flight.bookSeat()) {
            return "Booking successful for flight: " + flight.getFlightNumber();
        } else {
            return "Booking failed for flight: " + flight.getFlightNumber() + " - No available seats.";
        }
    }
}

public class FlightBookingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get flight details from user
        System.out.print("Enter flight number: ");
        String flightNumber = scanner.nextLine();

        System.out.print("Enter number of available seats: ");
        int availableSeats = scanner.nextInt();

        System.out.print("Enter number of booking requests: ");
        int numberOfRequests = scanner.nextInt();

        Flight flight = new Flight(flightNumber, availableSeats);
        ExecutorService executorService = Executors.newFixedThreadPool(3); // 3 concurrent bookings

        Future<String>[] futures = new Future[numberOfRequests]; // Dynamic number of booking requests

        // Simulate booking requests
        for (int i = 0; i < numberOfRequests; i++) {
            BookingTask task = new BookingTask(flight);
            futures[i] = executorService.submit(task);
        }

        // Retrieve results
        for (int i = 0; i < numberOfRequests; i++) {
            try {
                String result = futures[i].get();
                System.out.println(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Shutdown the executor service
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        scanner.close();
    }
}