class Users {
  constructor(initial = []) {
    this.users = initial;
  }

  getUser(username) {
    return this.users.find((user) => user.username === username) || null;
  }

  addUser(user) {
    this.users.push(user);
  }

  list() {
    return [...this.users];
  }
}

export { Users };
