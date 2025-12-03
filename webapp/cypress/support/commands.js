// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
Cypress.Commands.add('login', (name, pass) => {
  cy.visit('/');
  cy.get('#username').type(name);
  cy.get('#password').type(pass);
  cy.get('#loginBtn').click();
  cy.statusShows(`Login successful`);
});

Cypress.Commands.add('logout', () => {
  cy.visit('/');
  cy.get('#logoutButton').click();
  cy.statusShows('Signed out.');
});

Cypress.Commands.add('statusShows', (string) => {
  cy.get('#statusMessage').should('contain', string);
});

Cypress.Commands.add('checkCurrentUser', (user) => {
  cy.get('#currentUser').should('contain', user);
});

Cypress.Commands.add('checkBookStatus', (bookName, status) => {
    cy.get('#catalogueTable tr')
        .contains('td', bookName)
        .parent()              // move to the row <tr>
        .within(() => {
            cy.contains(status).should('exist');
        });

});

Cypress.Commands.add('borrowBook', (bookName, hold) => {
    if (hold) {
        cy.get('#catalogueTable tr')
            .contains('td', bookName)
            .parent()              // move to the row <tr>
            .within(() => {
                cy.contains('Place Hold').click();
            });
        cy.statusShows('Hold placed.');
    } else {
        cy.get('#catalogueTable tr')
            .contains('td', bookName)
            .parent()              // move to the row <tr>
            .within(() => {
                cy.contains('Borrow').click();
            });
        cy.statusShows('Book borrowed: ' + bookName);
    }
});

Cypress.Commands.add('returnBook', (bookName) => {
    cy.get('#borrowedTable tr')
        .contains('td', bookName)
        .parent()              // move to the row <tr>
        .within(() => {
            cy.contains('Return').click();
        });
    cy.statusShows('Book returned.');
});

// for due date check in all books list
Cypress.Commands.add('dueDateCheckList', (bookName, date) => {
    cy.get('#catalogueTable tr')
        .contains('td', bookName)
        .parent()              // move to the row <tr>
        .within(() => {
            cy.contains(date).should('exist');
        });
});

// for borrowed books list due date check
Cypress.Commands.add('dueDateCheckSelf', (bookName, date) => {
    cy.get('#borrowedTable tr')
        .contains('td', bookName)
        .parent()              // move to the row <tr>
        .within(() => {
            cy.contains(date).should('exist');
        });
});

// check borrowed contains book
Cypress.Commands.add('checkBorrowedContains', (bookName) => {
    cy.get('#borrowedTable tr')
        .contains('td', bookName)
        .should('exist');
});

// check borrow button doesnt exist for a book
Cypress.Commands.add('checkBorrowButtonNotExist', (bookName) => {
    cy.get('#catalogueTable tr')
        .contains('td', bookName)
        .parent()              // move to the row <tr>
        .within(() => {
            cy.contains('Borrow').should('not.exist');
        });
});

// check hold button disabled for a book
Cypress.Commands.add('checkHoldButtonDisabled', (bookName) => {
    cy.get('#catalogueTable tr')
        .contains('td', bookName)
        .parent()              // move to the row <tr>
        .within(() => {
            cy.contains('Hold Placed').should('be.disabled');
        });
});

Cypress.Commands.add('checkBorrowedCount', (count) => {
    cy.get('#borrowCount').should('contain', count + " / 3");
});

// check notification contains text 
Cypress.Commands.add('checkNotificationContains', (text) => {
    cy.get('#notificationList').should('contain', text);
});

Cypress.Commands.add('checkQueuePosition', (bookName, position) => {
    cy.get('#catalogueTable tr')
        .contains('td', bookName)
        .parent()              // move to the row <tr>
        .within(() => {
            cy.contains(`Hold position ${position}`).should('exist');
        });
});

Cypress.Commands.add('borrowBookOverLimit', (bookName) => {
    cy.get('#catalogueTable tr')
            .contains('td', bookName)
            .parent()              // move to the row <tr>
            .within(() => {
                cy.contains('button', 'Borrow').click();
            });
        cy.statusShows('Borrowing limit reached');
});
 
