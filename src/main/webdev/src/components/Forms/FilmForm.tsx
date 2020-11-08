import React from "react";

import { Form, Input, InputNumber, DatePicker, Button } from "antd";

export interface FilmFormValues {
  name?: string;
  description?: string;
  duration?: number;
  uploadDate?: any;
}
export interface FilmFormProps {
  handleSubmit: (values: FilmFormValues) => void;
}
export const FilmForm = React.memo<FilmFormProps>((props) => {
  const { handleSubmit } = props;

  return (
    <Form
      labelCol={{ span: 4 }}
      wrapperCol={{ span: 20 }}
      onFinish={(values: FilmFormValues) => {
        const { uploadDate, ...others } = values;
        handleSubmit({
          uploadDate: uploadDate.format("YYYY-MM-DD"),
          ...others,
        });
      }}
    >
      <Form.Item label="Nombre" name="name">
        <Input />
      </Form.Item>
      <Form.Item label="Descripcion" name="description">
        <Input />
      </Form.Item>
      <Form.Item label="Duracion" name="duration">
        <InputNumber />
      </Form.Item>
      <Form.Item label="Fecha de subida" name="uploadDate">
        <DatePicker />
      </Form.Item>

      <Button type="primary" htmlType="submit">
        Enviar
      </Button>
    </Form>
  );
});
