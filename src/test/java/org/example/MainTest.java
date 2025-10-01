package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

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

    // test for checking


}
