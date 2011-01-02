<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ include file="header.jsp" %> 
<script src="http://ajax.googleapis.com/ajax/libs/mootools/1.2.3/mootools.js"></script>
<style>
#header {
  padding: 5px 15px;
}
#title {
  font-size: 200%;
}
#imgContainer {
  position: absolute;
  padding: 0 15px;
}
.sentence {
  padding: 3px 0;
}
#sentenceContainer {
  position: absolute;
  left: 450px;
  background-color: white;
  padding: 20px;
}
#actions {
  font-size: 90%;
  color: #666;
}
#content {
  margin-top: 15px;
}
#diag {
  border: 1px solid #ccc;
}
.sentenceAction {
  cursor: pointer;
  vertical-align: middle;
}
#sharingSettings {
  background: #FFF;
  position: absolute;
  z-index: 100;
  border: 5px solid black;
  left: 200px;
  top: 150px;
  padding: 20px;
  width: 300px;
  display: none;
}
</style>
<script><!--

var diagram;

function Diagrammr() {
  this.key = /[\\?&]key=([^&#]*)/.exec(window.location.href)[1];	
}

Diagrammr.start = function() {
	diagram = new Diagrammr();
	diagram.loadData({});

        $('sharingForm').addEvent('submit',
          function(e) {
            new Event(e).stop();
            diagram.saveSharing();
          });
};

function setOpacity(elt, amt) {
  elt.style.opacity = amt/10;
	elt.style.filter = 'alpha(opacity=' + amt*10 + ')';
}

function onDiagLoad() {
	document.getElementById('imgContainer').style.display = '';
	setOpacity(document.getElementById('diag'), 10);	
}

Diagrammr.prototype.sentenceToHtml = function(sentence) {
	var actions = ' <img title="delete" onclick="diagram.deleteSentence(this)" class="sentenceAction" src="/images/delete.png">';
	if (this.layout == 'COLLABORATION') {
		actions += ' <img title="move up" onclick="diagram.moveUp(this)" class="sentenceAction" src="/images/up.png">';
	  actions += ' <img title="move down" onclick="diagram.moveDown(this)" class="sentenceAction" src="/images/down.png">';
	}
  var retVal =
    '<div class=sentence>' +
      sentence +
      actions +
    '</div>';
  return retVal;
}

Diagrammr.prototype.deleteSentence = function(node) {
	this.sentenceAction(node, 'delete');
};

Diagrammr.prototype.moveUp = function(node) {
	this.sentenceAction(node, 'moveup');
};

Diagrammr.prototype.moveDown = function(node) {
	this.sentenceAction(node, 'movedown');
};

Diagrammr.prototype.sentenceAction = function(node, action) {
	var sentence = node.parentNode.firstChild.nodeValue;
	this.loadData({'cmd': action, 'sent': sentence});
};

Diagrammr.prototype.changeSize = function() {
	var width = prompt('How wide, in pixels? (currently ' + this.width + ')');
	var height = prompt('How high, in pixels? (currently ' + this.height + ')');
	this.loadData({'cmd':'resize', 'height': height, 'width': width});
};

Diagrammr.prototype.changeLayout = function() {
	this.loadData({'cmd':'changelayout'});
};

Diagrammr.prototype.embed = function() {
	alert('Just add <img src=' +
			window.location.href.replace('/edit','/png') +
			'> to your web page');
};
	
Diagrammr.prototype.loadData = function(params) {
  params['key'] = this.key;
	var jsonRequest = new Request.JSON({url: "/data", onSuccess: this.handleNewData.bind(this)})
	    .send({ data: new Hash(params).toQueryString() });
	setOpacity($('diag'), 5);
};

Diagrammr.prototype.addSentence = function(sentence) {
	this.loadData({ 'cmd' : 'add',
				          'sent' : sentence });  
};

Diagrammr.prototype.sharingSettings = function() {
  $('sharingSettings').style.display = 'block';
};

Diagrammr.prototype.cancelSharing = function() {
  $('sharingSettings').style.display = 'none';
};

Diagrammr.prototype.saveSharing = function() {
  $('sharingForm').set('send', {
     onSuccess: function() {
       alert('Settings saved!');
       $('sharingSettings').style.display = 'none';
     },
     onFailure: function() {
       alert('A problem occurred saving your new settings.');
       $('sharingSettings').style.display = 'none';
     },     
  });

  $('sharingForm').send();
};

Diagrammr.prototype.handleNewData = function(json, jsonText) {
  this.layout = json.layout;
  this.height = json.height;
  this.width = json.width;
  
	$('sentences').set('html', '');
	if (!!json.sentences) {
		var mapped = json.sentences.map(this.sentenceToHtml, this);
		$('sentences').set('html', mapped.join(''));
	}
  // TODO: should just prevent caching for embedding
  var diag = document.getElementById('diag');
  diag.height = this.height;
  diag.width = this.width;
  diag.onload = onDiagLoad;  
	diag.src = '/png?zx=' + Math.random() + '&key=' + this.key;
	$('sentence').focus();
	$('sentenceContainer').style.left = this.width + 50 + 'px';
	$('sharingButton').style.display = json.owners ? '' : 'none';
  $('editlevel').selectedIndex = json.editPermission == 'URL' ? 0 : 1; 
  $('viewlevel').selectedIndex = json.viewPermission == 'URL' ? 0 : 1; 
  $('owners').value = (json.owners) ? json.owners.join(',') : '';
  $('sharingFormKey').value = this.key;
};

Diagrammr.prototype.maybeDelete = function() {
	// TODO: Form with post
	if (confirm('Are sure you want to delete?')) {
		window.location = '/delete?key=' + this.key;
	}
};


function keyHandler(e) {
	if(e && e.which){
	  e = e;
  	characterCode = e.which;
	} else{
  	e = event;
	  characterCode = e.keyCode;
	}

	if (characterCode == 13) { 
		var newSentence = document.getElementById('sentence');
		diagram.addSentence(newSentence.value);
		newSentence.value = '';
	}
}
--></script>
<div id="content">
<div id="imgContainer" style="display:none">
<img id="diag" src=""/>
<br/><br/>
<div id="actions">
You can return to this diagram by bookmarking this page. You can also share the URL with friends:
<script>
document.write(window.location);
</script>
<br/><br/>
<a id="sharingButton" href="javascript:diagram.sharingSettings()">sharing settings</a> &nbsp;
<a href="javascript:diagram.changeSize()">change size</a> &nbsp;
<a href="javascript:diagram.changeLayout()">change layout</a> &nbsp;
<a href="javascript:diagram.maybeDelete()">delete</a> &nbsp;
<a href="javascript:diagram.embed()">embed</a> &nbsp;
</div>
</div>
<div id="sharingSettings">
<form id="sharingForm" action="/data" method="POST">
<b>Who can view this diagram?</b>
<br/>
<select id="viewlevel" name="viewlevel">
<option value="URL">Anybody with this URL</option>
<option value="OWNERS">Only the people listed below</option>
</select>
<br/><br/>
<b>Who can edit this diagram?</b><br/>
<select id="editlevel" name="editlevel">
<option value="URL">Anybody with this URL</option>
<option value="OWNERS">Only the people listed below</option>
</select>
<br/><br/>
<b>Document participants.</b>
<br/>A comma-separated list of email addresses, if you want to control access to the document.
<br/>
<textarea id="owners" rows="4" cols="35" name="owners">
</textarea>
<br/><br/>
<input type="submit" value="Save"/>
<input type="button" value="Cancel" onclick="diagram.cancelSharing()"/>
<input type="hidden" name="key" id="sharingFormKey">
<input type="hidden" name="cmd" value="changepermission"">
</form>
</div>
<div id="sentenceContainer">
<div style="color: gray; margin-bottom: 10px">Enter a sentence like "Frontend queries database" or "Amazon owns Zappos"</div>
<input type="text" id="sentence" onkeypress="keyHandler(event)" style="width: 300px"/>
<br/><br/>
<div id="sentences">
</div>
</div>
</div>
<script>
Diagrammr.start();
</script>
</body>
</html>