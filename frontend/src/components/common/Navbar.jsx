import { AppBar, Box, Button, Toolbar, Typography } from "@mui/material";
import { Link as RouterLink } from "react-router-dom";
import useAuth from "../../hooks/useAuth";

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();

  return (
    <AppBar position="sticky" elevation={1}>
      <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
        <Typography component={RouterLink} to="/" variant="h6" color="inherit" sx={{ textDecoration: "none" }}>
          Study Group Platform
        </Typography>
        <Box sx={{ display: "flex", gap: 1 }}>
          {isAuthenticated ? (
            <>
              <Button color="inherit" component={RouterLink} to="/groups">
                Groups
              </Button>
              <Button color="inherit" component={RouterLink} to="/dashboard">
                Dashboard
              </Button>
              {user?.roles?.includes("ADMIN") && (
                <Button color="inherit" component={RouterLink} to="/admin">
                  Admin
                </Button>
              )}
              <Button color="inherit" onClick={logout}>
                Logout
              </Button>
            </>
          ) : (
            <>
              <Button color="inherit" component={RouterLink} to="/login">
                Login
              </Button>
              <Button color="inherit" component={RouterLink} to="/register">
                Register
              </Button>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
}
