package com.diagrammr;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import com.diagrammr.data.DiagramData;
import com.diagrammr.data.DiagramStoreManager;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Given a diagram key, look up a diagram and render it to a PNG.
 */
@SuppressWarnings("serial")
public class PngServlet extends HttpServlet {

  //private static final String SERVER = "http://localhost:7070/render";
  private static final String SERVER = "http://kushaldave.com/render";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.setContentType("image/png");

    try {
      User user = UserServiceFactory.getUserService().getCurrentUser();
      String email = user != null ? user.getEmail() : null;

      DiagramData data = DiagramStoreManager.get().getDiagram(
          req.getParameter("key"), email);

      // Post to render server
      String json = URLEncoder.encode(data.toJSON(), "UTF-8");

      HTTPRequest request = new HTTPRequest(new URL(SERVER), HTTPMethod.POST);
      request.setPayload(("json=" + json).getBytes("UTF-8"));
      HTTPResponse response = URLFetchServiceFactory.getURLFetchService()
          .fetch(request);
      // TODO(kushal): Stream this?
      resp.getOutputStream().write(response.getContent());
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
