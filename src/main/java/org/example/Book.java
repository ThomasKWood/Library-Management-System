package org.example;

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

    public Book(String title, String author){
        this.title = title;
        this.author = author;
        this.status = StatusCode.AVAIL; // default
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

    public StatusCode getStatusCode() {
        return status;
    }

    public void setStatus(StatusCode status) {
        this.status = status;
        // add logic to determine due date and clear when avail
    }
}
