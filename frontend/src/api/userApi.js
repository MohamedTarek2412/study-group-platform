import axiosInstance from "../utils/axiosInstance";

export const getCurrentUserProfile = async () => {
  const { data } = await axiosInstance.get("/users/me");
  return data;
};

export const getPendingCreators = async () => {
  const { data } = await axiosInstance.get("/admin/creator-requests");
  return data;
};
