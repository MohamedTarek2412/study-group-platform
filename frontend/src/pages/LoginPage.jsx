import { Grid } from "@mui/material";
import LoginForm from "../components/auth/LoginForm";

export default function LoginPage() {
  return (
    <Grid container justifyContent="center">
      <Grid item xs={12} md={7} lg={5}>
        <LoginForm />
      </Grid>
    </Grid>
  );
}
