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
    @DisplayName("Check books do not contain null and book title is unique")
    void RESP_01_test_02() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();

        ArrayList<String> titles = new ArrayList<String>();
        for (Book book : catalogue.getCatalogue()) {
            if (book.getTitle() == null || book.getAuthor() == null) {
                assert false; // null title or null author
            }
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
        assert true;
    }

    @Test
    @DisplayName("Check number of users")
    void RESP_02_test_01() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        assertEquals(3, users.getSize());
    }

    @Test
    @DisplayName("Check username & password validity")
    void RESP_02_test_02() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        ArrayList<String> usernames = new ArrayList<String>();

        for (User user : users.getUsers()) {
            if (user.getUsername() == null || !user.passwordValid()) {
                assert false; // fail if user name is null or password is not valid
            } else if (usernames.isEmpty()) {
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
        assert true;
    }

    @Test
    @DisplayName("Check borrowed is empty")
    void RESP_02_test_03() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        // test func
        //users.getUser("thomaswood").addBorrowed(new Book("test","test"));

        for (User user : users.getUsers()) {
            if (!user.getBorrowed().isEmpty()) {
                assert false;
            }
        }
        assert true;
    }

    @Test
    @DisplayName("Test Login - Valid")
    void RESP_03_test_01() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();


        Session session = new Session();

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (output.toString().contains("Welcome")) {
            assert true;
        }
        assert false;
    }

    @Test
    @DisplayName("Test Login - Incorrect username")
    void RESP_03_test_02() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();


        Session session = new Session();

        String input = "validusername12\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (output.toString().contains("Incorrect username or password")) {
            assert true;
        }
        assert false;
    }
    @Test
    @DisplayName("Test Login - Incorrect password")
    void RESP_03_test_03() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();


        Session session = new Session();

        String input = "thomaswood\n1235\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (output.toString().contains("Incorrect username or password")) {
            assert true;
        }
        assert false;
    }

    @Test
    @DisplayName("Test Login - Empty value")
    void RESP_03_test_04() {
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();


        Session session = new Session();

        String input = "\n\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        session.login(new Scanner(srInput), new PrintWriter(output));

        if (output.toString().contains("username or password cannot be blank")) {
            assert true;
        }
        assert false;
    }



}
