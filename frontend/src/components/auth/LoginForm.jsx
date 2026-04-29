import { useState } from "react";
import { Alert, Button, Paper, Stack, TextField, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import useAuth from "../../hooks/useAuth";

export default function LoginForm() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");

  const handleChange = (event) => {
    setForm((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    try {
      await login(form);
      navigate("/dashboard");
    } catch (err) {
      setError(err?.response?.data?.message || "Invalid credentials.");
    }
  };

  return (
    <Paper sx={{ p: 3 }}>
      <Stack component="form" spacing={2} onSubmit={handleSubmit}>
        <Typography variant="h5">Sign in</Typography>
        {error && <Alert severity="error">{error}</Alert>}
        <TextField name="email" type="email" label="Email" value={form.email} onChange={handleChange} required />
        <TextField
          name="password"
          type="password"
          label="Password"
          value={form.password}
          onChange={handleChange}
          required
        />
        <Button type="submit" variant="contained">
          Login
        </Button>
      </Stack>
    </Paper>
  );
}
