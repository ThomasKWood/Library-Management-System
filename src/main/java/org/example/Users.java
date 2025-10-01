package org.example;

import java.util.ArrayList;

public class Users {
    protected ArrayList<User> users;

    public Users() {
        users = new ArrayList<User>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User getUser(String usr) {
        for (User user : users) {
            if (user.getUsername().equals(usr)) {
                return user;
            }
        }
        return null;
    }

    public int getSize() {
        return users.size();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

}
