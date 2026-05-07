import { useState } from "react";
import { Button, Paper, Stack, TextField } from "@mui/material";
import toast from "react-hot-toast";
import { createPost } from "../../api/discussionApi";

export default function PostForm({ groupId }) {
  const [form, setForm] = useState({ title: "", content: "" });

  const handleChange = (event) => {
    setForm((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      await createPost(groupId, form);
      setForm({ title: "", content: "" });
      toast.success("Post created.");
    } catch {
      toast.error("Unable to create post.");
    }
  };

  return (
    <Paper sx={{ p: 2 }}>
      <Stack component="form" spacing={2} onSubmit={handleSubmit}>
        <TextField label="Title" name="title" value={form.title} onChange={handleChange} required />
        <TextField
          label="Content"
          name="content"
          value={form.content}
          onChange={handleChange}
          multiline
          minRows={3}
          required
        />
        <Button type="submit" variant="contained">
          Add Post
        </Button>
      </Stack>
    </Paper>
  );
}
