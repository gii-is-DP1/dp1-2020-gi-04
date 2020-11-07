import React from "react";
import { Link } from "@reach/router";

export const Landing = React.memo((props) => {
  return (
    <div>
      <h1>Landing page</h1>
      <Link to="/films">Peliculas</Link>
    </div>
  );
});
