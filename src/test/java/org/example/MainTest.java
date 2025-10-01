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
        for (int i = 0, i<catalogue.getSize(), i++) {
            if (titles.isEmpty() && catalogue.getSize() != 0) {
                if (catalogue.getBook(i).getTitle() != null) {
                    titles.add(catalogue.getBook(0).getTitle());
                } else {
                    if (catalogue.getBook(i).getTitle() != null && catalogue.getBook(i).getAuthor() != null) {
                        //check if in titles

                        if (titles.contains(catalogue.getBook(i).getTitle())) {
                            // throw error
                        } else {
                            titles.add(catalogue.getBook(i).getTitle());
                        }
                    } else {
                        // send false for being null
                    }
                }
            }
        }
    }



}
