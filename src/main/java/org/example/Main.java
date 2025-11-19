package org.example;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Main {

    // currently signed-in user (null when no one is signed in)
    private User currUser;
    // the library catalogue
    private final Catalogue library;
    // registered users
    private final Users users;
    // simple log of borrow/return actions
    private final ArrayList<String> record;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        PrintWriter output = new PrintWriter(System.out);

        Main app = new Main();
        //app.mainInit();

        // prompt for login before entering main loop
        app.login(input, output);

        while (app.isSignedIn()) {
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

    // get currently signed-in user
    public User getUser() {
        return this.currUser;
    }

    // set currently signed-in user - for testing
    protected void setUser(User usr){
        this.currUser = usr;
    }

    // get the borrow/return record log
    public ArrayList<String> getRecord() {
        return this.record;
    }

    // get the registered users
    public Users getUsers() {
        return this.users;
    }

    // get the library catalogue
    public Catalogue getCatalogue() {
        return this.library;
    }

    // returns true when a user is signed in
    public boolean isSignedIn() {
        return this.currUser != null;
    }

    // check availability by index (considers holds and current user position)
    public boolean checkAvailable(int bookIndex) {
        Book thisBook = this.library.getBook(bookIndex);

        if (thisBook.getAvailability()) {
            return true;
        } else {
            if (thisBook.getStatusCode().equals(Book.StatusCode.CHECKED)) {
                return false; // book is checked out
            } else if (thisBook.getFirst() != null) {
                // if current user is first in hold queue they can borrow
                return thisBook.getFirst().getUsername().equals(this.currUser.getUsername()); // currUser is first in hold queue

            }
            return false;
        }
    }

    // policy: user can borrow up to 3 books
    public boolean checkEligible() {
        return this.currUser.getBorrowed().size() < 3;
    }

    // final confirmation prompt after successful checkout
    public void confirmation(Scanner input, PrintWriter output, Book book) {
        // final acknowledgement shown to user after checkout
        output.println("You have successfully checked out " + book.getTitle() + " by " + book.getAuthor() + ". It is due on the " + book.getDue());
        output.println("Please acknowledge this confirmation by entering 1: ");
        output.flush();
        getPick(input, output, 1,1);
    }

    // get and validate user menu pick within a given range
    protected int getPick(Scanner input, PrintWriter output, int rangeMin, int rangeMax) {
        int selection;
        if (!input.hasNextLine()) {
            return -1;
        }
        // read string and parse to integer with retry on invalid input
        try {
            selection = Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
            output.println("Invalid Input - Input was not recognized. Please try again: ");
            output.flush();
            selection = getPick(input, output, rangeMin, rangeMax);
        }

        // ensure selection is within allowed range
        if (selection < rangeMin || selection > rangeMax) {
            output.println("Invalid Option - Please enter a number that is from " + rangeMin + " to " + rangeMax + ".");
            output.flush();
            selection = getPick(input, output, rangeMin, rangeMax);
        }

        return selection;
    }

    // show book details and confirm return
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

    // show book details and confirm borrow
    public int bookDetailsBorrow(Scanner input, PrintWriter output, Book book) {
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

    // process book return
    public void returnBook(Scanner input, PrintWriter output) {
        // if user has no books, bail out
        if (this.currUser.getBorrowed().isEmpty()) {
            output.println("You have no books currently checked out. Returning to main menu.");
            output.flush();
            // this is specifically for scenario 4. without this the scenario can not be tested properly given assignment constraints
            record.add(this.currUser.getUsername() + " attempted return with no books checked out");
        } else {
            output.println("Here are the books you have checked out. Pick one you would like to return:");
            output.flush();
            int i = 1;
            // list borrowed books; show current due by setting/refreshing due date (odd code in original)
            for (Book book : this.currUser.getBorrowed()) {
                output.println(i + ". " + book.getTitle() + " by " + book.getAuthor() + " due on " + book.getDue().toString());
                output.flush();
                i++;
            }
            int selection = getPick(input, output, 1, i-1);
            if (selection == -1) {
                return;
            }
            Book returnBook = this.currUser.getBorrowed().get(selection-1);
            selection = bookDetailsReturn(input, output, returnBook);
            if (selection == -1 || selection == 2) {
                return;
            }
            boolean success = returnBookLogic(returnBook);
            if (success) {
                output.println("Return successful. Returning to main menu.");
                output.flush();
            } else {
                output.println("Return failed. Returning to main menu.");
                output.flush();
            }
        }
    }

    protected boolean returnBookLogic(Book returnBook) {
        // handle no books borrowed
        if (this.currUser.getBorrowed().isEmpty()) {
            // this is specifically for scenario 4. without this the scenario can not be tested properly given assignment constraints
            record.add(this.currUser.getUsername() + " attempted return with no books checked out");
            return false;
        }
        // perform return: remove from user's list and update book status
        this.currUser.getBorrowed().remove(returnBook);
        returnBook.returnBook();
        this.record.add(returnBook.getTitle()+" returned on " + LocalDateTime.now());
        // notify first user in hold queue if present
        if (returnBook.getFirst() != null) {
            returnBook.getFirst().addNotification(returnBook.getTitle());
        }
        return true;
    }

    // show main menu and get user selection
    public int mainMenu(Scanner input, PrintWriter output) {
        output.println("\n---------------- MAIN MENU ----------------\n1. Borrow\n2. Return\n3. Sign-out");
        output.flush();
        return getPick(input, output, 1,3);
    }

    // show borrow menu and get user selection
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
        // get pick from user if catalogue not empty
        int selection = 0;
        if (catalogue.getSize() > 0) {
            selection = getPick(input, output, 1, catalogue.getSize());
        }


        return selection;
    }

    // check availability by Book instance (same logic as index version)
    public boolean checkAvailable(Book thisBook) {

        if (thisBook.getAvailability()) {
            return true;
        } else {
            if (thisBook.getStatusCode().equals(Book.StatusCode.CHECKED)) {
                return false; // book is checked out
            } else if (thisBook.getFirst() != null) {
                // if current user is first in hold queue they can borrow
                return thisBook.getFirst().getUsername().equals(this.currUser.getUsername()); // currUser is first in hold queue

            }
            return false;
        }
    }

    // process book borrowing
    public void borrow(Scanner input, PrintWriter output) {
        int selection;

        selection = borrowMenu(input, output, this.currUser, this.library);

        int bookIndex = selection-1;
        Book borrowedBook = this.library.getBook(bookIndex);
        selection = bookDetailsBorrow(input, output, borrowedBook);

        if (selection == -1) {
            return;
        }

        if (selection == 1) {
            boolean bAvailable = checkAvailable(bookIndex);
            boolean uEligible = checkEligible();

            // first check if user already has this book checked
            // RESP 17


            if (bAvailable && uEligible) {
                // process borrow
                boolean success = borrowBookLogic(borrowedBook);
                if (!success) {
                    output.println("Borrow failed. Returning to main menu.");
                    output.flush();
                    return;

                }
                // success message
                output.println("Acknowledged received. Returning to main menu.");
                output.flush();
            } else {
                // show hold menu or error messages

                // user already has a hold on this book
                if (borrowedBook.checkQueue(this.currUser)) {
                    output.println("You already have a hold on this book.");
                    output.flush();

                }
                // user already has this book checked out
                else if (this.currUser.getBorrowed().contains(borrowedBook)) {
                    output.println("You already have this booked checked out.");
                    output.flush();
                }

                // book is checked out by someone else - offer to place a hold
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
                } else if (this.currUser.getBorrowed().size()>=3) {
                    // user has reached borrow limit but book is available -> offer hold
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

    protected boolean borrowBookLogic(Book borrowedBook) {
        boolean bAvailable = checkAvailable(borrowedBook);
        boolean uEligible = checkEligible();

        // first check if user already has this book checked
        if (this.currUser.getBorrowed().contains(borrowedBook)) {
            return false;
        }

        if (bAvailable && uEligible) {
            // update book: set due date and mark checked out
            LocalDateTime date = borrowedBook.setDueDateNow();
            // append to local record log
            this.record.add(this.currUser.getUsername() + " borrowed " + borrowedBook.getTitle() + " - due " + date.toString());
            // add to user's borrowed list
            this.currUser.addBorrowed(borrowedBook);
            // if book was on hold by this user, remove them from the queue
            if (!borrowedBook.getAvailability()) {
                borrowedBook.popFirst();
            }
            return true;
        } else {
            // user failed check - shouldn't reach here normally - place hold
            borrowedBook.placeHold(this.currUser);
            return false;
        }
    }

    // process user logout
    public void logout(Scanner input, PrintWriter output) {
        output.println("Are you sure you want to sign out? \n1. Yes\n2. No");
        output.flush();
        int selection = getPick(input, output, 1,2);
        if (selection == -1 || selection == 2) {
            return;
        }
        output.println("Signing out " + this.currUser.getUsername());
        output.flush();
        boolean sucess = logoutLogic();
        if (sucess) {
            output.println("Successfully signed out.");
            output.flush();
        } else {
            output.println("Sign out failed.");
            output.flush();
        }
        // after logout immediately prompt for login again
        login(input, output);
    }

    protected boolean logoutLogic() {
        if (this.currUser != null) {
            this.currUser = null;
            return true;
        } else {
            return false;
        }
    }

    // process user login
    public void login(Scanner input, PrintWriter output) {
        // Only perform login if no user is currently signed in
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

            boolean success = loginLogic(usr, pass);
            if (success) {
                output.println("Login successful. Welcome, " + this.currUser.getUsername() + "!");
                output.flush();

                prompt(output);
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

    protected boolean loginLogic(String usr, String pass) {
        // basic blank input validation
        if (usr == null || pass == null || usr.isEmpty() || pass.isEmpty()) {
            return false;
        }
        // good thing this isn't algorithms where time complexity matters
        for (User user : this.users.getUsers()) {
            if (user.getUsername().equals(usr)) {
                if (user.passwordCorrect(pass)) {
                    setUser(user);
                    return true;
                }
            }
        }
        return false;
    }

    // show notifications for signed-in user
    public void prompt(PrintWriter output) {
        // show notifications for the signed-in user
        if (!isSignedIn()) {
            output.println("Not logged in!");
            output.flush();
        } else {
            int sent = 0;

            // process all queued notifications for the user
            while (this.currUser.hasNotification()) {
                String thisTitle = this.currUser.popNotification();
                Book thisBook = this.library.getBook(thisTitle);

                // ensure the book is valid and currently available
                if (thisBook != null && checkAvailable(thisBook)) {
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
}
