import { Paper, Stack, Typography } from "@mui/material";
import JoinRequestButton from "./JoinRequestButton";

export default function GroupDetail({ group }) {
  if (!group) {
    return null;
  }

  return (
    <Paper sx={{ p: 3 }}>
      <Stack spacing={1.5}>
        <Typography variant="h5">{group.name}</Typography>
        <Typography color="text.secondary">{group.description}</Typography>
        <JoinRequestButton groupId={group.id} />
      </Stack>
    </Paper>
  );
}
