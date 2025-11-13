# BDD A2 Scenarios
Feature: Library Operations
  As a user
  I want to borrow and return books from the library
  So that I can read the books when they are available and I am not confused when they are not available

  Scenario: Multi User Borrow and Return with Availability Validated
    Given the library is initialized
    And "The Great Gatsby" is available in the library
    # alice logs in and borrows The Great Gatsby
    When "alice" logs in
    Then the current session should be for "alice"
    When "alice" attempts to borrow "The Great Gatsby"
    Then "The Great Gatsby" should be marked as checked out
    When "alice" logs out
    # bob logs in and sees The Great Gatsby is checked out
    And "bob" logs in
    Then the current session should be for "bob"
    When "bob" checks availability of "The Great Gatsby"
    Then "The Great Gatsby" should be marked as checked out
    When "bob" logs out
    # alice returns The Great Gatsby
    And "alice" logs back in
    Then the current session should be for "alice"
    When "alice" returns "The Great Gatsby"
    Then "The Great Gatsby" should be marked as available
    When "alice" logs out
    # bob sees The Great Gatsby is now available
    And "bob" logs back in
    Then the current session should be for "bob"
    When "bob" checks availability of "The Great Gatsby"
    Then "The Great Gatsby" should be marked as available

  Scenario: Multiple holds placed on book - queue testing
    Given the library is initialized
    And our testing variables have been cleared
    And "1984" is available in the library
    # alice borrows 1984, bob and charlie place holds
    When "alice" logs in
    Then the current session should be for "alice"
    When "alice" attempts to borrow "1984"
    Then "1984" should be marked as checked out
    When "alice" logs out
    And "charlie" logs in
    Then the current session should be for "charlie"
    When "charlie" attempts to borrow "1984"
    Then "charlie" should be in the hold queue for "1984"
    When "charlie" logs out
    And "bob" logs in
    Then the current session should be for "bob"
    When "bob" attempts to borrow "1984"
    Then "bob" should be in the hold queue for "1984"
    When "bob" logs out
    # alice returns 1984
    And "alice" logs back in
    Then the current session should be for "alice"
    When "alice" returns "1984"
    Then "1984" should be marked as on hold
    When "alice" logs out
    Then the current session should be for "nobody"
    # bob attempts to borrow 1984 but charlie is first in queue
    When "bob" logs in
    Then the current session should be for "bob"
    When "bob" checks availability of "1984"
    Then "1984" should be marked as on hold
    When "bob" logs out
    # charlie notified and borrows 1984
    And "charlie" logs back in
    Then "charlie" should have a notification for "1984"
    When "charlie" attempts to borrow "1984"
    Then "1984" should be marked as checked out
    # bob now next in queue
    And "bob" should be next in the hold queue for "1984"

    Scenario: Borrowing limit and hold interactions
      Given the library is initialized
      And our testing variables have been cleared
      # alice borrows 3 books
      When "alice" logs in
      Then the current session should be for "alice"
      When "alice" attempts to borrow "The Great Gatsby"
      Then "The Great Gatsby" should be marked as checked out
      When "alice" attempts to borrow "1984"
      Then "1984" should be marked as checked out
      When "alice" attempts to borrow "Pride and Prejudice"
      Then "Pride and Prejudice" should be marked as checked out
      And "alice" should have 3 books borrowed
      # alice attempts to borrow a 4th but is offered to place hold instead
      When "alice" attempts to borrow "The Hobbit"
      Then "The Hobbit" should be marked as on hold
      And "alice" should be in the hold queue for "The Hobbit"
      And "alice" should have 3 books borrowed
      # alice returns one book to drop below limit
      When "alice" returns "1984"
      Then "1984" should be marked as available
      # alice is now able to The Hobbit
      When "alice" attempts to borrow "The Hobbit"
      Then "The Hobbit" should be marked as checked out
      When "alice" logs out
      # bob and charlie place holds on Harry Potter
      And "bob" logs in
      Then the current session should be for "bob"
      When "bob" attempts to borrow "Harry Potter"
      Then "Harry Potter" should be marked as checked out
      When "bob" logs out
      And "charlie" logs in
      Then the current session should be for "charlie"
      When "charlie" attempts to borrow "Harry Potter"
      Then "Harry Potter" should be marked as checked out
      And "charlie" should be in the hold queue for "Harry Potter"
      When "charlie" logs out
      # now alice places a hold on Harry Potter
      And "alice" logs back in
      Then the current session should be for "alice"
      When "alice" attempts to borrow "Harry Potter"
      Then "Harry Potter" should be marked as checked out
      And "alice" should be in the hold queue for "Harry Potter"
      When "alice" logs out
      # bob returns Harry Potter, charlie should be notified
      And "bob" logs back in
      Then the current session should be for "bob"
      When "bob" returns "Harry Potter"
      Then "Harry Potter" should be marked as on hold
      When "bob" logs out
      Then the current session should be for "nobody"
      # charlie borrows Harry Potter, alice should be next in queue but not notified yet
      When "charlie" logs in
      Then "charlie" should have a notification for "Harry Potter"
      When "charlie" attempts to borrow "Harry Potter"
      Then "Harry Potter" should be marked as checked out
      When "charlie" logs out
      And "alice" logs back in
      Then "alice" should have no notifications for "Harry Potter"
      And "alice" should be next in the hold queue for "Harry Potter"
      # charlie returns Harry Potter, alice should be notified
      When "alice" logs out
      And "charlie" logs back in
      Then the current session should be for "charlie"
      When "charlie" returns "Harry Potter"
      Then "Harry Potter" should be marked as on hold
      When "charlie" logs out
      Then the current session should be for "nobody"
      # alice returns to go back under limit and borrow Harry Potter
      When "alice" logs in
      Then the current session should be for "alice"
      When "alice" returns "The Great Gatsby"
      Then "The Great Gatsby" should be marked as available
      When "alice" attempts to borrow "Harry Potter"
      Then "Harry Potter" should be marked as checked out

  Scenario: No books borrowed
    Given the library is initialized
    And our testing variables have been cleared
    # alice logs in, check no books borrowed and all books available
    When "alice" logs in
    Then the current session should be for "alice"
    And "alice" should have 0 books borrowed
    And all books are marked as available
    # alice attempts to return a book when none are borrowed
    When "alice" returns "The Great Gatsby"
    Then the system should have a record stating "alice attempted return with no books checked out"
    When "alice" logs out
    # bob logs in, check no books borrowed and all books available
    And "bob" logs in
    Then the current session should be for "bob"
    And "bob" should have 0 books borrowed
    And all books are marked as available
    # bob attempts to return a book when none are borrowed
    When "bob" returns "1984"
    Then the system should have a record stating "bob attempted return with no books checked out"
    When "bob" logs out
    # charlie logs in, check no books borrowed and all books available
    And "charlie" logs in
    Then the current session should be for "charlie"
    And "charlie" should have 0 books borrowed
    And all books are marked as available
    # charlie attempts to return a book when none are borrowed
    When "charlie" returns "Pride and Prejudice"
    Then the system should have a record stating "charlie attempted return with no books checked out"