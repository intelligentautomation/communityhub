<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Alerts</title>


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
    
    <p class="lead">
      Alerts
    </p>
    
    <div class="row">
      <div class="span3">
        
        <div class="side-bar">
          
          <ul class="nav nav-list">
	    <%-- list groups --%>
	    <g:if test="${groups}">
              <li class="nav-header">Community Groups</li>
	      <g:each in="${groups}" var="group">
		<li class="<g:if test='${group.id == id}'>active</g:if>">
		  <g:link 
		     controller="alert" action="view" id="${group.id}">${group.name}</g:link>
		</li>
	      </g:each>
	    </g:if>
          </ul>
          
        </div>
	
      </div>
      <div class="span9">
	
	<g:if test="${groups}">
	  <g:if test="${group}">
	  
	    <p class="lead">
	      <g:link title="Alert feed" controller="community" 
		      action="feed" id="${group.id}"><g:img dir="images" file="rss.png" class="pull-right" /></g:link>
	      <g:link title="Report" controller="alert" 
		      action="report" id="${group.id}"><g:img dir="images" file="document.png" class="pull-right" style="padding: 0px 10px;" /></g:link>
	      ${group.name} 
	    </p>
	    
	    <%-- only show pagination controls if needed --%> 
	    <g:if test="${paginator.needed()}">
	      <div class="pagination">
		<ul>
		  <li <g:if test="${paginator.isFirstPage()}">class='disabled'</g:if>>
		    <g:link action='view' id='${id}' params='[page: "${paginator.prevPage()}"]'>Prev</g:link>
		  </li>
		  <li class="active">
		    <g:link 
		       action='view' id='${id}'
		       params='[page: "${paginator.nextPage()}"]'>${paginator.curPage}/${paginator.numPages}</g:link>
		  </li>
		  <li <g:if test="${paginator.isLastPage()}">class='disabled'</g:if>>
		    <g:link 
		       action='view' id='${id}' 
		       params='[page: "${paginator.nextPage()}"]'>Next</g:link>
		  </li>
		</ul>
	      </div>
	    </g:if>
	    
	    <g:if test="${paginator.count() > 0}">
	      <ul class="alerts clickables">
		<g:set var="counter" value="${0}" />
		<g:each var="alert" in="${paginator.objects()}">
		  
		  <li>
		    <img src="${resource(dir: 'images/', file: 'exclamation.png') }" />
		    <strong>${alert.getTypePretty()}</strong>
		    <br />
		    At sensor offering ${alert.offering}, 
		    recorded:  
		    <g:formatDate format="yyyy-MM-dd kk:mm" date="${alert.timestamp}"/>
		    (<a href="/communityhub/alert/id/${alert.id}/">details</a>)
		  </li>
		  <g:set var="counter" value="${counter + 1}" />
		</g:each>
	      </ul>
	    </g:if>
	    <g:else>
	      <p><i>No alerts were found in this group.</i></p>
	    </g:else>
	  </g:if>
	  <g:else>
	    <em>No group (or an incorrect group) has been selected</em>
	  </g:else>
	</g:if>
	<g:else>
	  <em>There are no groups, and no alerts, yet.</em>
	</g:else>
	
      </div>
    </div>

  </body>
</html>


