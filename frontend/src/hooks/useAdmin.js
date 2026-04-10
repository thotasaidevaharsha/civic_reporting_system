import { useCallback, useEffect, useState } from "react";
import {
  adminLogin,
  getAdminStats,
  getComplaints,
  updateComplaint as updateComplaintRequest
} from "../services/api";

const SESSION_STORAGE_KEY = "civicreport_admin_session";

function readStoredSession() {
  const rawValue = localStorage.getItem(SESSION_STORAGE_KEY);
  if (!rawValue) {
    return null;
  }
  try {
    return JSON.parse(rawValue);
  } catch (_error) {
    localStorage.removeItem(SESSION_STORAGE_KEY);
    return null;
  }
}

export function useAdmin() {
  const [session, setSession] = useState(() => readStoredSession());
  const [loginError, setLoginError] = useState("");
  const [dashboardError, setDashboardError] = useState("");
  const [isLoggingIn, setIsLoggingIn] = useState(false);
  const [isLoadingDashboard, setIsLoadingDashboard] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [stats, setStats] = useState({
    total: 0,
    pending: 0,
    resolved: 0,
    highPriority: 0
  });
  const [filters, setFilters] = useState({
    status: "",
    priority: ""
  });
  const [complaints, setComplaints] = useState([]);

  const token = session?.token || "";

  useEffect(() => {
    if (session) {
      localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session));
      return;
    }
    localStorage.removeItem(SESSION_STORAGE_KEY);
  }, [session]);

  const loadDashboard = useCallback(
    async (activeFilters = filters) => {
      if (!session?.zone || !token) {
        return;
      }

      setIsLoadingDashboard(true);
      setDashboardError("");
      try {
        const [statsData, complaintData] = await Promise.all([
          getAdminStats(session.zone, token),
          getComplaints(
            {
              zone: session.zone,
              status: activeFilters.status,
              priority: activeFilters.priority
            },
            token
          )
        ]);

        setStats(statsData);
        setComplaints(complaintData);
      } catch (error) {
        setDashboardError(error.message);
      } finally {
        setIsLoadingDashboard(false);
      }
    },
    [filters, session, token]
  );

  async function login(payload) {
    setIsLoggingIn(true);
    setLoginError("");
    try {
      const response = await adminLogin(payload);
      const nextSession = {
        token: response.token,
        adminName: response.adminName,
        zone: response.zone
      };
      setSession(nextSession);
      return nextSession;
    } catch (error) {
      setLoginError(error.message);
      throw error;
    } finally {
      setIsLoggingIn(false);
    }
  }

  function logout() {
    setSession(null);
    setStats({
      total: 0,
      pending: 0,
      resolved: 0,
      highPriority: 0
    });
    setComplaints([]);
    setFilters({
      status: "",
      priority: ""
    });
    setLoginError("");
    setDashboardError("");
  }

  async function saveComplaintUpdate(complaintId, updatePayload) {
    if (!token) {
      throw new Error("You are not logged in.");
    }
    setIsSaving(true);
    setDashboardError("");
    try {
      const response = await updateComplaintRequest(complaintId, updatePayload, token);
      await loadDashboard(filters);
      return response;
    } catch (error) {
      setDashboardError(error.message);
      throw error;
    } finally {
      setIsSaving(false);
    }
  }

  function setFilterValue(name, value) {
    setFilters((currentFilters) => ({
      ...currentFilters,
      [name]: value
    }));
  }

  return {
    session,
    token,
    stats,
    filters,
    complaints,
    allComplaints: complaints,
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
  };
}
