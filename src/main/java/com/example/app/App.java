package com.example.app;

import com.example.library.User;
import com.example.library.UserService;

public class App {
    public static void main(String[] args) {
        System.out.println("I love bazel");

        UserService userService = new UserService();
        System.out.println("All users: " + userService.findAll());

        User user = userService.findById(1L);
        System.out.println("Found user: " + user);

        User newUser = userService.createUser("bob@example.com", "Bob Builder");
        System.out.println("Created user: " + newUser);
    }
}
