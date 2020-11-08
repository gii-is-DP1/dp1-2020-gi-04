import React from "react";

import { Film } from "../../views/Films/types";
import { Descriptions } from "antd";
import { DescriptionsProps } from "antd/lib/descriptions";

export interface FilmDescriptionProps extends DescriptionsProps {
  film: Film;
}
export const FilmDescription = React.memo<FilmDescriptionProps>((props) => {
  const { film, ...rest } = props;

  return (
    <Descriptions
      bordered
      style={{ marginTop: "2rem" }}
      column={{ xxl: 1, xl: 1, lg: 1, md: 1, sm: 1, xs: 1 }}
      {...rest}
    >
      <Descriptions.Item label="Nombre">{film.name}</Descriptions.Item>
      <Descriptions.Item label="Descripcion">
        {film.description}
      </Descriptions.Item>
      <Descriptions.Item label="Duracion">{film.duration}</Descriptions.Item>
      <Descriptions.Item label="Fecha de subida">
        {film.uploadDate}
      </Descriptions.Item>
    </Descriptions>
  );
});
