package org.example;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private User currUser;
    private final Catalogue library;
    private final Users users;
    private final ArrayList<String> record;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        PrintWriter output = new PrintWriter(System.out);

        Main app = new Main();
        //app.mainInit();

        app.login(input, output);

        while (app.signedIn()) {
            int selection = app.mainMenu(input, output);

            switch (selection) {
                case 1:
                    app.borrow(input, output);
                    break;
                case 2:
                    app.returnBook(input, output);
                    break;
                case 3:
                    app.logout(input, output);
                    break;

            }
        }
    }

    public Main() {
        InitializeLibrary lib = new InitializeLibrary();
        library = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        users = sampleUsers.initUsers();
        record = new ArrayList<>();
    }

    public void login(Scanner input, PrintWriter output) {
        //
        if (this.currUser == null) {
            output.println("Please enter your username: ");
            output.flush();
            String usr;
            if (input.hasNextLine()) {
                usr = input.nextLine();
            } else {
                return;
            }
            output.println("Please enter your password: ");
            output.flush();
            String pass;
            if (input.hasNextLine()) {
                pass = input.nextLine();
            } else {
                return;
            }

            if (usr == null || pass == null || usr.isEmpty() || pass.isEmpty()) {
                output.println("Username or password cannot be blank.");
                output.flush();
                login(input, output);
            }
            // good thing this isn't algorithms where time complexity matters
            for (User user : this.users.getUsers()) {
                if (user.getUsername().equals(usr)) {
                    if (user.passwordCorrect(pass)) {
                        setUser(user);
                        output.println("Welcome " + this.currUser.getUsername());
                        output.flush();
                        prompt(output);
                    }
                }
            }
            // this will also print if the username doesn't exist. technically this is more secure
            if (this.currUser == null) {
                output.println("Username or password incorrect.");
                output.flush();
                login(input, output);
            }
        } else {
            output.println("You are already logged in as: " + this.currUser.getUsername());
            output.flush();
        }
    }

    public void prompt(PrintWriter output) {
        if (!signedIn()) {
            output.println("Not logged in!");
            output.flush();
        } else {
            int sent = 0;

            while (this.currUser.hasNoti()) {
                String thisTitle = this.currUser.popNoti();
                Book thisBook = this.library.getBook(thisTitle);

                // check valid & available
                if (thisBook != null && checkAvail(thisBook)) {
                    output.println(thisTitle + " is available!");
                    output.flush();
                    sent++;
                }
            }

            if (sent == 0) {
                output.println("No notifications!");
                output.flush();
            }
        }
    }

    public boolean signedIn() {
        return this.currUser != null;
    }

    public void logout(Scanner input, PrintWriter output) {
        output.println("Are you sure you want to sign out? Yes/No");
        output.flush();
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
            output.flush();
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

    public boolean checkAvail(int bookIndex) {
        Book thisBook = this.library.getBook(bookIndex);

        if (thisBook.getAvailability()) {
            return true;
        } else {
            if (thisBook.getStatusCode().equals(Book.StatusCode.CHECKED)) {
                return false; // book is checked out
            } else if (thisBook.firstQueue() != null) {
                // check hold queue to see if current signed in is in front
                return thisBook.firstQueue().getUsername().equals(this.currUser.getUsername()); // currUser is first in hold queue

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
                // check hold queue to see if current signed in is in front
                return thisBook.firstQueue().getUsername().equals(this.currUser.getUsername()); // currUser is first in hold queue

            }
            return false;
        }
    }

    public void borrow(Scanner input, PrintWriter output) {
        int selection;

        selection = borrowMenu(input, output, this.currUser, this.library);

        int bookIndex = selection-1;
        Book borrowedBook = this.library.getBook(bookIndex);
        selection = bookDetails(input, output, borrowedBook);

        if (selection == -1) {
            return;
        }

        if (selection == 1) {
            boolean bAvailable = checkAvail(bookIndex);
            boolean uEligible = checkElig();

            // first check if user already has this book checked
            // RESP 17


            if (bAvailable && uEligible) {
                // update book
                LocalDateTime date = borrowedBook.setDueDateNow();
                // update record
                this.record.add(this.currUser.getUsername() + " borrowed " + borrowedBook.getTitle() + " - due " + date.toString());
                // update account
                this.currUser.addBorrowed(borrowedBook);
                // present info to user
                // get ack
                confirmation(input, output, borrowedBook);
                // return
                mainMenu(input,output);

                if (!borrowedBook.getAvailability()) {
                    borrowedBook.popFirst();
                }
            } else {
                // show hold menu

                // book is already on hold by user
                if (borrowedBook.checkQueue(this.currUser)) {
                    output.println("You already have a hold on this book.");
                    output.flush();

                }
                // book is already checked out by user
                else if (this.currUser.getBorrowed().contains(borrowedBook)) {
                    output.println("You already have this booked checked out.");
                    output.flush();
                }

                // book is checked out by someone else
                else if (uEligible) {
                    output.println("The book you selected is checked out by someone else. Would you like to place a hold? ");
                    output.println("1: Yes\n2. No");
                    output.flush();
                    selection = getPick(input, output, 1,2);
                    if (selection == -1) {
                        return;
                    }
                    if (selection == 1) {
                        borrowedBook.placeHold(this.currUser);
                        output.println("You have been added to the hold queue.");
                        output.flush();
                    } else {
                        borrow(input,output);
                    }
                } else if (bAvailable) {
                    output.println("You are currently at your borrow limit. You cannot borrow any more books at this time. Would you like to place a hold instead?");
                    output.println("1: Yes\n2. No");
                    output.flush();
                    selection = getPick(input, output, 1,2);
                    if (selection == -1) {
                        return;
                    }
                    if (selection == 1) {
                        borrowedBook.placeHold(this.currUser);
                        output.println("You have been added to the hold queue.");
                        output.flush();
                    } else {
                        borrow(input,output);
                    }
                } else {
                    output.println("Something went wrong. Returning to main menu");
                    output.flush();
                }
            }
        }
    }

    public boolean checkElig() {
        return this.currUser.getBorrowed().size() < 3;
    }

    public ArrayList<String> getRecord() {
        return this.record;
    }

    public void returnBook(Scanner input, PrintWriter output) {
        if (this.currUser.getBorrowed().isEmpty()) {
            output.println("You have no books currently checked out. Returning to main menu.");
            output.flush();
        } else {
            output.println("Here are the books you have checked out. Pick one you would like to return:");
            output.flush();
            int i = 1;
            for (Book book : this.currUser.getBorrowed()) {
                output.println(i + ". " + book.getTitle() + " by " + book.getAuthor() + " due on " + book.setDueDateNow().toString());
                output.flush();
                i++;
            }
            int selection = getPick(input, output, 1, i);
            if (selection == -1) {
                return;
            }
            Book returnBook = this.currUser.getBorrowed().get(selection-1);
            selection = bookDetailsReturn(input, output, returnBook);
            if (selection == -1 || selection == 2) {
                return;
            }
            // do return process
            this.currUser.getBorrowed().remove(returnBook);
            returnBook.returnBook();
            this.record.add(returnBook.getTitle()+" returned on " + LocalDateTime.now());
            // notify
            if (returnBook.firstQueue() != null) {
                returnBook.firstQueue().addNoti(returnBook.getTitle());
            }
        }
    }

    public int mainMenu(Scanner input, PrintWriter output) {
        output.println("\n---------------- MAIN MENU ----------------\n1. Borrow\n2. Return\n3. Sign-out");
        output.flush();
        return getPick(input, output, 1,3);
    }

    public int borrowMenu(Scanner input, PrintWriter output, User user, Catalogue catalogue) {
        output.println("\n---------------- BORROW ----------------");
        output.println("\nYou have " + user.getBorrowed().size() + " books borrowed.");
        output.println("\nSelect a book: ");
        output.flush();
        for (int i = 0; i < catalogue.getSize(); i++) {
            Book thisBook = catalogue.getBook(i);
            if (thisBook.getStatusCode().equals(Book.StatusCode.AVAIL) || thisBook.getStatusCode().equals(Book.StatusCode.HOLD)) {
                if (thisBook.getStatusCode().equals(Book.StatusCode.HOLD) && thisBook.checkQueue(user)) {
                    output.println(i + 1 + ". " + thisBook.getTitle() + " by " + thisBook.getAuthor() + " - Available (on hold for you)");
                    output.flush();
                    continue;
                } else if (thisBook.getStatusCode().equals(Book.StatusCode.HOLD)) {
                    output.println(i + 1 + ". " + thisBook.getTitle() + " by " + thisBook.getAuthor() + " - On Hold");
                    output.flush();
                    continue;
                } else {
                    output.println(i + 1 + ". " + thisBook.getTitle() + " by " + thisBook.getAuthor() + " - Available");
                    output.flush();
                }
            } else if (thisBook.getStatusCode().equals(Book.StatusCode.CHECKED)) { // could be else
                // print available on
                output.println(i + 1 + ". " + thisBook.getTitle() + " by " + thisBook.getAuthor() + " - " + thisBook.getStatusCode().getLabel() + " - Available on " + thisBook.getDue().toString());
                output.flush();
            }
        }
        // get pick
        int selection = 0;
        if (catalogue.getSize() > 0) {
            selection = getPick(input, output, 1, catalogue.getSize());
        }


        return selection;
    }

    public int bookDetails(Scanner input, PrintWriter output, Book book) {
        output.println("\nYou have selected:");
        output.println(book.getTitle());
        output.println(book.getAuthor());
        output.println(book.getStatusCode().getLabel());
        output.println("\n Are you sure you would like to proceed with borrowing this book?");
        output.println("1. Yes\n2. No");
        output.flush();
        int selected = getPick(input, output, 1, 2);

        if (selected == 1) {
            output.println("You have selected Yes. Proceeding to booking...");
            output.flush();
        } else if (selected == 0) {
            output.println("You have selected No. Bringing you back to main menu.");
            output.flush();

        }
        return selected;
    }

    public int bookDetailsReturn(Scanner input, PrintWriter output, Book book) {
        output.println("\nYou have selected:");
        output.println(book.getTitle());
        output.println(book.getAuthor());
        output.println("\n Are you sure you would like to proceed with returning this book?");
        output.println("1. Yes\n2. No");
        output.flush();
        int selected = getPick(input, output, 1, 2);

        if (selected == 1) {
            output.println("You have selected Yes. Processing return...");
            output.flush();
        } else if (selected == 0) {
            output.println("You have selected No. Bringing you back to main menu.");
            output.flush();
        }
        return selected;
    }

    protected int getPick(Scanner input, PrintWriter output, int rangeMin, int rangeMax) {
        int selection;
        if (!input.hasNextLine()) {
            return -1;
        }
        // get number
        try {
            selection = Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
            output.println("Invalid Input - Input was not recognized. Please try again: ");
            output.flush();
            selection = getPick(input, output, rangeMin, rangeMax);
        }

        if (selection < rangeMin || selection > rangeMax) {
            output.println("Invalid Option - Please enter a number that is from " + rangeMin + " to " + rangeMax + ".");
            output.flush();
            selection = getPick(input, output, rangeMin, rangeMax);
        }

        return selection;
    }

    public void confirmation(Scanner input, PrintWriter output, Book book) {
        output.println("You have successfully checked out " + book.getTitle() + " by " + book.getAuthor() + ". It is due on the " + book.setDueDateNow());
        output.println("Please acknowledge this confirmation by entering 1: ");
        output.flush();
        getPick(input, output, 1,1);
    }

    public Users getUsers() {
        return this.users;
    }

    public Catalogue getCatalogue() {
        return this.library;
    }
}

