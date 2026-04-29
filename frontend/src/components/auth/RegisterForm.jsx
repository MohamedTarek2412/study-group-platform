import { useState } from "react";
import { Alert, Button, MenuItem, Paper, Stack, TextField, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import useAuth from "../../hooks/useAuth";

const availableRoles = ["USER", "MANAGER", "EMPLOYEE"];

export default function RegisterForm() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    role: "USER",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (event) => {
    setForm((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");
    try {
      await register(form);
      setSuccess("Registration successful. You can log in now.");
      setTimeout(() => navigate("/login"), 800);
    } catch (err) {
      setError(err?.response?.data?.message || "Registration failed.");
    }
  };

  return (
    <Paper sx={{ p: 3 }}>
      <Stack component="form" spacing={2} onSubmit={handleSubmit}>
        <Typography variant="h5">Create account</Typography>
        {error && <Alert severity="error">{error}</Alert>}
        {success && <Alert severity="success">{success}</Alert>}
        <TextField name="fullName" label="Full name" value={form.fullName} onChange={handleChange} required />
        <TextField name="email" label="Email" type="email" value={form.email} onChange={handleChange} required />
        <TextField
          name="password"
          label="Password"
          type="password"
          value={form.password}
          onChange={handleChange}
          required
        />
        <TextField select name="role" label="Role" value={form.role} onChange={handleChange}>
          {availableRoles.map((role) => (
            <MenuItem key={role} value={role}>
              {role}
            </MenuItem>
          ))}
        </TextField>
        <Button type="submit" variant="contained">
          Register
        </Button>
      </Stack>
    </Paper>
  );
}
