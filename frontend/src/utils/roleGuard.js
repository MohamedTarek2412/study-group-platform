export const hasAnyRole = (userRoles = [], requiredRoles = []) => {
  if (!requiredRoles.length) {
    return true;
  }
  return requiredRoles.some((role) => userRoles.includes(role));
};
