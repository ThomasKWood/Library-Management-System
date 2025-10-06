package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.time.LocalDateTime;
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

        if (!output.toString().contains("Stealth is available")){
            assert false;
        }
        if (!output.toString().contains("Mickey7 is available")){
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
        catalogue.getBook("Stealth").setDueDateNow();

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

    @Test
    @DisplayName("Menu - main menu options presented")
    void RESP_07_test_01() {
        Menu menu = new Menu();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.mainMenu(new Scanner(srInput), new PrintWriter(output));

        String[] words = {"1.", "2.", "3.", "Borrow", "Return", "Sign-out"};

        for (String word : words) {
            if (!output.toString().contains(word)) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Menu - valid option selected")
    void RESP_07_test_02() {
        Menu menu = new Menu();

        String input = "1";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        int selected = menu.getPick(new Scanner(srInput), new PrintWriter(output), 1,3);
        output.flush();

        assertEquals(1,selected);
    }

    @Test
    @DisplayName("Menu - invalid option selected - bad number - above max")
    void RESP_07_test_03() {
        Menu menu = new Menu();

        String input = "4";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.getPick(new Scanner(srInput), new PrintWriter(output), 1,3);
        output.flush();

        String[] words = {"Invalid Option", "from 1 to 3"};

        for (String word : words) {
            if (!output.toString().contains(word)) {
                assert false;
            }
        }

    }

    @Test
    @DisplayName("Menu - invalid option selected - bad number - bellow min")
    void RESP_07_test_04() {
        Menu menu = new Menu();

        String input = "0";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.getPick(new Scanner(srInput), new PrintWriter(output), 1,3);
        output.flush();

        String[] words = {"Invalid Option", "from 1 to 3"};

        for (String word : words) {
            if (!output.toString().contains(word)) {
                assert false;
            }
        }

    }

    @Test
    @DisplayName("Menu - invalid option selected - not a number")
    void RESP_07_test_05() {
        Menu menu = new Menu();

        String input = "a";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.getPick(new Scanner(srInput), new PrintWriter(output), 1,3);
        output.flush();

        String[] words = {"Invalid Input"};

        for (String word : words) {
            if (!output.toString().contains(word)) {
                assert false;
            }
        }

    }


    @Test
    @DisplayName("Borrow Count - no borrowed")
    void RESP_08_test_01() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        Menu menu = new Menu();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.borrowMenu(new Scanner(srInput), new PrintWriter(output), users.getUser("thomaswood"), catalogue);

        String[] words = {"0", "borrowed"};

        for (String word : words) {
            if (!output.toString().contains(word)) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Borrow Count - some borrowed")
    void RESP_08_test_02() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        User testUser = users.getUser("thomaswood");
        testUser.addBorrowed(catalogue.getBook("Skunk Works"));

        Menu menu = new Menu();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        String[] words = {"1", "borrowed"};

        for (String word : words) {
            if (!output.toString().contains(word)) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Borrow Count - at limit")
    void RESP_08_test_03() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        User testUser = users.getUser("thomaswood");
        testUser.addBorrowed(catalogue.getBook("Skunk Works"));
        testUser.addBorrowed(catalogue.getBook("Fulcrum"));
        testUser.addBorrowed(catalogue.getBook("No Easy Day"));

        Menu menu = new Menu();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        String[] words = {"3", "borrowed"};

        for (String word : words) {
            if (!output.toString().contains(word)) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Borrow - List Books - all books printed")
    void RESP_09_test_01() {
        InitializeLibrary lib = new InitializeLibrary();
        Catalogue catalogue = lib.initLibrary();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        User testUser = users.getUser("thomaswood");

        Menu menu = new Menu();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        for (Book book : catalogue.getCatalogue()) {
            if (!output.toString().contains(book.getTitle())) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Borrow - List Books - availability check")
    void RESP_09_test_02() {
        Catalogue catalogue = new Catalogue();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        User testUser = users.getUser("thomaswood");

        // test books:
        Book book1 = new Book("Stealth", "Peter J. Westwick");
        Book book2 = new Book("No Easy Day", "Matt Bissonnette & Kevin Maurer");
        Book book3 = new Book("Mickey7", "Edward Ashton");

        book2.setDueDateNow();
        book3.placeHold(users.getUser("thomaswood"));

        catalogue.addBook(book1);
        catalogue.addBook(book2);
        catalogue.addBook(book3);

        Menu menu = new Menu();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        String[] words = {
                "1. Stealth by Peter J. Westwick - Available",
                "2. No Easy Day by Matt Bissonnette & Kevin Maurer - Checked Out - Available on ",
                "3. Mickey7 by Edward Ashton - On Hold",
        };

        for (String word : words) {
            if (!output.toString().contains(word)) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Borrow - List Books - due date check")
    void RESP_09_test_03() {
        Catalogue catalogue = new Catalogue();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        User testUser = users.getUser("thomaswood");

        // test books:
        Book book1 = new Book("Stealth", "Peter J. Westwick");
        LocalDateTime expected = book1.setDueDateNow();
        catalogue.addBook(book1);

        Menu menu = new Menu();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        menu.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        if (!output.toString().contains(expected.toString())) {
            assert false;
        }

    }

    @Test
    @DisplayName("Borrow - Select Book - confirm selected is correct")
    void RESP_10_test_01() {
        Catalogue catalogue = new Catalogue();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        User testUser = users.getUser("thomaswood");

        // test books:
        Book book1 = new Book("Stealth", "Peter J. Westwick");
        catalogue.addBook(book1);

        Menu menu = new Menu();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        int selected = menu.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        menu.bookDetails(new Scanner(srInput), new PrintWriter(output), catalogue.getBook(selected));
        if (output.toString().contains("Stealth")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - Select Book - user confirmation is correct")
    void RESP_10_test_02() {
        Catalogue catalogue = new Catalogue();
        InitializeUsers sampleUsers = new InitializeUsers();
        Users users = sampleUsers.initUsers();

        User testUser = users.getUser("thomaswood");

        // test books:
        Book book1 = new Book("Stealth", "Peter J. Westwick");
        catalogue.addBook(book1);

        Menu menu = new Menu();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        int selected = menu.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        selected = menu.bookDetails(new Scanner(srInput), new PrintWriter(output), catalogue.getBook(selected));
        assertEquals(1, selected);
    }
}
