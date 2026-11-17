const request = async (path, options = {}) => {
  const response = await fetch(path, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  })
  if (response.status === 204) return null
  const text = await response.text()
  let data = null
  try { data = text ? JSON.parse(text) : null } catch { data = text }
  if (!response.ok) {
    const message = data?.message || data?.error || (typeof data === 'string' ? data : `Request failed (${response.status})`)
    throw new Error(message)
  }
  return data
}

export const api = {
  getExpenses: () => request('/api/expenses'),
  getExpense: id => request(`/api/expenses/${id}`),
  createExpense: body => request('/api/expenses', { method: 'POST', body: JSON.stringify(body) }),
  updateExpense: (id, body) => request(`/api/expenses/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  deleteExpense: id => request(`/api/expenses/${id}`, { method: 'DELETE' }),
  getByCategory: category => request(`/api/expenses/category/${encodeURIComponent(category)}`),
  getSummary: () => request('/api/expenses/summary'),
  getMonthlySpending: () => request('/api/expenses/monthly-spending'),
  saveBudget: body => request('/api/budget', { method: 'POST', body: JSON.stringify(body) }),
  getBudget: (month, year) => request(`/api/budget/${month}/${year}`),
  getBudgetSummary: (month, year) => request(`/api/budget/summary?month=${month}&year=${year}`)
}