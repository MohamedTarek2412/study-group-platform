import { Grid } from "@mui/material";
import RegisterForm from "../components/auth/RegisterForm";

export default function RegisterPage() {
  return (
    <Grid container justifyContent="center">
      <Grid item xs={12} md={7} lg={5}>
        <RegisterForm />
      </Grid>
    </Grid>
  );
}
