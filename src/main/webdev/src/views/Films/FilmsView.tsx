import React, { useCallback, useState } from "react";

import { RouteComponentProps } from "@reach/router";
import { Divider } from "antd";

import Title from "antd/lib/typography/Title";
import { Box } from "@material-ui/core";
import { FilmFormModalCreate } from "./components/FilmFormModalCreate";
import { FilmTableDisplay } from "./components/FilmTableDisplay";
import { FilmFormModalEdit } from "./components/FilmFormModalEdit";

export const FilmsView = React.memo<RouteComponentProps>((props) => {
  const [forceFetch, setForceFetch] = useState(0);
  const [editingFilm, setEditingFilm] = useState(null);

  const handleEditCancel = useCallback(() => setEditingFilm(null), []);
  const handleEditSubmit = useCallback(() => {
    setEditingFilm(null);
    setForceFetch((forceFetch) => forceFetch + 1);
  }, []);
  const handleCreateSubmit = useCallback(() => {
    setForceFetch((forceFetch) => forceFetch + 1);
  }, []);
  
  return (
    <Box>
      <Title level={3}>Films</Title>
      <FilmTableDisplay forceFetch={forceFetch} handleEdit={setEditingFilm} />
      <Divider />
      <FilmFormModalCreate handleSubmit={handleCreateSubmit} />
      <FilmFormModalEdit
        handleCancel={handleEditCancel}
        handleSubmit={handleEditSubmit}
        film={editingFilm}
      />
    </Box>
  );
});
