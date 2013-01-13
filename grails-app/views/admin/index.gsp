<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Admin</title>

    <script type="text/javascript" charset="utf-8">
      <!--

	  $(document).ready(function() {
	      
	      $("button#service").click(function() {
		  $.post('${createLink(controller: "community", action: "ajaxExecuteServiceDownRules")}', 
			 { }, 
			 function(data, status) {
			     if (data != null && data.status == "OK") {
				 // TODO: indicate success
				 var text = 'Rules to be executed: ' + 
				     data.noRules;
				 $("#result-holder").html(text);
			     } else {
				 // TODO: indicate failure 
			     }
			 }, 'json');
		  
	      });

	      $("button#irregular").click(function() {
		  $.post('${createLink(controller: "community", action: "ajaxExecuteIrregularDataDeliveryRules")}', 
			 { }, 
			 function(data, status) {
			     if (data != null && data.status == "OK") {
				 // TODO: indicate success
				 var text = 'Rules to be executed: ' + 
				     data.noRules;
				 $("#result-holder").html(text);
			     } else {
				 // TODO: indicate failure 
			     }
			 }, 'json');
		  
	      });

	      
	  });

        -->
    </script>

  </head>
  <body>
    
    <p class="lead">Admin</p>

    <p>Select the appropriate action:</p>

    <p>
      <button id="service" class="btn">Execute service down alerts</button> 
    </p>
    <p>
      <button id="irregular" class="btn">
	Execute irregular data delivery alerts
      </button> 
    </p>

    <p id="result-holder"></p>
    
  </body>
</html>


