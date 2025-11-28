import express from 'express';
import path from 'path';
import { fileURLToPath } from 'url';
import {
  login,
  logout,
  getBooks,
  borrowBook,
  returnBook,
  placeHold,
  getNotifications,
  getBorrowedBooks,
  getCurrentUser,
  resetLibrary
} from './services/libraryService.js';


// Used GPT 5.1 Codex with Github Copilot Agent mode to convert 

const app = express();
const PORT = process.env.PORT || 3000;
const __dirname = path.dirname(fileURLToPath(import.meta.url));

app.use(express.json());
app.use(express.static(path.join(__dirname, '../public')));

app.get('/api/session', (req, res) => {
  res.json({ user: getCurrentUser() });
});

app.post('/api/login', (req, res) => {
  const { username, password } = req.body;
  const result = login(username, password);
  if (!result.success) {
    return res.status(401).json(result);
  }
  res.json(result);
});

app.post('/api/logout', (req, res) => {
  res.json(logout());
});

app.get('/api/books', (req, res) => {
  res.json({ books: getBooks() });
});

app.get('/api/borrowed', (req, res) => {
  res.json({ borrowed: getBorrowedBooks() });
});

app.post('/api/books/:id/borrow', (req, res) => {
  const result = borrowBook(req.params.id);
  if (!result.success) {
    return res.status(400).json(result);
  }
  res.json(result);
});

app.post('/api/books/:id/return', (req, res) => {
  const result = returnBook(req.params.id);
  if (!result.success) {
    return res.status(400).json(result);
  }
  res.json(result);
});

app.post('/api/books/:id/hold', (req, res) => {
  const result = placeHold(req.params.id);
  if (!result.success) {
    return res.status(400).json(result);
  }
  res.json(result);
});

app.get('/api/notifications', (req, res) => {
  res.json({ notifications: getNotifications() });
});

app.post('/api/reset', (req, res) => {
  res.json(resetLibrary());
});

app.listen(PORT, () => {
  console.log(`Library web server running on http://localhost:${PORT}`);
});
