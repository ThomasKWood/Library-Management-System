package org.example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        PrintWriter output = new PrintWriter(System.out);

        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(catalogue, users);

        session.login(input, output);

        Menu menu = new Menu();
        while (session.signedIn()) {
            int selection = menu.mainMenu(input, output);

            switch (selection) {
                case 1:
                    session.borrow(input, output);
                    output.flush();
            }

            output = new PrintWriter(System.out);
        }

    }
}

