<!doctype html>
<html>
  <head>
    <meta name="layout" content="main"/>
    <title>Alert</title>
    
    <link rel="stylesheet" media="screen" href="${resource(dir: 'rickshaw/', file: 'rickshaw.min.css' )}">

    <link rel="stylesheet" media="screen" href="${resource(dir: 'css/ui-lightness/', file: 'jquery-ui-1.9.1.custom.min.css' )}">

    <link rel="stylesheet" media="screen" type="text/css"
	  href="${resource(dir: 'css/', file: 'legend.css')}">

    <script src="${resource(dir: 'js/', file: 'jquery-ui-1.9.1.custom.min.js')}" type="text/javascript"></script>

    <script src="${resource(dir: 'js/', file: 'd3.v2.min.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'rickshaw/', file: 'rickshaw.min.js')}" type="text/javascript"></script>

    <style type="text/css">
    
      #slider {
	margin-top: 10px;
      }

      #legend {
        display: inline-block;
        vertical-align: top;
        margin: 0 0 0 10px;
      }

    .rickshaw_graph .detail .x_label { display: none }

    </style>
		
    <script type="text/javascript">
      <!--

	  var series = new Array(); 

	  $(document).ready(function () {

	      $("#btn-preview").click(function() {
		  fetchData();
	      });

	      // automatically try and fetch the data 
	      fetchData();
	      
	  });

          function fetchData() {
	      // hide button 
	      $("#p-btn-preview").hide();
	      // show progress bar
	      $("#p-progress").show();
	      $.getJSON('${createLink(controller: "alert", action: "ajaxGetSensorData")}', 
		  	{
		  	    alert_id : ${id}
		  	}, 
		  	function (data) {
			    // hide progress bar
			    $("#p-progress").hide();
		  	    if (data.status == "KO") {
				var div = '<div class="alert alert-error">' + 
				    '<strong>Error</strong> ' + 
				    'There was an error fetching the sensor data from the server, it might no longer be available.' + 
				    '</div>';
				$("#holder-data-error").append(div);
				// remove chart container 'div'
				$("#chart_container").remove();
		  	    } else {
		  		generateGraph(data); 
			    }
		  	}
		       );
	  }

          var map = {}; 
          var counter = 0;

          function generateGraph(json) {

	      var data = new Array();
	      for (var i in json) {
		  // convert milliseconds to Epoch time (seconds) 
		  var time = json[i] / 1000;
		  data.push({ "x" : time, "y": 1 }); 
	      }

	      var graph = new Rickshaw.Graph( {
		  element: document.querySelector("#chart"),
		  width: 720,
		  height: 80, 
		  renderer: 'bar',
		  interpolation: 'linear',  
		  series: [ {
		      color: '#b1dcfe',
		      data: data, 
		      name: 'Available sensor data reading'
		  } ]
	      } );
	      graph.render();

	      var hoverDetail = new Rickshaw.Graph.HoverDetail( {
	       	  graph: graph,
		  formatter: function(series, x, y) {
		      var date = '<span class="date">' + new Date(x * 1000).toUTCString() + '</span>';
		      //var swatch = '<span class="detail_swatch" style="background-color: ' + series.color + '"></span>';
		      //var content = swatch + series.name + ": " + parseInt(y) + '<br>' + date;
		      var content = date;
		      return content;
		  }
	      } );

	      var legend = new Rickshaw.Graph.Legend( {
		  graph: graph,
		  element: document.getElementById("legend"),
	      } );
	      
	      var slider = new Rickshaw.Graph.RangeSlider( {
		  graph: graph,
		  element: $('#slider')
	      } );

	      var xAxis = new Rickshaw.Graph.Axis.Time( {
		  graph: graph, 
	      } );
	  }

          function getTimeColumn(columns) {
	      for (var i = 0; i < columns.length; i++) {
		  if (columns[i]['type'] == 'TIMESTAMP')
		      return columns[i]['name']; 
	      }
	      return null;
	  }

          function getValueColumns(columns) {
	      var cols = new Array(); 
	      for (var i = 0; i < columns.length; i++) {
		  if (columns[i]['type'] == 'DOUBLE')
		      cols.push(columns[i]['name']); 
	      }
	      return cols;
	  }

     -->
    </script>
  </head>
  <body>

    <g:if test="${alert}">

      <p class="lead">${alert.getTypePretty()}</p>

      <g:if test="${alert.type == 'ALERT_SERVICE_DOWN'}">
	<g:if test="${service}">

	  <div class="alert alert-block">
	    <h4>Alert generated ${alert.timestamp}</h4>
	  </div>

	  <p>
	    This alert indicates that the
	    service <g:link controller="service" action="view"
	    id="${service.id}"><strong>${service.title}</strong></g:link>
	    was considered down and its Capabilities document could
	    not be retrieved.
	  </p>

	</g:if>
	<g:else>
	  This alert indicates that a service was down. 
	</g:else>
      </g:if>

      <g:if test="${alert.type == 'ALERT_IRREGULAR_DATA_DELIVERY'}">

	<div class="alert alert-block">
	  <h4>Alert generated ${alert.timestamp}</h4>
	</div>

	<p>
	  This alert indicates that sensor data from the sensor
	  offering <strong>${alert.offering}</strong> was delivered at
	  irregular itervals.
	  
	  <g:if test="${service}">
	    The offering belongs to the service 
	    <g:link controller="service"
		    action="view"
		    id="${service.id}"><strong>${service.title}</strong></g:link>. 
	  </g:if>	  

	  The sensor data was retrieved using the observed property
	  <strong>${alert.observedProperty}</strong>.
	</p>

	<g:if test="${alert.validFrom && alert.validTo}">
	  <p>
	    Alert covers sensor data between 
	    <strong>${alert.validFrom}</strong> &ndash; <strong>${alert.validTo}</strong>
	  </p>
	</g:if>

	<%--
	<p id="p-btn-preview">
	  <button id="btn-preview" 
		  class="btn btn-primary">View sensor data delivery intervals</button>
	</p>
	  --%>
	<div id="p-progress" style="display: none;">
	  <p>
	    Fetching sensor data...
	    <div class="progress progress-striped active">
	      <div class="bar" style="width: 100%;"></div>
	    </div>
	  </p>
	</div>

	<p>
	<div id="holder-data-error"></div>
	<div id="chart_container">
	  <div id="chart"></div>
	  <div id="legend_container">
	    <div id="legend"></div>
	  </div>
	  <div id="slider"></div>
	</div>
	</p>
	
      </g:if>

      <p class="lead">Details</p>
      <p>${alert.detail}</p>
      
    </g:if>
    <g:else>
      <h1>No alert with ID ${id} found</h1>
    </g:else>
    
  </body>
</html>
