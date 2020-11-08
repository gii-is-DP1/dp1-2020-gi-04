import React, { useEffect, useMemo } from "react";

import {
  Form,
  Input,
  InputNumber,
  DatePicker,
  Button,
  Row,
  Divider,
} from "antd";
import { Film } from "../../views/Films/types";
import moment from "moment";
export interface FilmFormValues {
  name?: string;
  description?: string;
  duration?: number;
  uploadDate?: any;
}
export interface FilmFormProps {
  handleSubmit: (values: FilmFormValues) => void;
  initialValues?: Partial<Film>;
}
export const FilmForm = React.memo<FilmFormProps>((props) => {
  const { initialValues, handleSubmit } = props;

  const [form] = Form.useForm();

  const parsedInitialValues = useMemo(() => {
    if (!initialValues) return {};
    const { uploadDate } = initialValues;
    if (uploadDate) {
      return { ...initialValues, uploadDate: moment(uploadDate) };
    }
    return initialValues;
  }, [initialValues, form]);

  useEffect(() => form.resetFields(), [initialValues]);

  return (
    <Form
      form={form}
      initialValues={parsedInitialValues}
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
