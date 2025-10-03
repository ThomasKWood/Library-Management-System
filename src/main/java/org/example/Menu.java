package org.example;

import java.io.PrintWriter;
import java.util.Scanner;

public class Menu {

    public Menu() {};
    public int mainMenu(Scanner input, PrintWriter output) {
        output.println("");
        output.flush();
        return 0;
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

    protected int getPick(Scanner input, int rangeMin, int rangeMax) {
        return 0;
    }
}
