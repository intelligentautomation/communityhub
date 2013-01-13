<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Community</title>

    <script type="text/javascript" charset="utf-8">
      <!--

	  $(document).ready(function() {

	      $("ul.clickables").each(function() {
		  $(this).find("li").click(function() {
		      var link = $(this).find("a");
		      if (link.length > 0)
			  window.location = link[0]
		  });
	      });

	  });

	-->
    </script>


  </head>
  <body>
    
    <p class="lead">Groups</p>

    <sec:ifNotLoggedIn>
      <div class="alert alert-info">
	<i class="icon-info-sign"></i>
	Log in to create and configure groups
      </div>
    </sec:ifNotLoggedIn>

    <div class="container-fluid">
      
      <div class="row-fluid">
	<div class="span3">

	  <sec:ifLoggedIn>	  
	  <a class="btn btn-link" href="${createLink(controller: 'community', action: 'create')}"><i class="icon-plus"></i>&nbsp;Group</a>
	  </sec:ifLoggedIn>

          <div class="side-bar">
	    
	    <ul class="nav nav-list">
	      <%-- list groups --%>
	      <g:if test="${groups}">
		<li class="nav-header">Community Groups</li>
		<g:each in="${groups}" var="group">
		  <li class="<g:if test='${group.id == id}'>active</g:if>">
		    <g:link 
		       controller="community" action="index" id="${group.id}">${group.name}</g:link>
		  </li>
		</g:each>
	      </g:if>
	    </ul>
	    
          </div>
	  
	</div>
	<div class="span9">
	  
          <g:if test="${group}">
	    
	    <p class="lead">
	      <g:link controller="community" title="Alert feed" 
		      action="feed" id="${group.id}"><g:img dir="images" file="rss.png" style="float: right;" /></g:link>
	      <g:link title="Report" controller="alert" 
		      action="report" id="${group.id}"><g:img dir="images" file="document.png" class="pull-right" style="padding: 0px 10px;" /></g:link>
	      ${group.name} 
	    </p>

	    <p>
	      <strong>
		<g:if test="${group.description == null}">
		  <div class="alert alert-warning">
		    <em>No description available</em>
		  </div>
		</g:if>
		<g:else>
		  <div class="alert alert-success">
		    ${group.description}
		  </div>
		</g:else>
	      </strong>
	    </p>
	    
	    <h5>Most recent alerts <g:link controller="alert" action="view" id="${group.id}">(view more alerts)</g:link></h5>
	    <g:if test="${alerts}">
	      <ul class="alerts clickables">
		<g:each var="alert" in="${alerts}">
		  
		  <li>
		    <img src="${resource(dir: 'images/', file: 'exclamation.png') }" />
		    <strong>${alert.getTypePretty()}</strong>
		    <br />
		    At sensor offering ${alert.offering}, 
		    recorded:  
		    <g:formatDate format="yyyy-MM-dd kk:mm" date="${alert.timestamp}"/>
		    (<a href="/communityhub/alert/id/${alert.id}/">details</a>)
		  </li>
		</g:each>
	      </ul>
	    </g:if>
	    <g:else>
	      <em>There are no alerts for this group yet.</em>
	    </g:else>
	    
	  </g:if>
	  <g:else>
	    <em>No group (or an incorrect group) has been selected</em>
	  </g:else>

	  <h5>Reports</h5>

	  <g:link title="Report" controller="alert" 
		  action="report" id="${group.id}">View group reports</g:link>	  

	  <h5>Configure group</h5>
	  <g:isGroupAdmin group="${group}">
	    <g:link title="Configure" controller="community" action="rules"
		    id="${group.id}">Configure group</g:link> (e.g. add or remove rules, or delete group)
	  </g:isGroupAdmin> 
	  <g:isNotGroupAdmin group="${group}">
	    <div class="alert alert-info">
	      <i class="icon-info-sign"></i>
	      Log in as group admin to configure the group
	    </div>
	  </g:isNotGroupAdmin>
	  
	</div>

      </div>

    </div>
    
  </body>
</html>


