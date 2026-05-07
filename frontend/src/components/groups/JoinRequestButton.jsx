import { Button } from "@mui/material";
import toast from "react-hot-toast";
import { requestJoinGroup } from "../../api/groupApi";

export default function JoinRequestButton({ groupId }) {
  const handleRequest = async () => {
    try {
      await requestJoinGroup(groupId);
      toast.success("Join request submitted.");
    } catch {
      toast.error("Could not submit request.");
    }
  };

  return (
    <Button variant="outlined" onClick={handleRequest}>
      Request to Join
    </Button>
  );
}
