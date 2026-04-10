import { useState } from "react";
import { createComplaint, getComplaintByTrackingId } from "../services/api";

export function useComplaint() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isTracking, setIsTracking] = useState(false);
  const [submitError, setSubmitError] = useState("");
  const [trackingError, setTrackingError] = useState("");
  const [trackingResult, setTrackingResult] = useState(null);

  async function submitComplaint(payload) {
    setIsSubmitting(true);
    setSubmitError("");
    try {
      const response = await createComplaint(payload);
      return response;
    } catch (error) {
      setSubmitError(error.message);
      throw error;
    } finally {
      setIsSubmitting(false);
    }
  }

  async function checkStatus(trackingId) {
    setIsTracking(true);
    setTrackingError("");
    try {
      const response = await getComplaintByTrackingId(trackingId);
      setTrackingResult(response);
      return response;
    } catch (error) {
      setTrackingResult(null);
      setTrackingError(error.message);
      throw error;
    } finally {
      setIsTracking(false);
    }
  }

  return {
    isSubmitting,
    isTracking,
    submitError,
    trackingError,
    trackingResult,
    setTrackingResult,
    submitComplaint,
    checkStatus
  };
}
