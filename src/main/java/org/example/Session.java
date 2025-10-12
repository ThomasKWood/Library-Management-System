package org.example;

import java.io.PrintWriter;
import java.time.LocalDateTime;
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
                        prompt(input, output);
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
        login(input, output);
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
                if (thisBook != null && checkAvail(thisBook)) {
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

    public boolean checkAvail(Book thisBook) {

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
        int selection;

        selection = menu.borrowMenu(input, output, currUser, library);

        int bookIndex = selection-1;
        Book borrowedBook = library.getBook(bookIndex);
        selection = menu.bookDetails(input, output, borrowedBook);

        if (selection == -1) {
            return;
        }

        if (selection == 1) {
            boolean bAvail = checkAvail(bookIndex);
            boolean uElig = checkElig();

            // first check if user already has this book checked
            // RESP 17


            if (bAvail && uElig) {
                // update book
                LocalDateTime date = borrowedBook.setDueDateNow();
                // update record
                this.record.add(currUser.getUsername() + " borrowed " + borrowedBook.getTitle() + " - due " + date.toString());
                // update account
                currUser.addBorrowed(borrowedBook);
                // present info to user
                // get ack
                menu.confirmation(input, output, borrowedBook);
                // return
                menu.mainMenu(input,output);

                // TODO pop queue
                if (!borrowedBook.getAvailability()) {
                    borrowedBook.popFirst();
                }
            } else {
                // show hold menu

                // book is already on hold by user
                if (borrowedBook.checkQueue(currUser)) {
                    output.println("You already have a hold on this book.");

                }
                // book is already checked out by user
                else if (currUser.getBorrowed().contains(borrowedBook)) {
                    output.println("You already have this booked checked out.");
                }

                // book is checked out by someone else
                else if (uElig) {
                    output.println("The book you selected is checked out by someone else. Would you like to place a hold? ");
                    output.println("1: Yes\n2. No");
                    selection = menu.getPick(input, output, 1,2);
                    if (selection == -1) {
                        return;
                    }
                    if (selection == 1) {
                        borrowedBook.placeHold(currUser);
                        output.println("You have been added to the hold queue.");
                    } else {
                        borrow(input,output);
                    }
                } else if (bAvail && !uElig) {
                    output.println("You are currently at your borrow limit. You cannot borrow any more books at this time. Would you like to place a hold instead?");
                    output.println("1: Yes\n2. No");
                    selection = menu.getPick(input, output, 1,2);
                    if (selection == -1) {
                        return;
                    }
                    if (selection == 1) {
                        borrowedBook.placeHold(currUser);
                        output.println("You have been added to the hold queue.");
                    } else {
                        borrow(input,output);
                    }
                } else {
                    output.println("Something went wrong. Returning to main menu");
                }
            }
        } else {
            return; // will this work in main?
        }

    }

    public boolean checkElig() {
       return currUser.getBorrowed().size() < 3;
    }

    public ArrayList<String> getRecord() {
        return record;
    }

    public void returnBook(Scanner input, PrintWriter output) {
        if (currUser.getBorrowed().isEmpty()) {
            output.println("You have no books currently checked out. Returning to main menu.");
        } else {
            output.println("Here are the books you have checked out. Pick one you would like to return:");
            int i = 1;
            for (Book book : currUser.getBorrowed()) {
                output.println(i + ". " + book.getTitle() + " by " + book.getAuthor() + " due on " + book.setDueDateNow().toString());
                i++;
            }
            int selection = menu.getPick(input, output, 1, i);
            if (selection == -1) {
                return;
            }
            Book returnBook = currUser.getBorrowed().get(selection-1);
            selection = menu.bookDetailsReturn(input, output, returnBook);
            if (selection == -1) {
                return;
            }
            // do return process
            currUser.getBorrowed().remove(returnBook);
            returnBook.returnBook();
            this.record.add(returnBook.getTitle()+" returned on " + LocalDateTime.now().toString());
            // notify
            if (returnBook.firstQueue() != null) {
                returnBook.firstQueue().addNoti(returnBook.getTitle());
            }
        }
    }
}
