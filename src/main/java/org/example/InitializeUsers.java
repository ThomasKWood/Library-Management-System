package org.example;

public class InitializeUsers {

    protected Users users = new Users();

    public Users initUsers(){
        users.addUser(new User("alice", "pass123"));
        users.addUser(new User("bob", "pass456"));
        users.addUser(new User("charlie", "pass789"));
        return users;
    }
}
