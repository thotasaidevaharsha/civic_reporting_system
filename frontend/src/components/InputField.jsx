import PropTypes from "prop-types";
import "./InputField.css";

function InputField({
  label,
  name,
  value,
  onChange,
  type = "text",
  placeholder = "",
  error = "",
  required = false,
  as = "input",
  rows = 4,
  options = []
}) {
  return (
    <label className="field-wrapper" htmlFor={name}>
      <span className="field-label">
        {label}
        {required ? <strong className="field-required"> *</strong> : null}
      </span>

      {as === "textarea" ? (
        <textarea
          id={name}
          name={name}
          className={`field-control${error ? " field-error" : ""}`}
          value={value}
          rows={rows}
          onChange={onChange}
          placeholder={placeholder}
        />
      ) : null}

      {as === "select" ? (
        <select
          id={name}
          name={name}
          className={`field-control${error ? " field-error" : ""}`}
          value={value}
          onChange={onChange}
        >
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      ) : null}

      {as === "input" ? (
        <input
          id={name}
          name={name}
          type={type}
          className={`field-control${error ? " field-error" : ""}`}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
        />
      ) : null}

      {error ? <span className="field-error-text">{error}</span> : null}
    </label>
  );
}

InputField.propTypes = {
  label: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  type: PropTypes.string,
  placeholder: PropTypes.string,
  error: PropTypes.string,
  required: PropTypes.bool,
  as: PropTypes.oneOf(["input", "textarea", "select"]),
  rows: PropTypes.number,
  options: PropTypes.arrayOf(
    PropTypes.shape({
      value: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired
    })
  )
};

export default InputField;
