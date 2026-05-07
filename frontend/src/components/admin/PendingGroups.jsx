import { List, ListItem, ListItemText, Paper, Typography } from "@mui/material";

export default function PendingGroups({ groups = [] }) {
  return (
    <Paper sx={{ p: 2 }}>
      <Typography variant="h6" sx={{ mb: 1 }}>
        Pending Group Approvals
      </Typography>
      <List disablePadding>
        {groups.map((group) => (
          <ListItem key={group.id} divider>
            <ListItemText primary={group.name} secondary={group.description} />
          </ListItem>
        ))}
      </List>
    </Paper>
  );
}
