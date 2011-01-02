package com.diagrammr;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import com.diagrammr.data.DiagramData;
import com.diagrammr.data.DiagramStoreManager;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class DataServlet extends HttpServlet {

  private static final int MAX_SENTENCE_LENGTH = 300;
  private static final int MAX_DIAGRAM_LENGTH = 100;
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");

    User user = UserServiceFactory.getUserService().getCurrentUser();
    String email = user != null ? user.getEmail() : null;
    
    String sentence = req.getParameter("sent");
    if (sentence != null) {
      sentence = sentence.trim();
    }
    String command = req.getParameter("cmd");
    String key = req.getParameter("key");
    DiagramData data = DiagramStoreManager.get().getDiagram(key, email);
    if ("add".equals(command)) {
      if (sentence.length() > MAX_SENTENCE_LENGTH ||
          data.getSentences().size() > MAX_DIAGRAM_LENGTH) {
        return;
      }
      data.addSentence(sentence);
    } else if ("delete".equals(command)) {
      data.deleteSentence(sentence);
    } else if ("moveup".equals(command)) {
      data.moveUp(sentence);
    } else if ("movedown".equals(command)) {
      data.moveDown(sentence);
    } else if ("changelayout".equals(command)) {
      data.changeLayout();
    } else if ("resize".equals(command)) {
      int width = Integer.parseInt(req.getParameter("width"));
      int height = Integer.parseInt(req.getParameter("height"));
      data.setSize(new int[] { width, height });
    } else if ("changepermission".equals(command)) {
      data.setOwners(Arrays.asList(req.getParameter("owners").split(",")));
      data.setViewPermission(DiagramData.Permission.valueOf(req
          .getParameter("viewlevel")));
      data.setEditPermission(DiagramData.Permission.valueOf(req
          .getParameter("editlevel")));
    }
    if (command != null) {
      DiagramStoreManager.get().updateDiagram(key, email, data);
    }
    try {
      resp.getWriter().write(data.toJSON(true));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
