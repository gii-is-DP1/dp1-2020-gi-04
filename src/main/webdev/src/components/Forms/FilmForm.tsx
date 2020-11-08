import React from "react";

import {
  Form,
  Input,
  InputNumber,
  DatePicker,
  Button,
  Row,
  Divider,
} from "antd";

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
      labelCol={{ span: 6 }}
      wrapperCol={{ span: 18 }}
      onFinish={(values: FilmFormValues) => {
        const { uploadDate, ...others } = values;
        handleSubmit({
          uploadDate: uploadDate?.toISOString(),
          ...others,
        });
      }}
    >
      <Form.Item label="Name" name="name">
        <Input />
      </Form.Item>
      <Form.Item label="Description" name="description">
        <Input />
      </Form.Item>
      <Form.Item label="Duration" name="duration">
        <InputNumber />
      </Form.Item>
      <Form.Item label="Upload Date" name="uploadDate">
        <DatePicker />
      </Form.Item>
      <Divider />
      <Row justify="center">
        <Button type="primary" htmlType="submit">
          Submit
        </Button>
      </Row>
    </Form>
  );
});
