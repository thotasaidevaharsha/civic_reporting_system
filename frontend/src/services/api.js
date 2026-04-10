const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

function buildHeaders(token, extraHeaders = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...extraHeaders
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return headers;
}

async function request(path, { method = "GET", body, token, headers = {} } = {}) {
  if (!API_BASE_URL) {
    throw new Error("VITE_API_BASE_URL is missing. Configure it in frontend/.env.");
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers: buildHeaders(token, headers),
    body: body ? JSON.stringify(body) : undefined
  });

  const contentType = response.headers.get("content-type") || "";
  const isJson = contentType.includes("application/json");

  let payload;
  if (isJson) {
    payload = await response.json();
  } else {
    const text = await response.text();
    payload = { error: text };
  }

  if (!response.ok) {
    const message =
      payload?.error ||
      payload?.message ||
      `Request failed with status ${response.status}`;
    throw new Error(message);
  }

  return payload;
}

export function createComplaint(complaintPayload) {
  return request("/complaints", {
    method: "POST",
    body: complaintPayload
  });
}

export function getComplaintByTrackingId(trackingId) {
  return request(`/complaints/${encodeURIComponent(trackingId)}`);
}

export function adminLogin(loginPayload) {
  return request("/admin/login", {
    method: "POST",
    body: loginPayload
  });
}

export function getAdminStats(zone, token) {
  return request(`/admin/stats/${encodeURIComponent(zone)}`, {
    token
  });
}

export function getComplaints(filters, token) {
  const params = new URLSearchParams();
  if (filters.zone) {
    params.set("zone", filters.zone);
  }
  if (filters.status) {
    params.set("status", filters.status);
  }
  if (filters.priority) {
    params.set("priority", filters.priority);
  }

  const query = params.toString();
  const suffix = query ? `?${query}` : "";

  return request(`/complaints${suffix}`, { token });
}

export function updateComplaint(id, updatePayload, token) {
  return request(`/complaints/${id}`, {
    method: "PATCH",
    body: updatePayload,
    token
  });
}
