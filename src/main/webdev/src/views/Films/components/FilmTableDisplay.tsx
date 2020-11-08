import React, { useEffect } from "react";

import { FilmTable } from "../../../components/pure/FilmTable";

import useAxios from "axios-hooks";
import { notification } from "antd";
import { Film } from "../types";

export interface FilmTableDisplayProps {
  forceFetch: number;
  handleEdit?: (film: Film) => void;
  handleDelete?: (film: Film) => void;
}
export const FilmTableDisplay = React.memo<FilmTableDisplayProps>((props) => {
  const { forceFetch, handleDelete, handleEdit } = props;

  const [{ data, loading, error }, refetch] = useAxios("/films");
  const sendErrorNotification = notification["error"];
  useEffect(() => {
    if (forceFetch) {
      refetch();
    }
  }, [forceFetch]);

  useEffect(() => {
    if (error) {
      sendErrorNotification({
        message: error.message,
      });
    }
  }, [error]);

  return (
    <FilmTable
      films={data}
      style={{ overflowX: "auto" }}
      loading={loading}
      handleEdit={handleEdit}
      handleDelete={handleDelete}
    />
  );
});
