<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Create group</title>

    <script type="text/javascript" charset="utf-8">
      <!--

	  $(document).ready(function() {
	      $("#inputName").focus();

	      $("#btn-cancel").click(function() {
		  window.location = "${createLink(action: 'index')}";
	      });
	  });

       -->
    </script>

  </head>
  <body>
    
    <p class="lead">New group</p>

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
    
    <form class="form-horizontal" action="" method="POST">
      <div class="control-group">
	<label class="control-label" for="inputName">Name</label>
	<div class="controls">
	  <input type="text" name="inputName" 
		 id="inputName" placeholder="Group name">
	</div>
      </div>
      <div class="control-group">
	<label class="control-label" for="inputDescription">Description</label>
	<div class="controls">
	  <input type="text" name="inputDescription" 
		 id="inputDescription" placeholder="Description">
	</div>
      </div>
      <div class="form-actions">
	<button type="submit" class="btn btn-primary">Create group</button>
	<button id="btn-cancel" type="button" class="btn">Cancel</button>
      </div>

    </form>


  </body>
</html>


