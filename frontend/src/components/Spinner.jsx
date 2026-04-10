import PropTypes from "prop-types";
import "./Spinner.css";

function Spinner({ size = "medium" }) {
  return <span className={`spinner spinner-${size}`} aria-label="Loading" />;
}

Spinner.propTypes = {
  size: PropTypes.oneOf(["small", "medium", "large"])
};

export default Spinner;
