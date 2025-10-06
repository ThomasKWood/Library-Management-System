package org.example;

import java.io.PrintWriter;
import java.util.Scanner;

public class Menu {

    public Menu() {};
    public int mainMenu(Scanner input, PrintWriter output) {
        output.println("\n---------------- MAIN MENU ----------------\n1. Borrow\n2. Return\n3.Sign-out");
        int selection = getPick(input, output, 1,3);
        output.flush();
        return selection;
    }

    public int borrowMenu(Scanner input, PrintWriter output, User user, Catalogue catalogue) {
        output.println("\n---------------- BORROW ----------------");
        output.println("\nYou have " + user.getBorrowed().size() + " books borrowed.");
        output.println("\nSelect a book: ");
        for (int i = 0; i < catalogue.getSize(); i++) {
            Book thisBook = catalogue.getBook(i);
            if (thisBook.getStatusCode().equals(Book.StatusCode.AVAIL) || thisBook.getStatusCode().equals(Book.StatusCode.HOLD)) {
                output.println(i + 1 + ". " + thisBook.getTitle() + " by " + thisBook.getAuthor() + " - " + thisBook.getStatusCode().getLabel());
            } else if (thisBook.getStatusCode().equals(Book.StatusCode.CHECKED)) { // could be else
                // print available on
                output.println(i + 1 + ". " + thisBook.getTitle() + " by " + thisBook.getAuthor() + " - " + thisBook.getStatusCode().getLabel() + " - Available on " + thisBook.getDue().toString());
            }
        }
        // get pick
        int selection = 0;
        if (catalogue.getSize() > 0) {
            selection = getPick(input, output, 1, catalogue.getSize());
        }

        output.flush();
        return selection;
    }

    public int bookDetails(Scanner input, PrintWriter output, Book book) {
        output.println("\nYou have selected:");
        output.println(book.getTitle());
        output.println(book.getAuthor());
        output.println(book.getStatusCode().getLabel());
        output.println("\n Are you sure you would like to proceed with borrowing this book?");
        output.println("1. Yes\n2. No");

        int selected = getPick(input, output, 1, 2);

        if (selected == 1) {
            output.println("You have selected Yes. Proceeding to booking...");
        } else {
            output.println("You have selected No. Bringing you back to main menu.");
        }
        output.flush();
        return selected;
    }

    public int returnMenu(Scanner input, PrintWriter output, User user, Catalogue catalogue) {
        output.println("");
        output.flush();
        return 0;
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
            selection = getPick(input, output, rangeMin, rangeMax);
        }

        if (selection < rangeMin || selection > rangeMax) {
            output.println("Invalid Option - Please enter a number that is from " + rangeMin + " to " + rangeMax + ".");
            selection = getPick(input, output, rangeMin, rangeMax);
        }

        return selection;
    }
}
