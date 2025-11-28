class User {
  constructor(username, password) {
    this.username = username;
    this.password = password;
    this.borrowed = [];
    this.notifications = [];
  }

  hasBorrowed(book) {
    return this.borrowed.some((b) => b.id === book.id);
  }

  addBorrowed(book) {
    if (this.borrowed.length >= 3) {
      return false;
    }
    if (this.hasBorrowed(book)) {
      return false;
    }
    this.borrowed.push(book);
    return true;
  }

  removeBorrowed(bookId) {
    const idx = this.borrowed.findIndex((book) => book.id === bookId);
    if (idx >= 0) {
      this.borrowed.splice(idx, 1);
      return true;
    }
    return false;
  }

  borrowLimitReached() {
    return this.borrowed.length >= 3;
  }

  passwordCorrect(given) {
    return this.password === given;
  }

  addNotification(message) {
    this.notifications.push(message);
  }

  popNotification() {
    return this.notifications.shift();
  }

  hasNotification() {
    return this.notifications.length > 0;
  }

  toJSON() {
    return {
      username: this.username,
      borrowed: this.borrowed.map((book) => ({
        id: book.id,
        title: book.title,
        author: book.author,
        due: book.due
      })),
      notifications: [...this.notifications]
    };
  }
}

export { User };
