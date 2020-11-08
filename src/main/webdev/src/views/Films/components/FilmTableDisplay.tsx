import React, { useEffect } from "react";

import { FilmTable } from "../../../components/pure/FilmTable";

import useAxios from "axios-hooks";
import { get } from "lodash";
import { Film } from "../types";
import { notification } from "antd";

export interface FilmTableDisplayProps {
  forceFetch: number;
}
export const FilmTableDisplay = React.memo<FilmTableDisplayProps>((props) => {
  const { forceFetch } = props;

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
    <FilmTable films={data} style={{ overflowX: "auto" }} loading={loading} />
  );
});
