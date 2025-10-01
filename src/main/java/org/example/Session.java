package org.example;

import java.io.PrintWriter;
import java.util.Scanner;

public class Session {
    private User currUser;

    public Session() {
        currUser = null;
    }

    public void login(Scanner input, PrintWriter output) {
        //
        output.println("");
        output.flush();
    }

    public boolean signedIn() {
        return false;
    }
}
