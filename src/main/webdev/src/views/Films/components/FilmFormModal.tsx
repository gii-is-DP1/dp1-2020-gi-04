import React from "react";

import { Modal } from "antd";
import Title from "antd/lib/typography/Title";
import { FilmForm, FilmFormProps, FilmFormValues } from "../../../components/Forms/FilmForm";

export interface FilmFormModalProps extends FilmFormProps{
  visible: boolean;
  title: string;
  handleCancel: () => void;
}

export const FilmFormModal = React.memo<FilmFormModalProps>((props) => {
  const { visible, title, handleCancel, ...formProps } = props;
  return (
    <Modal
      visible={visible}
      footer={null}
      onCancel={handleCancel}
      title={title}
    >
      <FilmForm {...formProps} />
    </Modal>
  );
});
