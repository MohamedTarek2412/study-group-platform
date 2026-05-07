import { Grid } from "@mui/material";
import GroupCard from "./GroupCard";

export default function GroupList({ groups = [] }) {
  return (
    <Grid container spacing={2}>
      {groups.map((group) => (
        <Grid item xs={12} md={6} lg={4} key={group.id}>
          <GroupCard group={group} />
        </Grid>
      ))}
    </Grid>
  );
}
