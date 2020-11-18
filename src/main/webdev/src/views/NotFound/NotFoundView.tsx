import React from "react";

import { Box } from "@material-ui/core";
import { RouteComponentProps } from "@reach/router";
import { Empty } from "antd";


export const NotFoundView = React.memo<RouteComponentProps>((props) => {
    return <Box>
        <Empty description="Page was not found." />
    </Box>
})