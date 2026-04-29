import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Box, Container } from "@mui/material";
import Navbar from "./components/common/Navbar";
import ProtectedRoute from "./components/common/ProtectedRoute";
import { AuthProvider } from "./context/AuthContext";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import GroupsPage from "./pages/GroupsPage";
import GroupDetailPage from "./pages/GroupDetailPage";
import DashboardPage from "./pages/DashboardPage";
import AdminPage from "./pages/AdminPage";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Box sx={{ minHeight: "100vh", backgroundColor: "background.default" }}>
          <Navbar />
          <Container maxWidth="lg" sx={{ py: 4 }}>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route
                path="/groups"
                element={
                  <ProtectedRoute>
                    <GroupsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/groups/:groupId"
                element={
                  <ProtectedRoute>
                    <GroupDetailPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute>
                    <DashboardPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin"
                element={
                  <ProtectedRoute requiredRoles={["ADMIN"]}>
                    <AdminPage />
                  </ProtectedRoute>
                }
              />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Container>
        </Box>
      </BrowserRouter>
    </AuthProvider>
  );
}