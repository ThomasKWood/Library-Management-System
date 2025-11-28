# Library Web Application

Node.js/Express web UI that mirrors the Assignment 1 Java console workflows: login, browse catalogue, borrow/return, place holds, and view notifications.

## Getting Started

```bash
cd webapp
npm install
npm run start
```

The server defaults to port 3000. Override with `PORT=4000 npm run start` if 3000 is already in use.

Visit `http://localhost:3000` (or your override) to load the UI.

### Resetting In-Memory State

For automated tests you can restore the default catalogue/users by calling:

```bash
curl -X POST http://localhost:3000/api/reset
```

This endpoint clears `currentUser`, rebuilds the catalogue and user list, and empties the activity log.

## Features

- Single-user session with an in-memory `currentUser` variable (no session middleware)
- Sample users: `alice/pass123`, `bob/pass456`, `charlie/pass789`
- Displays availability, due dates, and hold positions for all catalogue items
- Borrow limit of 3 books enforced; when at the limit, the UI offers hold placement instead
- Holds notify the next user when a book is returned
- Notifications panel consumes queued alerts the moment you sign in
- All state is in-memory with simple data classes (`Book`, `Catalogue`, `User`, `Users`) converted from the Java version
