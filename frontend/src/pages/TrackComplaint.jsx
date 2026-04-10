import { useState } from "react";
import Button from "../components/Button";
import InputField from "../components/InputField";
import StatusBadge from "../components/StatusBadge";
import { useComplaint } from "../hooks/useComplaint";
import { PRIORITY_LABELS } from "../utils/constants";
import { formatDate } from "../utils/formatDate";
import { validateTrackingForm } from "../utils/validateForm";
import "./TrackComplaint.css";

function TrackComplaint() {
  const [trackingId, setTrackingId] = useState("");
  const [inputError, setInputError] = useState("");
  const { checkStatus, trackingResult, trackingError, isTracking } = useComplaint();

  async function handleSubmit(event) {
    event.preventDefault();

    const validationMessage = validateTrackingForm(trackingId);
    setInputError(validationMessage);
    if (validationMessage) {
      return;
    }

    try {
      await checkStatus(trackingId);
    } catch (_error) {
      // Error state is shown from hook.
    }
  }

  return (
    <div className="page-wrap">
      <section className="card">
        <h2>Track Complaint</h2>
        <p className="page-description">
          Enter your complaint tracking ID to view the latest action taken by the municipal team.
        </p>

        <form className="track-form" onSubmit={handleSubmit} noValidate>
          <InputField
            label="Tracking ID"
            name="trackingId"
            value={trackingId}
            onChange={(event) => {
              setTrackingId(event.target.value);
              setInputError("");
            }}
            error={inputError}
            placeholder="Example: CR-XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
            required
          />
          <Button type="submit" loading={isTracking}>
            Check Status
          </Button>
        </form>

        {trackingError ? (
          <div className="track-error-card">
            <h3>Complaint not found</h3>
            <p>{trackingError}</p>
          </div>
        ) : null}

        {trackingResult ? (
          <article className="track-result-card">
            <div className="result-header">
              <div>
                <h3>{trackingResult.trackingId}</h3>
                <p>Submitted on {formatDate(trackingResult.createdAt)}</p>
              </div>
              <StatusBadge status={trackingResult.status} />
            </div>

            <div className="result-grid">
              <div>
                <p className="label-text">Issue Name</p>
                <p>{trackingResult.issueName}</p>
              </div>
              <div>
                <p className="label-text">Zone</p>
                <p>{trackingResult.zone}</p>
              </div>
              <div>
                <p className="label-text">Department Assigned</p>
                <p>{trackingResult.assignedDepartment || "Pending assignment"}</p>
              </div>
              <div>
                <p className="label-text">Priority</p>
                <p>{PRIORITY_LABELS[trackingResult.adminPriority] || "-"}</p>
              </div>
              <div className="result-grid-full">
                <p className="label-text">Location Address</p>
                <p>{trackingResult.locationAddress}</p>
              </div>
              <div className="result-grid-full">
                <p className="label-text">Issue Description</p>
                <p>{trackingResult.description}</p>
              </div>
              <div className="result-grid-full">
                <p className="label-text">Admin Notes</p>
                <p>{trackingResult.adminNote || "No admin notes yet."}</p>
              </div>
              <div className="result-grid-full">
                <p className="label-text">Issue Image</p>
                {trackingResult.imageData ? (
                  <img
                    src={trackingResult.imageData}
                    alt="Complaint evidence"
                    className="result-image"
                  />
                ) : (
                  <p>No image uploaded.</p>
                )}
              </div>
            </div>
          </article>
        ) : null}
      </section>
    </div>
  );
}

export default TrackComplaint;
