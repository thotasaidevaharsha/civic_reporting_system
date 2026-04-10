export function validateReportForm(formData) {
  const errors = {};

  if (!formData.reporterName.trim()) {
    errors.reporterName = "Full name is required.";
  }

  if (formData.reporterEmail.trim()) {
    const isValidEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.reporterEmail.trim());
    if (!isValidEmail) {
      errors.reporterEmail = "Enter a valid email address.";
    }
  }

  if (!formData.zone) {
    errors.zone = "Zone is required.";
  }

  if (!formData.issueName.trim()) {
    errors.issueName = "Issue name is required.";
  }

  if (!formData.description.trim()) {
    errors.description = "Issue description is required.";
  } else if (formData.description.trim().length < 20) {
    errors.description = "Issue description must be at least 20 characters.";
  }

  if (!formData.locationAddress.trim()) {
    errors.locationAddress = "Location address is required.";
  }

  return errors;
}

export function validateAdminLoginForm(formData) {
  const errors = {};

  if (!formData.zoneId) {
    errors.zoneId = "Zone is required.";
  }
  if (!formData.adminId.trim()) {
    errors.adminId = "Admin ID is required.";
  }
  if (!formData.password.trim()) {
    errors.password = "Password is required.";
  }

  return errors;
}

export function validateTrackingForm(trackingId) {
  if (!trackingId.trim()) {
    return "Tracking ID is required.";
  }
  return "";
}
