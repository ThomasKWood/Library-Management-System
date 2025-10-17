package org.example;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Represents a single library book with title, author, status, due date and a hold queue.
 * Responsibilities:
 * - Track availability status and due date when checked out.
 * - Manage a queue of Users who placed holds.
 */
public class Book {
    public enum StatusCode {
        // Book is available for checkout
        AVAIL("Available", true),
        // Book is currently checked out by a user
        CHECKED("Checked Out", false),
        // Book has one or more holds placed on it
        HOLD("On Hold", false);

        private final String label;
        private final boolean available;

        StatusCode(String label, boolean available) {
            this.label = label;
            this.available = available;
        }

        public String getLabel() {
            return label;
        }

        public boolean isAvailable() {
            return available;
        }
    }

    // Title of the book (immutable)
    private final String title;
    // Author of the book (immutable)
    private final String author;
    // Current status of the book (Available / Checked Out / On Hold)
    private StatusCode status;
    // Due date when the book is checked out (null when not checked out)
    private LocalDateTime due;
    // Queue of users waiting for the book when it's checked out
    private Queue<User> holdQueue;

    public Book(String title, String author){
        this.title = title;
        this.author = author;
        this.status = StatusCode.AVAIL; // default: book starts available
        this.due = null;
        this.holdQueue = new LinkedList<>();
    }

    // Return the book title
    public String getTitle(){
        return this.title;
    }

    // Return the book author
    public String getAuthor(){
        return this.author;
    }

    // Return whether the book is currently available for checkout
    public boolean getAvailability() {
        return status.isAvailable();
    }

    // Return the internal status code enum
    public StatusCode getStatusCode() {
        return status;
    }

    // Return the currently set due date (null if none)
    public LocalDateTime getDue() {
        return this.due;
    }

    // Peek at the first user in the hold queue without removing them
    public User getFirst() {
        return holdQueue.peek();
    }

    // Internal helper to update the status code
    private void setStatus(StatusCode status) {
        this.status = status;
    }

    /**
     * Set due date to 14 days from now if the book is currently available.
     * Also marks the book as checked out.
     * Returns the newly set due date, or the current due date if not available.
     */
    public LocalDateTime setDueDateNow() {
        // If the book is available, assign a due date 14 days from now and mark as checked out.
        if (getAvailability()) {
            this.due = LocalDateTime.now().plusDays(14);
            setStatus(StatusCode.CHECKED);
        }

        // Return current due date (either newly set or existing)
        return this.due;
    }

    /**
     * Place a hold for the given user. If the book is currently checked out,
     * the user is added to the hold queue if not already present. If the book
     * is available, placing a hold moves it into HOLD state and adds the user.
     */
    public void placeHold(User usr) {
        if (status.equals(StatusCode.CHECKED)) {
            // Book is checked out: only enqueue if not already waiting
            if (!holdQueue.contains(usr)) {
                holdQueue.add(usr);
            }
        } else {
            // Book is available or in HOLD: add user and set status to HOLD
            if (!holdQueue.contains(usr)) {
                holdQueue.add(usr);
                setStatus(StatusCode.HOLD);
            }
        }
    }

    /**
     * Return the book. Clears the due date and updates status depending on whether
     * there are pending holds (HOLD) or it becomes available (AVAIL).
     */
    public void returnBook() {
        this.due = null;
        if (holdQueue.isEmpty()) {
            // No pending holds: book becomes available
            setStatus(StatusCode.AVAIL);
        } else {
            // Holds exist: keep the book in HOLD state
            setStatus(StatusCode.HOLD);
        }
    }

    // Check whether a specific user is in the hold queue
    public boolean checkQueue(User usr) {
        return holdQueue.contains(usr);
    }

    // Remove the first user from the hold queue (when their hold is fulfilled)
    public void popFirst() {
        holdQueue.poll();
    }
}
