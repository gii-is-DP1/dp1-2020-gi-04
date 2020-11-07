import React from "react";
import { Loading } from "../components/pure/Loading";
import { AppRouter } from "../components/Router/AppRouter";
import { routes } from "../components/Router/routes";

export const App = React.memo(() => {
  return (
    <React.Suspense fallback={<Loading />}>
      <AppRouter routes={routes} />
    </React.Suspense>
  );
});
