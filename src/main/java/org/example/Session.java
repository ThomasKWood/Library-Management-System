package org.example;

import jdk.jfr.Category;

import java.io.PrintWriter;
import java.util.Scanner;

public class Session {
    private User currUser;
    private Catalogue library;
    private Users users;

    public Session(Catalogue library, Users users) {
        currUser = null;
        this.users = users;
        this.library = library;
    }

    public void login(Scanner input, PrintWriter output) {
        //
        if (currUser == null) {
            output.println("Please enter your username: ");
            String usr = input.nextLine();
            output.println("Please enter your password: ");
            String pass = input.nextLine();

            if (usr == null || pass == null || usr.equals("") || pass.equals("")) {
                output.println("Username or password cannot be blank.");
            }
            // good thing this isnt algorithms where time complexity matters
            for (User user : users.getUsers()) {
                if (user.getUsername().equals(usr)) {

                    if (user.passwordCorrect(pass)) {
                        currUser = user;
                        output.println("Welcome " + currUser.getUsername());
                        output.flush();
                        return;
                    }
                }
            }
            // this will also print if the username doesn't exist. technically this is more secure
            output.println("Username or password incorrect.");
            output.flush();
        } else {
            output.println("You are already logged in as: " + currUser.getUsername());
            output.flush();
        }
        return;
    }

    public boolean signedIn() {
        return false;
    }
}
