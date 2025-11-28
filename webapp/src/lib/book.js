const STATUS = {
  AVAIL: { code: 'AVAIL', label: 'Available', borrowable: true },
  CHECKED: { code: 'CHECKED', label: 'Checked Out', borrowable: false },
  HOLD: { code: 'HOLD', label: 'On Hold', borrowable: false }
};

class Book {
  constructor(id, title, author) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.status = STATUS.AVAIL;
    this.due = null;
    this.holdQueue = [];
  }

  isAvailable() {
    return this.status.borrowable;
  }

  getStatusCode() {
    return this.status.code;
  }

  getStatusLabel() {
    return this.status.label;
  }

  getDue() {
    return this.due;
  }

  getFirstHold() {
    return this.holdQueue.length > 0 ? this.holdQueue[0] : null;
  }

  isBorrowableBy(user) {
    if (!user) {
      return this.isAvailable();
    }
    if (this.isAvailable()) {
      return true;
    }
    const firstHold = this.getFirstHold();
    const onHoldForUser =
      this.status.code === STATUS.HOLD.code && !this.due && firstHold && firstHold.username === user.username;
    return onHoldForUser;
  }

  setDueDateNow() {
    const dueDate = new Date();
    dueDate.setDate(dueDate.getDate() + 14);
    this.due = dueDate.toISOString();
    this.status = STATUS.CHECKED;
    return this.due;
  }

  placeHold(user) {
    const alreadyQueued = this.holdQueue.find((queued) => queued.username === user.username);
    if (!alreadyQueued) {
      this.holdQueue.push(user);
    }
    if (this.status.code !== STATUS.CHECKED.code) {
      this.status = STATUS.HOLD;
    }
  }

  returnBook() {
    this.due = null;
    if (this.holdQueue.length === 0) {
      this.status = STATUS.AVAIL;
    } else {
      this.status = STATUS.HOLD;
    }
  }

  popFirstHold() {
    return this.holdQueue.shift();
  }

  hasHoldFor(user) {
    return this.holdQueue.some((queued) => queued.username === user.username);
  }

  toJSON(currentUser) {
    const holdPosition = this.holdQueue.findIndex((user) => user.username === currentUser?.username);
    const borrowedByCurrentUser = currentUser?.borrowed?.some((book) => book.id === this.id) || false;
    const available = this.isAvailable();
    const borrowable = this.isBorrowableBy(currentUser) && !borrowedByCurrentUser;
    return {
      id: this.id,
      title: this.title,
      author: this.author,
      status: this.status.code,
      statusLabel: this.status.label,
      available,
      borrowable,
      due: this.due,
      holdQueueSize: this.holdQueue.length,
      holdPosition: holdPosition >= 0 ? holdPosition + 1 : null,
      borrowedByCurrentUser
    };
  }
}

export { Book, STATUS };
