import { useEffect, useState } from "react";
import { Grid, Typography } from "@mui/material";
import { getPendingCreators } from "../api/userApi";
import PendingCreators from "../components/admin/PendingCreators";
import PendingGroups from "../components/admin/PendingGroups";

export default function AdminPage() {
  const [creators, setCreators] = useState([]);

  useEffect(() => {
    const load = async () => {
      const data = await getPendingCreators();
      setCreators(data?.content || data || []);
    };
    load();
  }, []);

  return (
    <>
      <Typography variant="h4" sx={{ mb: 2 }}>
        Admin Console
      </Typography>
      <Grid container spacing={2}>
        <Grid item xs={12} md={6}>
          <PendingCreators creators={creators} />
        </Grid>
        <Grid item xs={12} md={6}>
          <PendingGroups groups={[]} />
        </Grid>
      </Grid>
    </>
  );
}
