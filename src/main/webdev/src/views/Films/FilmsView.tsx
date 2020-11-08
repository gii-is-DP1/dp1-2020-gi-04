import React, { useState } from "react";

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
  return (
    <Box>
      <Title level={3}>Films</Title>
      <FilmTableDisplay forceFetch={forceFetch} handleEdit={setEditingFilm} />
      <Divider />
      <FilmFormModalCreate handleSubmit={() => setForceFetch(forceFetch + 1)} />
      <FilmFormModalEdit
        handleCancel={() => setEditingFilm(null)}
        handleSubmit={() => {
          setEditingFilm(null);
          setForceFetch(forceFetch + 1);
        }}
        film={editingFilm}
      />
    </Box>
  );
});
