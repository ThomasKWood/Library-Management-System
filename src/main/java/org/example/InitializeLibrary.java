package org.example;

public class InitializeLibrary {

    protected Catalogue catalogue = new Catalogue();

    public Catalogue initLibrary() {
        catalogue.addBook("The Great Gatsby", "A2BookAuthor1"); // 1
        catalogue.addBook("To Kill a Mockingbird", "A2BookAuthor2"); // 2
        catalogue.addBook("1984", "A2BookAuthor3"); // 3
        catalogue.addBook("Pride and Prejudice", "A2BookAuthor4"); // 4
        catalogue.addBook("The Hobbit", "A2BookAuthor5"); // 5
        catalogue.addBook("Harry Potter", "A2BookAuthor6"); // 6
        catalogue.addBook("The Catcher in the Rye", "A2BookAuthor7"); // 7
        catalogue.addBook("Animal Farm", "A2BookAuthor8"); // 8
        catalogue.addBook("Lord of the Flies", "A2BookAuthor9"); // 9
        catalogue.addBook("Jane Eyre", "A2BookAuthor10"); // 10
        catalogue.addBook("Wuthering Heights", "A2BookAuthor11"); // 11
        catalogue.addBook("Moby Dick", "A2BookAuthor12"); // 12
        catalogue.addBook("The Odyssey", "A2BookAuthor13"); // 13
        catalogue.addBook("Hamlet", "A2BookAuthor14"); // 14
        catalogue.addBook("War and Peace", "A2BookAuthor15"); // 15
        catalogue.addBook("The Divine Comedy", "A2BookAuthor16"); // 16
        catalogue.addBook("Crime and Punishment", "A2BookAuthor17"); // 17
        catalogue.addBook("Don Quixote", "A2BookAuthor18"); // 18
        catalogue.addBook("The Iliad", "A2BookAuthor19"); // 19
        catalogue.addBook("Ulysses", "A2BookAuthor20"); // 20

        return catalogue;
    }

}