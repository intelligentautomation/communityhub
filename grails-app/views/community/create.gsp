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

    <form class="form-horizontal" method="POST"
	  action="${createLink(controller: 'community', action: 'save')}">
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
	  <span class="help-block">Tell us what this group about</span>
	</div>
      </div>
      <div class="form-actions">
	<button type="submit" class="btn btn-primary"><i class="icon-ok icon-white"></i> Create group</button>
	<button id="btn-cancel" type="button" class="btn">Cancel</button>
      </div>

    </form>


  </body>
</html>


