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
    
    <p class="lead">Services</p>
    
    <%-- Only admins can add service --%> 
    <sec:access expression="hasRole('ROLE_ADMIN')">
      <p>
	<a class="btn btn-link" href="${createLink(controller: 'service', action: 'add')}"><i class="icon-plus"></i>&nbsp;Service</a>
      </p>
    </sec:access>

    <sec:noAccess expression="hasRole('ROLE_ADMIN')">
      <div class="alert alert-info">
	<i class="icon-info-sign"></i>
	Log in is an administrator to add services.
      </div>
    </sec:noAccess>
    
    <%-- only show pagination controls if needed --%> 
    <g:if test="${paginator.needed()}">
      <div class="pagination">
	<ul>
	  <li <g:if test="${paginator.isFirstPage()}">class='disabled'</g:if>>
	    <g:link params='[page: "${paginator.prevPage()}"]'>Prev</g:link>
	  </li>
	  <li class="active">
	    <g:link params='[page: "${paginator.nextPage()}"]'>${paginator.curPage}/${paginator.numPages}</g:link>
	  </li>
	  <li <g:if test="${paginator.isLastPage()}">class='disabled'</g:if>>
	    <g:link params='[page: "${paginator.nextPage()}"]'>Next</g:link>
	  </li>
	</ul>
      </div>
    </g:if>
    
    <g:if test="${paginator.count() > 0}">
      <ul class="alerts clickables">
	<g:set var="counter" value="${0}" />
	<g:each var="service" in="${paginator.objects()}">
	  <li>

	    <g:if test="${service.alive}">
	      <g:img dir="images" file="tick.png" title="Service OK"/>
	    </g:if>
	    <g:else>
	      <g:img dir="images" file="exclamation-24.png" 
		     title="Service seems to be down" />
	    </g:else>

	    <a href="${createLink(action: 'view', id: service.id)}">${service.title}</a>
	    <%--<strong>${service.getTitle()}</strong>--%>
	    <%--(${service.endpoint})--%>
	  </li>
	  <g:set var="counter" value="${counter + 1}" />
	</g:each>
      </ul>
    </g:if>
    <g:else>
      <p><em>No services were found.</em></p>
    </g:else>
	
</body>
</html>


