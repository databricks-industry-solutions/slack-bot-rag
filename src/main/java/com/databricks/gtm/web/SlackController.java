package com.databricks.gtm.web;

import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/slack/events")
public class SlackController extends SlackAppServlet {
    public SlackController(App app) {
        super(app);
    }
}
