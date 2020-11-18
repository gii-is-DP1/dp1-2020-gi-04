import React from "react";

import { Layout, Breadcrumb, Card, Image, Row, Col } from "antd";
import { Box } from "@material-ui/core";
import logo from "../../assets/logo.png";
import { Link } from "@reach/router";
import Title from "antd/lib/typography/Title";

const { Header, Content, Footer } = Layout;

export const AppLayout = React.memo((props) => {
  const { children } = props;

  return (
    <Layout className="layout">
      <Header>
        <Link to="/">
          <Row justify="start" wrap={false}>
            <Col>
              <Image src={logo} preview={false} width="54px" height="54px" />
            </Col>
            <Col style={{ marginLeft: "15px" }}>
              <Title level={3} style={{ color: "white", lineHeight: 2.5 }}>
                Home
              </Title>
            </Col>
          </Row>
        </Link>
      </Header>
      <Content style={{ padding: "0 1%" }}>
        <Breadcrumb style={{ margin: "16px 0" }}>
          <Breadcrumb.Item>Home</Breadcrumb.Item>
          <Breadcrumb.Item>List</Breadcrumb.Item>
          <Breadcrumb.Item>App</Breadcrumb.Item>
        </Breadcrumb>
        <Box>
          <Card>{children}</Card>
        </Box>
      </Content>
      <Footer style={{ textAlign: "center" }}></Footer>
    </Layout>
  );
});
