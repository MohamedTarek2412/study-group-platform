import { Button, Card, CardActions, CardContent, Chip, Stack, Typography } from "@mui/material";
import { Link as RouterLink } from "react-router-dom";

export default function GroupCard({ group }) {
  return (
    <Card sx={{ height: "100%" }}>
      <CardContent>
        <Stack direction="row" justifyContent="space-between" sx={{ mb: 1 }}>
          <Typography variant="h6">{group.name}</Typography>
          <Chip size="small" label={group.visibility || "PUBLIC"} />
        </Stack>
        <Typography color="text.secondary">{group.description}</Typography>
      </CardContent>
      <CardActions>
        <Button component={RouterLink} to={`/groups/${group.id}`} size="small">
          View Details
        </Button>
      </CardActions>
    </Card>
  );
}
