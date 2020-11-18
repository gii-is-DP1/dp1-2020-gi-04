import React from "react";

import { Button, Col, Row, Table } from "antd";
import { Film } from "../../views/Films/types";
import { ColumnsType, TableProps } from "antd/lib/table";
import moment from "moment";

export interface FilmTableProps extends TableProps<Film> {
  films: Film[];
  handleEdit?: (film: Film) => void;
  handleDelete?: (film: Film) => void;
}
export const FilmTable = React.memo<FilmTableProps>((props) => {
  const { films, handleEdit, handleDelete, ...rest } = props;
  const columns: ColumnsType<Film> = [
    {
      title: "Id",
      dataIndex: "id",
      key: "id",
      render: (id: number) => <span>{id}</span>,
    },
    {
      title: "Name",
      dataIndex: "name",
      key: "name",
      render: (name?: string) => <span>{name}</span>,
    },
    {
      title: "Description",
      dataIndex: "description",
      key: "description",
      render: (description?: string) => <span>{description}</span>,
    },
    {
      title: "Duration",
      dataIndex: "duration",
      key: "duration",
      render: (duration?: string) => <span>{duration}</span>,
    },
    {
      title: "Upload Date",
      dataIndex: "uploadDate",
      key: "uploadDate",
      render: (uploadDate?: string) => (
        <span>{moment(uploadDate).format("YYYY-MM-DD")}</span>
      ),
    },
    {
      title: "Actions",
      dataIndex: "id",
      key: "actions",
      render: (id: number, film: Film) => (
        <Row justify="space-around" style={{ flexWrap: "nowrap" }}>
          <Col>
            <Button
              onClick={() => {
                if (handleEdit) handleEdit(film);
              }}
              type="primary"
            >
              Edit
            </Button>
          </Col>
          <Col>
            <Button onClick={() => {
                if (handleDelete) handleDelete(film);
              }} danger>
              Delete
            </Button>
          </Col>
        </Row>
      ),
    },
  ];
  return <Table dataSource={films} columns={columns} rowKey={(film) => film.id} {...rest} />;
});
