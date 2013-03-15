<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Community</title>


    <script type="text/javascript" charset="utf-8">
      <!--

	  $(document).ready(function() {

	      // update input for removing a rule 
	      $(".action-remove-rule").click(function(e) {
		  // get rule id
		  var ruleId = $(this).data('id');
		  // update the submission form 
		  var div = 
		      '<input type="hidden" name="rule_id"' + 
		      ' value="' + ruleId + '" />';
		  // the submission form 
		  var form = $("#form-remove-rule");
		  // remove old hidden values (inputs)
		  form.find("input").remove();
		  // add new input 
		  form.append(div);
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
    </div>

    <p class="lead">${group.name}</p>

    <p>
      <strong>
	<g:if test="${group.description == null}">
	  <div class="alert alert-warning"><em>No description available</em></div>
	</g:if>
	<g:else>
	  <div class="alert alert-info">${group.description}</div>
	</g:else>
      </strong>
    </p>
      
    <g:if test="${rules}">
      <ul class="unstyled rules">
	<g:each var="rule" in="${rules}">
	  <li data-id="${rule.id}">
	    ${rule.getTypePretty()}
	    <g:ifAlert rule="${rule}" type="service">
	      (<strong>${rule.service.endpoint}</strong>)
	    </g:ifAlert>
	    <g:if test="${rule.offering}">
	      <g:if test="${rule.observedProperty}">
		(offering/observed property: <strong>${rule.offering} / <g:niceProperty>${rule.observedProperty}</g:niceProperty></strong>)
	      </g:if>
	      <g:else>
		(sensor offering: <strong>${rule.offering}</strong>) 
	      </g:else>
	    </g:if>
	    <g:else>
	      <g:if test="${rule.observedProperty}">
		<em>
		  (observed property: 
		  <strong><g:niceProperty>${rule.observedProperty}</g:niceProperty></strong>) 
		</em>
	      </g:if>
	    </g:else>
	    <g:ifGroupAdmin group="${group}">
	      <a href="#modal-rule-remove-confirm" class="action-remove-rule" 
		 data-id="${rule.id}" data-toggle="modal"
		 title="Remove rule"><i class="icon-trash pull-right"></i></a>
	    </g:ifGroupAdmin> 
	  </li>
	</g:each>
      </ul>
    </g:if>
    <g:else>
      <p>
	<em>This group does not contain any rules yet</em>
      </p>
    </g:else>

    <a class="btn btn-primary" title="Add rule" 
       href="${createLink(controller: 'community', action: 'add', id: group.id)}"><i class="icon-plus icon-white"></i>&nbsp; Add rule</a>

    <%-- modal for removing rule --%>
    <div id="modal-rule-remove-confirm" class="modal hide">
      <div class="modal-header">
	<h3>Remove rule</h3>
      </div>
      <div class="modal-body">
	<p>Are you sure you want to remove the rule from this group?</p>
      </div>
      <div class="modal-footer">
	<%-- form for deleting a rule --%>
	<form id="form-remove-rule" method="POST"
	      action="${createLink(controller: 'community', action: 'removeRule', id: group.id)}">
	  <button data-dismiss="modal" class="btn" aria-hidden="true">Cancel</button>
	  <button type="submit" class="btn btn-primary"><i class="icon-trash icon-white"></i> Remove rule</button>
	</form>
      </div>
    </div>

  </body>
</html>


