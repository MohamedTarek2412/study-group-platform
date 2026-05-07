import axiosInstance from "../utils/axiosInstance";

export const getPostsByGroupId = async (groupId) => {
  const { data } = await axiosInstance.get(`/groups/${groupId}/posts`);
  return data;
};

export const createPost = async (groupId, payload) => {
  const { data } = await axiosInstance.post(`/groups/${groupId}/posts`, payload);
  return data;
};

export const uploadMaterial = async (groupId, formData) => {
  const { data } = await axiosInstance.post(
    `/groups/${groupId}/materials`,
    formData,
    { headers: { "Content-Type": "multipart/form-data" } }
  );
  return data;
};
