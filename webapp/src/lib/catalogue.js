import { Book } from './book.js';

class Catalogue {
  constructor() {
    this.books = [];
  }

  addBook(title, author) {
    const id = String(this.books.length + 1);
    const book = new Book(id, title, author);
    this.books.push(book);
    return book;
  }

  getBookById(id) {
    return this.books.find((book) => book.id === id) || null;
  }

  all() {
    return [...this.books];
  }
}

export { Catalogue };
