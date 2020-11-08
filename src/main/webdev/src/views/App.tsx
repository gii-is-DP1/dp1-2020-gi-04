import React from "react";
import { AppLayout } from "../components/Layout/AppLayout";
import { Loading } from "../components/pure/Loading";
import { AppRouter } from "../components/Router/AppRouter";
import { routes } from "../components/Router/routes";
import 'antd/dist/antd.css';

export const App = React.memo(() => {
  return (
    <React.Suspense fallback={<Loading />}>
      <AppLayout>
        <AppRouter routes={routes} />
      </AppLayout>
    </React.Suspense>
  );
});
