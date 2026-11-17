import React, { useCallback, useEffect, useMemo, useState } from 'react'
import {
  Activity, ArrowDownRight, ArrowUpRight, BadgeIndianRupee, BarChart3, CalendarDays,
  Check, ChevronDown, CirclePlus, CreditCard, IndianRupee, LayoutDashboard, LoaderCircle,
  Pencil, PieChart, Plus, RefreshCw, Search, Settings2, Trash2, Wallet, X
} from 'lucide-react'
import {
  Area, AreaChart, CartesianGrid, Cell, Pie, PieChart as RePieChart,
  ResponsiveContainer, Tooltip, XAxis, YAxis, BarChart, Bar
} from 'recharts'
import { api } from './api'

const CATEGORIES = ['FOOD','TRAVEL','SHOPPING','BILLS','ENTERTAINMENT','HEALTH','EDUCATION','OTHER']
const COLORS = ['#7c8cff','#31b89b','#f2a65a','#e879a8','#8b7bea','#54a6d8','#c0a05b','#8c9aa9']
const money = value => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(Number(value || 0))
const today = () => new Date().toISOString().slice(0,10)
const monthName = m => new Date(2000, Number(m)-1, 1).toLocaleString('en-IN', { month: 'long' })
const blankForm = () => ({ title:'', amount:'', description:'', expenseDate:today(), category:'FOOD' })
const toForm = e => ({ title:e.title || '', amount:String(e.amount ?? ''), description:e.description || '', expenseDate:e.expenseDate || today(), category:e.category || 'OTHER' })

