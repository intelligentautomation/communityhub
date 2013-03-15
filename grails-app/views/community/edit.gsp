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
		  window.location = "${createLink(action: 'view', id: group.id)}"
	      });

	      $("#btn-delete-group").click(function() {
		  $.ajax({
		      type: 'POST', 
		      url: '${createLink(controller: "community", action: "delete", id: group.id)}',
		      data: {},
		      success: function(msg) {
			  // re-direct
			  window.location.replace('${createLink(controller: "community")}');
			  return false;
		      }, 
		      error: function(msg) {
			  // re-load
			  location.reload();
			  return false;
			  //window.location.replace('${createLink(controller: "community")}');
		      }
		  });
	      });

	  });

       -->
    </script>

  </head>
  <body>
    
    <p class="lead">Edit group</p>

    <g:if test="${params.error}">
      <div class="alert alert-error">
	<strong>Failure!</strong> ${params.error}
      </div>
    </g:if>
    
    <g:if test="${group}">
      <form class="form-horizontal" method="POST"
	    action="${createLink(controller: 'community', action: 'update', id: group.id)}">
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
	    <span class="help-block">Tell us what this group about</span>
	  </div>
	</div>
	<div class="form-actions">
	  <button type="submit" class="btn btn-primary"><i class="icon-pencil icon-white"></i> Save</button>
	  <button id="btn-cancel" type="button" class="btn">Cancel</button>
	  <button id="btn-delete" type="button" 
		  href="#modalDeleteGroup" data-toggle="modal"
		  class="btn btn-danger pull-right"><i class="icon-trash icon-white"></i> Delete</button>
	</div>
      </form>
    </g:if>

    <%-- modal for deleting group --%>
    <div id="modalDeleteGroup" class="modal hide fade">
      <div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	<h3>Delete group</h3>
      </div>
      <div class="modal-body">
	<p>Are you sure you want to delete the group?</p>
      </div>
      <div class="modal-footer">
	<form method="POST"
	      action="${createLink(controller: 'community', action: 'delete', id: group.id)}">
	  <a href="#" class="btn" data-dismiss="modal">No, cancel</a>
	  <button type="submit" class="btn btn-danger"><i class="icon-trash icon-white"></i> Delete group</button>
	</form>
      </div>
    </div>    

  </body>
</html>


