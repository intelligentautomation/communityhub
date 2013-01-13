<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Edit group</title>

    <script type="text/javascript" charset="utf-8">
      <!--

	  $(document).ready(function() {
	      $("#inputName").focus();

	      $("#btn-cancel").click(function() {
		  window.location = "${createLink(action: 'index', id: group.id)}"
	      });
	  });

       -->
    </script>

  </head>
  <body>
    
    <p class="lead">Edit group</p>

    <g:if test="${success}">
      <div class="alert alert-block alert-success fade in">
	<h4 class="alert-heading">Success!</h4>
	<p>${success}</p>
      </div>
    </g:if>

    <g:if test="${error}">
      <div class="alert alert-block alert-error fade in">
	<h4 class="alert-heading">Failure!</h4>
	<p>${error}</p>
      </div>
    </g:if>
    
    <g:if test="${group}">
      <form class="form-horizontal" action="" method="POST">
	<div class="control-group">
	  <label class="control-label" for="inputName">Name</label>
	  <div class="controls">
	    <input type="text" name="inputName" class="input-xxlarge" 
		   value="${group.name}" 
		   id="inputName" placeholder="Group name" />
	  </div>
	</div>
	<div class="control-group">
	  <label class="control-label" for="inputDescription">Description</label>
	  <div class="controls">
	    <textarea rows="6" name="inputDescription" class="input-xxlarge" 
		      id="inputDescription" placeholder="Description">${group.description}</textarea>
	  </div>
	</div>
	<div class="form-actions">
	  <button type="submit" class="btn btn-primary">Save</button>
	  <button id="btn-cancel" type="button" class="btn">Cancel</button>
	</div>
      </form>
    </g:if>

  </body>
</html>


