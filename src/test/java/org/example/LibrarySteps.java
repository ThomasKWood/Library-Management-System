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
    private ArrayList<String> bookAvailability;

    @Given("the library is initialized")
    public void the_library_is_initialized() {
        library = new Main();
        System.out.println("Library initialized");
    }

    @And("our testing variables have been cleared")
    public void our_testing_variables_have_been_cleared() {
        savedNotifications = new ArrayList<>();
        bookAvailability = new ArrayList<>();
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
        userSessionMatches(username);

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
        userSessionMatches(username);
        Book book = getBookByTitle(bookTitle);
        assertNotNull(book, "Book should not be null: " + bookTitle);

        bookAvailability.add(book.getTitle() + library.checkAvailable(book) + username);
    }

    @Then("{string} should not be available for {string}")
    public void should_not_be_available_for(String bookTitle, String username) {
        String match = bookTitle + false + username;
        assertTrue(bookAvailability.contains(match), bookTitle + " should not be available for " + username);
        bookAvailability.remove(match);
    }

    @Then("{string} should be available for {string}")
    public void should_be_available_for(String bookTitle, String username) {
        String match = bookTitle + true + username;
        assertTrue(bookAvailability.contains(match), bookTitle + " should not be available for " + username);
        bookAvailability.remove(match);
    }

    @When("{string} returns {string}")
    public void returns(String username, String bookTitle) {
        // check user session matches username
        User currentUser = userSessionMatches(username);

        int bookIndex = 0;
        if (!currentUser.getBorrowed().isEmpty()) {
            bookIndex = getBorrowedIndex(currentUser, bookTitle);
        }

        // handle normal return
        if (bookIndex != 0) {
            String input = bookIndex + "\n1\n"; // return book
            StringReader srInput = new StringReader(input);
            StringWriter output = new StringWriter();
            library.returnBook(new Scanner(srInput), new PrintWriter(output));
            // return check
            if(currentUser.getBorrowed().contains(getBookByTitle(bookTitle))) {
                fail(username + " should have returned " + bookTitle);
            }
        // no book to return handle
        } else {
            // capture borrowed count before
            int borrowedCountBefore = currentUser.getBorrowed().size();

            String input = "21\n1\n"; // return non existing book
            StringReader srInput = new StringReader(input);
            StringWriter output = new StringWriter();
            library.returnBook(new Scanner(srInput), new PrintWriter(output));

            if (currentUser.getBorrowed().size() != borrowedCountBefore) {
                fail(username + " should not have been able to return " + bookTitle);
            }
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

    @Then("{string} should have no notifications for {string}")
    public void should_have_no_notifications_for(String username, String bookTitle) {
        assertFalse(savedNotifications.contains(username+bookTitle), username + " should have no notifications for " + bookTitle);
    }

    @And("{string} should have {int} books borrowed")
    public void should_have_books_borrowed(String username, int count) {
        User user = library.getUsers().getUser(username);
        assertNotNull(user, "User should not be null: " + username);
        assertEquals(count, user.getBorrowed().size(), username + " should have " + count + " books borrowed");
    }

    @And("all books are marked as available")
    public void all_books_are_marked_as_available() {
        Catalogue catalogue = library.getCatalogue();
        for (int i = 0; i < catalogue.getSize(); i++) {
            Book book = catalogue.getBook(i);
            assertTrue(book.getAvailability(), book.getTitle() + " should be available");
        }
    }

    @Then("the system should have a record stating {string}")
    public void the_system_should_have_a_record_stating(String record) {
        ArrayList<String> records = library.getRecord();
        boolean found = false;
        for (String rec : records) {
            if (rec.contains(record)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "System should have a record stating: " + record);
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