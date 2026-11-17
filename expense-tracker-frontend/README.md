# Expense Tracker Frontend

React + Vite dashboard for the Spring Boot Expense Tracker backend.

## Requirements
- Node.js 20+ (recommended)
- Spring Boot backend running on `http://localhost:8081`
- MySQL database configured in the backend

## Run locally
```bash
npm install
npm run dev
```
Open http://localhost:5173.

Vite proxies `/api` requests to `http://localhost:8081`, so the frontend and backend can run separately during development.

## Backend endpoints used
- `GET /api/expenses`
- `GET /api/expenses/{id}`
- `POST /api/expenses`
- `PUT /api/expenses/{id}`
- `DELETE /api/expenses/{id}`
- `GET /api/expenses/category/{category}`
- `GET /api/expenses/summary`
- `GET /api/expenses/monthly-spending`
- `POST /api/budget`
- `GET /api/budget/{month}/{year}`
- `GET /api/budget/summary?month={month}&year={year}`

## Request shape for an expense
```json
{
  "title": "Groceries",
  "amount": 750.00,
  "description": "Weekly groceries",
  "expenseDate": "2026-11-17",
  "category": "FOOD"
}
```

## Notes
- This frontend expects the category enum values `FOOD`, `TRAVEL`, `SHOPPING`, `BILLS`, `ENTERTAINMENT`, `HEALTH`, `EDUCATION`, `OTHER`.
- Budget creation expects `monthlyLimit`, `month`, and `year`.
- If your backend uses a different port, update the proxy target in `vite.config.js`.
- This frontend is a development UI, not a production-hardened deployment. Add authentication before exposing it publicly.