function App() {
  const [page, setPage] = useState('Overview')
  const [expenses, setExpenses] = useState([])
  const [summary, setSummary] = useState(null)
  const [monthly, setMonthly] = useState([])
  const [budgetSummary, setBudgetSummary] = useState(null)
  const [budget, setBudget] = useState(null)
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth()+1)
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear())
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [query, setQuery] = useState('')
  const [categoryFilter, setCategoryFilter] = useState('ALL')
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(blankForm())
  const [saving, setSaving] = useState(false)
  const [budgetLimit, setBudgetLimit] = useState('')

  const load = useCallback(async () => {
    setLoading(true); setError('')
    try {
      const [e,s,m] = await Promise.all([api.getExpenses(), api.getSummary(), api.getMonthlySpending()])
      setExpenses(Array.isArray(e) ? e : [])
      setSummary(s || null)
      setMonthly(Array.isArray(m) ? m : [])
      try {
        const [b,bs] = await Promise.all([
          api.getBudget(selectedMonth, selectedYear),
          api.getBudgetSummary(selectedMonth, selectedYear)
        ])
        setBudget(b); setBudgetSummary(bs); setBudgetLimit(b?.monthlyLimit ?? '')
      } catch { setBudget(null); setBudgetSummary(null); setBudgetLimit('') }
    } catch (err) {
      setError(`${err.message}. Check that your Spring Boot API is running on port 8081 and the Vite proxy is configured.`)
    } finally { setLoading(false) }
  }, [selectedMonth, selectedYear])

  useEffect(() => { load() }, [load])
  useEffect(() => {
    if (!notice) return
    const t = setTimeout(() => setNotice(''), 3500)
    return () => clearTimeout(t)
  }, [notice])

  const filteredExpenses = useMemo(() => expenses.filter(e => {
    const matchesCategory = categoryFilter === 'ALL' || e.category === categoryFilter
    const q = query.trim().toLowerCase()
    const matchesQuery = !q || [e.title,e.description,e.category].some(v => String(v || '').toLowerCase().includes(q))
    return matchesCategory && matchesQuery
  }), [expenses, categoryFilter, query])

  const categoryData = useMemo(() => CATEGORIES.map((name,i) => ({
    name, value: expenses.filter(e => e.category === name).reduce((sum,e) => sum + Number(e.amount || 0),0), color:COLORS[i]
  })).filter(x => x.value > 0), [expenses])

  const openCreate = () => { setEditing(null); setForm(blankForm()); setShowModal(true) }
  const openEdit = e => { setEditing(e); setForm(toForm(e)); setShowModal(true) }
  const saveExpense = async ev => {
    ev.preventDefault()
    setSaving(true)
    const body = { ...form, amount:Number(form.amount) }
    try {
      if (editing) await api.updateExpense(editing.id, body)
      else await api.createExpense(body)
      setShowModal(false); setNotice(editing ? 'Expense updated successfully' : 'Expense added successfully')
      await load()
    } catch (err) { setError(err.message) } finally { setSaving(false) }
  }
  const removeExpense = async e => {
    if (!window.confirm(`Delete "${e.title}"?`)) return
    try { await api.deleteExpense(e.id); setNotice('Expense deleted'); await load() }
    catch (err) { setError(err.message) }
  }
  const saveBudget = async ev => {
    ev.preventDefault()
    try {
      const saved = await api.saveBudget({ monthlyLimit:Number(budgetLimit), month:Number(selectedMonth), year:Number(selectedYear) })
      setBudget(saved); setNotice('Monthly budget saved'); await load()
    } catch (err) { setError(err.message) }
  }
  const nav = [
    {name:'Overview',icon:LayoutDashboard},{name:'Transactions',icon:CreditCard},
    {name:'Analytics',icon:BarChart3},{name:'Budgets',icon:Wallet}
  ]

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><div className="brand-mark"><Wallet size={21}/></div><div><b>Pocketwise</b><span>EXPENSE TRACKER</span></div></div>
      <div className="workspace-label">WORKSPACE</div>
      <nav>{nav.map(n => <button key={n.name} className={`nav-link ${page===n.name?'active':''}`} onClick={()=>setPage(n.name)}><n.icon size={18}/>{n.name}{n.name==='Transactions' && <span className="nav-count">{expenses.length}</span>}</button>)}</nav>
      <div className="sidebar-bottom"><div className="mini-card"><div className="mini-icon"><Activity size={17}/></div><b>Stay on track</b><p>Small steps today lead to better financial habits.</p><button onClick={()=>setPage('Budgets')}>Manage budget <ArrowUpRight size={14}/></button></div><div className="profile"><div className="avatar">EJ</div><div><b>My Account</b><span>Personal workspace</span></div><Settings2 size={17}/></div></div>
    </aside>

    <main className="main">
      <header className="topbar"><div><div className="eyebrow">PERSONAL FINANCE / {page.toUpperCase()}</div><h1>{page==='Overview'?'Good day.':page}</h1><p className="subhead">{page==='Overview'?'Here is what is happening with your money.':page==='Transactions'?'Track and manage all your expenses.':page==='Analytics'?'Understand where your money goes.':'Set spending limits and stay in control.'}</p></div><div className="top-actions"><button className="icon-button" title="Refresh data" onClick={load}><RefreshCw size={17}/></button><button className="primary-button" onClick={openCreate}><Plus size={17}/> Add expense</button></div></header>
      {error && <div className="alert error"><span>{error}</span><button onClick={()=>setError('')}><X size={16}/></button></div>}
      {notice && <div className="alert success"><Check size={16}/>{notice}<button onClick={()=>setNotice('')}><X size={16}/></button></div>}
      {loading ? <div className="loading"><LoaderCircle className="spin" size={25}/>Loading your financial overview...</div> : <>
        {page==='Overview' && <Overview expenses={expenses} summary={summary} monthly={monthly} categoryData={categoryData} budgetSummary={budgetSummary} onAdd={openCreate} onTransactions={()=>setPage('Transactions')} />}
        {page==='Transactions' && <section className="panel transactions-panel"><div className="panel-heading"><div><h2>All transactions</h2><p>{filteredExpenses.length} records found</p></div><button className="secondary-button" onClick={openCreate}><CirclePlus size={16}/> New transaction</button></div><div className="filters"><div className="search-box"><Search size={16}/><input placeholder="Search expenses..." value={query} onChange={e=>setQuery(e.target.value)}/></div><select value={categoryFilter} onChange={e=>setCategoryFilter(e.target.value)}><option value="ALL">All categories</option>{CATEGORIES.map(c=><option key={c}>{c}</option>)}</select></div><ExpenseTable expenses={filteredExpenses} onEdit={openEdit} onDelete={removeExpense}/></section>}
        {page==='Analytics' && <Analytics expenses={expenses} monthly={monthly} categoryData={categoryData}/>}
        {page==='Budgets' && <BudgetPage month={selectedMonth} year={selectedYear} setMonth={setSelectedMonth} setYear={setSelectedYear} budget={budget} budgetSummary={budgetSummary} limit={budgetLimit} setLimit={setBudgetLimit} onSave={saveBudget} expenses={expenses}/>}
      </>}
      <footer>POCKETWISE <span>•</span> Expense tracking made clearer</footer>
    </main>

    {showModal && <div className="modal-backdrop" onMouseDown={e=>{if(e.target===e.currentTarget)setShowModal(false)}}><form className="modal" onSubmit={saveExpense}><div className="modal-head"><div><div className="eyebrow">{editing?'UPDATE RECORD':'NEW RECORD'}</div><h2>{editing?'Edit expense':'Add an expense'}</h2></div><button type="button" className="icon-button" onClick={()=>setShowModal(false)}><X size={18}/></button></div><div className="form-grid"><label className="wide">Title<input required maxLength="100" placeholder="e.g. Grocery shopping" value={form.title} onChange={e=>setForm({...form,title:e.target.value})}/></label><label>Amount (₹)<input required min="0.01" step="0.01" type="number" placeholder="0.00" value={form.amount} onChange={e=>setForm({...form,amount:e.target.value})}/></label><label>Date<input required type="date" value={form.expenseDate} onChange={e=>setForm({...form,expenseDate:e.target.value})}/></label><label>Category<select value={form.category} onChange={e=>setForm({...form,category:e.target.value})}>{CATEGORIES.map(c=><option key={c}>{c}</option>)}</select></label><label className="wide">Description<textarea rows="3" placeholder="Add a note (optional)" value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></label></div><div className="modal-actions"><button type="button" className="secondary-button" onClick={()=>setShowModal(false)}>Cancel</button><button className="primary-button" disabled={saving}>{saving?<LoaderCircle className="spin" size={16}/>:<Check size={16}/>} {saving?'Saving...':editing?'Save changes':'Save expense'}</button></div></form></div>}
  </div>
}

