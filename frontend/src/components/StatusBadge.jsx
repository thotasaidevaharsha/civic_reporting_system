import PropTypes from "prop-types";
import Badge from "./Badge";
import { formatStatusLabel } from "../utils/formatDate";

function StatusBadge({ status }) {
  const toneMap = {
    PENDING: "amber",
    IN_PROGRESS: "blue",
    RESOLVED: "green",
    REJECTED: "red"
  };

  return <Badge tone={toneMap[status] || "neutral"}>{formatStatusLabel(status)}</Badge>;
}

StatusBadge.propTypes = {
  status: PropTypes.string
};

StatusBadge.defaultProps = {
  status: ""
};

export default StatusBadge;
