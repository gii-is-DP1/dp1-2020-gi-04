import { Box } from "@material-ui/core";
import { Button, notification, Row } from "antd";
import useAxios from "axios-hooks";
import React, { useEffect, useState } from "react";
import { Loading } from "../../../components/pure/Loading";
import { Film } from "../types";
import { FilmFormModal } from "./FilmFormModal";

export interface FilmFormModalCreateProps {
  handleSubmit?: (film: Film) => void;
}

export const FilmFormModalCreate = React.memo<FilmFormModalCreateProps>(
  (props) => {
    const { handleSubmit } = props;
    const [isCreating, setIsCreating] = useState(false);

    const sendSuccessNotification = notification["success"];
    const sendErrorNotification = notification["error"];

    const [{ data, loading, error }, createFilm] = useAxios<Film>(
      { url: "/films", method: "POST" },
      { manual: true }
    );

    useEffect(() => {
      if (data) {
        sendSuccessNotification({ message: "Film created successfully!" });
        handleSubmit(data);
      }
    }, [data]);

    useEffect(() => {
      if (error) {
        sendErrorNotification({ message: error.message });
      }
    }, [error]);

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
  }
);
