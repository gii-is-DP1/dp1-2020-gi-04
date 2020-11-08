import React from "react";

import { Modal } from "antd";
import Title from "antd/lib/typography/Title";
import { FilmForm, FilmFormValues } from "../../../components/Forms/FilmForm";

export interface FilmFormModalProps {
  visible: boolean;
  title: string;
  handleCancel: () => void;
  handleSubmit: (values: FilmFormValues) => void;
}

export const FilmFormModal = React.memo<FilmFormModalProps>((props) => {
  const { visible, title, handleCancel, handleSubmit } = props;
  return (
    <Modal
      visible={visible}
      footer={null}
      onCancel={handleCancel}
      title={title}
    >
      <FilmForm handleSubmit={handleSubmit} />
    </Modal>
  );
});
