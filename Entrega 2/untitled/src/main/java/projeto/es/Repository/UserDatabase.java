package projeto.es.Repository;

import projeto.es.models.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private static final String FILE_PATH = "users.txt";
    private static Map<String, User> users = new HashMap<>();

    static {
        // Default users for testing
        users.put("alice", new User("alice", "password123", true));
        users.put("bob", new User("bob", "password123", false));
        
        // Save default users to file if it doesn't exist
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("DEBUG: Creating new users file with default users");
            saveUser(users.get("alice"));
            saveUser(users.get("bob"));
        }
    }

    public static void saveUser(User user) {
        List<User> usersList = getAllUsers();
        
        // Update existing user if it exists
        boolean found = false;
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getName().equals(user.getName())) {
                usersList.set(i, user);
                found = true;
                break;
            }
        }
        
        // Add new user if it doesn't exist
        if (!found) {
            usersList.add(user);
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (User u : usersList) {
                writer.println(String.format("%s,%s,%b",
                    u.getName(),
                    u.getPassword(),
                    u.isAdmin()));
            }
            System.out.println("DEBUG: Saved users to " + new File(FILE_PATH).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("ERROR: Failed to save users - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            System.out.println("DEBUG: Creating new users file with default users");
            // Create default admin user if file doesn't exist
            User admin = new User("admin", "admin", true);
            User testUser = new User("test", "test", false);
            usersList.add(admin);
            usersList.add(testUser);
            saveUser(admin); // This will save both users since we're using the list
            return usersList;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        User user = new User(
                            parts[0],
                            parts[1],
                            Boolean.parseBoolean(parts[2])
                        );
                        usersList.add(user);
                        System.out.println("DEBUG: Loaded user - " + user.getName());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load users - " + e.getMessage());
            e.printStackTrace();
        }
        return usersList;
    }

    public static User getUserByUsername(String username) {
        System.out.println("DEBUG: Looking for user - " + username);
        User user = getAllUsers().stream()
                .filter(u -> u.getName().equals(username))
                .findFirst()
                .orElse(null);
        System.out.println("DEBUG: Found user? " + (user != null));
        if (user != null) {
            System.out.println("DEBUG: User details - Name: " + user.getName() + ", IsAdmin: " + user.isAdmin());
        }
        return user;
    }

    public static void deleteUser(String username) {
        List<User> usersList = getAllUsers();
        usersList.removeIf(user -> user.getName().equals(username));
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (User user : usersList) {
                writer.println(String.format("%s,%s,%b",
                    user.getName(),
                    user.getPassword(),
                    user.isAdmin()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User authenticate(String username, String password) {
        System.out.println("DEBUG: Attempting to authenticate user - " + username);
        
        // First check the file-based storage
        User fileUser = getUserByUsername(username);
        if (fileUser != null) {
            System.out.println("DEBUG: Found user in file storage - " + username);
            if (fileUser.getPassword().equals(password)) {
                System.out.println("DEBUG: Password matches for file user - " + username);
                return fileUser;
            } else {
                System.out.println("DEBUG: Password mismatch for file user - " + username);
            }
        }
        
        // Then check the static map (for backward compatibility)
        User staticUser = users.get(username);
        if (staticUser != null) {
            System.out.println("DEBUG: Found user in static map - " + username);
            if (staticUser.getPassword().equals(password)) {
                System.out.println("DEBUG: Password matches for static user - " + username);
                // If user exists in static map but not in file, save it
                saveUser(staticUser);
                return staticUser;
            } else {
                System.out.println("DEBUG: Password mismatch for static user - " + username);
            }
        }
        
        System.out.println("DEBUG: Authentication failed for user - " + username);
        return null;
    }
}