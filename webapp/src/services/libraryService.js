import { Catalogue } from '../lib/catalogue.js';
import { Users } from '../lib/users.js';
import { User } from '../lib/user.js';

let catalogue;
let recordLog;
let users;
let currentUser = null;

const seedBooks = [
  'The Great Gatsby',
  'To Kill a Mockingbird',
  '1984',
  'Pride and Prejudice',
  'The Hobbit',
  'Harry Potter',
  'The Catcher in the Rye',
  'Animal Farm',
  'Lord of the Flies',
  'Jane Eyre',
  'Wuthering Heights',
  'Moby Dick',
  'The Odyssey',
  'Hamlet',
  'War and Peace',
  'The Divine Comedy',
  'Crime and Punishment',
  'Don Quixote',
  'The Iliad',
  'Ulysses'
];

const seedUserData = [
  { username: 'alice', password: 'pass123' },
  { username: 'bob', password: 'pass456' },
  { username: 'charlie', password: 'pass789' }
];

function buildCatalogue() {
  const nextCatalogue = new Catalogue();
  seedBooks.forEach((title, index) => {
    nextCatalogue.addBook(title, `A2BookAuthor${index + 1}`);
  });
  return nextCatalogue;
}

function buildUsers() {
  const initialUsers = seedUserData.map(({ username, password }) => new User(username, password));
  return new Users(initialUsers);
}

function resetState() {
  catalogue = buildCatalogue();
  users = buildUsers();
  recordLog = [];
  currentUser = null;
}

resetState();

function login(username, password) {
  if (currentUser && currentUser.username !== username) {
    return { success: false, message: 'Another user is already signed in.' };
  }
  const user = users.getUser(username);
  if (!user) {
    return { success: false, message: 'Username or password incorrect.' };
  }
  if (!user.passwordCorrect(password)) {
    return { success: false, message: 'Username or password incorrect.' };
  }
  currentUser = user;
  return { success: true, user: currentUser.toJSON() };
}

function logout() {
  currentUser = null;
  return { success: true };
}

function requireUser() {
  if (!currentUser) {
    return { error: 'Not signed in.' };
  }
  return null;
}

function getBooks() {
  return catalogue.all().map((book) => book.toJSON(currentUser));
}

function getBorrowedBooks() {
  if (!currentUser) {
    return [];
  }
  return currentUser.borrowed.map((book) => ({
    id: book.id,
    title: book.title,
    author: book.author,
    due: book.due
  }));
}

function getNotifications() {
  if (!currentUser) {
    return [];
  }
  const notifications = [];
  while (currentUser.hasNotification()) {
    notifications.push(currentUser.popNotification());
  }
  return notifications;
}

function borrowBook(bookId) {
  const guard = requireUser();
  if (guard) {
    return { success: false, message: guard.error };
  }
  const book = catalogue.getBookById(bookId);
  if (!book) {
    return { success: false, message: 'Book not found.' };
  }

  if (currentUser.hasBorrowed(book)) {
    return { success: false, message: 'You already have this book.' };
  }

  const bookAvailable = book.isBorrowableBy(currentUser);

  if (!bookAvailable) {
    return { success: false, message: 'Book unavailable. Consider placing a hold.' };
  }

  if (currentUser.borrowLimitReached()) {
    return { success: false, message: 'Borrowing limit reached (3 books).' };
  }

  book.setDueDateNow();
  const added = currentUser.addBorrowed(book);
  if (!added) {
    return { success: false, message: 'Unable to borrow book.' };
  }

  const firstHold = book.getFirstHold();
  if (!book.isAvailable() && firstHold && firstHold.username === currentUser.username) {
    book.popFirstHold();
  }
  recordLog.push(`${currentUser.username} borrowed ${book.title}`);
  return { success: true, book: book.toJSON(currentUser) };
}

function returnBook(bookId) {
  const guard = requireUser();
  if (guard) {
    return { success: false, message: guard.error };
  }
  const book = catalogue.getBookById(bookId);
  if (!book) {
    return { success: false, message: 'Book not found.' };
  }
  if (!currentUser.removeBorrowed(bookId)) {
    recordLog.push(`${currentUser.username} attempted to return ${book.title} without borrowing it`);
    return { success: false, message: 'You do not have this book.' };
  }
  book.returnBook();
  recordLog.push(`${book.title} returned`);
  const nextInQueue = book.getFirstHold();
  if (nextInQueue) {
    nextInQueue.addNotification(book.title);
  }
  return { success: true, book: book.toJSON(currentUser) };
}

function placeHold(bookId) {
  const guard = requireUser();
  if (guard) {
    return { success: false, message: guard.error };
  }
  const book = catalogue.getBookById(bookId);
  if (!book) {
    return { success: false, message: 'Book not found.' };
  }
  if (book.hasHoldFor(currentUser)) {
    return { success: false, message: 'You already have a hold on this book.' };
  }
  book.placeHold(currentUser);
  recordLog.push(`${currentUser.username} placed hold on ${book.title}`);
  return { success: true };
}

function getRecordLog() {
  return [...recordLog];
}

function getCurrentUser() {
  return currentUser ? currentUser.toJSON() : null;
}

function resetLibrary() {
  resetState();
  return { success: true, message: 'Library reset to defaults.' };
}

export {
  login,
  logout,
  getBooks,
  borrowBook,
  returnBook,
  placeHold,
  getNotifications,
  getBorrowedBooks,
  getCurrentUser,
  getRecordLog,
  resetLibrary
};
