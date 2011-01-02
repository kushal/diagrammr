<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.diagrammr.data.DiagramData" %>
<%@ page import="com.diagrammr.data.DiagramStoreManager" %>
<%@ include file="header.jsp" %> 
<div style='padding: 0 30px; float:left; max-width: 300px'>
<h1>Create and share diagrams by writing sentences!</h1>
<br/><br/>
<form action="new" method="post">
<input type="submit" style="font-size: 200%" value="Start a diagram"/>
</form>
<br/><br/><br/><br/><br/><br/>
<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
%>
Logged in as <b><%= user.getEmail() %></b>. <a href=<%= userService.createLogoutURL(request.getRequestURI()) %>>Log out</a> 
<br/><br/>
Your existing diagrams:
<%
        for (Map.Entry<String, DiagramData> diagram : DiagramStoreManager.get().getDiagrams(user.getEmail()).entrySet()) {
%>
                <br/><a href="edit?key=<%= diagram.getKey() %>"><%= diagram.getValue().getDisplayTitle() %></a>
<%
        }
    } else {
%>
Protect and re-find your diagrams by <a href=<%= userService.createLoginURL(request.getRequestURI()) %>>logging in with your Google account</a>.
<%
    }
%>
<br/><br/><br/><br/><br/><br/>
<a href="/about">About this site</a><br/>
<a href="http://twitter.com/diagrammr">Follow us on Twitter</a>
</div>
<img src="/images/intro.png" style="float: left"/>
<!--  br clear="all">
<div style="width: 500px; background-color: #eee; padding: 5px; margin: 25px; height: 110px">
<a href="http://www.amazon.com/gp/product/0061748366?ie=UTF8&tag=diagrammr-20&linkCode=as2&camp=1789&creative=9325&creativeASIN=0061748366"><img style="margin-right: 10px" border="0" align="left" src="images/51vZDcux65L._SL110_.jpg"></a>
For those who like communicating visually, we've been enjoying <a href="http://www.amazon.com/gp/product/0061748366?ie=UTF8&tag=diagrammr-20&linkCode=as2&camp=1789&creative=9325&creativeASIN=0061748366">The Visual Miscellaneum</a>. And buying it through this link will help keep Diagrammr running!
<img src="http://www.assoc-amazon.com/e/ir?t=diagrammr-20&l=as2&o=1&a=0061748366" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />
</div-->
</body>
</html>
