package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Session {
    private User currUser;
    private Catalogue library;
    private Users users;
    private Menu menu;
    private ArrayList<String> record;

    public Session(Catalogue library, Users users) {
        this.currUser = null;
        this.users = users;
        this.library = library;
        this.menu = new Menu();
        this.record = new ArrayList<>();
    }

    public void login(Scanner input, PrintWriter output) {
        //
        if (this.currUser == null) {
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
                        this.currUser = user; // TODO replace with setter
                        output.println("Welcome " + this.currUser.getUsername());
                    }
                }
            }
            // this will also print if the username doesn't exist. technically this is more secure
            if (this.currUser == null) {
                output.println("Username or password incorrect.");
                login(input, output);
            }
        } else {
            output.println("You are already logged in as: " + this.currUser.getUsername());
        }
        output.flush();
    }

    public boolean signedIn() {
        return this.currUser != null;
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
            this.currUser = null;
        } else if (lineIn.contains("No")) {
            return;
        } else {
            output.println("Input not recognized");
            logout(input, output);
        }
        output.flush();
    }

    public User getUser() {
        return this.currUser;
    }

    public void setUser(User usr){
        this.currUser = usr;
    }

    public void prompt(Scanner input, PrintWriter output) {
        if (!signedIn()) {
            output.println("Not logged in!");
        } else {
            int sent = 0;

            while (this.currUser.hasNoti()) {
                String thisTitle = this.currUser.popNoti();
                Book thisBook = library.getBook(thisTitle);

                // check valid & available
                if (thisBook != null && thisBook.getAvailability()) {
                    output.println(thisTitle + " is available!");
                    sent++;
                }
            }

            if (sent == 0) {
                output.println("No notifications!");
            }
        }
    }

    public boolean checkAvail(int bookIndex) {
        Book thisBook = library.getBook(bookIndex);

        if (thisBook.getAvailability()) {
            return true;
        } else {
            if (thisBook.getStatusCode().equals(Book.StatusCode.CHECKED)) {
                return false; // book is checked out
            } else if (thisBook.firstQueue() != null) {
                // check hold queue to see if current signed in is infront
                if (thisBook.firstQueue().getUsername().equals(currUser.getUsername())) {
                    return true; // currUser is first in hold queue
                } else {
                    return false;
                }

            }
            return false;
        }
    }

    public void borrow(Scanner input, PrintWriter output) {
        return;
    }

    public boolean checkElig() {
       return currUser.getBorrowed().size() < 3;
    }

    public ArrayList<String> getRecord() {
        return record;
    }
}
