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

    <sec:ifLoggedIn>
      <p class="pull-right">
	<a class="btn btn-link" href="${createLink(controller: 'community', action: 'create')}"><i class="icon-plus"></i>&nbsp;Group</a>
      </p>
    </sec:ifLoggedIn>

    <p class="lead">Groups</p>

    <sec:ifNotLoggedIn>
      <div class="alert alert-info">
	<i class="icon-info-sign"></i>
	Log in to create and configure groups
      </div>
    </sec:ifNotLoggedIn>

    <g:if test="${groups.getTotalCount()}">

      <p>The following are the community groups that have been
      created. Select one to view more details</p>
    
      <g:paginate prev="Prev" next="Next" 
		  controller="community" action="list" 
		  total="${groups.getTotalCount()}" />

      <table class="table table-striped">
	<thead>
	  <tr>
	    <th>Name</th>
	    <th>Description</th>
	    <th>Created by</th>
	  </tr>
	</thead>
	<tbody>
	  <g:each var="group" in="${groups}">
	    <tr>
	      <td>
		<a href="${createLink(action: 'view', id: group.id)}">${group.name}</a>
	      </td>
	      <td>${group.description.intro(40)}</td>
	      <td>${group.createdBy}</td>
	    </tr>
	  </g:each>
	</tbody>
      </table>

    </g:if>
    <g:else>
      <p><em>There are currently no groups defined</em></p>
    </g:else>
	
  </body>
</html>


