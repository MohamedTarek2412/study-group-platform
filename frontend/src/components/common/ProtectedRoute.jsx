import { Navigate, useLocation } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import { hasAnyRole } from "../../utils/roleGuard";
import LoadingSpinner from "./LoadingSpinner";

export default function ProtectedRoute({ children, requiredRoles = [] }) {
  const { user, loading, isAuthenticated } = useAuth();
  const location = useLocation();

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (!hasAnyRole(user?.roles || [], requiredRoles)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
