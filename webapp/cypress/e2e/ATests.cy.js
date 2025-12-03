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
    //cy.checkBorrowButtonNotExist('The Great Gatsby'); // this checks that the borrow button does not exist for the checked out book. it asserts on the #catalogueTable row for "The Great Gatsby" not containing a "Borrow" button
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
    // alice logs in and borrows 1984
    cy.login('alice', 'pass123');
    cy.checkCurrentUser('alice');
    cy.borrowBook('1984', false);
    cy.logout();

    // charlie logs in and places hold on 1984
    cy.login('charlie', 'pass789');
    cy.checkCurrentUser('charlie');
    cy.checkBookStatus('1984', 'Checked Out');
    //cy.checkBorrowButtonNotExist('1984');
    cy.borrowBook('1984', true);
    cy.checkQueuePosition('1984', 1);
    cy.checkHoldButtonDisabled('1984');
    cy.logout();

    // bob logs in and places hold on 1984
    cy.login('bob', 'pass456');
    cy.checkCurrentUser('bob');
    cy.checkBookStatus('1984', 'Checked Out');
    //cy.checkBorrowButtonNotExist('1984');
    cy.borrowBook('1984', true);
    cy.checkQueuePosition('1984', 2);
    cy.checkHoldButtonDisabled('1984');
    cy.logout();

    // alice logs back in and returns 1984
    cy.login('alice', 'pass123');
    cy.checkCurrentUser('alice');
    cy.returnBook('1984');
    cy.checkBookStatus('1984', 'On Hold');
    cy.logout();

    // bob logs in and checks availability of 1984
    cy.login('bob', 'pass456');
    cy.checkCurrentUser('bob');
    cy.checkBookStatus('1984', 'On Hold');
    cy.checkHoldButtonDisabled('1984');
    cy.logout();

    // charlie logs back in, sees notification, and borrows 1984
    cy.login('charlie', 'pass789');
    cy.checkCurrentUser('charlie');
    cy.checkNotificationContains('1984');
    cy.borrowBook('1984', false);
    cy.logout();

    // bob should be next in hold queue for 1984
    cy.login('bob', 'pass456');
    cy.checkCurrentUser('bob');
    cy.checkQueuePosition('1984', 1);
    cy.logout();
  });

  it('borrowing_limit_and_hold_interactions - limit interactions', () => {
      // alice logs in and borrows 3 books and tries to borrow a 4th but fails
      cy.login('alice', 'pass123');
      cy.borrowBook('The Great Gatsby', false);
      cy.borrowBook('1984', false);
      cy.borrowBook('Pride and Prejudice', false);
      cy.checkBorrowedCount(3);
      cy.borrowBookOverLimit('The Hobbit'); // important assertion
      cy.checkBorrowedCount(3);
      cy.checkBookStatus('The Hobbit', 'Available');
      // alice holds the hobbit instead
      cy.borrowBook('The Hobbit', true);
      cy.checkBookStatus('The Hobbit', 'On Hold');
      cy.checkQueuePosition('The Hobbit', 1);
      cy.checkBorrowedCount(3);
      // alice returns one book to drop below limit
      cy.returnBook('1984');
      cy.checkBorrowedCount(2);
      // alice borrows the hobbit now that she is under the limit
      cy.borrowBook('The Hobbit', false);
      cy.checkBorrowedCount(3);
      // alice logs out bob logs in and borrows harry potter
      cy.logout();
      cy.login('bob', 'pass456');
      cy.borrowBook('Harry Potter', false);
      cy.logout();
      // charlie places hold on harry potter
      cy.login('charlie', 'pass789');
      cy.checkBookStatus('Harry Potter', 'Checked Out');
      cy.borrowBook('Harry Potter', true);
      cy.checkQueuePosition('Harry Potter', 1);
      cy.logout();
      // alice places hold on harry potter
      cy.login('alice', 'pass123');
      cy.checkBookStatus('Harry Potter', 'Checked Out');
      cy.borrowBook('Harry Potter', true);
      cy.checkQueuePosition('Harry Potter', 2);
      cy.logout();
      // bob returns harry potter, charlie should be notified
      cy.login('bob', 'pass456');
      cy.returnBook('Harry Potter');
      cy.checkBookStatus('Harry Potter', 'On Hold');
      cy.logout();
      // charlie borrows harry potter, alice should be next in queue but not notified yet
      cy.login('charlie', 'pass789');
      cy.checkNotificationContains('Harry Potter');
      cy.checkBookStatus('Harry Potter', 'On Hold');
      cy.borrowBook('Harry Potter', false);
      cy.checkBookStatus('Harry Potter', 'Checked Out');
      cy.logout();
      // alice should be next in queue for harry potter
      cy.login('alice', 'pass123');
      // notification list should not contain harry potter yet
      cy.get('#notificationList').should('not.contain', 'Harry Potter');
      cy.checkQueuePosition('Harry Potter', 1);
      cy.logout();
      // charlie returns harry potter, alice should be notified
      cy.login('charlie', 'pass789');
      cy.returnBook('Harry Potter');
      cy.checkBookStatus('Harry Potter', 'On Hold');
      cy.logout();  
      // alice returns to go back under limit and borrow harry potter
      cy.login('alice', 'pass123');
      cy.checkNotificationContains('Harry Potter')
      cy.returnBook('The Great Gatsby');
      cy.checkBookStatus('Harry Potter', 'On Hold');
      cy.borrowBook('Harry Potter', false);
      cy.checkBookStatus('Harry Potter', 'Checked Out');
      cy.logout();




  });
});

function calculationDueDate() {
    // format for due books is MM/DD/YYYY 14 days from today
    const dueDate = new Date();
    dueDate.setDate(dueDate.getDate() + 14);
    const dueDateString = (dueDate.getMonth() + 1).toString().padStart(2, '0') + '/' + dueDate.getDate().toString().padStart(2, '0') + '/' + dueDate.getFullYear();
    return dueDateString;
}