package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

public class MainATest {
    @Test
    @DisplayName("A-TEST-01 - Multi-User Borrow and Return with Availability Validated")
    void A_TEST_01(){
        Main app = new Main();

        // User1 logs in, borrows "The Great Gatsby", logs out. User2 logs in, sees book checked out. User1 logs back in, returns book. User2 sees book available again
        String input = "thomaswood\n1234\n"; // User1 login
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        app.login(new Scanner(srInput), new PrintWriter(output));
        // login check
        assert app.signedIn();

        // instead of borrowing "The Great Gatsby", we borrow "Stealth"
        input = "15\n1\n1\n";
        srInput = new StringReader(input);
        app.borrow(new Scanner(srInput), new PrintWriter(output));
        // list check
        assert output.toString().contains("15. Stealth");
        // borrow check
        assert output.toString().contains("Acknowledged");

        // User1 logs out
        input = "3\n1\n";
        srInput = new StringReader(input);
        app.logout(new Scanner(srInput), new PrintWriter(output));
        // logout check
        assert !app.signedIn();

        // clear
        output = new StringWriter();

        // User2 logs in
        input = "jeff\n6789\n"; // User2 login
        srInput = new StringReader(input);
        app.login(new Scanner(srInput), new PrintWriter(output));
        // login check
        assert app.signedIn();

        // list check - should see book is checked out
        input = "15\n"; // User2 login
        srInput = new StringReader(input);
        app.borrow(new Scanner(srInput), new PrintWriter(output));
        assert output.toString().contains("15. Stealth by Peter J. Westwick - Checked Out");

        // clear
        output = new StringWriter();

        // logout User2
        input = "3\n1\n";
        srInput = new StringReader(input);
        app.logout(new Scanner(srInput), new PrintWriter(output));
        // logout check
        assert !app.signedIn();

        // User1 logs back in
        input = "thomaswood\n1234\n"; // User1 login
        srInput = new StringReader(input);
        app.login(new Scanner(srInput), new PrintWriter(output));
        // login check
        assert app.signedIn();

        // return book
        input = "2\n1\n1\n";
        srInput = new StringReader(input);
        app.returnBook(new Scanner(srInput), new PrintWriter(output));
        // return check
        assert app.getCatalogue().getBook("Stealth").getAvailability();

        // User1 logs out
        input = "3\n1\n";
        srInput = new StringReader(input);
        app.logout(new Scanner(srInput), new PrintWriter(output));
        // logout check
        assert !app.signedIn();

        // clear
        output = new StringWriter();

        // User2 logs back in
        input = "jeff\n6789\n"; // User2 login
        srInput = new StringReader(input);
        app.login(new Scanner(srInput), new PrintWriter(output));
        // login check
        assert app.signedIn();

        // list check - should see book is available
        input = "15\n"; // User2 login
        srInput = new StringReader(input);
        app.borrow(new Scanner(srInput), new PrintWriter(output));
        assert output.toString().contains("15. Stealth by Peter J. Westwick - Available");
    }
}
