<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Add service</title>

    <script type="text/javascript" charset="utf-8">
      <!--

	  $(document).ready(function() {
	      $("#inputUrl").focus();

	      $("#btn-cancel").click(function() {
		  window.location = "${createLink(action: 'index')}";
	      });
	  });

       -->
    </script>

  </head>
  <body>
    
    <p class="pull-right">
      <a class="btn btn-link" href="${createLink(controller: 'service', action: 'list')}"><i class="icon-list"></i>&nbsp;Services</a>
    </p>

    <p class="lead">Add service</p>

    <form class="form-horizontal" method="POST"
	  action="${createLink(controller: 'service', action: 'save')}" >
      <div class="control-group">
	<label class="control-label" for="inputUrl">Service URL</label>
	<div class="controls">
	  <input type="text" name="inputUrl" class="input-xxlarge" 
		 id="inputUrl" placeholder="Service URL" 
		 <g:if test="${params.url}">value="${params.url}"</g:if> />
	</div>
      </div>
      <div class="control-group">
	<label class="control-label" for="inputServiceType">Service type</label>
	<div class="controls">
	  <select id="inputServiceType" name="inputServiceType">
	     <option
		<g:if test="${params.type == 'SOS'}">selected</g:if>
		>SOS</option>
	  </select>
	</div>
      </div>
      <div class="form-actions">
	<button type="submit" class="btn btn-primary"><i class="icon-ok icon-white"></i> Add service</button>
	<button id="btn-cancel" type="button" class="btn">Cancel</button>
      </div>

    </form>

  </body>
</html>


