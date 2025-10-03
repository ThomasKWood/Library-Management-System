package org.example;

import java.io.PrintWriter;
import java.util.Scanner;

public class Menu {

    public Menu() {};
    public int mainMenu(Scanner input, PrintWriter output) {
        output.println("---------------- MAIN MENU ----------------\n1. Borrow\n2. Return\n3.Sign-out");
        int selection = getPick(input, output, 1,3);
        output.flush();
        return selection;
    }

    public int borrowMenu(Scanner input, PrintWriter output, User user, Catalogue catalogue) {
        output.println("");
        output.flush();
        return 0;
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
