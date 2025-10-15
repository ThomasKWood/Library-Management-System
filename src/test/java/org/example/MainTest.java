package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

public class MainTest {

    @Test
    @DisplayName("Check Library is 20")
    void RESP_01_test_01() {
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

        for (User user : users.getUsers()) {
            if (!user.getBorrowed().isEmpty()) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Test Login - Empty value")
    void RESP_03_test_01() {
        Main app = new Main();
        String input = "\n\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("cannot be blank")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test prompts for username and password")
    void RESP_03_test_02() {
        Main app = new Main();
        //app.mainInit();

        String input = "\n\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("enter your username") && output.toString().contains("enter your password")) {
            assert false;
        }
    }


    @Test
    @DisplayName("Test Login - Valid")
    void RESP_04_test_01() {
        Main app = new Main();
        //app.mainInit();

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("Welcome")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test Login - Incorrect username")
    void RESP_04_test_02() {
        Main app = new Main();
        //app.mainInit();

        String input = "validusername12\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("incorrect")) {
            assert true;
        }
    }

    @Test
    @DisplayName("Test Login - Incorrect password")
    void RESP_04_test_03() {
        Main app = new Main();
        //app.mainInit();

        String input = "thomaswood\n1235\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("incorrect")) {
            assert true;
        }
    }

