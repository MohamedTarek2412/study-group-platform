import { useQuery } from "react-query";
import { getGroups } from "../api/groupApi";

export default function useGroups(params = {}) {
  return useQuery(["groups", params], () => getGroups(params));
}
