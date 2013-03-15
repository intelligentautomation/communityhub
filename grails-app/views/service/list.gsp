<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Services</title>


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

    <%-- Only admins can add service --%> 
    <sec:access expression="hasRole('ROLE_ADMIN')">
      <p class="pull-right">
	<a class="btn btn-link" href="${createLink(controller: 'service', action: 'create')}"><i class="icon-plus"></i>&nbsp;Service</a>
      </p>
    </sec:access>
    
    <p class="lead">Services</p>

    <sec:noAccess expression="hasRole('ROLE_ADMIN')">
      <div class="alert alert-info">
	<i class="icon-info-sign"></i>
	Log in is an administrator to add services.
      </div>
    </sec:noAccess>

    <g:if test="${services.getTotalCount()}">

      <p>The following are the services that have been
      added. Select one to view more details</p>

      <g:paginate prev="Prev" next="Next" 
		  controller="service" action="list" 
		  total="${services.getTotalCount()}" />

      <table class="table table-striped">
	<thead>
	  <tr>
	    <th>Status</th>
	    <th>Title</th>
	    <th>Endpoint</th>
	    <th>Added</th>
	  </tr>
	</thead>
	<tbody>
	  <g:each var="service" in="${services}">
	    <tr>
	      <td>
		<g:if test="${service.alive}">
		  <g:img dir="images/fugue" file="tick-24.png" title="Service OK"/>
		</g:if>
		<g:else>
		  <g:img dir="images/fugue" file="exclamation-24.png" 
			 title="Service seems to be down" />
		</g:else>
	      </td>
	      <td>
		<a href="${createLink(action: 'view', id: service.id)}">${service.title}</a>
	      </td>
	      <td>
		<a href="${service.endpoint}" 
		   title="${service.title}">${service.endpoint}</a>
	      </td>
	      <td>
		<g:formatDate format="yyyy-MM-dd HH:mm" date="${service.dateCreated}" />
	      </td>
	    </tr>
	  </g:each>
	</tbody>
      </table>

    </g:if>
    <g:else>
      <p><em>There are currently no services defined</em></p>
    </g:else>
	
</body>
</html>


