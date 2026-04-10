import PropTypes from "prop-types";
import "./Badge.css";

function Badge({ children, tone = "neutral" }) {
  return <span className={`badge badge-${tone}`}>{children}</span>;
}

Badge.propTypes = {
  children: PropTypes.node.isRequired,
  tone: PropTypes.oneOf(["neutral", "blue", "green", "amber", "red"])
};

export default Badge;
