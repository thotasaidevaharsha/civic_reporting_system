import { useEffect } from "react";
import { Link, NavLink, Route, Routes } from "react-router-dom";
import AdminDashboard from "./pages/AdminDashboard";
import AdminLogin from "./pages/AdminLogin";
import Home from "./pages/Home";
import ReportIssue from "./pages/ReportIssue";
import TrackComplaint from "./pages/TrackComplaint";
import { useAdmin } from "./hooks/useAdmin";

function App() {
  const {
    session,
    stats,
    filters,
    complaints,
    loginError,
    dashboardError,
    isLoggingIn,
    isLoadingDashboard,
    isSaving,
    login,
    logout,
    loadDashboard,
    saveComplaintUpdate,
    setFilterValue
  } = useAdmin();

  useEffect(() => {
    if (!session) {
      return;
    }
    loadDashboard(filters);
  }, [filters, loadDashboard, session]);

  return (
    <div className="app-root">
      <header className="site-header">
        <div className="container site-header-row">
          <Link className="brand-link" to="/">
            <span className="logo-placeholder">CR</span>
            <span className="brand-text">CivicReport</span>
          </Link>
          <nav className="site-nav">
            <NavLink to="/" end>
              Home
            </NavLink>
            <NavLink to="/report">Report Issue</NavLink>
            <NavLink to="/track">Track Complaint</NavLink>
            <NavLink to="/admin/login">Admin Login</NavLink>
          </nav>
        </div>
      </header>

      <main className="container main-content">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/report" element={<ReportIssue />} />
          <Route path="/track" element={<TrackComplaint />} />
          <Route
            path="/admin/login"
            element={
              <AdminLogin onLogin={login} isLoggingIn={isLoggingIn} loginError={loginError} />
            }
          />
          <Route
            path="/admin/dashboard"
            element={
              <AdminDashboard
                session={session}
                stats={stats}
                complaints={complaints}
                filters={filters}
                dashboardError={dashboardError}
                isLoadingDashboard={isLoadingDashboard}
                isSaving={isSaving}
                onFilterChange={setFilterValue}
                onRefresh={() => loadDashboard(filters)}
                onSaveComplaintUpdate={saveComplaintUpdate}
                onLogout={logout}
              />
            }
          />
        </Routes>
      </main>

      <footer className="site-footer">
        <div className="container">
          <p>CivicReport Municipal Support Desk</p>
          <p>Contact: +91-00000-00000 | support@civicreport.gov</p>
        </div>
      </footer>
    </div>
  );
}

export default App;
