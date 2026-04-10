import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { Navigate } from "react-router-dom";
import Badge from "../components/Badge";
import Button from "../components/Button";
import InputField from "../components/InputField";
import Spinner from "../components/Spinner";
import StatusBadge from "../components/StatusBadge";
import {
  DEPARTMENT_OPTIONS,
  PRIORITY_LABELS,
  PRIORITY_OPTIONS,
  STATUS_OPTIONS
} from "../utils/constants";
import { formatDate } from "../utils/formatDate";
import "./AdminDashboard.css";

function getPriorityTone(priority) {
  if (priority === "HIGH") {
    return "red";
  }
  if (priority === "MEDIUM") {
    return "amber";
  }
  if (priority === "LOW") {
    return "green";
  }
  return "neutral";
}

function initialEditState() {
  return {
    assignedDepartment: DEPARTMENT_OPTIONS[0],
    adminPriority: PRIORITY_OPTIONS[1].value,
    status: STATUS_OPTIONS[0].value,
    adminNote: ""
  };
}

function AdminDashboard({
  session,
  stats,
  complaints,
  filters,
  dashboardError,
  isLoadingDashboard,
  isSaving,
  onFilterChange,
  onRefresh,
  onSaveComplaintUpdate,
  onLogout
}) {
  const [selectedComplaint, setSelectedComplaint] = useState(null);
  const [editState, setEditState] = useState(initialEditState);
  const [localMessage, setLocalMessage] = useState("");

  useEffect(() => {
    if (!selectedComplaint) {
      return;
    }
    const updatedComplaint = complaints.find((item) => item.id === selectedComplaint.id);
    if (!updatedComplaint) {
      setSelectedComplaint(null);
      return;
    }
    setSelectedComplaint(updatedComplaint);
  }, [complaints, selectedComplaint]);

  if (!session) {
    return <Navigate to="/admin/login" replace />;
  }

  function handleRowSelect(complaint) {
    setSelectedComplaint(complaint);
    setLocalMessage("");
    setEditState({
      assignedDepartment: complaint.assignedDepartment || DEPARTMENT_OPTIONS[0],
      adminPriority: complaint.adminPriority || PRIORITY_OPTIONS[1].value,
      status: complaint.status,
      adminNote: complaint.adminNote || ""
    });
  }

  async function handleUpdateSubmit(event) {
    event.preventDefault();
    if (!selectedComplaint) {
      setLocalMessage("Select a complaint first.");
      return;
    }

    const confirmed = window.confirm(
      `Save updates for complaint ${selectedComplaint.trackingId}?`
    );
    if (!confirmed) {
      return;
    }

    try {
      await onSaveComplaintUpdate(selectedComplaint.id, editState);
      setLocalMessage("Complaint updated successfully.");
      onRefresh();
    } catch (_error) {
      // Error state is shown via dashboardError from hook.
    }
  }

  return (
    <div className="page-wrap">
      <section className="card admin-dashboard-header">
        <div>
          <h2>Admin Dashboard</h2>
          <p className="page-description">
            Zone: <strong>{session.zone}</strong> | Admin: <strong>{session.adminName}</strong>
          </p>
        </div>
        <div className="dashboard-header-actions">
          <Button variant="secondary" onClick={onRefresh} loading={isLoadingDashboard}>
            Refresh
          </Button>
          <Button variant="danger" onClick={onLogout}>
            Logout
          </Button>
        </div>
      </section>

      <section className="stats-grid">
        <article className="card stat-card">
          <h3>Total in Zone</h3>
          <p>{stats.total}</p>
        </article>
        <article className="card stat-card">
          <h3>Pending</h3>
          <p>{stats.pending}</p>
        </article>
        <article className="card stat-card">
          <h3>Resolved</h3>
          <p>{stats.resolved}</p>
        </article>
        <article className="card stat-card">
          <h3>High Priority</h3>
          <p>{stats.highPriority}</p>
        </article>
      </section>

      <section className="card">
        <h3>Filters</h3>
        <div className="filter-grid">
          <InputField
            label="Status"
            name="status"
            value={filters.status}
            onChange={(event) => onFilterChange("status", event.target.value)}
            as="select"
            options={[{ value: "", label: "All" }, ...STATUS_OPTIONS]}
          />
          <InputField
            label="Priority"
            name="priority"
            value={filters.priority}
            onChange={(event) => onFilterChange("priority", event.target.value)}
            as="select"
            options={[{ value: "", label: "All" }, ...PRIORITY_OPTIONS]}
          />
        </div>
      </section>

      {dashboardError ? <p className="form-error-block">{dashboardError}</p> : null}

      <section className="admin-content-grid">
        <article className="card table-card">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Tracking ID</th>
                  <th>Issue Name</th>
                  <th>Location</th>
                  <th>Priority</th>
                  <th>Status</th>
                  <th>Submitted Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {complaints.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="empty-row">
                      {isLoadingDashboard ? (
                        <span className="table-loading">
                          <Spinner size="medium" />
                          Loading complaints...
                        </span>
                      ) : (
                        "No complaints found for current filters."
                      )}
                    </td>
                  </tr>
                ) : (
                  complaints.map((complaint) => {
                    const effectivePriority = complaint.adminPriority;
                    return (
                      <tr
                        key={complaint.id}
                        className={selectedComplaint?.id === complaint.id ? "active-row" : ""}
                        onClick={() => handleRowSelect(complaint)}
                      >
                        <td>{complaint.trackingId}</td>
                        <td>{complaint.issueName}</td>
                        <td>{complaint.locationAddress}</td>
                        <td>
                          <Badge tone={getPriorityTone(effectivePriority)}>
                            {PRIORITY_LABELS[effectivePriority] || effectivePriority}
                          </Badge>
                        </td>
                        <td>
                          <StatusBadge status={complaint.status} />
                        </td>
                        <td>{formatDate(complaint.createdAt)}</td>
                        <td>
                          <Button variant="secondary" onClick={() => handleRowSelect(complaint)}>
                            View / Edit
                          </Button>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </article>

        <aside className="card detail-panel">
          <h3>Complaint Details</h3>
          {!selectedComplaint ? (
            <p className="page-description">Select a complaint row to view details and update status.</p>
          ) : (
            <>
              <div className="detail-list">
                <p><span>Tracking ID:</span> {selectedComplaint.trackingId}</p>
                <p><span>Reporter:</span> {selectedComplaint.reporterName}</p>
                <p><span>Email:</span> {selectedComplaint.reporterEmail || "Not provided"}</p>
                <p><span>Issue Name:</span> {selectedComplaint.issueName}</p>
                <p><span>Location:</span> {selectedComplaint.locationAddress}</p>
                <p><span>Description:</span> {selectedComplaint.description}</p>
                <p><span>Image:</span></p>
                {selectedComplaint.imageData ? (
                  <img
                    src={selectedComplaint.imageData}
                    alt="Complaint evidence"
                    className="detail-image"
                  />
                ) : (
                  <p className="page-description">No image uploaded.</p>
                )}
              </div>

              <form className="edit-form" onSubmit={handleUpdateSubmit}>
                <InputField
                  label="Update Department"
                  name="assignedDepartment"
                  value={editState.assignedDepartment}
                  onChange={(event) =>
                    setEditState((currentValue) => ({
                      ...currentValue,
                      assignedDepartment: event.target.value
                    }))
                  }
                  as="select"
                  options={DEPARTMENT_OPTIONS.map((department) => ({
                    value: department,
                    label: department
                  }))}
                  required
                />

                <InputField
                  label="Priority"
                  name="adminPriority"
                  value={editState.adminPriority}
                  onChange={(event) =>
                    setEditState((currentValue) => ({
                      ...currentValue,
                      adminPriority: event.target.value
                    }))
                  }
                  as="select"
                  options={PRIORITY_OPTIONS}
                  required
                />

                <InputField
                  label="Status"
                  name="status"
                  value={editState.status}
                  onChange={(event) =>
                    setEditState((currentValue) => ({
                      ...currentValue,
                      status: event.target.value
                    }))
                  }
                  as="select"
                  options={STATUS_OPTIONS}
                  required
                />

                <InputField
                  label="Add Note"
                  name="adminNote"
                  value={editState.adminNote}
                  onChange={(event) =>
                    setEditState((currentValue) => ({
                      ...currentValue,
                      adminNote: event.target.value
                    }))
                  }
                  as="textarea"
                  rows={4}
                />

                {localMessage ? <p className="success-block">{localMessage}</p> : null}
                <Button type="submit" loading={isSaving}>
                  Save Update
                </Button>
              </form>
            </>
          )}
        </aside>
      </section>
    </div>
  );
}

AdminDashboard.propTypes = {
  session: PropTypes.shape({
    token: PropTypes.string.isRequired,
    adminName: PropTypes.string.isRequired,
    zone: PropTypes.string.isRequired
  }),
  stats: PropTypes.shape({
    total: PropTypes.number.isRequired,
    pending: PropTypes.number.isRequired,
    resolved: PropTypes.number.isRequired,
    highPriority: PropTypes.number.isRequired
  }).isRequired,
  complaints: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      trackingId: PropTypes.string.isRequired,
      reporterName: PropTypes.string.isRequired,
      reporterEmail: PropTypes.string,
      issueName: PropTypes.string.isRequired,
      locationAddress: PropTypes.string.isRequired,
      description: PropTypes.string.isRequired,
      adminPriority: PropTypes.string,
      status: PropTypes.string.isRequired,
      createdAt: PropTypes.string.isRequired,
      imageData: PropTypes.string
    })
  ).isRequired,
  filters: PropTypes.shape({
    status: PropTypes.string.isRequired,
    priority: PropTypes.string.isRequired
  }).isRequired,
  dashboardError: PropTypes.string.isRequired,
  isLoadingDashboard: PropTypes.bool.isRequired,
  isSaving: PropTypes.bool.isRequired,
  onFilterChange: PropTypes.func.isRequired,
  onRefresh: PropTypes.func.isRequired,
  onSaveComplaintUpdate: PropTypes.func.isRequired,
  onLogout: PropTypes.func.isRequired
};

AdminDashboard.defaultProps = {
  session: null
};

export default AdminDashboard;
