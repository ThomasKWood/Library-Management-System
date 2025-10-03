package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.util.ArrayList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

public class MainTest {

    @Test
    @DisplayName("Check Library is 20")
    void RESP_01_test_01(){
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        int size = catalogue.getSize();

        assertEquals(20, size);

    }

    @Test
    @DisplayName("Check books do not contain null")
    void RESP_01_test_02() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();

        for (Book book : catalogue.getCatalogue()) {
            if (book.getTitle() == null || book.getAuthor() == null) {
                assert false; // null title or null author
            }
        }
    }

    @Test
    @DisplayName("Check library uniqueness")
    void RESP_01_test_03() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();

        ArrayList<String> titles = new ArrayList<String>();
        for (Book book : catalogue.getCatalogue()) {
            if (titles.isEmpty()) {
                titles.add(book.getTitle());
            } else {
                int size = titles.size();
                for (int i = 0; i < size; i++) {
                    if (titles.get(i).equals(book.getTitle())) {
                        assert false; // duplicate title
                    } else {
                        titles.add(book.getTitle());
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Check all books are available on initialization")
    void RESP_01_test_04() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();

        ArrayList<String> titles = new ArrayList<String>();
        for (Book book : catalogue.getCatalogue()) {
            if (!book.getAvailability()) {
                assert false; // fail if book is not available
            }
        }

    }

    @Test
    @DisplayName("Check number of users")
    void RESP_02_test_01() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        assertEquals(3, users.getSize());
    }

    @Test
    @DisplayName("Check username not null")
    void RESP_02_test_02() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        for (User user : users.getUsers()) {
            if (user.getUsername() == null) {
                assert false; // fail if username is null
            }
        }
    }

    @Test
    @DisplayName("Check passwords valid for all accounts")
    void RESP_02_test_03() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        for (User user : users.getUsers()) {
            if (!user.passwordValid()) {
                assert false; // fail if password is not valid
            }
        }
    }

    @Test
    @DisplayName("Check all usernames are unique")
    void RESP_02_test_04() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        ArrayList<String> usernames = new ArrayList<String>();

        for (User user : users.getUsers()) {
            if (usernames.isEmpty()) {
                usernames.add(user.getUsername());
            } else {
                for (String username : usernames) {
                    if (user.getUsername().equals(username)) {
                        assert false; // username not unique
                    }
                }
                usernames.add(user.getUsername());
            }
        }
    }

    @Test
    @DisplayName("Check borrowed is empty")
    void RESP_02_test_05() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        // test func
        //users.getUser("thomaswood").addBorrowed(new Book("test","test"));

        for (User user : users.getUsers()) {
            if (!user.getBorrowed().isEmpty()) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Test Login - Empty value")
    void RESP_03_test_01() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "\n\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("cannot be blank")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test prompts for username and password")
    void RESP_03_test_02() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "\n\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("enter your username") && output.toString().contains("enter your password")) {
            assert false;
        }
    }


    @Test
    @DisplayName("Test Login - Valid")
    void RESP_04_test_01() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("Welcome")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test Login - Incorrect username")
    void RESP_04_test_02() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "validusername12\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("incorrect")) {
            assert true;
        }
    }

    @Test
    @DisplayName("Test Login - Incorrect password")
    void RESP_04_test_03() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "thomaswood\n1235\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("incorrect")) {
            assert true;
        }
    }

    @Test
    @DisplayName("Test Login - Already logged in")
    void RESP_04_test_04() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        output = new StringWriter();
        session.login(new Scanner(srInput), new PrintWriter(output));
        if (!output.toString().contains("already")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test retry")
    void RESP_04_test_05() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "thomaswood\n1235\nthomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("incorrect") && output.toString().contains("Welcome")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test that session is null on initialize")
    void RESP_05_test_01() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        if (session.signedIn()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test that session is set once logged in")
    void RESP_05_test_02() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (!session.signedIn()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test that session username is correct")
    void RESP_05_test_03() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (!session.getUser().getUsername().equals("thomaswood")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test that session is cleared on logout")
    void RESP_05_test_04() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        input = "Yes\n";
        srInput = new StringReader(input);
        output = new StringWriter();
        session.logout(new Scanner(srInput), new PrintWriter(output));

        if (session.signedIn()) {
            assert false;
        }
    }
    @Test
    @DisplayName("Test user decides to cancel logout")
    void RESP_05_test_05() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        input = "No\n";
        srInput = new StringReader(input);
        output = new StringWriter();
        session.logout(new Scanner(srInput), new PrintWriter(output));

        if (!session.signedIn()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test user tries logout before logging in")
    void RESP_05_test_06() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(null, users);

        String input = "No\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        session.logout(new Scanner(srInput), new PrintWriter(output));

        if (session.signedIn()) {
            assert false;
        }
    }
    @Test
    @DisplayName("Notifications - no one logged in")
    void RESP_06_test_01() {
        Session session = new Session(null, null);

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        session.prompt(new Scanner(srInput), new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("ot logged in")){
            assert false;
        }
    }

    @Test
    @DisplayName("Notifications for when books become available - no notifications")
    void RESP_06_test_02() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(catalogue, users);

        // add in user nofitcations
        // not for this one


        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        session.setUser(users.getUser("thomaswood")); // skip login
        session.prompt(new Scanner(srInput), new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("o notifications")){
            assert false;
        }
    }

    @Test
    @DisplayName("Notifications for when books become available - valid books")
    void RESP_06_test_03() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(catalogue, users);

        // add in user nofitcations
        users.getUser("thomaswood").addNoti("Stealth");
        users.getUser("thomaswood").addNoti("Mickey7");

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        session.setUser(users.getUser("thomaswood")); // skip login
        session.prompt(new Scanner(srInput), new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("o notifications")){
            assert false;
        }
    }

    @Test
    @DisplayName("Notifications for when books become available - invalid books - book isnt available")
    void RESP_06_test_04() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(catalogue, users);

        // set not available
        catalogue.getBook("Stealth").setAvailability(false);

        // add in user nofitcations
        users.getUser("thomaswood").addNoti("Stealth");

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        session.setUser(users.getUser("thomaswood")); // skip login
        session.prompt(new Scanner(srInput), new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("o notifications")){
            assert false;
        }
    }

    @Test
    @DisplayName("Notifications for when books become available - invalid books - book doesnt exist")
    void RESP_06_test_05() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();
        Session session = new Session(catalogue, users);

        // add in user nofitcations
        users.getUser("thomaswood").addNoti("Mark Carney's Downtown Ottawa Adventure");

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        session.setUser(users.getUser("thomaswood")); // skip login
        session.prompt(new Scanner(srInput), new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("o notifications")){
            assert false;
        }
    }
}
