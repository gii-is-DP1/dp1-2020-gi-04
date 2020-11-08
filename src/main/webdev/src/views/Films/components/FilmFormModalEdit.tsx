import { Box } from "@material-ui/core";
import { Button, notification, Row } from "antd";
import useAxios from "axios-hooks";
import React, { useEffect, useState } from "react";
import { FilmFormValues } from "../../../components/Forms/FilmForm";
import { Loading } from "../../../components/pure/Loading";
import { Film } from "../types";
import { FilmFormModal } from "./FilmFormModal";

export interface FilmFormModalEditProps {
  handleSubmit?: (film: Film) => void;
  handleCancel?: () => void;
  film: Film | null;
}

export const FilmFormModalEdit = React.memo<FilmFormModalEditProps>((props) => {
  const { film, handleCancel, handleSubmit } = props;

  const sendSuccessNotification = notification["success"];
  const sendErrorNotification = notification["error"];

  const [{ data, loading, error }, editFilm] = useAxios<Film>(
    { method: "PUT" },
    { manual: true }
  );

  useEffect(() => {
    if (data) {
      sendSuccessNotification({ message: "Film updated successfully!" });
      handleSubmit(data);
    }
  }, [data]);

  useEffect(() => {
    if (error) {
      sendErrorNotification({ message: error.message });
    }
  }, [error]);

  if (loading) return <Loading />;

  const handleEditFilm = (values: FilmFormValues) => {
    const id = film ? film.id : "";

    editFilm({
      data: values,
      url: `/films/${id}`,
    });
  };

  return (
    <Box>
      <FilmFormModal
        visible={!!film}
        title="Edit film"
        handleCancel={handleCancel}
        handleSubmit={handleEditFilm}
        initialValues={film}
      />
    </Box>
  );
});
