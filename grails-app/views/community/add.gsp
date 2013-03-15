<!doctype html>
<html>
  <head>
    <meta name="layout" content="main"/> 
    <title>Add rule</title>
    
    <script type="text/javascript">
      <!--

      var services = null;

      $(document).ready(function() {

//	  $.ajaxSetup({
//	      contentType: "application/json; charset=utf-8"
//	  });

	  jQuery.ajaxSetup({
	      beforeSend: function() {
		  $('#loader').show();
	      },
	      complete: function(){
		  $('#loader').hide();
	      },
	      success: function() {}
	  });
	  
	  $('ul.nav-tabs a').click(function (e) {
	      e.preventDefault();
	      $(this).tab('show');
	      // update add button status 
	      updateAddButton(); 
	  })
	  
	  // button to create rule 
	  $("#btn-create-rule").click(function() {

	      // check if the button is enabled
	      if ($(this).hasClass('disabled'))
		  return;

	      // find the active rule type 
	      var ruleType = getActiveRuleType(); 
	      var json = {};
	      json['type'] = ruleType; 
	      json['group'] = ${group.id}; 
	      var details = {}; 

	      // find the service ID
	      var serviceId = 
		  $("#services-down-outer button.btn-primary").data("service");
	      details['serviceId'] = serviceId; 

	      if (ruleType == 'service') {

		  // nothing more to add, service has already been added 

	      } else if (ruleType == 'irregular') {

		  // find the offering
		  var offering = 
		      $("#irregular-offering-outer button.btn-primary").data("offering");
		  // find the observed property 
		  var property = 
		      $("#irregular-properties-outer button.btn-primary").data("property");
		  // check options 
		  var option_add_all_properties = 
		      $("#option-add-all-properties").is(":checked") ? 
		      true : false; 

		  details['offering'] = offering;
		  details['property'] = property; 
		  
		  var options = {};
		  options['add-all-properties'] = option_add_all_properties;

		  details['options'] = options; 
	      }

	      json['details'] = details; 

	      $.ajax({
		  type: "POST",
		  url: '${createLink(controller: "community", action: "createRule")}', 
		  contentType : "text/plain",
		  dataType: 'json',
		  data: JSON.stringify(json),
		  success: function(msg) {
		      showSuccessAlert(); 
		  }, 
		  error: function(msg) {
		      showErrorAlert(); 
		  }
	      });
	  });

	  // "cancel" button 
	  $("#btn-cancel").click(function() {
	      window.location.href = '${createLink(controller: "community", action: "config", id: group.id)}';
	  });

	  $(".target-service-outer button.btn-service").click(function () {
	      // get service
	      var serviceId = $(this).data("service");
	      // hide observed properties
	      hideObservedProperties();
	      // hide sensor data preview
	      // toggleSensorDataPreview(false);
	      // change selected service
	      // setSelectedButton("target-service-outer", "service", serviceId); 
	      // hide offerings
	      $(".target-offering-outer").hide(); 
	      $.get('${createLink(controller: "community", action: "ajaxOfferings")}', 
		    {
			service_id : serviceId
		    }, 
		    function (data) {
			$(".target-offering").html(data);
			// show offerings again 
			$(".target-offering-outer").show(); 
			// register listeners
			registerOfferingListeners(serviceId); 
		    });
	  }); 

	  registerListeners($(".selection-box-container"));
      });

      function showSuccessAlert() {
	  var div = 
	      '<div class="alert alert-success fade in">' + 
	      '<button type="button" class="close" data-dismiss="alert">×</button>' + 
	      '<strong>Success!</strong> The rule was successfully added to the group' + 
	      '</div>';

	  $("#alert-holder").append($(div)); 
      }

      function showErrorAlert() {
	  var div = 
	      '<div class="alert alert-error fade in">' + 
	      '<button type="button" class="close" data-dismiss="alert">×</button>' + 
	      '<strong>Failure!</strong> Sorry, something went wrong during the request' + 
	      '</div>';

	  $("#alert-holder").append($(div)); 
      }

      function showUnsupportedFormatAlert(offeringId) {
	  var div = 
	      '<div class="alert alert-warning fade in">' + 
	      '<button type="button" class="close" data-dismiss="alert">×</button>' + 
	      '<strong>Warning!</strong> We currently do not support any of the available response formats for offering: <strong>' + offeringId + '</strong></p>' + 
	      '</div>';

	  $("#alert-holder").append($(div).delay(4000)); 
      }


      /**
       * Returns the active rule type that is being created 
       * 
       */ 
      function getActiveRuleType() {
	  var ruleType = $(".tab-content").find(".active").attr("id");
	  if (ruleType == 'service')
	      return 'service';
	  else if (ruleType == 'irregular')
	      return 'irregular';
	  // default
	  return null;
      }

      /**
       * Updates the status of the 'add rule' button
       * 
       */
      function updateAddButton() {
	  // clear options  
	  clearOptions();

	  var type = getActiveRuleType();
	  if (type == 'service') {
	      // find the service ID
	      var serviceId = 
		  $("#services-down-outer button.btn-primary").data("service");
	      if (serviceId != null) {
		  // enable
		  toggleAddButton(true);
		  return;
	      }
	  } else if (type == 'irregular') {
	      // find the offering ID
	      var offeringId = 
		  $("#irregular-offering-outer button.btn-primary").data("offering");
	      // find the observed property 
	      var property = 
		  $("#irregular-properties-outer button.btn-primary").data("property");

	      if (offeringId != null && property != null) {
		  // enable
		  toggleAddButton(true);
		  return;
	      }
	  }

	  // default: disable button
	  toggleAddButton(false);
      }

      function registerListeners(elmt) {
	  elmt.find("button.btn-service").click(function () {
	      // get service
	      var serviceId = $(this).data("service");
	      // change selected service
	      setSelectedButton("target-service-outer", "service", serviceId); 
	  });
      }

      function registerOfferingListeners(serviceId) {
	  $(".target-offering button").click(function () {
	      var offeringId = $(this).data("offering");
	      // hide sensor data preview
	      //toggleSensorDataPreview(false);
	      // remove properties
	      $(".target-properties").empty(); 
	      // change selected offering
	      setSelectedButton("target-offering-outer", 
				"offering", offeringId); 
	      // hide properties 
	      $(".target-properties-outer").hide(); 
	      $.get('${createLink(controller: "community", action: "ajaxProperties")}',  
		    {
			service_id : serviceId, 
			offering_id : offeringId
		    }, 
		    function (data) {
			$(".target-properties").html(data);
			// show properties again 
			$(".target-properties-outer").show(); 
			// register listeners
			registerPropertiesListeners(serviceId, offeringId); 
		    });

	      // check supported formats
	      $.getJSON('${createLink(controller: "community", action: "ajaxCheckSupportedFormats")}', 
			{
			    service_id : serviceId, 
			    offering_id : offeringId
			}, 
			function (data) {
			    if (data.status == 'KO') {
				// show alert 
				showUnsupportedFormatAlert(offeringId);
			    }
			});
	  });
      }


      function registerPropertiesListeners(serviceId, offeringId) {
	  $(".target-properties button").click(function () {
	      var property = $(this).data("property");
	      // change selected offering
	      setSelectedButton("target-properties-outer", 
				"property", property); 
	      // fetch 
	      $.getJSON('${createLink(controller: "community", action: "ajaxOfferingsWithSameProperty")}', 
	      	  {
	      	      service_id : serviceId, 
	      	      observed_property : property
	      	  }, 
	      	  function (data) {
		      if (data.status == "OK") {
			  clearOptions();
			  addAllPropertiesOption(data.number);
		      }
	      	  }); 
	      
	  });
      }

      function addAllPropertiesOption(numProperties) {
	  var option = '<label class="checkbox">' + 
	      '<input id="option-add-all-properties" type="checkbox">' + 
	      'Create a rule for all <strong>' + numProperties + '</strong> ' + 
	      'offerings with the same observed property' + 
	      '</input></label>';
	  optionsAvailable();
	  $("#options-holder").append(option);
      }

      function clearOptions() {
	  $("#options-holder").empty();
	  $("#options-empty").show(); 
      }

     function optionsAvailable() {
	 $("#options-empty").hide(); 
     }

      /**
       * Hides observed properties 
       * 
       */ 
      function hideObservedProperties() {
	  $(".target-properties-outer").hide();
      }

      /**
       * Enables or disables the 'add rule' button
       *
       * @param status true if enabled, false otherwise 
       */
      function toggleAddButton(status) {
	  if (status)
	      $("#btn-create-rule").removeClass('disabled');
	  else
	      $("#btn-create-rule").addClass('disabled');
      }

      /**
       * Sets the selected button under the container with ID 'containerId'
       *
       * @param containerClass The container containing <button/> elements
       * @param dataIdName The data-* name to inspect
       * @param value The value to compare dataIdName against 
       *
       */
      function setSelectedButton(containerClass, dataIdName, value) {
	  $("." + containerClass + " button").each(function () {
	      var idName = $(this).data(dataIdName); 
	      if (idName == value) {
		  $(this).removeClass("btn-link");
		  $(this).addClass("btn-primary");
	      } else {
		  $(this).addClass("btn-link");
		  $(this).removeClass("btn-primary");
	      }
	      // update add button status 
	      updateAddButton(); 
	  });
      }

    -->
    </script>

  </head>
  <body>

    <p class="lead">Create rule</p>

    <div id="alert-holder"></div>

    <ul class="nav nav-tabs">
      <li class="active">
	<a href="#service" data-toggle="tab">Service down alert</a>
      </li>
      <li>
	<a href="#irregular" data-toggle="tab">Irregular data delivery alert</a>
      </li>
      <!-- <li>
	   <a href="#custom" data-toggle="tab">Custom alert</a>
      </li> -->
    </ul>

    <div class="tab-content">
      
      <%-- SERVICES --%>

      <div class="tab-pane active" id="service">
	<p>Select services you want to generate 'service down' alerts for:</p>
	
	<%-- list services --%>
	<div id="services-down-outer" class="target-service-outer selection-box-container">
	  <p>
	    <strong>Services</strong>
	  </p>
	  <div class="selection-box">
	    <g:if test="${services}">
	      <g:each in="${services}" var="service">
		<button 
		   data-service="${service.id}" 
		   class="btn btn-service btn-full <g:if test='${service.id == serviceId}'>btn-primary</g:if><g:else>btn-link</g:else>">${service.title}</button>
	      </g:each>
	    </g:if>
	    <g:else>
	      <p><em>There are currently no services.</em></p>
	      <p><g:link controller="service">Manage services</g:link></p>
	    </g:else>
	  </div>
	</div> <!-- close div.selection-box-container -->	

	<%--
	   <div id="holder-services">
	     <div id="loader"><g:img dir="images" file="spinner-large.gif"/></div>
	   </div>
	   --%>

      </div>


      <%-- IRREGULAR --%>

      <div class="tab-pane" id="irregular">
	<p>Select services and offerings you want to generate
	  'irregular sensor data' alerts for:</p>

	<%-- list services --%>
	<div id="irregular-service-outer" 
	     class="target-service-outer selection-box-container">
	  <p>
	    <strong>Services</strong>
	  </p>
	  <div class="selection-box">
	    <g:if test="${services}">
	      <g:each in="${services}" var="service">
		<button 
		   data-service="${service.id}" 
		   class="btn btn-service btn-full <g:if test='${service.id == serviceId}'>btn-primary</g:if><g:else>btn-link</g:else>">${service.title}</button>
	      </g:each>
	    </g:if>
	    <g:else>
	      <p><em>There are currently no services.</em></p>
	      <p><g:link controller="service">Manage services</g:link></p>
	    </g:else>
	  </div>
	</div> <!-- close div.selection-box-container -->	

	<%-- where sensor offerings are displayed --%> 
	<div id="irregular-offering-outer" 
	     class="target-offering-outer box-outer" style="display: none;">
	  <p>
	    <strong>Sensor Offerings</strong>
	  </p>
	  <div class="target-offering box">
	  </div>
	</div> 

	<%-- where observed properties are displayed --%> 
	<div id="irregular-properties-outer" 
	     class="target-properties-outer box-outer" style="display: none;">
	  <p>
	    <strong>Observed properties</strong>
	  </p>
	  <div class="target-properties box">
	  </div>
	</div> 	

	<div style="clear: both;"></div>
	<p>
	  <strong>Options</strong>
	  <div id="options-outer">
	    <div id="options-holder"></div>
	    <div id="options-empty">
	      <p><i>No options currently available.</i></p>
	    </div>
	  </div>
	</p>

      </div> <!-- close div.tab-pane --> 

      <%-- CUSTOM --%>

      <div class="tab-pane" id="custom">
	<p>Custom</p>
      </div>
      
    </div>

    <div style="clear: both;"></div>
    <p>
      <a id="btn-create-rule" class="btn btn-success disabled"><i class="icon-plus icon-white"></i> Add rule to group</a>
      <a id="btn-cancel" class="btn"><i class="icon-chevron-left"></i> Go back to group overview</a>
    </p>

  </body>
</html>
