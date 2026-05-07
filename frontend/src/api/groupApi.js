import axiosInstance from "../utils/axiosInstance";

export const getGroups = async (params = {}) => {
  const { data } = await axiosInstance.get("/groups", { params });
  return data;
};

export const getGroupById = async (groupId) => {
  const { data } = await axiosInstance.get(`/groups/${groupId}`);
  return data;
};

export const createGroup = async (payload) => {
  const { data } = await axiosInstance.post("/groups", payload);
  return data;
};

export const requestJoinGroup = async (groupId) => {
  const { data } = await axiosInstance.post(`/groups/${groupId}/join-requests`);
  return data;
};
