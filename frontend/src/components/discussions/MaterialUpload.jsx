import { useState } from "react";
import { Button, Paper, Stack, Typography } from "@mui/material";
import toast from "react-hot-toast";
import { uploadMaterial } from "../../api/discussionApi";

export default function MaterialUpload({ groupId }) {
  const [file, setFile] = useState(null);

  const handleUpload = async () => {
    if (!file) {
      return;
    }
    const formData = new FormData();
    formData.append("file", file);
    try {
      await uploadMaterial(groupId, formData);
      setFile(null);
      toast.success("Material uploaded.");
    } catch {
      toast.error("Upload failed.");
    }
  };

  return (
    <Paper sx={{ p: 2 }}>
      <Stack spacing={1.5}>
        <Typography variant="h6">Learning Material</Typography>
        <input type="file" onChange={(event) => setFile(event.target.files?.[0] || null)} />
        <Button variant="outlined" onClick={handleUpload} disabled={!file}>
          Upload
        </Button>
      </Stack>
    </Paper>
  );
}
