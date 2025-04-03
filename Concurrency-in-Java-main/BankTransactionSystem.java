import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Account {
    private final String accountNumber;
    private double balance;
    private final Lock lock = new ReentrantLock();

    public Account(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        lock.lock();
        try {
            balance += amount;
            System.out.println("Deposited " + amount + " to account " + accountNumber + ". New balance: " + balance);
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(double amount) {
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                System.out.println("Withdrew " + amount + " from account " + accountNumber + ". New balance: " + balance);
            } else {
                System.out.println("Insufficient funds for withdrawal from account " + accountNumber);
            }
        } finally {
            lock.unlock();
        }
    }

    public void transfer(Account target, double amount) {
        // Lock accounts in a consistent order to prevent deadlock
        Account firstLock = this;
        Account secondLock = target;

        if (this.accountNumber.compareTo(target.accountNumber) > 0) {
            firstLock = target;
            secondLock = this;
        }

        firstLock.lock.lock();
        try {
            secondLock.lock.lock();
            try {
                if (balance >= amount) {
                    withdraw(amount);
                    target.deposit(amount);
                    System.out.println("Transferred " + amount + " from account " + this.accountNumber + " to account " + target.accountNumber);
                } else {
                    System.out.println("Insufficient funds for transfer from account " + this.accountNumber);
                }
            } finally {
                secondLock.lock.unlock();
            }
        } finally {
            firstLock.lock.unlock();
        }
    }

    // Getter for balance
    public double getBalance() {
        return balance;
    }
}

class Transaction implements Runnable {
    private final Account source;
    private final Account target;
    private final double amount;

    public Transaction(Account source, Account target, double amount) {
        this.source = source;
        this.target = target;
        this.amount = amount;
    }

    @Override
    public void run() {
        source.transfer(target, amount);
    }
}

public class BankTransactionSystem {
    public static void main(String[] args) {
        Account account1 = new Account("12345", 1000);
        Account account2 = new Account("67890", 500);

        Thread t1 = new Thread(new Transaction(account1, account2, 300));
        Thread t2 = new Thread(new Transaction(account2, account1, 200));
        Thread t3 = new Thread(new Transaction(account1, account2, 800)); // This should fail due to insufficient funds

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Use the getter method to access the balance
        System.out.println("Final balance of account 1: " + account1.getBalance());
        System.out.println("Final balance of account 2: " + account2.getBalance());
    }
}