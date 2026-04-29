import { List, ListItem, ListItemText, Paper, Typography } from "@mui/material";

export default function PendingCreators({ creators = [] }) {
  return (
    <Paper sx={{ p: 2 }}>
      <Typography variant="h6" sx={{ mb: 1 }}>
        Pending Creator Requests
      </Typography>
      <List disablePadding>
        {creators.map((creator) => (
          <ListItem key={creator.id} divider>
            <ListItemText primary={creator.fullName} secondary={creator.email} />
          </ListItem>
        ))}
      </List>
    </Paper>
  );
}
