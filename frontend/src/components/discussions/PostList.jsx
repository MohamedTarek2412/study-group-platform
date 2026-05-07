import { List, ListItem, ListItemText, Paper, Typography } from "@mui/material";

export default function PostList({ posts = [] }) {
  return (
    <Paper sx={{ p: 2 }}>
      <Typography variant="h6" sx={{ mb: 1 }}>
        Discussion
      </Typography>
      <List disablePadding>
        {posts.map((post) => (
          <ListItem key={post.id} divider>
            <ListItemText primary={post.title} secondary={post.content} />
          </ListItem>
        ))}
      </List>
    </Paper>
  );
}
