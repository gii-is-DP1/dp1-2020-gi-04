import React from "react";
import { Loading } from "../components/pure/Loading";

export const loadable = (loader: () => Promise<any>) => {
  const LazyLoad = React.lazy(loader);
  return (props: any) => (
    <React.Suspense fallback={<Loading />}>
      <LazyLoad {...props} />
    </React.Suspense>
  );
};