    @Test
    @DisplayName("Test Login - Already logged in")
    void RESP_04_test_04() {
        Main app = new Main();
        //app.mainInit();

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        output = new StringWriter();
        app.login(new Scanner(srInput), new PrintWriter(output));
        if (!output.toString().contains("already")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test retry")
    void RESP_04_test_05() {
        Main app = new Main();
        //app.mainInit();

        String input = "thomaswood\n1235\nthomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("incorrect") && output.toString().contains("Welcome")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test that session is null on initialize")
    void RESP_05_test_01() {
        Main app = new Main();
        //app.mainInit();

        if (app.signedIn()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test that session is set once logged in")
    void RESP_05_test_02() {
        Main app = new Main();
        //app.mainInit();

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        if (!app.signedIn()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test that session username is correct")
    void RESP_05_test_03() {
        Main app = new Main();
        //app.mainInit();

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        if (!app.getUser().getUsername().equals("thomaswood")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test that session is cleared on logout")
    void RESP_05_test_04() {
        Main app = new Main();
        //app.mainInit();

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        input = "1\n";
        srInput = new StringReader(input);
        output = new StringWriter();
        app.logout(new Scanner(srInput), new PrintWriter(output));

        if (app.signedIn()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test user decides to cancel logout")
    void RESP_05_test_05() {
        Main app = new Main();
        //app.mainInit();

        String input = "thomaswood\n1234\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();


        app.login(new Scanner(srInput), new PrintWriter(output));

        input = "No\n";
        srInput = new StringReader(input);
        output = new StringWriter();
        app.logout(new Scanner(srInput), new PrintWriter(output));

        if (!app.signedIn()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Test user tries logout before logging in")
    void RESP_05_test_06() {
        Main app = new Main();
        //app.mainInit();

        String input = "No\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.logout(new Scanner(srInput), new PrintWriter(output));

        if (app.signedIn()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Notifications - no one logged in")
    void RESP_06_test_01() {
        Main app = new Main();
        //app.mainInit();

        StringWriter output = new StringWriter();

        app.prompt(new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("ot logged in")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Notifications for when books become available - no notifications")
    void RESP_06_test_02() {
        Main app = new Main();
        //app.mainInit();
        Users users = app.getUsers();

        StringWriter output = new StringWriter();

        app.setUser(users.getUser("thomaswood")); // skip login
        app.prompt(new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("o notifications")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Notifications for when books become available - valid books")
    void RESP_06_test_03() {
        Main app = new Main();
        Users users = app.getUsers();

        // add in user nofitcations
        users.getUser("thomaswood").addNoti("Stealth");
        users.getUser("thomaswood").addNoti("Mickey7");

        StringWriter output = new StringWriter();

        app.setUser(users.getUser("thomaswood")); // skip login
        app.prompt(new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("Stealth is available")) {
            assert false;
        }
        if (!output.toString().contains("Mickey7 is available")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Notifications for when books become available - invalid books - book isnt available")
    void RESP_06_test_04() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        // set not available
        catalogue.getBook("Stealth").setDueDateNow();

        // add in user nofitcations
        users.getUser("thomaswood").addNoti("Stealth");

        StringWriter output = new StringWriter();

        app.setUser(users.getUser("thomaswood")); // skip login
        app.prompt(new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("o notifications")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Notifications for when books become available - invalid books - book doesnt exist")
    void RESP_06_test_05() {
        Main app = new Main();
        Users users = app.getUsers();

        // add in user nofitcations
        users.getUser("thomaswood").addNoti("Mark Carney's Downtown Ottawa Adventure");

        StringWriter output = new StringWriter();

        app.setUser(users.getUser("thomaswood")); // skip login
        app.prompt(new PrintWriter(output));
        output.flush();

        if (!output.toString().contains("o notifications")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Menu - main menu options presented")
    void RESP_07_test_01() {
        Main app = new Main();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.mainMenu(new Scanner(srInput), new PrintWriter(output));

        String[] words = {"1.", "2.", "3.", "Borrow", "Return", "Sign-out"};

        for (String word : words) {
            if (!output.toString().contains(word)) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Menu - invalid option selected - bad number - bellow min")
    void RESP_07_test_04() {
        Main app = new Main();

        String input = "0";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.getPick(new Scanner(srInput), new PrintWriter(output), 1, 3);
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
        Main app = new Main();

        String input = "a";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.getPick(new Scanner(srInput), new PrintWriter(output), 1, 3);
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
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrowMenu(new Scanner(srInput), new PrintWriter(output), users.getUser("thomaswood"), catalogue);

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
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");
        testUser.addBorrowed(catalogue.getBook("Skunk Works"));

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

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
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");
        testUser.addBorrowed(catalogue.getBook("Skunk Works"));
        testUser.addBorrowed(catalogue.getBook("Fulcrum"));
        testUser.addBorrowed(catalogue.getBook("No Easy Day"));

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

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
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        for (Book book : catalogue.getCatalogue()) {
            if (!output.toString().contains(book.getTitle())) {
                assert false;
            }
        }
    }

    @Test
    @DisplayName("Borrow - List Books - due date check")
    void RESP_09_test_02() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        // test books:
        Book book1 = catalogue.getBook("Stealth");
        LocalDateTime expected = book1.setDueDateNow();
        catalogue.addBook(book1);

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        if (!output.toString().contains(expected.toString())) {
            assert false;
        }

    }

    @Test
    @DisplayName("Borrow - List Books - availability check - available")
    void RESP_09_test_03() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        assert output.toString().contains("Stealth by Peter J. Westwick - Available");
    }

    @Test
    @DisplayName("Borrow - List Books - availability check - Available for this user")
    void RESP_09_test_04() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        // test book:
        Book book = catalogue.getBook("Mickey7");
        book.placeHold(users.getUser("thomaswood"));

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        assert output.toString().contains("Mickey7 by Edward Ashton - Available (on hold for you)");
    }

    @Test
    @DisplayName("Borrow - List Books - availability check - On Hold")
    void RESP_09_test_05() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        // test book:
        Book book = catalogue.getBook("Mickey7");
        book.placeHold(users.getUser("jeff"));

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        assert output.toString().contains("Mickey7 by Edward Ashton - On Hold");
    }

    @Test
    @DisplayName("Borrow - List Books - availability check - Checked Out")
    void RESP_09_test_06() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        // test book:
        Book book = catalogue.getBook("Mickey7");
        book.setDueDateNow();

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);

        assert output.toString().contains("Mickey7 by Edward Ashton - Checked Out");
    }

    @Test
    @DisplayName("Borrow - Select Book - confirm selected is correct")
    void RESP_10_test_01() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        String input = "15";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        int selected = app.borrowMenu(new Scanner(srInput), new PrintWriter(output), users.getUser("thomaswood"), catalogue);
        output = new StringWriter();
        app.bookDetails(new Scanner(srInput), new PrintWriter(output), catalogue.getBook(selected - 1));
        if (!output.toString().contains("Stealth")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - Select Book - user confirmation is correct")
    void RESP_10_test_02() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        String input = "15\n1\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        int selected = app.borrowMenu(new Scanner(srInput), new PrintWriter(output), testUser, catalogue);
        srInput = new StringReader(input);
        output = new StringWriter();
        selected = app.bookDetails(new Scanner(srInput), new PrintWriter(output), catalogue.getBook(selected - 1));
        assertEquals(1, selected);
    }

    @Test
    @DisplayName("Borrow - verify availability - book free")
    void RESP_11_test_01() {
        Main app = new Main();
        Users users = app.getUsers();

        User testUser = users.getUser("thomaswood");

        // test books:
        app.setUser(testUser);

        assert app.checkAvail(14);
    }

    @Test
    @DisplayName("Borrow - verify availability - book on hold but this user is first in queue")
    void RESP_11_test_02() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        Book book1 = catalogue.getBook("Stealth");
        book1.placeHold(testUser);
        catalogue.addBook(book1);

        app.setUser(testUser);

        assert app.checkAvail(14);
    }

    @Test
    @DisplayName("Borrow - verify availability - book not available - user is not first")
    void RESP_11_test_03() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        // test books:
        Book book1 = catalogue.getBook("Stealth");
        book1.placeHold(users.getUser("jeff"));
        catalogue.addBook(book1);

        app.setUser(testUser);

        assert !app.checkAvail(14);
    }

    @Test
    @DisplayName("Borrow - verify availability - book not available - checked out")
    void RESP_11_test_04() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUser = users.getUser("thomaswood");

        // test books:
        Book book1 = catalogue.getBook("Stealth");
        book1.setDueDateNow();

        app.setUser(testUser);

        assert !app.checkAvail(14);
    }

    @Test
    @DisplayName("Borrow - verify eligibility - not at limit")
    void RESP_12_test_01() {
        Main app = new Main();
        Users users = app.getUsers();

        app.setUser(users.getUser("thomaswood"));

        assert app.checkElig();
    }

    @Test
    @DisplayName("Borrow - verify eligibility - at limit")
    void RESP_12_test_02() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        testUsr.addBorrowed(catalogue.getBook(0));
        testUsr.addBorrowed(catalogue.getBook(1));
        testUsr.addBorrowed(catalogue.getBook(2));

        app.setUser(testUsr);

        assert !app.checkElig();
    }

    @Test
    @DisplayName("Borrow - calculate due date")
    void RESP_13_test_01() {
        LocalDateTime expected = LocalDateTime.now().plusDays(14);

        Book book1 = new Book("Stealth", "Peter J. Westwick");
        LocalDateTime actual = book1.setDueDateNow();

        // break down into just DD-MM-YY
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        assertEquals(expected.format(formatter), actual.format(formatter));
    }

    @Test
    @DisplayName("Borrow - borrow pass - record check")
    void RESP_14_test_01() {
        Main app = new Main();
        Users users = app.getUsers();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        String input = "15\n1\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        boolean assertValue = false;
        for (String string : app.getRecord()) {
            if (string.contains("thomaswood borrowed Stealth")) {
                assertValue = true;
            }
        }
        assert assertValue;
    }

    @Test
    @DisplayName("Borrow - borrow fail - record check")
    void RESP_14_test_02() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        catalogue.getBook("Stealth").setDueDateNow();

        String input = "15\n1\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        boolean assertValue = true;
        for (String string : app.getRecord()) {
            if (string.contains("thomaswood borrowed Stealth")) {
                assertValue = false;
            }
        }
        assert assertValue;
    }

    @Test
    @DisplayName("Borrow - borrow pass - account check")
    void RESP_14_test_03() {
        Main app = new Main();
        Users users = app.getUsers();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        String input = "15\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        // pre borrow check
        if (!testUsr.getBorrowed().isEmpty()) {
            assert false;
        }

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        // post borrow check
        if (testUsr.getBorrowed().isEmpty()) {
            assert false;
        } else if (!testUsr.getBorrowed().getFirst().getTitle().equals("Stealth")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - borrow fail - account check")
    void RESP_14_test_04() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        catalogue.getBook("Stealth").setDueDateNow();

        String input = "15\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        // pre borrow check
        if (!testUsr.getBorrowed().isEmpty()) {
            assert false;
        }

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        // post borrow check
        if (!testUsr.getBorrowed().isEmpty()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - borrow pass - book check")
    void RESP_14_test_05() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        String input = "15\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        // post borrow check
        assert !catalogue.getBook("Stealth").getAvailability();
    }

    @Test
    @DisplayName("Borrow - borrow fail - book check")
    void RESP_14_test_06() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        catalogue.getBook("Stealth").setDueDateNow();

        String input = "15\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        // post borrow check
        assert !catalogue.getBook("Stealth").getAvailability();
    }

    @Test
    @DisplayName("Borrow - due date presented upon borrow success")
    void RESP_15_test_01() {
        Main app = new Main();
        Users users = app.getUsers();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        String input = "15\n1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("You have successfully checked out Stealth by Peter J. Westwick. It is due on the ")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - upon confirmation ack - main menu is presented")
    void RESP_15_test_02() {
        Main app = new Main();
        Users users = app.getUsers();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        String input = "15\n1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("MAIN MENU")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - hold - allow hold")
    void RESP_16_test_01() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        catalogue.getBook("Stealth").setDueDateNow();

        String input = "15\n1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("Would you like to place a hold?")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - hold - hold already placed")
    void RESP_16_test_03() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        Book testBook = catalogue.getBook("Stealth");
        testBook.setDueDateNow();
        testBook.placeHold(testUsr);


        String input = "15\n1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("have a hold on this book")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - hold - already checked out")
    void RESP_16_test_04() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        Book testBook = catalogue.getBook("Stealth");
        testBook.setDueDateNow();
        testUsr.addBorrowed(testBook);

        String input = "15\n1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("already have this booked checked out")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - hold - book available but at limit")
    void RESP_16_test_05() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        testUsr.addBorrowed(catalogue.getBook(0));
        testUsr.addBorrowed(catalogue.getBook(1));
        testUsr.addBorrowed(catalogue.getBook(2));

        String input = "15\n1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("borrow limit. You cannot borrow any more books at this time")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Borrow - hold - added to queue after hold transaction")
    void RESP_16_test_06() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        catalogue.getBook("Stealth").setDueDateNow();

        String input = "15\n1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.borrow(new Scanner(srInput), new PrintWriter(output));

        if (!catalogue.getBook("Stealth").firstQueue().getUsername().equals("thomaswood")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Present borrowed - no books borrowed")
    void RESP_17_test_01() {
        Main app = new Main();
        Users users = app.getUsers();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.returnBook(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("no books currently checked out")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Present borrowed - books borrowed")
    void RESP_17_test_02() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        Book book1 = catalogue.getBook("Stealth");
        LocalDateTime date1 = book1.setDueDateNow();
        Book book2 = catalogue.getBook("Mickey7");
        LocalDateTime date2 = book2.setDueDateNow();

        testUsr.addBorrowed(book1);
        testUsr.addBorrowed(book2);


        String input = "1\n1\n"; // inputs required to return stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.returnBook(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("Stealth by Peter J. Westwick due on " + date1.toString())) {
            assert false;
        }
        if (!output.toString().contains("Mickey7 by Edward Ashton due on " + date2.toString())) {
            assert false;
        }
    }

    @Test
    @DisplayName("Present borrowed - book selected")
    void RESP_17_test_03() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        Book book1 = catalogue.getBook("Stealth");
        Book book2 = catalogue.getBook("Mickey7");

        testUsr.addBorrowed(book1);
        testUsr.addBorrowed(book2);


        String input = "1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.returnBook(new Scanner(srInput), new PrintWriter(output));

        if (!output.toString().contains("You have selected:")) {
            assert false;
        }
        if (!output.toString().contains("Are you sure you would like to proceed with returning this book?")) {
            assert false;
        }
        if (!output.toString().contains("Yes")) {
            assert false;
        }
        if (!output.toString().contains("No")) {
            assert false;
        }
        if (!output.toString().contains("Processing return")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Return confirmed - removed from account")
    void RESP_18_test_01() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        Book book1 = catalogue.getBook("Stealth");
        Book book2 = catalogue.getBook("Mickey7");

        testUsr.addBorrowed(book1);
        testUsr.addBorrowed(book2);


        String input = "1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.returnBook(new Scanner(srInput), new PrintWriter(output));
        if (testUsr.getBorrowed().contains(book1)) {
            assert false;
        }
    }

    @Test
    @DisplayName("Return confirmed - book now available")
    void RESP_18_test_02() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        Book book1 = catalogue.getBook("Stealth");
        Book book2 = catalogue.getBook("Mickey7");

        testUsr.addBorrowed(book1);
        testUsr.addBorrowed(book2);


        String input = "1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.returnBook(new Scanner(srInput), new PrintWriter(output));

        if (!book1.getAvailability()) {
            assert false;
        }
    }

    @Test
    @DisplayName("Return confirmed - record made")
    void RESP_18_test_03() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        Book book1 = catalogue.getBook("Stealth");
        Book book2 = catalogue.getBook("Mickey7");

        testUsr.addBorrowed(book1);
        testUsr.addBorrowed(book2);


        String input = "1\n1\n"; // inputs required to borrow stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.returnBook(new Scanner(srInput), new PrintWriter(output));

        boolean assertValue = false;
        for (String record : app.getRecord()) {
            if (record.contains("Stealth returned")) {
                assertValue = true;
            }
        }
        assert assertValue;
    }

    @Test
    @DisplayName("Return cancelled")
    void RESP_18_test_04() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);
        Book book1 = catalogue.getBook("Stealth");
        Book book2 = catalogue.getBook("Mickey7");

        testUsr.addBorrowed(book1);
        testUsr.addBorrowed(book2);


        String input = "1\n2\n"; // inputs required to cancel return on stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.returnBook(new Scanner(srInput), new PrintWriter(output));

        assert !output.toString().contains("Stealth returned");
    }

    @Test
    @DisplayName("Hold - On return check proper status - queue is empty")
    void RESP_19_test_01() {
        Book bookTest = new Book("Test Book", "Tester");
        bookTest.placeHold(new User("testuser", "tester"));

        // borrow
        bookTest.popFirst();
        bookTest.setDueDateNow();

        // return
        bookTest.returnBook();

        // check - should be available since queue is empty
        if (bookTest.getStatusCode().equals(Book.StatusCode.CHECKED) || bookTest.getStatusCode().equals(Book.StatusCode.HOLD)) {
            assert false;
        }
    }

    @Test
    @DisplayName("Hold - On return check proper status - queue is not empty")
    void RESP_19_test_02() {
        Book bookTest = new Book("Test Book", "Tester");
        bookTest.placeHold(new User("testuser", "tester"));
        bookTest.placeHold(new User("testuser2", "tester"));

        // borrow
        bookTest.popFirst();
        bookTest.setDueDateNow();

        // return
        bookTest.returnBook();

        // check - should be status hold since one more person is in queue
        if (!bookTest.getStatusCode().equals(Book.StatusCode.HOLD)) {
            assert false;
        }
    }

    @Test
    @DisplayName("Hold - check for notification")
    void RESP_19_test_03() {
        Main app = new Main();
        Users users = app.getUsers();
        Catalogue catalogue = app.getCatalogue();

        User testUsr = users.getUser("thomaswood");
        User testUsr2 = users.getUser("jeff");
        Book book1 = catalogue.getBook("Stealth");
        book1.setDueDateNow();
        testUsr.addBorrowed(book1);

        // setup book for noti
        book1.placeHold(testUsr2);

        // simulate return
        app.setUser(testUsr);

        String input = "1\n1\n"; // inputs required to return stealth
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        app.returnBook(new Scanner(srInput), new PrintWriter(output));

        // clear session and login as new user
        input = "1\n";
        srInput = new StringReader(input);
        output = new StringWriter();

        app.logout(new Scanner(srInput), new PrintWriter(output));

        input = "jeff\n6789\n";
        srInput = new StringReader(input);
        output = new StringWriter();
        app.login(new Scanner(srInput), new PrintWriter(output));

        // check output
        if (!output.toString().contains("Stealth is available!")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Logout - check if login appears")
    void RESP_20_test_01() {
        Main app = new Main();
        Users users = app.getUsers();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        // clear session and login as new user
        input = "1\n";
        srInput = new StringReader(input);
        output = new StringWriter();

        app.logout(new Scanner(srInput), new PrintWriter(output));

        // check output
        if (!output.toString().contains("lease enter your username")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Logout - confirmation check")
    void RESP_20_test_02() {
        Main app = new Main();
        Users users = app.getUsers();

        User testUsr = users.getUser("thomaswood");
        app.setUser(testUsr);

        String input = "";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        // clear session and login as new user
        input = "";
        srInput = new StringReader(input);
        output = new StringWriter();

        app.logout(new Scanner(srInput), new PrintWriter(output));

        // check output
        if (!output.toString().contains("Are you sure you want to sign out? \n1. Yes\n2. No")) {
            assert false;
        }
    }

    @Test
    @DisplayName("Capture user input - valid number input")
    void RESP_21_test_01() {
        Main app = new Main();
        String input = "2\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        int pick = app.getPick(new Scanner(srInput), new PrintWriter(output), 1, 3);
        assertEquals(2, pick);
    }

    @Test
    @DisplayName("Capture user input - valid number input - edge")
    void RESP_21_test_02() {
        Main app = new Main();
        String input = "3\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        int pick = app.getPick(new Scanner(srInput), new PrintWriter(output), 1, 3);
        assertEquals(3, pick);
    }

    @Test
    @DisplayName("Capture user input - invalid number input")
    void RESP_21_test_03() {
        Main app = new Main();
        String input = "0\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        int pick = app.getPick(new Scanner(srInput), new PrintWriter(output), 1, 3);
        assertEquals(-1, pick);
    }

    @Test
    @DisplayName("Capture user input - invalid then valid")
    void RESP_21_test_04() {
        Main app = new Main();
        String input = "0\n1\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        int pick = app.getPick(new Scanner(srInput), new PrintWriter(output), 1, 3);
        assertEquals(1, pick);
    }

    @Test
    @DisplayName("Capture user input - invalid not a number")
    void RESP_21_test_05() {
        Main app = new Main();
        String input = "test\n";
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        app.getPick(new Scanner(srInput), new PrintWriter(output), 1, 3);
        assert output.toString().contains("Invalid Input");
    }
    // TODO signout uses Yes or No. should be 1 or 2
    // TODO on return with one book 2 can be entered to crash
}
