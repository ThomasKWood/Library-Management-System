package org.example;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarySteps {
    private Main library;
    private ArrayList<String> savedNotifications;


    @Given("the library is initialized")
    public void the_library_is_initialized() {
        library = new Main();
        System.out.println("Library initialized");
    }

    @And("our testing variables have been cleared")
    public void our_testing_variables_have_been_cleared() {
        savedNotifications = new ArrayList<>();
    }

    @And("{string} is available in the library")
    public void is_available_in_the_library(String bookTitle) {
        Book book = getBookByTitle(bookTitle);
        assertNotNull(book, "Book should not be null: " + bookTitle);
        assertTrue(book.getAvailability(), bookTitle + " is be available");
    }

    @When("{string} logs in")
    public void logs_in(String username) {
        // verify user exists
        assertTrue(verifyUser(username), "User should exist: " + username);
        String password = determinePassword(username);

        // grab notifications before login
        User user = library.getUsers().getUser(username);
        if (user.hasNotification()) {
            ArrayList<String> userNotifications = user.getNotifications();
            for (String noti : userNotifications) {
                savedNotifications.add(username + noti);
            }
        }

        // simulate login with PrintWriter and Scanner
        String input = username + "\n" + password + "\n"; // User1 login
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        library.login(new Scanner(srInput), new PrintWriter(output));
        // login check
        assertTrue(library.isSignedIn(), "User " + username + " signed in");
    }

    @Then("the current session should be for {string}")
    public void the_current_session_should_be_for(String username) {
        if(Objects.equals(username, "nobody")) {
            assertNull(library.getUser(), "No user should be signed in");
            return;
        }
        User currentUser = library.getUser();
        assertNotNull(currentUser, "Current user should not be null");
        assertEquals(username, currentUser.getUsername(), "Current session user is " + username);
    }

    @When("{string} attempts to borrow {string}")
    public void borrows(String username, String bookTitle) {
        // check user session matches username
        User currentUser = userSessionMatches(username);

        Book book = getBookByTitle(bookTitle);
        assertNotNull(book, "Book should not be null: " + bookTitle);
        int bookIndex = getBookIndex(bookTitle);

        boolean availabilityBefore = book.getAvailability();

        String input = bookIndex + "\n1\n1\n"; // borrow book
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        library.borrow(new Scanner(srInput), new PrintWriter(output));

        if (currentUser.getBorrowed().contains(book)) {
            // book was borrowed good
        } else if (book.checkQueue(currentUser)) {
            // book was put on hold good
        } else {
            fail(username + " should have either borrowed or placed a hold on " + bookTitle);
        }
    }

    @Then("{string} should be marked as checked out")
    public void should_be_marked_as_checked_out(String bookTitle) {
        Book book = getBookByTitle(bookTitle);
        assertNotNull(book, "Book should not be null: " + bookTitle);
        assertFalse(book.getAvailability(), bookTitle + " should be checked out");
    }

    @When ("{string} logs out")
    public void logs_out(String username) {
        // check user session matches username
        User currentUser = userSessionMatches(username);

        // simulate logout with PrintWriter and Scanner
        String input = "1\n"; // logout
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        library.logout(new Scanner(srInput), new PrintWriter(output));
        // logout check
        assertFalse(library.isSignedIn(), "User " + username + " should be logged out");
    }

    @And("{string} logs back in")
    public void logs_back_in(String username) {
        logs_in(username);
    }

    @When("{string} checks availability of {string}")
    public void checks_availability_of(String username, String bookTitle) {
        // check user session matches username
        User currentUser = userSessionMatches(username);

        int bookIndex = getBookIndex(bookTitle);
        String input = bookIndex + "\n"; // check availability
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();

        library.borrowMenu(new Scanner(srInput), new PrintWriter(output), currentUser, library.getCatalogue());
        // book should be in output but we dont care about its state here we just wanna make sure its there
        assertTrue(output.toString().contains(bookTitle), "Output should contain book title: " + bookTitle);
    }

    @When("{string} returns {string}")
    public void returns(String username, String bookTitle) {
        // check user session matches username
        User currentUser = userSessionMatches(username);

        int bookIndex = getBorrowedIndex(currentUser, bookTitle);
        String input = bookIndex + "\n1\n"; // return book
        StringReader srInput = new StringReader(input);
        StringWriter output = new StringWriter();
        library.returnBook(new Scanner(srInput), new PrintWriter(output));
        // return check
        if(currentUser.getBorrowed().contains(getBookByTitle(bookTitle))) {
            fail(username + " should have returned " + bookTitle);
        }
    }

    @Then("{string} should be marked as available")
    public void should_be_marked_as_available(String bookTitle) {
        Book book = getBookByTitle(bookTitle);
        assertNotNull(book, "Book should not be null: " + bookTitle);
        assertTrue(book.getAvailability(), bookTitle + " should be available");
    }

    @Then("{string} should be in the hold queue for {string}")
    public void should_be_in_the_hold_queue_for(String username, String bookTitle) {
        Book book = getBookByTitle(bookTitle);
        assertNotNull(book, "Book should not be null: " + bookTitle);
        User currentUser = library.getUsers().getUser(username);

        if(!book.checkQueue(currentUser)) {
            fail(username + " should be in the hold queue for " + bookTitle);
        }
    }

    @Then("{string} should have a notification for {string}")
    public void should_have_a_notification_for(String username, String bookTitle) {
        assertTrue(savedNotifications.contains(username+bookTitle), username + " should have a notification for " + bookTitle);
    }

    @And("{string} should be next in the hold queue for {string}")
    public void should_be_next_in_the_hold_queue_for(String username, String bookTitle) {
        Book book = getBookByTitle(bookTitle);
        assertNotNull(book, "Book should not be null: " + bookTitle);

        User currentUser = library.getUsers().getUser(username);
        assertNotNull(currentUser, "User should not be null: " + username);

        assertEquals(currentUser, book.getFirst(), username + " should be next in the hold queue for " + bookTitle);
    }

    @Then("{string} should be marked as on hold")
    public void should_be_marked_as_on_hold(String bookTitle) {
        Book book = getBookByTitle(bookTitle);
        assertNotNull(book, "Book should not be null: " + bookTitle);
        assertEquals(Book.StatusCode.HOLD, book.getStatusCode(), bookTitle + " should be on hold");
    }

    // HELPERS
    private User userSessionMatches(String username) {
        User currentUser = library.getUser();
        assertNotNull(currentUser, "Current user should not be null");
        assertEquals(username, currentUser.getUsername(), "Current session user is " + username);
        return currentUser;
    }

    private int getBookIndex(String bookTitle) {
        Catalogue catalogue = library.getCatalogue();
        for (int i = 0; i < catalogue.getSize(); i++) {
            if (catalogue.getBook(i).getTitle().equals(bookTitle)) {
                return i + 1;
            }
        }
        fail("Book not found in catalogue: " + bookTitle);
        return -1;
    }


    private int getBorrowedIndex(User user, String bookTitle) {
        for (int i = 0; i < user.getBorrowed().size(); i++) {
            if (user.getBorrowed().get(i).getTitle().equals(bookTitle)) {
                return i + 1;
            }
        }
        fail("Book not found in user's borrowed list: " + bookTitle);
        return -1;
    }

    private boolean verifyUser(String username) {
        User user = library.getUsers().getUser(username);
        return user != null;
    }

    private String determinePassword(String username) {
        return switch (username) {
            case "alice" -> "pass123";
            case "bob" -> "pass456";
            case "charlie" -> "pass789";
            default -> {
                fail("Unknown user: " + username);
                yield null;
            }
        };
    }

    private Book getBookByTitle(String bookTitle) {
        Book book = library.getCatalogue().getBook(bookTitle);
        if (book != null) {
            return book;
        }
        fail("Book not found in catalogue: " + bookTitle);
        return null;
    }
}
