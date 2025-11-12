
# BDD A2 Scenarios
Feature: Library Operations
  As a user
  I want to borrow and return books from the library
  So that I can read the books when they are available and I am not confused when they are not available

  # Multi User Borrow and Return with Availability Validated Acceptance Test
  # user User1 logs in, borrows "The Great Gatsby", logs out. User2 logs in, sees book checked out. User1 logs back in, returns book. User2 sees book available again.
  # test that only one user can borrow a book at a time and the availability status updates correctly upon return.
  Scenario: Multi User Borrow and Return with Availability Validated
    Given the library is initialized
    And "The Great Gatsby" is available in the library
    When "alice" logs in
    Then the current session should be for "alice"
    When "alice" attempts to borrow "The Great Gatsby"
    Then "The Great Gatsby" should be marked as checked out
    When "alice" logs out
    And "bob" logs in
    Then the current session should be for "bob"
    When "bob" checks availability of "The Great Gatsby"
    Then "The Great Gatsby" should be marked as checked out
    When "bob" logs out
    And "alice" logs back in
    Then the current session should be for "alice"
    When "alice" returns "The Great Gatsby"
    Then "The Great Gatsby" should be marked as available
    When "alice" logs out
    And "bob" logs back in
    Then the current session should be for "bob"
    When "bob" checks availability of "The Great Gatsby"
    Then "The Great Gatsby" should be marked as available

  #multiple_holds_queue_processing
  #Test the hold queue system with three users competing for the same book, demonstrating that:
  #● Users can place holds on unavailable books
  #● Hold queue follows FIFO ordering
  #● Notifications are sent to the correct user when book becomes available
  #● Only the notified user can borrow the reserved book
  #● Queue advances properly when reserved books are borrowed or returned
  #Assert: Hold-queue order and notifications are correct.
  Scenario: Multiple holds placed on book - queue testing
    Given the library is initialized
    And our testing variables have been cleared
    And "1984" is available in the library
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
    And "alice" logs back in
    Then the current session should be for "alice"
    When "alice" returns "1984"
    Then "1984" should be marked as on hold
    When "alice" logs out
    Then the current session should be for "nobody"
    When "bob" logs in
    Then the current session should be for "bob"
    When "bob" checks availability of "1984"
    Then "1984" should be marked as on hold
    When "bob" logs out
    And "charlie" logs back in
    Then "charlie" should have a notification for "1984"
    When "charlie" attempts to borrow "1984"
    Then "1984" should be marked as checked out
    And "bob" should be next in the hold queue for "1984"

    # borrowing_limit_and_hold_interactions
    # Test the interaction between borrowing limits and holds, demonstrating that:
    #   ● Users cannot exceed the 3-book borrowing limit
    #   ● Users can place holds even when at the borrowing limit (i.e., when they already have 3
    #     books borrowed)
    #   ● When a user at the borrowing limit returns a book, they drop below the limit
    #   ● If that user is next in the hold queue for a book, they receive a notification that the book
    #     is now available for them to borrow
    # Assert: Users can't exceed 3 books, can place holds at the limit, gain borrowing capacity after
    # returns, and receive notifications when their held books are returned by others (regardless of
    # their current borrowing capacity).
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