import PropTypes from "prop-types";
import Spinner from "./Spinner";
import "./Button.css";

function Button({
  type = "button",
  variant = "primary",
  fullWidth = false,
  loading = false,
  disabled = false,
  onClick,
  children
}) {
  const className = `btn btn-${variant}${fullWidth ? " btn-full" : ""}`;

  return (
    <button
      type={type}
      className={className}
      onClick={onClick}
      disabled={disabled || loading}
    >
      {loading ? <Spinner size="small" /> : children}
    </button>
  );
}

Button.propTypes = {
  type: PropTypes.oneOf(["button", "submit", "reset"]),
  variant: PropTypes.oneOf(["primary", "secondary", "danger"]),
  fullWidth: PropTypes.bool,
  loading: PropTypes.bool,
  disabled: PropTypes.bool,
  onClick: PropTypes.func,
  children: PropTypes.node.isRequired
};

export default Button;
