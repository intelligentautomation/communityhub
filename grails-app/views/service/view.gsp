<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Services</title>
    
    <script src="http://www.openlayers.org/api/OpenLayers.js" type="text/javascript"></script>
    
    <script type="text/javascript" charset="utf-8">
    <!--

    /*
     * Map
     */
    var map, layerBase, layerVector;

    function initMap() {

	map = new OpenLayers.Map("map");
	layerBase = new OpenLayers.Layer.OSM();
   	//map.zoomToMaxExtent();
	
	var fromProjection = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
	var toProjection   = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection
	var position       = new OpenLayers.LonLat(13.41,52.52).transform( fromProjection, toProjection);
	var zoom           = 2;
	
	map.addLayer(layerBase);
	map.setCenter(position, zoom);
	
	layerVector = new OpenLayers.Layer.Vector("Markers", {
	    style: OpenLayers.Util.extend(
		OpenLayers.Feature.Vector.style['default'],
		{ graphicName: 'circle', strokeColor: 'blue',
		  fillCapacity: 0.2, graphicOpacity: 1,
		  fillColor: 'blue', pointRadius: 4,
		  strokeWidth: 1 })
	});
	map.addLayer(layerVector);
    }

    /**
     * Adds a point to the map
     */
    function addPoint(lat, lon) {

	// transform from WGS 1984
	var fromProjection = new OpenLayers.Projection("EPSG:4326");
	
	// create a point feature
	var point = new OpenLayers.Geometry.Point(lon, lat).
    	    transform(fromProjection, map.getProjectionObject());
	var feature = new OpenLayers.Feature.Vector(point);
	
	layerVector.addFeatures([ feature ]);
    }

    /**
     * Adds a box to the map
     */
    function addBox(latUpper, lonUpper, latLower, lonLower) {
	// transform from WGS 1984
	var fromProjection = new OpenLayers.Projection("EPSG:4326");
	// box
	
	//var left = lonUpper < lonLower ? lonUpper : lonLower;
	//var bottom = latUpper < latLower ? latUpper : latLower;
	//var right = lonUpper > lonLower ? lonUpper : lonLower;
	//var top = latUpper > latLower ? latUpper : latLower;
	
	var bounds = new OpenLayers.Bounds(lonUpper, latUpper, lonLower, latLower).
	    transform(fromProjection, map.getProjectionObject());
	//	var bounds = new OpenLayers.Bounds(left, bottom, right, top).
	//		transform(fromProjection, map.getProjectionObject());
	
	// create box
	var box = new OpenLayers.Feature.Vector(bounds.toGeometry());
	// add to layer
	layerVector.addFeatures([box]);
    }

    /**
     * Adds markers to the map
     *
     * The JSON is in the following format:
     *
     * {"station-mkgm4":
     *  	{"upper-lon":-86.339,"lower-lon":-86.339,
     *		 "upper-lat":43.228,"lower-lat":43.228 },
     * 	...
     * }
     */
    function addMarkers(json) {
	$.each(json,
               function(key, value) {
		   var latU = value['upper-lat'];
		   var lonU = value['upper-lon'];
		   var latL = value['lower-lat'];
		   var lonL = value['lower-lon'];
		   if (isPoint(latU, lonU, latL, lonL)) {
                       addPoint(latU, lonU);
		   } else {
                       // TODO: disabled for now, need more testing
                       // addBox(latU, lonU, latL, lonL);
		   }
               }
	      );
    }

    /**
     * Returns true if the lat-lon for the upper and lower corner
     * is in fact a point, false otherwise
     */
    function isPoint(latU, lonU, latL, lonL) {
	if (latU == latL && lonU == lonL)
            return true;
	return false;
    }


    $(document).ready(function() {

	/*
	 * Deleting the service
	 */
	
	$("#btn-modal-delete-cancel").click(function() {
	    $("#modal-delete").modal('hide');
	});
	
	$("#btn-modal-delete-delete").click(function() {
	    // show loader
	    $("#ajax-loader").show();
	    // issue request

	    //	    $.ajax({
	    //	type: 'POST',
	    //	url: '${createLink(controller: "service", action: "delete", id: service.id)}', 
	    //data: {}, 

	    $.get("${createLink(action: 'remove', id: service.id)}", 
        	  {}, 
        	  function(data, status) {
        	      if (data.status == 'OK') {
	        	  // hide modal
			  $("#modal-delete").modal('hide');
			  // re-direct user
			  window.location = '${createLink(controller: "service")}';
        	      }
        	  }
		 );
	});
	
	/*
	 * Prettifying
	 */
	//var k = $(".service-keywords");
	//k.html(k.html().split(',').join(', '));
	
	initMap();
	
	// fetch additional data
	<%--
	$.get("@routes.Catalog.details(service.id)",
              { },
              function(data, status) {
        	  // hide ajax loader row
		  $('#loader-row').remove();
        	  if (data.status == 'OK') {
        	      // add rows
        	      var html = "";
        	      $.each(data.data, function(key, value) {
        		  html += "<tr><td><strong>" + key + "</strong></td>" +
        		      "<td>" + value + "</td></tr>";
        	      });
        	      $("table.table").append(html);
        	  }
              }
             );

	--%>

	// fetch bounding boxes
	$.getJSON('${createLink(controller: "service", action: "boundingboxes", id: service.id)}', 
              { },
              function(data, status) {
        	  if (data.status == 'OK') {
        	      // update map with markers
        	      addMarkers(data.data);
        	  }
              }
             );

    });

    -->
    </script>

  </head>
  <body>
    
    <p class="lead">${service.title}</p>

    <div id="map" style="height: 350px;"></div>

    <br />

    <table class="table table-striped">
      <tr>
	<td>Service URL</td>
	<td><a href="${service.endpoint}">${service.endpoint}</a></td>
      </tr>
    </table>
    
    <sec:access expression="hasRole('ROLE_ADMIN')">
      <button class="btn btn-danger pull-right" type="button" data-toggle="modal" 
	      data-target="#modal-delete">Delete</button>
    </sec:access>
    
    <div class="modal hide" id="modal-delete">
      <div class="modal-header">
	<button class="close" data-dismiss="modal">x</button>
	<h3>Delete service</h3>
      </div>
      <div class="modal-body">
	<p>Are you sure you want to delete this service from the catalog?</p>
      </div>
      <div class="modal-footer">
  	<img src="${resource(dir: 'images', file: 'spinner.gif' )}"
  	     style="display: none;"
	     id="ajax-loader"
	     alt="Loading" title="Loading" />

	<%-- form to delete the service --%>
	<form action="${createLink(controller: 'service', action: 'delete', id: service.id)}" method="POST">
	  <a href="#" id="btn-modal-delete-cancel" class="btn">Cancel</a>
	  <input type="submit" class="btn btn-danger" value="Delete" />
	</form>
      </div>
    </div>

  </body>
</html>
