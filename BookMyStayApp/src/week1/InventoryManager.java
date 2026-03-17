package week1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class InventoryManager {

    // Product stock storage
    private ConcurrentHashMap<String, AtomicInteger> stockMap;

    // Waiting list for products (FIFO)
    private ConcurrentHashMap<String, LinkedList<Integer>> waitingList;

    public InventoryManager() {
        stockMap = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    // Add product with stock
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock availability
    public int checkStock(String productId) {

        AtomicInteger stock = stockMap.get(productId);

        if (stock == null) {
            return 0;
        }

        return stock.get();
    }

    // Purchase item
    public synchronized String purchaseItem(String productId, int userId) {

        AtomicInteger stock = stockMap.get(productId);

        if (stock == null) {
            return "Product not found";
        }

        if (stock.get() > 0) {

            int remaining = stock.decrementAndGet();

            return "Success! User " + userId +
                    " purchased item. Remaining stock: " + remaining;
        } else {

            LinkedList<Integer> queue = waitingList.get(productId);

            queue.add(userId);

            return "Out of stock. User " + userId +
                    " added to waiting list. Position #" + queue.size();
        }
    }

    // Process waiting list when stock refills
    public void restock(String productId, int quantity) {

        AtomicInteger stock = stockMap.get(productId);

        if (stock == null) {
            return;
        }

        stock.addAndGet(quantity);

        LinkedList<Integer> queue = waitingList.get(productId);

        while (stock.get() > 0 && !queue.isEmpty()) {

            int userId = queue.poll();
            stock.decrementAndGet();

            System.out.println("Waiting list purchase processed for user: " + userId);
        }
    }

    // Show waiting list
    public void showWaitingList(String productId) {

        LinkedList<Integer> queue = waitingList.get(productId);

        if (queue == null || queue.isEmpty()) {
            System.out.println("Waiting list empty");
            return;
        }

        System.out.println("Waiting list for " + productId + ": " + queue);
    }
}

public class FlashSaleSystem {

    public static void main(String[] args) {

        InventoryManager manager = new InventoryManager();

        // Add product with 100 stock
        manager.addProduct("IPHONE15_256GB", 100);

        // Check stock
        System.out.println("Stock: "
                + manager.checkStock("IPHONE15_256GB") + " units available");

        // Simulate purchases
        for (int i = 1; i <= 105; i++) {

            String result = manager.purchaseItem("IPHONE15_256GB", i);

            System.out.println(result);
        }

        // Show waiting list
        manager.showWaitingList("IPHONE15_256GB");

        // Restock items
        System.out.println("\nRestocking 5 units...\n");
        manager.restock("IPHONE15_256GB", 5);

        // Check stock again
        System.out.println("\nStock after restock: "
                + manager.checkStock("IPHONE15_256GB"));
    }
}