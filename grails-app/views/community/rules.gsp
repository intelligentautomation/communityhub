<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Community</title>


    <script type="text/javascript" charset="utf-8">
      <!--

	  $(document).ready(function() {

	      // $("ul.clickables").each(function() {
	      // 	  $(this).find("li").click(function() {
	      // 	      var link = $(this).find("a");
	      // 	      if (link.length > 0)
	      // 		  window.location = link[0]
	      // 	  });
	      // });

	      var ruleIdDataAttr = "rule-id"; 
	      var divRemoveRuleModalId = "#modal-rule-remove-confirm";
	      var divDeleteGroupModalId = "#modal-group-delete-confirm";

	      <%-- actions only available if there is a group available --%>
	      <g:if test="${group}">
	      // show modal for deleting a group
	      $("#btn-delete-group").click(function(e) {
		  $(divDeleteGroupModalId).modal();
	      });

	      // react to delete group modal 
	      $("#btn-group-delete-confirm").click(function() {
		  $.post('${createLink(controller: "community", action: "ajaxDeleteGroup", id: "${group.id}")}', 
			 { }, 
			 function(data, status) {
			     if (data != null && data.status == "OK") {
				 // TODO: indicate success
				 // hide modal
				 $(divDeleteGroupModalId).modal('hide'); 
				 // go to location
				 window.location = '${createLink(controller: "community")}';
			     } else {
				 // TODO: indicate failure 
				 alert('Something went wrong, sorry');
			     }
			 }, 'json');
	      });

	      // show modal for removing a rule 
	      $(".remove-rule").click(function () {
		  var ruleId = $(this).data("id");
		  // remember the rule id
		  $(divRemoveRuleModalId).data(ruleIdDataAttr, ruleId);
		  $(divRemoveRuleModalId).modal(); 
	      });

	      // react to remove rule modal 
	      $("#btn-remove-rule-confirm").click(function() {
		  // get the rule id 
		  var ruleId = $(divRemoveRuleModalId).data(ruleIdDataAttr);
		  $.post('${createLink(controller: "community", action: "ajaxRemoveRule")}', 
			 {
			     group_id : ${group.id}, 
			     rule_id : ruleId
			 }, 
			 function(data, status) {
			     if (data != null && data.status == "OK") {
				 // TODO: indicate success
				 // hide modal
				 $(divRemoveRuleModalId).modal('hide'); 
				 // hide rule 
				 $("li[data-id='" + ruleId + "']").remove();
			     } else {
				 // TODO: indicate failure 
			     }
			 }, 'json');
	      });
	      </g:if>
	  });

	-->
    </script>


  </head>
  <body>
    
    <p class="lead">Groups</p>

    <sec:ifNotLoggedIn>
      <div class="alert alert-info">
	<i class="icon-info-sign"></i>
	Log in to create and configure groups
      </div>
    </sec:ifNotLoggedIn>

    <div class="container-fluid">
      
      <div class="row-fluid">
	<div class="span3">

	  <sec:ifLoggedIn>	  
	  <a class="btn btn-link" href="${createLink(controller: 'community', action: 'create')}"><i class="icon-plus"></i>&nbsp;Group</a>
	  </sec:ifLoggedIn>

          <div class="side-bar">
	    
	    <ul class="nav nav-list">
	      <%-- list groups --%>
	      <g:if test="${groups}">
		<li class="nav-header">Community Groups</li>
		<g:each in="${groups}" var="group">
		  <li class="<g:if test='${group.id == id}'>active</g:if>">
		    <g:link 
		       controller="community" action="index" id="${group.id}">${group.name}</g:link>
		  </li>
		</g:each>
	      </g:if>
	    </ul>
	    
          </div>
	  
	</div>
	<div class="span9">
	  
          <g:if test="${group}">
	    
	    <p class="lead">
	      <g:link controller="community" title="Alert feed" 
		      action="feed" id="${group.id}"><g:img dir="images" file="rss.png" style="float: right;" /></g:link>
	      <g:link title="Report" controller="alert" 
		      action="report" id="${group.id}"><g:img dir="images" file="document.png" class="pull-right" style="padding: 0px 10px;" /></g:link>
	      ${group.name} 
	    </p>

	    <p>
	      <strong>
		<g:if test="${group.description == null}">
		  <div class="alert alert-warning">
		    <em>No description available</em>
		  </div>
		</g:if>
		<g:else>
		  <div class="alert alert-success">
		    ${group.description}
		  </div>
		</g:else>
	      </strong>
	    </p>
	    
	    <g:if test="${rules}">
	      <ul class="unstyled rules">
	      <%--<ul class="unstyled rules clickables">--%>
		<g:each var="rule" in="${rules}">
		  <li data-id="${rule.id}">
		    ${rule.getTypePretty()}
		    <g:ifAlert rule="${rule}" type="service">
		      (<strong><g:getServiceUrl rule="${rule}" /></strong>)
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
		    <g:isGroupAdmin group="${group}">
		    <a href="#" class="remove-rule" data-id="${rule.id}" 
		       title="Remove rule"><i class="icon-remove pull-right"></i></a>
		    </g:isGroupAdmin> 
		  </li>
		</g:each>
	      </ul>
	    </g:if>
	    <g:else>
	      <em>This group does not contain any rules yet</em>
	    </g:else>
	    
	    <g:isGroupAdmin group="${group}">
	    <p style="margin: 20px 0px;">
	      <a class="btn" title="Edit group"
		 href="${createLink(controller: 'community', action: 'edit', id: group.id)}"><i class="icon-edit"></i>&nbsp; Edit group</a>
	      <a class="btn" title="Add rule" 
		 href="${createLink(controller: 'community', action: 'add', id: group.id)}"><i class="icon-pencil"></i>&nbsp; Add rule</a>
	      <a class="btn btn-danger pull-right" 
		 id="btn-delete-group" 
		 title="Delete group" 
		 href="#"><i class="icon-remove"></i>&nbsp; Delete group</a>
	    </p>
	    </g:isGroupAdmin>
	    
	  </g:if>
	  <g:else>
	    <em>No group (or an incorrect group) has been selected</em>
	  </g:else>
	  
	</div>

      </div>

    </div>

    <%-- MODALS --%>

    <div id="modal-rule-remove-confirm" class="modal hide">
      <div class="modal-header">
	<h3>Remove rule</h3>
      </div>
      <div class="modal-body">
	<p>Are you sure you want to remove the rule from this group?</p>
      </div>
      <div class="modal-footer">
	<a data-dismiss="modal" href="#" class="btn" aria-hidden="true">Cancel</a>
	<a id="btn-remove-rule-confirm" href="#" class="btn btn-primary">Remove rule</a>
      </div>
    </div>

    <div id="modal-group-delete-confirm" class="modal hide">
      <div class="modal-header">
	<h3>Delete group</h3>
      </div>
      <div class="modal-body">
	<p>Are you sure you want to delete the group?</p>
      </div>
      <div class="modal-footer">
	<a data-dismiss="modal" href="#" class="btn" aria-hidden="true">Cancel</a>
	<a id="btn-group-delete-confirm" href="#" class="btn btn-primary">Delete group</a>
      </div>
    </div>
    
  </body>
</html>


