import React from "react";
import { Spin, Card } from "antd";

export const Loading = () => (
  <Card
    style={{
      minHeight: "250px",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
    }}
  >
    <Spin size="large" />
  </Card>
);