function StatCard({label,value,sub,icon:Icon,tone='blue'}) {
  return <div className="stat-card"><div className={`stat-icon ${tone}`}><Icon size={19}/></div><div className="stat-label">{label}</div><div className="stat-value">{value}</div><div className="stat-sub">{sub}</div></div>
}
function Overview({expenses,summary,monthly,categoryData,budgetSummary,onAdd,onTransactions}) {
  const recent=[...expenses].sort((a,b)=>new Date(b.expenseDate)-new Date(a.expenseDate)).slice(0,5)
  const thisMonth=expenses.filter(e=>{const d=new Date(e.expenseDate);const n=new Date();return d.getMonth()===n.getMonth()&&d.getFullYear()===n.getFullYear()}).reduce((s,e)=>s+Number(e.amount||0),0)
  const chart=monthly.slice(-6).map(m=>({...m,label:`${monthName(m.month).slice(0,3)} '${String(m.year).slice(-2)}`,total:Number(m.totalAmount||0)}))
  return <div className="content-grid">
    <div className="stats-grid">
      <StatCard label="TOTAL SPENT" value={money(summary?.totalAmount)} sub={`${summary?.totalExpenses ?? expenses.length} recorded expenses`} icon={IndianRupee} tone="blue"/>
      <StatCard label="THIS MONTH" value={money(thisMonth)} sub="Based on expense dates" icon={CalendarDays} tone="green"/>
      <StatCard label="AVERAGE EXPENSE" value={money(summary?.averageExpense)} sub="Average per transaction" icon={Activity} tone="orange"/>
      <StatCard label="HIGHEST EXPENSE" value={money(summary?.highestExpense)} sub="Single largest transaction" icon={ArrowUpRight} tone="purple"/>
    </div>
    <section className="panel chart-panel"><div className="panel-heading"><div><h2>Spending overview</h2><p>Your monthly expense trend</p></div><span className="period-pill">LAST 6 MONTHS</span></div><div className="chart-wrap">{chart.length?<ResponsiveContainer width="100%" height="100%"><AreaChart data={chart} margin={{top:12,right:12,left:0,bottom:0}}><defs><linearGradient id="areaFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#7888ff" stopOpacity={0.25}/><stop offset="95%" stopColor="#7888ff" stopOpacity={0}/></linearGradient></defs><CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#edf0f5"/><XAxis dataKey="label" axisLine={false} tickLine={false} tick={{fill:'#8c96a8',fontSize:11}}/><YAxis axisLine={false} tickLine={false} tick={{fill:'#8c96a8',fontSize:11}} tickFormatter={v=>`₹${v>=1000?(v/1000).toFixed(0)+'k':v}`}/><Tooltip formatter={v=>money(v)} contentStyle={{border:'1px solid #edf0f5',borderRadius:12,boxShadow:'0 8px 24px #1c294012'}}/><Area type="monotone" dataKey="total" stroke="#7888ff" strokeWidth={2.5} fill="url(#areaFill)" activeDot={{r:5}}/></AreaChart></ResponsiveContainer>:<Empty text="Monthly spending data will appear once you add expenses."/>}</div></section>
    <section className="panel category-panel"><div className="panel-heading"><div><h2>Spending by category</h2><p>Category distribution</p></div><PieChart size={18} className="muted-icon"/></div>{categoryData.length?<><div className="donut-wrap"><ResponsiveContainer width="100%" height="100%"><RePieChart><Pie data={categoryData} dataKey="value" nameKey="name" innerRadius={62} outerRadius={86} paddingAngle={3} stroke="none">{categoryData.map((d,i)=><Cell key={d.name} fill={d.color}/>)}</Pie><Tooltip formatter={v=>money(v)}/></RePieChart></ResponsiveContainer><div className="donut-center"><b>{categoryData.length}</b><span>categories</span></div></div><div className="legend">{categoryData.slice(0,5).map(d=><div key={d.name}><i style={{background:d.color}}/><span>{d.name}</span><b>{money(d.value)}</b></div>)}</div></>:<Empty text="Add expenses to see category breakdown."/>}</section>
    <section className="panel recent-panel"><div className="panel-heading"><div><h2>Recent transactions</h2><p>Your latest activity</p></div><button className="text-button" onClick={onTransactions}>View all <ArrowUpRight size={14}/></button></div><ExpenseTable expenses={recent} compact/></section>
    <section className="budget-banner"><div className="banner-icon"><Wallet size={22}/></div><div className="banner-copy"><b>Monthly budget</b><span>{budgetSummary?`${money(budgetSummary.totalSpent)} spent of ${money(budgetSummary.monthlyLimit)}`:'Set a monthly limit to keep your spending on track.'}</span></div>{budgetSummary&&<div className="banner-progress"><div><span style={{width:`${Math.min(Number(budgetSummary.budgetUtilization||0),100)}%`}}/></div><b>{Number(budgetSummary.budgetUtilization||0).toFixed(0)}%</b></div>}<button className="banner-button" onClick={()=>window.dispatchEvent(new CustomEvent('go-budgets'))}>Manage budget <ArrowUpRight size={15}/></button></section>
  </div>
}
function ExpenseTable({expenses,onEdit,onDelete,compact=false}) {
  if(!expenses?.length) return <Empty text="No expenses found. Add your first expense to get started."/>
  return <div className="table-scroll"><table><thead><tr><th>DESCRIPTION</th><th>CATEGORY</th><th>DATE</th><th className="right">AMOUNT</th>{!compact&&<th className="right">ACTIONS</th>}</tr></thead><tbody>{expenses.map(e=><tr key={e.id}><td><div className="expense-title"><span className={`category-dot ${String(e.category||'other').toLowerCase()}`}/><div><b>{e.title}</b><small>{e.description || 'No description'}</small></div></div></td><td><span className="category-chip">{e.category}</span></td><td className="date-cell">{e.expenseDate}</td><td className="right amount-cell">{money(e.amount)}</td>{!compact&&<td className="right actions"><button title="Edit" onClick={()=>onEdit(e)}><Pencil size={15}/></button><button title="Delete" onClick={()=>onDelete(e)}><Trash2 size={15}/></button></td>}</tr>)}</tbody></table></div>
}
function Analytics({expenses,monthly,categoryData}) {
  const data=monthly.map(m=>({label:`${monthName(m.month).slice(0,3)} ${m.year}`,amount:Number(m.totalAmount||0),count:Number(m.totalExpenses||0)}))
  return <div className="analytics-grid"><section className="panel wide-panel"><div className="panel-heading"><div><h2>Monthly spending</h2><p>Total expenses recorded per month</p></div><span className="period-pill">ALL TIME</span></div><div className="large-chart">{data.length?<ResponsiveContainer width="100%" height="100%"><BarChart data={data} margin={{top:10,right:20,left:5,bottom:5}}><CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#edf0f5"/><XAxis dataKey="label" axisLine={false} tickLine={false} tick={{fill:'#8c96a8',fontSize:11}}/><YAxis axisLine={false} tickLine={false} tick={{fill:'#8c96a8',fontSize:11}}/><Tooltip formatter={v=>money(v)}/><Bar dataKey="amount" fill="#7888ff" radius={[7,7,0,0]}/></BarChart></ResponsiveContainer>:<Empty text="No monthly data available yet."/>}</div></section><section className="panel"><div className="panel-heading"><div><h2>Category totals</h2><p>Lifetime spending by category</p></div></div><div className="category-list">{categoryData.length?categoryData.map(d=><div className="category-row" key={d.name}><div><i style={{background:d.color}}/><b>{d.name}</b></div><strong>{money(d.value)}</strong></div>):<Empty text="No category data yet."/>}</div></section><section className="panel"><div className="panel-heading"><div><h2>Quick statistics</h2><p>Across all transactions</p></div></div><div className="quick-stats"><div><span>Total transactions</span><b>{expenses.length}</b></div><div><span>Categories used</span><b>{categoryData.length}</b></div><div><span>Average transaction</span><b>{money(expenses.length?expenses.reduce((s,e)=>s+Number(e.amount||0),0)/expenses.length:0)}</b></div></div></section></div>
}
function BudgetPage({month,year,setMonth,setYear,budget,budgetSummary,limit,setLimit,onSave,expenses}) {
  const months=Array.from({length:12},(_,i)=>i+1)
  const spent=Number(budgetSummary?.totalSpent||0), cap=Number(budgetSummary?.monthlyLimit||0), pct=Number(budgetSummary?.budgetUtilization||0)
  return <div className="budget-layout"><section className="panel budget-editor"><div className="panel-heading"><div><h2>Budget settings</h2><p>Choose a month and set your spending limit</p></div><Wallet size={19} className="muted-icon"/></div><form onSubmit={onSave} className="budget-form"><div className="two-fields"><label>Month<select value={month} onChange={e=>setMonth(Number(e.target.value))}>{months.map(m=><option value={m} key={m}>{monthName(m)}</option>)}</select></label><label>Year<input type="number" min="2000" max="2100" value={year} onChange={e=>setYear(Number(e.target.value))}/></label></div><label>Monthly limit (₹)<input type="number" min="0.01" step="0.01" required value={limit} onChange={e=>setLimit(e.target.value)} placeholder="e.g. 20000"/></label><button className="primary-button"><Check size={16}/> Save budget</button></form></section><section className="panel budget-status"><div className="panel-heading"><div><h2>{monthName(month)} {year}</h2><p>Your budget progress</p></div><span className={`status-pill ${budgetSummary?.exceeded?'over':'ok'}`}>{budgetSummary?.exceeded?'OVER BUDGET':'ON TRACK'}</span></div>{budgetSummary?<><div className="budget-total">{money(spent)}<span> spent</span></div><div className="progress-track"><span className={pct>100?'over':''} style={{width:`${Math.min(pct,100)}%`}}/></div><div className="budget-metrics"><div><span>Monthly limit</span><b>{money(cap)}</b></div><div><span>Remaining</span><b className={Number(budgetSummary.remainingBudget)<0?'negative':''}>{money(budgetSummary.remainingBudget)}</b></div><div><span>Utilization</span><b>{pct.toFixed(1)}%</b></div></div></>:<Empty text="No budget found for this month. Set a limit to start tracking."/>}</section><section className="panel budget-note"><div className="note-icon"><Activity size={18}/></div><div><b>Budget tip</b><p>Your spending summary is calculated from expenses dated in the selected month. You can update the budget whenever your plans change.</p></div></section></div>
}
function Empty({text}) { return <div className="empty-state"><div className="empty-icon"><Activity size={20}/></div><p>{text}</p></div> }

export default App