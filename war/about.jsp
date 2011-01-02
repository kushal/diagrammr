<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.diagrammr.data.DiagramData" %>
<%@ page import="com.diagrammr.data.DiagramStoreManager" %>
<%@ include file="header.jsp" %>

<div style="padding: 20px; width: 300px">
<b>What's this for?</b><br/>
<a href="http://www.kushaldave.com">Kushal</a> wrote Diagrammr to make it easier for people to communicate diagrams. You can learn more in
<a href="http://kushalhisblog.blogspot.com/2009/08/diagrammr.html">this blog post</a>.
<br/><br/>

<b>Can I put more than one word in a box? Or keep my capitalization?</b><br/>
Yes, just put it in quotes, such as
<pre>
"Frontend server" talks to "backend server"
</pre>
<br/>

<b>How can I provide feedback?</b><br/>
Please provide feedback at <a href="http://diagrammr.uservoice.com">diagrammr.uservoice.com</a> or <a href="http://twitter.com/diagrammr">via Twitter</a>.
<br/><br/>

<b>How secure is my data?</b><br/>
Although we don't offer HTTPS, your data is secure in most other ways. All diagrams live in Google App Engine servers and, if you make a diagram private, is only visible to the participants.
The developers (that's just me for now) have access to this data via their Google accounts, but would only look at it if they received a bug report. 
<br/><br/>
<b>I love this thing! How can I help?</b><br/>
For now, mostly by spreading the word! The code is also open source; you can find both <a href="https://github.com/kushal/diagram-server">the server</a> (which renders diagrams) and <a href="https://github.com/kushal/diagrammr">the App Engine app</a> (which acts as the frontend) on <a href="https://github.com/">GitHub</a>, so direct coding help works too. I'm also experimenting with using Amazon Affiliates to defray the small hosting cost.
At the moment, I'm recommending <a href="http://www.amazon.com/gp/product/0061748366?ie=UTF8&tag=diagrammr-20&linkCode=as2&camp=1789&creative=9325&creativeASIN=0061748366">The Visual Miscellaneum</a>, on the assumption that
Diagrammr users are probably enthusiastic about communicating visually.

</div>
</body>
</html>
