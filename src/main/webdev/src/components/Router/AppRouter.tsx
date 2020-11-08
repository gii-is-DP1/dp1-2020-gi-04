import { Router } from "@reach/router";
import React from "react";
import { AppLayout } from "../Layout/AppLayout";
import { Route } from "./routes";

export interface RouterProps {
  routes: Route[];
}
export const AppRouter = React.memo<RouterProps>((props) => {
  const { routes } = props;
  return (
    <Router>
      {routes.map(({ Component, ...rest }) => (
        <Component key={"route-" + rest.path} {...rest} />
      ))}
    </Router>
  );
});
