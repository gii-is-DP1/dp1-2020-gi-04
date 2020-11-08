import { Box } from "@material-ui/core";
import { Button, notification, Row } from "antd";
import useAxios from "axios-hooks";
import React, { useEffect, useState } from "react";
import { Loading } from "../../../components/pure/Loading";
import { FilmFormModal } from "./FilmFormModal";

export const FilmFormModalCreate = React.memo((props) => {
  const [isCreating, setIsCreating] = useState(false);

  const sendSuccessNotification = notification["success"];
  const sendErrorNotification = notification["error"];

  const [{ data, loading, error }, createFilm] = useAxios(
    { url: "/films", method: "POST" },
    { manual: true }
  );

  useEffect(() => {
    if (data) {
      sendSuccessNotification({ message: "Film created successfully!" });
    }
  }, [data]);

  useEffect(() => {
    if (error) {
      sendErrorNotification({ message: error.message });
    }
  }, [error]);

  if (loading) return <Loading />;

  return (
    <Box>
      <FilmFormModal
        visible={isCreating}
        title="Create film"
        handleCancel={() => setIsCreating(false)}
        handleSubmit={(values) => {
          setIsCreating(false);
          createFilm({ data: values });
        }}
      />
      <Row justify="center">
        <Button type="primary" onClick={() => setIsCreating(true)}>
          Create
        </Button>
      </Row>
    </Box>
  );
});
