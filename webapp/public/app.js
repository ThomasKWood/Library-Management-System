const loginForm = document.getElementById('loginForm');
const sessionInfo = document.getElementById('sessionInfo');
const currentUserLabel = document.getElementById('currentUser');
const logoutButton = document.getElementById('logoutButton');
const catalogueSection = document.getElementById('catalogueSection');
const borrowedSection = document.getElementById('borrowedSection');
const notificationsSection = document.getElementById('notifications');
const statusMessage = document.getElementById('statusMessage');
const catalogueTable = document.getElementById('catalogueTable');
const borrowedTable = document.getElementById('borrowedTable');
const borrowCount = document.getElementById('borrowCount');
const notificationList = document.getElementById('notificationList');
let borrowTotal = 0;

async function apiRequest(path, options = {}) {
  const response = await fetch(path, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
    body: options.body ? JSON.stringify(options.body) : undefined
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message || 'Request failed');
  }
  return data;
}

function setStatus(message, type = 'info') {
  statusMessage.textContent = message;
  statusMessage.dataset.type = type;
}

function formatDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString();
}

async function refreshSession() {
  const session = await apiRequest('/api/session');
  if (session.user) {
    loginForm.hidden = true;
    sessionInfo.hidden = false;
    currentUserLabel.textContent = session.user.username;
    await refreshBorrowed();
    await refreshBooks();
    await refreshNotifications();
  } else {
    loginForm.hidden = false;
    sessionInfo.hidden = true;
    catalogueSection.hidden = true;
    borrowedSection.hidden = true;
    notificationsSection.hidden = true;
    currentUserLabel.textContent = '';
    catalogueTable.innerHTML = '';
    borrowedTable.innerHTML = '';
    notificationList.innerHTML = '';
  }
}

async function refreshBooks() {
  const { books } = await apiRequest('/api/books');
  catalogueTable.innerHTML = '';
  const atLimit = borrowTotal >= 3;
  books.forEach((book) => {
    const dueLabel = book.due
      ? book.borrowedByCurrentUser
        ? `Due ${formatDate(book.due)}`
        : `Should be available on ${formatDate(book.due)}`
      : book.holdPosition
        ? `Hold position ${book.holdPosition}`
        : '';
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${book.title}</td>
      <td>${book.author}</td>
      <td>${book.statusLabel}</td>
      <td>${dueLabel}</td>
      <td></td>
    `;
    const actionsCell = row.querySelector('td:last-child');

    if (book.borrowedByCurrentUser) {
      actionsCell.textContent = 'Borrowed by you';
    } else if (book.borrowable && !atLimit) {
      const borrowBtn = document.createElement('button');
      borrowBtn.textContent = 'Borrow';
      borrowBtn.addEventListener('click', () => handleBorrow(book.id));
      actionsCell.appendChild(borrowBtn);
    } else {
      if (book.borrowable && atLimit) {
        const note = document.createElement('span');
        note.textContent = 'Borrow limit reached';
        note.classList.add('muted');
        actionsCell.appendChild(note);
      }
      const holdBtn = document.createElement('button');
      const alreadyHolding = Boolean(book.holdPosition);
      holdBtn.textContent = alreadyHolding ? 'Hold Placed' : 'Place Hold';
      holdBtn.disabled = alreadyHolding;
      holdBtn.addEventListener('click', () => handleHold(book.id));
      actionsCell.appendChild(holdBtn);
    }

    catalogueTable.appendChild(row);
  });
  catalogueSection.hidden = false;
}

async function refreshBorrowed() {
  const { borrowed } = await apiRequest('/api/borrowed');
  borrowedTable.innerHTML = '';
  borrowed.forEach((book) => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${book.title}</td>
      <td>${book.author}</td>
      <td>${formatDate(book.due)}</td>
      <td><button>Return</button></td>
    `;
    const button = row.querySelector('button');
    button.addEventListener('click', () => handleReturn(book.id));
    borrowedTable.appendChild(row);
  });
  borrowTotal = borrowed.length;
  borrowCount.textContent = `Borrowed ${borrowTotal} / 3 books`;
  borrowedSection.hidden = false;
}

async function refreshNotifications() {
  const { notifications } = await apiRequest('/api/notifications');
  notificationList.innerHTML = '';
  notifications.forEach((note) => {
    const item = document.createElement('li');
    item.textContent = `${note} is now available`;
    notificationList.appendChild(item);
  });
  notificationsSection.hidden = notifications.length === 0;
}

async function handleBorrow(id) {
  try {
    const result = await apiRequest(`/api/books/${id}/borrow`, { method: 'POST' });
    setStatus(`Book borrowed: ${result.book.title}`, 'success');
    await refreshBorrowed();
    await refreshBooks();
  } catch (err) {
    setStatus(err.message, 'error');
  }
}

async function handleReturn(id) {
  try {
    await apiRequest(`/api/books/${id}/return`, { method: 'POST' });
    setStatus('Book returned.', 'success');
    await refreshBorrowed();
    await refreshBooks();
  } catch (err) {
    setStatus(err.message, 'error');
  }
}

async function handleHold(id) {
  try {
    await apiRequest(`/api/books/${id}/hold`, { method: 'POST' });
    setStatus('Hold placed.', 'success');
    await refreshBooks();
  } catch (err) {
    setStatus(err.message, 'error');
  }
}

loginForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(loginForm);
  try {
    await apiRequest('/api/login', {
      method: 'POST',
      body: Object.fromEntries(formData)
    });
    setStatus('Login successful', 'success');
    await refreshSession();
  } catch (err) {
    setStatus(err.message, 'error');
  }
});

logoutButton.addEventListener('click', async () => {
  await apiRequest('/api/logout', { method: 'POST' });
  setStatus('Signed out.', 'info');
  await refreshSession();
});

refreshSession().catch(() => setStatus('Unable to reach server', 'error'));
