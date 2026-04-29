import { Paper, Stack, Typography } from "@mui/material";
import useAuth from "../hooks/useAuth";

export default function DashboardPage() {
  const { user } = useAuth();

  return (
    <Paper sx={{ p: 3 }}>
      <Stack spacing={1}>
        <Typography variant="h4">Dashboard</Typography>
        <Typography>Welcome, {user?.fullName || "User"}.</Typography>
        <Typography color="text.secondary">Your roles: {(user?.roles || []).join(", ") || "N/A"}</Typography>
      </Stack>
    </Paper>
  );
}
