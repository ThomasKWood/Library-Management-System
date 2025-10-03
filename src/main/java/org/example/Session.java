package org.example;

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
            String usr;
            if (input.hasNextLine()) {
                usr = input.nextLine();
            } else {
                return;
            }
            output.println("Please enter your password: ");
            String pass;
            if (input.hasNextLine()) {
                pass = input.nextLine();
            } else {
                return;
            }

            if (usr == null || pass == null || usr.equals("") || pass.equals("")) {
                output.println("Username or password cannot be blank.");
                login(input, output);
            }
            // good thing this isnt algorithms where time complexity matters
            for (User user : users.getUsers()) {
                if (user.getUsername().equals(usr)) {
                    if (user.passwordCorrect(pass)) {
                        currUser = user;
                        output.println("Welcome " + currUser.getUsername());
                    }
                }
            }
            // this will also print if the username doesn't exist. technically this is more secure
            if (currUser == null) {
                output.println("Username or password incorrect.");
                login(input, output);
            }
        } else {
            output.println("You are already logged in as: " + currUser.getUsername());
        }
        output.flush();
    }

    public boolean signedIn() {
        return currUser != null;
    }

    public void logout(Scanner input, PrintWriter output) {
        output.println("Are you sure you want to sign out? Yes/No");
        String lineIn;
        if (input.hasNextLine()) {
            lineIn = input.nextLine();
        } else {
            return;
        }
        if (lineIn.contains("Yes")) {
            currUser = null;
        } else if (lineIn.contains("No")) {
            return;
        } else {
            output.println("Input not recognized");
            logout(input, output);
        }
        output.flush();
    }
}
