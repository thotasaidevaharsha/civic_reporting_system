import { useState } from "react";
import Button from "../components/Button";
import InputField from "../components/InputField";
import Modal from "../components/Modal";
import { useComplaint } from "../hooks/useComplaint";
import { ZONES } from "../utils/constants";
import { validateReportForm } from "../utils/validateForm";
import "./ReportIssue.css";

const initialFormState = {
  reporterName: "",
  reporterEmail: "",
  zone: ZONES[0],
  issueName: "",
  description: "",
  locationAddress: "",
  imageData: ""
};

function ReportIssue() {
  const [formData, setFormData] = useState(initialFormState);
  const [errors, setErrors] = useState({});
  const [trackingData, setTrackingData] = useState(null);
  const [copyText, setCopyText] = useState("Copy ID");
  const { submitComplaint, submitError, isSubmitting } = useComplaint();

  function handleFieldChange(event) {
    const { name, value } = event.target;
    setFormData((currentValue) => ({
      ...currentValue,
      [name]: value
    }));
    setErrors((currentErrors) => ({
      ...currentErrors,
      [name]: ""
    }));
  }

  function handleImageChange(event) {
    const file = event.target.files?.[0];
    if (!file) {
      setFormData((currentValue) => ({
        ...currentValue,
        imageData: ""
      }));
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      setFormData((currentValue) => ({
        ...currentValue,
        imageData: String(reader.result || "")
      }));
      setErrors((currentErrors) => ({
        ...currentErrors,
        imageData: ""
      }));
    };
    reader.onerror = () => {
      setErrors((currentErrors) => ({
        ...currentErrors,
        imageData: "Could not read image file. Please try another image."
      }));
    };
    reader.readAsDataURL(file);
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const validationErrors = validateReportForm(formData);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    try {
      const response = await submitComplaint(formData);
      setTrackingData(response);
      setFormData(initialFormState);
      setErrors({});
      setCopyText("Copy ID");
    } catch (_error) {
      // Error state is handled in useComplaint and shown in UI.
    }
  }

  async function handleCopyTrackingId() {
    if (!trackingData?.trackingId) {
      return;
    }

    try {
      await navigator.clipboard.writeText(trackingData.trackingId);
      setCopyText("Copied");
    } catch (_error) {
      setCopyText("Copy failed");
    }
  }

  return (
    <div className="page-wrap">
      <section className="card">
        <h2>Report Issue</h2>
        <p className="page-description">
          Complete all mandatory details so the zone team can verify, assign, and resolve the issue
          faster.
        </p>

        <form className="report-form" onSubmit={handleSubmit} noValidate>
          <InputField
            label="Full Name"
            name="reporterName"
            value={formData.reporterName}
            onChange={handleFieldChange}
            error={errors.reporterName}
            required
          />
          <InputField
            label="Email (Optional)"
            name="reporterEmail"
            value={formData.reporterEmail}
            onChange={handleFieldChange}
            error={errors.reporterEmail}
            type="email"
          />
          <InputField
            label="Zone / Area"
            name="zone"
            value={formData.zone}
            onChange={handleFieldChange}
            error={errors.zone}
            required
            as="select"
            options={ZONES.map((zone) => ({ value: zone, label: zone }))}
          />
          <InputField
            label="Issue Name"
            name="issueName"
            value={formData.issueName}
            onChange={handleFieldChange}
            error={errors.issueName}
            required
          />
          <div className="report-form-full">
            <InputField
              label="Issue Description"
              name="description"
              value={formData.description}
              onChange={handleFieldChange}
              error={errors.description}
              required
              as="textarea"
              rows={6}
            />
          </div>
          <div className="report-form-full">
            <InputField
              label="Location Address"
              name="locationAddress"
              value={formData.locationAddress}
              onChange={handleFieldChange}
              error={errors.locationAddress}
              required
            />
          </div>
          <div className="report-form-full">
            <label className="upload-field-label" htmlFor="imageUpload">
              Upload Issue Image
            </label>
            <input
              id="imageUpload"
              type="file"
              accept="image/*"
              className="upload-input"
              onChange={handleImageChange}
            />
            {errors.imageData ? <span className="field-error-text">{errors.imageData}</span> : null}
            {formData.imageData ? (
              <div className="image-preview-wrap">
                <img src={formData.imageData} alt="Issue preview" className="image-preview" />
              </div>
            ) : null}
          </div>

          {submitError ? <p className="form-error-block">{submitError}</p> : null}

          <div className="report-actions report-form-full">
            <Button type="submit" loading={isSubmitting}>
              Submit Complaint
            </Button>
          </div>
        </form>
      </section>

      <Modal
        title="Complaint Submitted"
        open={Boolean(trackingData)}
        onClose={() => setTrackingData(null)}
      >
        {trackingData ? (
          <div className="tracking-modal-content">
            <p>Your complaint has been submitted successfully.</p>
            <p className="tracking-label">Tracking ID</p>
            <p className="tracking-id-text">{trackingData.trackingId}</p>
            <div className="tracking-actions">
              <Button type="button" variant="secondary" onClick={handleCopyTrackingId}>
                {copyText}
              </Button>
            </div>
            <p className="tracking-instruction">
              Save this tracking ID to check your complaint status later.
            </p>
          </div>
        ) : null}
      </Modal>
    </div>
  );
}

export default ReportIssue;
