import { useState } from "react";
import PropTypes from "prop-types";
import { useNavigate } from "react-router-dom";
import Button from "../components/Button";
import InputField from "../components/InputField";
import { ZONES } from "../utils/constants";
import { validateAdminLoginForm } from "../utils/validateForm";
import "./AdminLogin.css";

const initialLoginForm = {
  zoneId: ZONES[0],
  adminId: "",
  password: ""
};

function AdminLogin({ onLogin, isLoggingIn, loginError }) {
  const navigate = useNavigate();
  const [formData, setFormData] = useState(initialLoginForm);
  const [errors, setErrors] = useState({});

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

  async function handleSubmit(event) {
    event.preventDefault();

    const validationErrors = validateAdminLoginForm(formData);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    try {
      await onLogin(formData);
      navigate("/admin/dashboard");
    } catch (_error) {
      // Error message comes from hook state.
    }
  }

  return (
    <div className="admin-login-page">
      <section className="card admin-login-card">
        <h2>Admin Login</h2>
        <p className="page-description">Sign in with your zone credentials to access the dashboard.</p>

        <form className="admin-login-form" onSubmit={handleSubmit} noValidate>
          <InputField
            label="Zone"
            name="zoneId"
            value={formData.zoneId}
            onChange={handleFieldChange}
            error={errors.zoneId}
            as="select"
            options={ZONES.map((zone) => ({ value: zone, label: zone }))}
            required
          />
          <InputField
            label="Admin ID"
            name="adminId"
            value={formData.adminId}
            onChange={handleFieldChange}
            error={errors.adminId}
            required
          />
          <InputField
            label="Password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleFieldChange}
            error={errors.password}
            required
          />

          {loginError ? <p className="form-error-block">{loginError}</p> : null}

          <Button type="submit" fullWidth loading={isLoggingIn}>
            Login
          </Button>
        </form>
      </section>
    </div>
  );
}

AdminLogin.propTypes = {
  onLogin: PropTypes.func.isRequired,
  isLoggingIn: PropTypes.bool.isRequired,
  loginError: PropTypes.string.isRequired
};

export default AdminLogin;
