package org.example;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.LinkedList;

public class Book {
    public enum StatusCode {
        AVAIL("Available", true),
        CHECKED("Checked Out", false),
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

    private final String title;
    private final String author;
    private StatusCode status;
    private LocalDateTime due;
    private Queue<User> holdQueue;

    public Book(String title, String author){
        this.title = title;
        this.author = author;
        this.status = StatusCode.AVAIL; // default
        this.due = null;
        this.holdQueue = new LinkedList<>();

    }

    public String getTitle(){
        return this.title;
    }

    public String getAuthor(){
        return this.author;
    }

    public boolean getAvailability() {
        return status.isAvailable();
    }
    // TODO: should this update status to true if due date is passed current date?

    public StatusCode getStatusCode() {
        return status;
    }

    private void setStatus(StatusCode status) {
        this.status = status;
    }


    public LocalDateTime setDueDateNow() {
        // implement later
        if (getAvailability()) {
            this.due = LocalDateTime.now().plusDays(14);
            setStatus(StatusCode.CHECKED);
        }


        return this.due; // return current due date if not available
    }

    public void placeHold(User usr) {
        if (!holdQueue.contains(usr)) {
            holdQueue.add(usr);
            setStatus(StatusCode.HOLD);
        }
    }

    public LocalDateTime getDue() {
        return this.due;
    }
    public User firstQueue() {
        return holdQueue.peek();
    }
    // return - clear CHECKED

}
