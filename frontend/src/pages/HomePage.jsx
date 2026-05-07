import { Paper, Stack, Typography } from "@mui/material";

export default function HomePage() {
  return (
    <Paper sx={{ p: 4 }}>
      <Stack spacing={2}>
        <Typography variant="h4">Study Group Platform</Typography>
        <Typography color="text.secondary">
          Production-ready collaborative learning platform with role-based access, moderated groups, and
          event-driven microservices backend integration.
        </Typography>
      </Stack>
    </Paper>
  );
}
