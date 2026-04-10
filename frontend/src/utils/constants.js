export const ZONES = [
  "North Zone",
  "South Zone",
  "East Zone",
  "West Zone",
  "Central Zone"
];

export const PRIORITY_OPTIONS = [
  { value: "LOW", label: "Low" },
  { value: "MEDIUM", label: "Medium" },
  { value: "HIGH", label: "High" }
];

export const STATUS_OPTIONS = [
  { value: "PENDING", label: "Pending" },
  { value: "IN_PROGRESS", label: "In Progress" },
  { value: "RESOLVED", label: "Resolved" },
  { value: "REJECTED", label: "Rejected" }
];

export const DEPARTMENT_OPTIONS = [
  "Roads Department",
  "Water Board",
  "Electrical Department",
  "Sanitation Department",
  "Public Works Department",
  "Drainage Department",
  "Town Planning Department"
];

export const STATUS_LABELS = STATUS_OPTIONS.reduce((accumulator, option) => {
  accumulator[option.value] = option.label;
  return accumulator;
}, {});

export const PRIORITY_LABELS = PRIORITY_OPTIONS.reduce((accumulator, option) => {
  accumulator[option.value] = option.label;
  return accumulator;
}, {});
