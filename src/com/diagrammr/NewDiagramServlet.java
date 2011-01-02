package com.diagrammr;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;

import com.diagrammr.data.DiagramStoreManager;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Generate a random ID and redirect to an editing UI for that diagram.
 * Only accept POST to keep robots from creating new diagrams.
 */
@SuppressWarnings("serial")
public class NewDiagramServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String newId = "d" + RandomStringUtils.randomAlphanumeric(10);
    
    User user = UserServiceFactory.getUserService().getCurrentUser();
    String email = user != null ? user.getEmail() : null;
    
    DiagramStoreManager.get().createDiagram(newId, email);
    resp.sendRedirect("/edit?key=" + newId);
  }
}
