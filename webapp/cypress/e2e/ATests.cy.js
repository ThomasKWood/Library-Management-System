describe('Library Book Management Scenario Tests', () => {
  beforeEach(() => {
    // Reset items before each test
    cy.request('POST', 'http://localhost:3000/api/reset');
    cy.visit('http://localhost:3000');
    cy.get('#statusMessage').should('contain', '');
  });

  it('A1_scenario - basic borrow/return workflow', () => {
    // alice logs in, checks book status, borrows a book, and checks borrowed contents, then signs out
    cy.log('alice logs in, borrows The Great Gatsby, and logs out');
    cy.login('alice', 'pass123'); // this signs in the first user. it asserts on #statusMessage showing "Login successful.", using statusShows custom command
    cy.checkCurrentUser('alice'); // this is an extra assert to check that the current user is alice. it asserts on the #currentUser element containing "alice"
    // see that book is available, borrow
    cy.log('checking that The Great Gatsby is available before borrowing');
    cy.checkBookStatus('The Great Gatsby', 'Available'); // this is a precheck to see that the book is available before borrowing. it asserts on the #catalogueTable row for "The Great Gatsby" containing "Available"
    cy.borrowBook('The Great Gatsby', false); // this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: The Great Gatsby"
    cy.checkBorrowedContains('The Great Gatsby'); // this confirms alice borrowed a book. it asserts on the #borrowedBooksList containing "The Great Gatsby"
    // due date check
    const dueDateString = calculationDueDate();
    cy.dueDateCheckSelf('The Great Gatsby', dueDateString); // this checks that the book that has been borrowed has a due date. it asserts on the #borrowedBooksList row for "The Great Gatsby" containing the due date string
    cy.logout(); // this logs out the first user. it asserts on #statusMessage showing "Signed out."

    // bob logs in, sees that book is checked out, signs out
    cy.log('bob logs in, checks The Great Gatsby status, and logs out');
    cy.login('bob', 'pass456'); // this signs in the second user. it asserts on #statusMessage showing "Login successful."
    cy.checkCurrentUser('bob'); // this is an extra assert to check that the current user is bob. it asserts on the #currentUser element containing "bob"
    // see that book is not available
    cy.checkBookStatus('The Great Gatsby', 'Checked Out'); // IMPORTANT ASSERTION // this checks that the book is now checked out. it asserts on the #catalogueTable row for "The Great Gatsby" containing "Checked Out"
    //cy.checkBorrowButtonNotExist('The Great Gatsby'); // this checks that the borrow button does not exist for the checked out book. it asserts on the #catalogueTable row for "The Great Gatsby" not containing a "Borrow" button
    cy.dueDateCheckList('The Great Gatsby', dueDateString); // this checks the due date in the list view for the checked out book. it asserts on the #catalogueTable row for "The Great Gatsby" containing the due date string
    cy.logout(); // this logs out the second user. it asserts on #statusMessage showing "Signed out."

    // alice logs back in, returns the book, and signs out
    cy.log('alice logs back in, returns The Great Gatsby, and logs out');
    cy.login('alice', 'pass123'); // this signs in the first user again. it asserts on #statusMessage showing "Login successful."
    cy.checkCurrentUser('alice'); // this is an extra assert to check that the current user is alice. it asserts on the #currentUser element containing "alice"
    cy.returnBook('The Great Gatsby'); // IMPORTANT ASSERTION // this returns the book that was borrowed to make it available again. it asserts on #statusMessage showing "Book returned."
    cy.logout(); // this logs out the first user. it asserts on #statusMessage showing "Signed out."

    // bob logs back in, sees that the book is available
    cy.log('bob logs back in, checks The Great Gatsby status, and logs out');
    cy.login('bob', 'pass456'); // this signs in the second user again. it asserts on #statusMessage showing "Login successful."
    cy.checkCurrentUser('bob'); // this is an extra assert to check that the current user is bob. it asserts on the #currentUser element containing "bob"
    cy.checkBookStatus('The Great Gatsby', 'Available'); // IMPORTANT ASSERTION // this checks that the book is now available again. it asserts on the #catalogueTable row for "The Great Gatsby" containing "Available"
    cy.logout(); // this logs out the second user. it asserts on #statusMessage showing "Signed out."

    // end of a1_scenario test
  });

  it('multiple_holds_queue_processing - FIFO hold queue', () => {
    // alice logs in and borrows 1984
    cy.log('alice logs in, borrows 1984, and logs out');
    cy.login('alice', 'pass123'); // this signs in the first user. it asserts on #statusMessage showing "Login successful."
    cy.checkCurrentUser('alice'); // this is an extra assert to check that the current user is alice. it asserts on the #currentUser element containing "alice"
    cy.borrowBook('1984', false); // this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: 1984"
    cy.logout();

    // charlie logs in and places hold on 1984
    cy.log('charlie logs in, places hold on 1984, and logs out');
    cy.login('charlie', 'pass789'); // this signs in the second user. it asserts on #statusMessage showing "Login successful."
    cy.checkCurrentUser('charlie'); // this is an extra assert to check that the current user is charlie. it asserts on the #currentUser element containing "charlie"
    cy.checkBookStatus('1984', 'Checked Out'); // this checks that the book is checked out. it asserts on the #catalogueTable row for "1984" containing "Checked Out"
    //cy.checkBorrowButtonNotExist('1984');
    cy.borrowBook('1984', true); // IMPORTANT ASSERTION // this places a hold on the book. it asserts on #statusMessage showing "Hold placed."
    cy.checkQueuePosition('1984', 1); // this checks that charlie is first in the hold queue. it asserts on the #catalogueTable row for "1984" containing "Hold Position 1"
    cy.checkHoldButtonDisabled('1984'); // this checks that the hold button is disabled for "1984"
    cy.logout(); // this logs out the second user. it asserts on #statusMessage showing "Signed out."

    // bob logs in and places hold on 1984
    cy.log('bob logs in, places hold on 1984, and logs out');
    cy.login('bob', 'pass456'); // this signs in the third user. it asserts on #statusMessage showing "Login successful."
    //cy.checkBorrowButtonNotExist('1984');
    cy.borrowBook('1984', true); // this places a hold on the book. it asserts on #statusMessage showing "Hold placed."
    cy.checkQueuePosition('1984', 2); // IMPORTANT ASSERTION - checking FIFO works when adding // this checks that bob is second in the hold queue. it asserts on the #catalogueTable row for "1984" containing "Hold Position 2"
    cy.logout(); // this logs out the third user. it asserts on #statusMessage showing "Signed out."

    // alice logs back in and returns 1984
    cy.log('alice logs back in, returns 1984, and logs out');
    cy.login('alice', 'pass123'); // this signs in the first user again. it asserts on #statusMessage showing "Login successful."
    cy.returnBook('1984'); // this returns the book that was borrowed to make it available again. it asserts on #statusMessage showing "Book returned."
    cy.checkBookStatus('1984', 'On Hold'); // this checks that the book is now on hold after being returned. it asserts on the #catalogueTable row for "1984" containing "On Hold"
    cy.logout(); // this logs out the first user. it asserts on #statusMessage showing "Signed out."

    // bob logs in and checks availability of 1984
    cy.log('bob logs in, checks 1984 status, and logs out');
    cy.login('bob', 'pass456'); // this signs in the third user again. it asserts on #statusMessage showing "Login successful."
    cy.checkBookStatus('1984', 'On Hold'); // this checks that the book is still on hold. it asserts on the #catalogueTable row for "1984" containing "On Hold"
    cy.checkHoldButtonDisabled('1984'); // IMPORTANT ASSERTION - checking FIFO works properly when subtracting // this checks that the hold button is disabled for "1984"
    cy.logout();

    // charlie logs back in, sees notification, and borrows 1984  
    cy.log('charlie logs back in, sees notification, and borrows 1984');
    cy.login('charlie', 'pass789'); // this signs in the second user again. it asserts on #statusMessage showing "Login successful."
    cy.checkCurrentUser('charlie'); // this is an extra assert to check that the current user is charlie. it asserts on the #currentUser element containing "charlie"
    cy.checkNotificationContains('1984');  // IMPORTANT ASSERTION - check notification exists // this checks that charlie received notification for 1984. it asserts on the #notificationList containing "1984"
    cy.borrowBook('1984', false); // this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: 1984"
    cy.logout();

    // bob should be next in hold queue for 1984
    cy.log('bob should be next in hold queue for 1984');
    cy.login('bob', 'pass456'); // this signs in the third user again. it asserts on #statusMessage showing "Login successful."
    cy.checkQueuePosition('1984', 1); // IMPORTANT ASSERTION - checking FIFO works properly // this checks that bob is first in the hold queue. it asserts on the #catalogueTable row for "1984" containing "Hold Position 1"
    cy.logout();
  });

  it('borrowing_limit_and_hold_interactions - limit interactions', () => {
      // alice logs in and borrows 3 books and tries to borrow a 4th but fails
      cy.log('alice logs in, borrows 3 books, tries to borrow a 4th but fails, then holds the 4th');
      cy.login('alice', 'pass123'); // this signs in the first user. it asserts on #statusMessage showing "Login successful."
      cy.borrowBook('The Great Gatsby', false); // 1st book. this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: The Great Gatsby"
      cy.borrowBook('1984', false); // 2nd book. this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: 1984"
      cy.borrowBook('Pride and Prejudice', false); // 3rd book. this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: Pride and Prejudice"
      cy.checkBorrowedCount(3); // this checks that alice has 3 books borrowed. it asserts on the #borrowedBooksList showing 3 items
      cy.borrowBookOverLimit('The Hobbit'); // IMPORTANT ASSERTION - checking overlimit handling // this attempts to borrow a 4th book and expects failure. it asserts on #statusMessage showing "Borrowing limit reached (3 books)."
      cy.checkBorrowedCount(3); // this checks that alice still has 3 books borrowed. it asserts on the #borrowedBooksList showing 3 items
      cy.checkBookStatus('The Hobbit', 'Available'); // this checks that The Hobbit is still available. it asserts on the #catalogueTable row for "The Hobbit" containing "Available"
      // alice holds the hobbit instead
      cy.log('alice holds The Hobbit instead'); 
      cy.borrowBook('The Hobbit', true); // this places a hold on the book. it asserts on #statusMessage showing "Hold placed."
      cy.checkBookStatus('The Hobbit', 'On Hold');  // IMPORTANT ASSERTION - checking user can place hold even at limit // this checks that The Hobbit is now on hold. it asserts on the #catalogueTable row for "The Hobbit" containing "On Hold"
      cy.checkQueuePosition('The Hobbit', 1); // this checks that alice is first in the hold queue. it asserts on the #catalogueTable row for "The Hobbit" containing "Hold Position 1"
      cy.checkBorrowedCount(3); // this checks that alice still has 3 books borrowed. it asserts on the #borrowedBooksList showing 3 items
      // alice returns one book to drop below limit
      cy.log('alice returns 1984 to drop below limit');
      cy.returnBook('1984'); // this returns one of the borrowed books. it asserts on #statusMessage showing "Book returned."
      cy.checkBorrowedCount(2); // IMPORTANT ASSERTION - when alice returns a book at limit the borrowed count decreases // this checks that alice now has 2 books borrowed. it asserts on the #borrowedBooksList showing 2 items
      // alice borrows the hobbit now that she is under the limit
      cy.log('alice borrows The Hobbit now that she is under the limit');
      cy.borrowBook('The Hobbit', false); // this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: The Hobbit"
      cy.checkBorrowedCount(3); // this checks that alice now has 3 books borrowed. it asserts on the #borrowedBooksList showing 3 items
      // alice logs out bob logs in and borrows harry potter
      cy.log('alice logs out, bob logs in and borrows Harry Potter'); 
      cy.logout(); // this logs out the first user. it asserts on #statusMessage showing "Signed out."
      cy.login('bob', 'pass456'); // this signs in the second user. it asserts on #statusMessage showing "Login successful."
      cy.borrowBook('Harry Potter', false); // this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: Harry Potter"
      cy.logout(); // this logs out the second user. it asserts on #statusMessage showing "Signed out."
      // charlie places hold on harry potter
      cy.log('charlie logs in, places hold on Harry Potter, and logs out');
      cy.login('charlie', 'pass789'); // this signs in the third user. it asserts on #statusMessage showing "Login successful."
      cy.checkBookStatus('Harry Potter', 'Checked Out'); // this checks that the book is checked out. it asserts on the #catalogueTable row for "Harry Potter" containing "Checked Out"
      cy.borrowBook('Harry Potter', true); // this places a hold on the book. it asserts on #statusMessage showing "Hold placed."
      cy.checkQueuePosition('Harry Potter', 1); // this checks that charlie is first in the hold queue. it asserts on the #catalogueTable row for "Harry Potter" containing "Hold Position 1"
      cy.logout(); // this logs out the third user. it asserts on #statusMessage showing "Signed out."
      // alice places hold on harry potter
      cy.log('alice logs in, places hold on Harry Potter, and logs out');
      cy.login('alice', 'pass123'); // this signs in the first user again. it asserts on #statusMessage showing "Login successful."
      cy.checkBookStatus('Harry Potter', 'Checked Out'); // this checks that the book is checked out. it asserts on the #catalogueTable row for "Harry Potter" containing "Checked Out"
      cy.borrowBook('Harry Potter', true); // this places a hold on the book. it asserts on #statusMessage showing "Hold placed."
      cy.checkQueuePosition('Harry Potter', 2); // this checks that alice is second in the hold queue. it asserts on the #catalogueTable row for "Harry Potter" containing "Hold Position 2"
      cy.logout(); // this logs out the first user. it asserts on #statusMessage showing "Signed out."
      // bob returns harry potter, charlie should be notified
      cy.log('bob logs in, returns Harry Potter, and logs out'); 
      cy.login('bob', 'pass456'); // this signs in the second user again. it asserts on #statusMessage showing "Login successful."
      cy.returnBook('Harry Potter'); // this returns the book. it asserts on #statusMessage showing "Book returned: Harry Potter"
      cy.checkBookStatus('Harry Potter', 'On Hold'); // this checks that the book is on hold. it asserts on the #catalogueTable row for "Harry Potter" containing "On Hold"
      cy.logout(); // this logs out the second user. it asserts on #statusMessage showing "Signed out."
      // charlie borrows harry potter, alice should be next in queue but not notified yet
      cy.log('charlie logs in, borrows Harry Potter, and logs out');
      cy.login('charlie', 'pass789'); // this signs in the third user again. it asserts on #statusMessage showing "Login successful."
      cy.checkNotificationContains('Harry Potter'); // IMPORTANT ASSERTION - checks that the next person in the hold queue gets notified after return // this checks that charlie received notification for Harry Potter. it asserts on the #notificationList containing "Harry Potter"
      cy.checkBookStatus('Harry Potter', 'On Hold'); // this checks that the book is on hold. it asserts on the #catalogueTable row for "Harry Potter" containing "On Hold"
      cy.borrowBook('Harry Potter', false); // this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: Harry Potter"
      cy.checkBookStatus('Harry Potter', 'Checked Out'); // this checks that the book is checked out. it asserts on the #catalogueTable row for "Harry Potter" containing "Checked Out"
      cy.logout(); // this logs out the third user. it asserts on #statusMessage showing "Signed out."
      // alice should be next in queue for harry potter
      cy.log('alice logs in, checks notifications and queue position for Harry Potter');
      cy.login('alice', 'pass123'); // this signs in the first user again. it asserts on #statusMessage showing "Login successful."
      // notification list should not contain harry potter yet
      cy.get('#notificationList').should('not.contain', 'Harry Potter'); // this checks that alice has not yet received notification for Harry Potter. it asserts on the #notificationList not containing "Harry Potter"
      cy.checkQueuePosition('Harry Potter', 1); // this checks that alice is now first in the hold queue. it asserts on the #catalogueTable row for "Harry Potter" containing "Hold Position 1"
      cy.logout(); // this logs out the first user. it asserts on #statusMessage showing "Signed out."
      // charlie returns harry potter, alice should be notified
      cy.log('charlie logs in, returns Harry Potter, and logs out');
      cy.login('charlie', 'pass789'); // this signs in the third user again. it asserts on #statusMessage showing "Login successful."
      cy.returnBook('Harry Potter'); // this returns the book. it asserts on #statusMessage showing "Book returned: Harry Potter"
      cy.checkBookStatus('Harry Potter', 'On Hold'); // this checks that the book is on hold. it asserts on the #catalogueTable row for "Harry Potter" containing "On Hold"
      cy.logout(); // this logs out the third user. it asserts on #statusMessage showing "Signed out."
      // alice returns to go back under limit and borrow harry potter
      cy.log('alice logs in, checks notifications, returns a book to go under limit, and borrows Harry Potter');
      cy.login('alice', 'pass123'); // this signs in the first user again. it asserts on #statusMessage showing "Login successful."
      cy.checkNotificationContains('Harry Potter') // this checks that alice received notification for Harry Potter. it asserts on the #notificationList containing "Harry Potter"
      cy.returnBook('The Great Gatsby'); // this returns one of the borrowed books to go under limit. it asserts on #statusMessage showing "Book returned."
      cy.checkBookStatus('Harry Potter', 'On Hold'); // this checks that the book is on hold. it asserts on the #catalogueTable row for "Harry Potter" containing "On Hold"
      cy.borrowBook('Harry Potter', false); // this borrows the book without placing a hold. it asserts on #statusMessage showing "Book borrowed: Harry Potter"
      cy.checkBookStatus('Harry Potter', 'Checked Out'); // this checks that the book is checked out. it asserts on the #catalogueTable row for "Harry Potter" containing "Checked Out"
      cy.logout(); // this logs out the first user. it asserts on #statusMessage showing "Signed out."
  });
});

function calculationDueDate() {
    // format for due books is MM/DD/YYYY 14 days from today
    const dueDate = new Date();
    dueDate.setDate(dueDate.getDate() + 14);
    const dueDateString = (dueDate.getMonth() + 1).toString().padStart(2, '0') + '/' + dueDate.getDate().toString().padStart(2, '0') + '/' + dueDate.getFullYear();
    return dueDateString;
}