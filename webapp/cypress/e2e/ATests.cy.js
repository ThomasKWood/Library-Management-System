describe('Library Book Management Scenario Tests', () => {
  beforeEach(() => {
    // Reset items before each test
    cy.request('POST', 'http://localhost:3000/api/reset');
    cy.visit('http://localhost:3000');
    cy.get('#statusMessage').should('contain', '');
  });

  it('A1_scenario - basic borrow/return workflow', () => {
    // alice logs in, checks book status, borrows a book, and checks borrowed contents, then signs out
    cy.login('alice', 'pass123'); // this signs in the first user. it asserts on #statusMessage showing "Login successful.", using statusShows custom command
    cy.checkCurrentUser('alice'); // this is an extra assert to check that the current user is alice. it asserts on the #currentUser element containing "alice"
    // see that book is available, borrow
    cy.checkBookStatus('The Great Gatsby', 'Available'); // this is a precheck to see that the book is available before borrowing. it asserts on the #catalogueTable row for "The Great Gatsby" containing "Available"
    cy.borrowBook('The Great Gatsby', false); // this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: The Great Gatsby"
    cy.checkBorrowedContains('The Great Gatsby'); // this confirms alice borrowed a book. it asserts on the #borrowedBooksList containing "The Great Gatsby"
    // due date check
    const dueDateString = calculationDueDate();
    cy.dueDateCheckSelf('The Great Gatsby', dueDateString); // this checks that the book that has been borrowed has a due date. it asserts on the #borrowedBooksList row for "The Great Gatsby" containing the due date string
    cy.logout(); // this logs out the first user. it asserts on #statusMessage showing "Signed out."

    // bob logs in, sees that book is checked out, signs out
    cy.login('bob', 'pass456'); // this signs in the second user. it asserts on #statusMessage showing "Login successful."
    cy.checkCurrentUser('bob'); // this is an extra assert to check that the current user is bob. it asserts on the #currentUser element containing "bob"
    // see that book is not available
    cy.checkBookStatus('The Great Gatsby', 'Checked Out'); // this checks that the book is now checked out. it asserts on the #catalogueTable row for "The Great Gatsby" containing "Checked Out"
    cy.checkBorrowButtonNotExist('The Great Gatsby'); // this checks that the borrow button does not exist for the checked out book. it asserts on the #catalogueTable row for "The Great Gatsby" not containing a "Borrow" button
    cy.dueDateCheckList('The Great Gatsby', dueDateString); // this checks the due date in the list view for the checked out book. it asserts on the #catalogueTable row for "The Great Gatsby" containing the due date string
    cy.logout(); // this logs out the second user. it asserts on #statusMessage showing "Signed out."

    // alice logs back in, returns the book, and signs out
    cy.login('alice', 'pass123'); // this signs in the first user again. it asserts on #statusMessage showing "Login successful."
    cy.checkCurrentUser('alice'); // this is an extra assert to check that the current user is alice. it asserts on the #currentUser element containing "alice"
    cy.returnBook('The Great Gatsby'); // this returns the book that was borrowed to make it available again. it asserts on #statusMessage showing "Book returned."
    cy.logout(); // this logs out the first user. it asserts on #statusMessage showing "Signed out."

    // bob logs back in, sees that the book is available
    cy.login('bob', 'pass456'); // this signs in the second user again. it asserts on #statusMessage showing "Login successful."
    cy.checkCurrentUser('bob'); // this is an extra assert to check that the current user is bob. it asserts on the #currentUser element containing "bob"
    cy.checkBookStatus('The Great Gatsby', 'Available'); // this checks that the book is now available again. it asserts on the #catalogueTable row for "The Great Gatsby" containing "Available"
    cy.logout(); // this logs out the second user. it asserts on #statusMessage showing "Signed out."

    // end of a1_scenario test
  });

  it('multiple_holds_queue_processing - FIFO hold queue', () => {
    // // Add first item
    // cy.get('#itemInput').type('First Item');
    // cy.get('#addButton').click();
    
    // // Add second item
    // cy.get('#itemInput').type('Second Item');
    // cy.get('#addButton').click();
    
    // // Verify both items are displayed
    // cy.get('[data-testid="item"]').should('have.length', 2);
    // cy.get('[data-testid="item"]').first().should('contain', 'First Item');
    // cy.get('[data-testid="item"]').last().should('contain', 'Second Item');
        cy.login('alice', 'pass123');
  });

  it('borrowing_limit_and_hold_interactions - limit interactions', () => {
    // cy.get('#itemInput').type('Test Item');
    // cy.get('#addButton').click();
    
    // // Input should be empty after adding
    // cy.get('#itemInput').should('have.value', '');
        cy.login('alice', 'pass123');
  });
});

function calculationDueDate() {
    // format for due books is MM/DD/YYYY 14 days from today
    const dueDate = new Date();
    dueDate.setDate(dueDate.getDate() + 14);
    const dueDateString = (dueDate.getMonth() + 1).toString().padStart(2, '0') + '/' + dueDate.getDate().toString().padStart(2, '0') + '/' + dueDate.getFullYear();
    return dueDateString;
}