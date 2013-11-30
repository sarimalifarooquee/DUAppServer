package com.du.appserver;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.search.DateUtil;

@SuppressWarnings("serial")
public class DUAppServerServlet extends HttpServlet {
	public void init() {
		Scrapper.InitCache();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		/**
		 * Set the content to expire in three hours.
		 */
		Calendar inThreeHours = Calendar.getInstance();
		inThreeHours.add(Calendar.HOUR_OF_DAY, 3);

		resp.setContentType("text/html");
		resp.setHeader("cache-control", "public, max-age=3600");
		resp.setHeader("Expires",DateUtil.formatDateTime(inThreeHours.getTime()));
		resp.getWriter().println(Scrapper.scrap(req.getParameter("sec"),
						req.getParameter("url")));
	}
}