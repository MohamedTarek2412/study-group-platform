import { useState } from "react";
import { Button, Paper, Stack, TextField, Typography } from "@mui/material";
import toast from "react-hot-toast";
import { createGroup } from "../../api/groupApi";

export default function CreateGroupForm() {
  const [form, setForm] = useState({ name: "", description: "" });

  const handleChange = (event) => {
    setForm((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      await createGroup(form);
      setForm({ name: "", description: "" });
      toast.success("Group created.");
    } catch {
      toast.error("Failed to create group.");
    }
  };

  return (
    <Paper sx={{ p: 3 }}>
      <Stack component="form" spacing={2} onSubmit={handleSubmit}>
        <Typography variant="h6">Create Study Group</Typography>
        <TextField label="Group name" name="name" value={form.name} onChange={handleChange} required />
        <TextField
          label="Description"
          name="description"
          value={form.description}
          onChange={handleChange}
          multiline
          minRows={3}
          required
        />
        <Button type="submit" variant="contained">
          Create
        </Button>
      </Stack>
    </Paper>
  );
}
