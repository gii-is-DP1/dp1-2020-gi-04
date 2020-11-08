import React from "react";

import { RouteComponentProps } from "@reach/router";
import { Divider } from "antd";

import Title from "antd/lib/typography/Title";
import { Box } from "@material-ui/core";
import { FilmFormModalCreate } from "./components/FilmFormModalCreate";
import { FilmTableDisplay } from "./components/FilmTableDisplay";

export const FilmsView = React.memo<RouteComponentProps>((props) => {
  return (
    <Box>
      <Title level={3}>Films</Title>
      <FilmTableDisplay forceFetch={0}/>
      <Divider />
      <FilmFormModalCreate />
    </Box>
  );
});
