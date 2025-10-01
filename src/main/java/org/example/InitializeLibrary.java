package org.example;

public class InitializeLibrary {

    protected Catalogue catalogue = new Catalogue();

    public Catalogue initLibrary() {
        catalogue.addBook("Skunk Works", "Ben Rich"); // 1
        catalogue.addBook("Mickey7", "Edward Ashton"); // 2
        catalogue.addBook("American Sniper", "Chris Kyle"); // 3
        catalogue.addBook("Strike Eagle", "William L. Smallwood"); // 4
        catalogue.addBook("The Shining (Novel)", "Stephen King"); // 5
        catalogue.addBook("Once Upon a Time in Hollywood (Novel)", "Quentin Tarantino"); // 6
        catalogue.addBook("Black Hawk Down (Novel)", "Mark Bowden"); // 7
        catalogue.addBook("Who Goes There?", "John W. Campbell"); // 8
        catalogue.addBook("The Bourne Identity (Novel)", "Robert Ludlum"); // 9
        catalogue.addBook("Band of Brothers", "Stephen E. Ambrose"); // 10
        catalogue.addBook("Permanent Record", "Edward Snowden"); // 11
        catalogue.addBook("Thomas the Tank Engine", "Wilbert Awdry"); // 12
        catalogue.addBook("Fulcrum", "Alexander Zuyev"); // 13
        catalogue.addBook("A Time to Die", "Robert Moore"); // 14
        catalogue.addBook("Stealth", "Peter J. Westwick"); // 15
        catalogue.addBook("No Easy Day", "Matt Bissonnette & Kevin Maurer"); // 16
        catalogue.addBook("The Man in the High Castle (Novel)", "Philip K. Dick"); // 17
        catalogue.addBook("Nineteen Eighty-Four", "George Orwell"); // 18
        catalogue.addBook("The Hunt for Red October", "Tom Clancy"); // 19
        catalogue.addBook("Shōgun (Novel)", "James Clavell"); // 20

        return catalogue;
    }

}
