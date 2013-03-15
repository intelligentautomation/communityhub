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
    
    <%-- action items --%>
    <div class="pull-right">
      <g:link title="Edit group" controller="community" 
	      action="edit" id="${group.id}"><g:img dir="images/fugue" file="pencil-24.png" style="padding: 0px 0px;" /></g:link>
      <g:link title="Configure group" controller="community" 
	      action="config" id="${group.id}"><g:img dir="images/fugue" file="gear-24.png" style="padding: 0px 10px;" /></g:link>
      <g:img dir="images" file="vertical-divider.png" />
      <g:link title="Report" controller="community" 
	      action="report" id="${group.id}"><g:img dir="images/fugue" file="table-sheet-24.png" style="padding: 0px 10px;" /></g:link>
      <g:link controller="community" title="Alert feed" 
	      action="feed" id="${group.id}"><g:img dir="images/fugue" file="feed-24.png" /></g:link>
    </div>
      
    <p class="lead">${group.name}</p>
      
    <p>
      <strong>
	<g:if test="${group.description == null}">
	  <div class="alert alert-warning">
	    <em>No description available</em>
	  </div>
	</g:if>
	<g:else>
	  <div class="alert alert-info">
	    ${group.description}
	  </div>
	</g:else>
      </strong>
    </p>

    <g:if test="${alerts.getTotalCount()}">
      
      <p>The following alerts have been generated for this group</p>

      <g:paginate prev="Prev" next="Next" 
		  controller="community" action="view" id="${group.id}" 
		  total="${alerts.getTotalCount()}" />

      <table class="table table-striped">
	<thead>
	  <tr>
	    <th></th>
	    <th>Type</th>
	    <th>Date</th>
	    <th>Details</th>
	    <th>Service</th>
	  </tr>
	</thead>
	<tbody>
	  <g:each var="alert" in="${alerts}">
	    <tr>
	      <td>
		<img src="${resource(dir: 'images/fugue', file: 'exclamation-24.png') }" />
		</td>
	      <td>
		${alert.getTypePretty()}
	      </td>
	      <td>
		<g:formatDate format="yyyy-MM-dd HH:mm" 
			      date="${alert.dateCreated}" />
	      </td>
	      <td>
		<g:link controller="alert" action="view" id="${alert.id}">
		${alert.detail.intro(40)}
		</g:link>
	      <td>
		<g:link 
		   href="${alert.service.endpoint}">${alert.service.endpoint}</g:link>
	      </td>
	    </tr>
	  </g:each>
	</tbody>
      </table>

      <%--
    <ul class="alerts clickables">
      <g:each var="alert" in="${alerts}">
	<li>
	  <img src="${resource(dir: 'images/fugue', file: 'exclamation-24.png') }" />
	  <strong>${alert.getTypePretty()}</strong>
	  <br />

	</li>
      </g:each>
    </ul>
    --%>
      
    </g:if>
    <g:else>
      <p><em>No alerts have been generated for this group yet (did you
      <g:link controller="community" action="config"
      id="${group.id}">configure</g:link> the group for
      alerts?</em></p>
    </g:else>
	  
      
  </body>
</html>


