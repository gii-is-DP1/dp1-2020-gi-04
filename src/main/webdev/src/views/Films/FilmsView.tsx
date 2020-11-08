import React, { useState } from "react";

import { RouteComponentProps } from "@reach/router";
import { useGet, usePost } from "../../utils/hooks";
import { Loading } from "../../components/pure/Loading";
import { Card, Col, Divider, Row } from "antd";
import { FilmForm } from "../../components/Forms/FilmForm";
import { FilmDescription } from "../../components/pure/FilmDescription";
import { get } from "lodash";
import { Film } from "./types";
import Title from "antd/lib/typography/Title";

export const FilmsView = React.memo<RouteComponentProps>((props) => {
  const { data, loading } = useGet("http://localhost:8080/api/films");
  const [values, setValues] = useState(null);
  const { data: uploadResponse, loading: uploadLoading, post } = usePost(
    "http://localhost:8080/api/films"
  );

  const films: Film[] = get(data, "data", []);
  console.log(films.length);
  return (
    <div>
      {loading && <Loading />}
      <Title level={3}>Peliculas</Title>
      <Card>
        {films.length === 0 ? (
          <h1>No hay ninguna pelicula</h1>
        ) : (
          films.map((film, index) => (
            <Row justify="center" key={"film-description-" + index}>
              <Col span={12}>
                <FilmDescription style={{ marginBottom: "20px" }} film={film} />
              </Col>
            </Row>
          ))
        )}
      </Card>
      <Divider />
      <FilmForm
        handleSubmit={(values) => {
          console.log(values);
        
          post(values);
        }}
      />
      {uploadResponse && JSON.stringify(uploadResponse.data)}
    </div>
  );
});
