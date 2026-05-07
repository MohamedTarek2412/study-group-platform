import { useEffect, useState } from "react";
import { Grid, Stack } from "@mui/material";
import { useParams } from "react-router-dom";
import { getGroupById } from "../api/groupApi";
import { getPostsByGroupId } from "../api/discussionApi";
import GroupDetail from "../components/groups/GroupDetail";
import PostList from "../components/discussions/PostList";
import PostForm from "../components/discussions/PostForm";
import MaterialUpload from "../components/discussions/MaterialUpload";
import LoadingSpinner from "../components/common/LoadingSpinner";

export default function GroupDetailPage() {
  const { groupId } = useParams();
  const [group, setGroup] = useState(null);
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      const [groupData, postsData] = await Promise.all([getGroupById(groupId), getPostsByGroupId(groupId)]);
      setGroup(groupData);
      setPosts(postsData?.content || postsData || []);
      setLoading(false);
    };
    load();
  }, [groupId]);

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <Stack spacing={2}>
      <GroupDetail group={group} />
      <Grid container spacing={2}>
        <Grid item xs={12} md={7}>
          <PostList posts={posts} />
        </Grid>
        <Grid item xs={12} md={5}>
          <Stack spacing={2}>
            <PostForm groupId={groupId} />
            <MaterialUpload groupId={groupId} />
          </Stack>
        </Grid>
      </Grid>
    </Stack>
  );
}
