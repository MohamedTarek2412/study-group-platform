import axiosInstance from "../utils/axiosInstance";

export const loginUser = async (credentials) => {
  const { data } = await axiosInstance.post("/auth/login", credentials);
  return data;
};

export const registerUser = async (payload) => {
  const { data } = await axiosInstance.post("/auth/register", payload);
  return data;
};
