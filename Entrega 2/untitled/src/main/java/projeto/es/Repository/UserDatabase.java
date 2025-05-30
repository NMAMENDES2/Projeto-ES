package projeto.es.Repository;

import projeto.es.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private static Map<String, User> users = new HashMap<>();

    static {
        users.put("alice", new User("alice", "password123", true));
        users.put("bob", new User("bob", "password123", false));
    }

    public static User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null; // or throw an exception
    }
}