import { Grid, Stack, Typography } from "@mui/material";
import useGroups from "../hooks/useGroups";
import LoadingSpinner from "../components/common/LoadingSpinner";
import GroupList from "../components/groups/GroupList";
import CreateGroupForm from "../components/groups/CreateGroupForm";

export default function GroupsPage() {
  const { data, isLoading } = useGroups();

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <Stack spacing={3}>
      <Typography variant="h4">Study Groups</Typography>
      <Grid container spacing={2}>
        <Grid item xs={12} lg={8}>
          <GroupList groups={data?.content || data || []} />
        </Grid>
        <Grid item xs={12} lg={4}>
          <CreateGroupForm />
        </Grid>
      </Grid>
    </Stack>
  );
}
