import React from "react";

import { Link } from "@reach/router";
import { Layout, Menu, Breadcrumb, Card } from "antd";
import { Box } from "@material-ui/core";
const logo = require("../../../public/logo.png");
const { Header, Content, Footer } = Layout;

export const AppLayout = React.memo((props) => {
  const { children } = props;

  return (
    <Layout className="layout">
      <Header>
        <div className="logo" />
        <Menu theme="dark" mode="horizontal" defaultSelectedKeys={["2"]}>
          <Menu.Item key="1">nav 1</Menu.Item>
          <Menu.Item key="2">nav 2</Menu.Item>
          <Menu.Item key="3">nav 3</Menu.Item>
        </Menu>
      </Header>
      <Content style={{ padding: "0 50px" }}>
        <Breadcrumb style={{ margin: "16px 0" }}>
          <Breadcrumb.Item>Home</Breadcrumb.Item>
          <Breadcrumb.Item>List</Breadcrumb.Item>
          <Breadcrumb.Item>App</Breadcrumb.Item>
        </Breadcrumb>
        <Box mx={4} my={5}>
          <Card>{children}</Card>
        </Box>
      </Content>
      <Footer style={{ textAlign: "center" }}></Footer>
    </Layout>
  );
});
