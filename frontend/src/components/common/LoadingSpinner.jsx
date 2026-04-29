import { Box, CircularProgress } from "@mui/material";

export default function LoadingSpinner() {
  return (
    <Box
      sx={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "35vh",
      }}
    >
      <CircularProgress />
    </Box>
  );
}
