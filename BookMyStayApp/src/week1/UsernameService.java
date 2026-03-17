package week1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class UsernameService {

    // username -> userId
    private ConcurrentHashMap<String, Integer> usernameMap;

    // username -> attempt count
    private ConcurrentHashMap<String, Integer> attemptFrequency;

    public UsernameService() {
        usernameMap = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();
    }

    // Register a new username
    public boolean registerUser(String username, int userId) {

        if (usernameMap.containsKey(username)) {
            return false; // already taken
        }

        usernameMap.put(username, userId);
        return true;
    }

    // Check availability
    public boolean checkAvailability(String username) {

        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        return !usernameMap.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // Append numbers
        for (int i = 1; i <= 5; i++) {

            String candidate = username + i;

            if (!usernameMap.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        // Replace "_" with "."
        String modified = username.replace("_", ".");

        if (!usernameMap.containsKey(modified)) {
            suggestions.add(modified);
        }

        // Random number suffix
        int random = new Random().nextInt(999);
        String randomCandidate = username + random;

        if (!usernameMap.containsKey(randomCandidate)) {
            suggestions.add(randomCandidate);
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String mostAttempted = null;
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {

            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        if (mostAttempted == null) {
            return "No attempts yet";
        }

        return mostAttempted + " (" + maxCount + " attempts)";
    }
}

public class UsernameCheckerSystem {

    public static void main(String[] args) {

        UsernameService service = new UsernameService();

        // Register users
        service.registerUser("john_doe", 1001);
        service.registerUser("admin", 1);
        service.registerUser("alex123", 1002);

        // Check availability
        System.out.println("checkAvailability(\"john_doe\") → "
                + service.checkAvailability("john_doe"));

        System.out.println("checkAvailability(\"jane_smith\") → "
                + service.checkAvailability("jane_smith"));

        // Suggestions
        System.out.println("\nSuggestions for 'john_doe':");

        List<String> suggestions = service.suggestAlternatives("john_doe");

        for (String s : suggestions) {
            System.out.println(s);
        }

        // Simulate attempts
        service.checkAvailability("admin");
        service.checkAvailability("admin");
        service.checkAvailability("admin");

        service.checkAvailability("guest");
        service.checkAvailability("guest");

        // Most attempted username
        System.out.println("\nMost attempted username:");
        System.out.println(service.getMostAttempted());
    }
}