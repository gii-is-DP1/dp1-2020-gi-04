import React from "react";
import { loadable } from "../../utils/fn";

export interface Route {
  path: string;
  Component: (props: any) => JSX.Element;
}

export const PATHS = {
  FILM: "/films",
  INDEX: "/",
};

export const routes: Route[] = [
  {
    path: PATHS.INDEX,
    Component: loadable(() =>
      import("../../views/Landing").then((module) => ({ default: module.Landing }))
    ),
  },
  {
    path: PATHS.FILM,
    Component: loadable(() =>
      import("../../views/Films").then((module) => ({ default: module.Films }))
    ),
  },
];
