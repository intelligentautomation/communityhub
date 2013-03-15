<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Alerts</title>

    <script src="${resource(dir: 'js', file: 'jquery.hoverIntent.minified.js')}" type="text/javascript"></script>

    <script src="http://www.openlayers.org/api/OpenLayers.js" type="text/javascript"></script>
	
    <script type="text/javascript" charset="utf-8">
    <!--

    /*
     * Map
     */
    var map, layerBase, layerVector, layerHighlight;

    function initMap() {

	map = new OpenLayers.Map({
	    div: "map",
	    allOverlays: true
	});
	layerBase = new OpenLayers.Layer.OSM();
	
	var osm = new OpenLayers.Layer.OSM();
	//var gmap = new OpenLayers.Layer.Google("Google Streets", {visibility: false});
	
	// note that first layer must be visible
	map.addLayers([osm]);
	
	layerVector = new OpenLayers.Layer.Vector("Markers", {
	    style: OpenLayers.Util.extend(
		OpenLayers.Feature.Vector.style['default'],
		{ graphicName: 'circle', strokeColor: 'blue',
		  fillCapacity: 0.2, graphicOpacity: 1,
		  fillColor: 'blue', pointRadius: 4,
		  strokeWidth: 1 })
	});
	map.addLayer(layerVector);
	
	layerHighlight = new OpenLayers.Layer.Vector("Higlights", {
	    style: OpenLayers.Util.extend(
		OpenLayers.Feature.Vector.style['default2'],
		{ graphicName: 'circle', strokeColor: 'red',
		  fillCapacity: 0.2, graphicOpacity: 1,
		  fillColor: 'red', pointRadius: 5,
		  strokeWidth: 1 })
	});
	map.addLayer(layerHighlight);
	
	//map.addControl(new OpenLayers.Control.LayerSwitcher());
	map.zoomToMaxExtent();
    }

    function getPosition(lat, lon) {
	// Transform from WGS 1984
	var fromProjection = new OpenLayers.Projection("EPSG:4326");
	// to Spherical Mercator Projection
	var toProjection = new OpenLayers.Projection("EPSG:900913");
	var position = new OpenLayers.LonLat(lon, lat).transform(fromProjection, toProjection);
	return position;
    }

    /**
     * Adds a point to the map
     */
    function addPoint(layer, lat, lon) {

	// transform from WGS 1984
	var fromProjection = new OpenLayers.Projection("EPSG:4326");
		
	// create a point feature
	var point = new OpenLayers.Geometry.Point(lon, lat).
    	    transform(fromProjection, map.getProjectionObject());
	var feature = new OpenLayers.Feature.Vector(point);
	
	layer.addFeatures([ feature ]);
    }

    /**
     * Adds a box to the map
     */
    function addBox(layer, latUpper, lonUpper, latLower, lonLower) {
	// transform from WGS 1984
	var fromProjection = new OpenLayers.Projection("EPSG:4326");
	var bounds = new OpenLayers.Bounds(lonUpper, latUpper, lonLower, latLower).
		transform(fromProjection, map.getProjectionObject());
	// create box
	var box = new OpenLayers.Feature.Vector(bounds.toGeometry());
	// add to layer
	layer.addFeatures([ box ]);
    }

    function addPointsToMap() {
	$("ul.alerts li").each(function() {
	    var lat_lower = $(this).data("lat_lower");
	    var lon_lower = $(this).data("lon_lower");
	    var lat_upper = $(this).data("lat_upper");
	    var lon_upper = $(this).data("lon_upper");
	    if (lat_lower != null && lon_lower != null && lat_upper != null && lon_upper != null) {
		if (lat_lower == lat_upper && lon_lower == lon_upper) {
		    // add a point 
		    addPoint(layerVector, lat_lower, lon_lower); 
		} else {
		    // add a bounded box
		    addBox(layerVector, lat_upper, lon_upper, lat_lower, lon_lower);
		}
	    }
	});
    }

   function mapHighlightAlert() {
       var lat_lower = $(this).data("lat_lower");
       var lon_lower = $(this).data("lon_lower");
       var lat_upper = $(this).data("lat_upper");
       var lon_upper = $(this).data("lon_upper");
       if (lat_lower != null && lon_lower != null && lat_upper != null && lon_upper != null) {
	   if (lat_lower == lat_upper && lon_lower == lon_upper) {
	       // add a point 
	       addPoint(layerHighlight, lat_lower, lon_lower); 
	       map.panTo(getPosition(lat_lower, lon_lower));
	   } else {
	       // add a bounded box
	       // TODO: fix this and pan to the appropriate location 
	       addBox(layerVector, lat_upper, lon_upper, lat_lower, lon_lower);
	   }
       }
   }

   function mapClearAlert() {
       layerHighlight.removeAllFeatures();
   }

    $(document).ready(function() {

	initMap();
	
	$("ul.clickables").each(function() {
	    $(this).find("li").click(function() {
		var link = $(this).find("a");
		if (link.length > 0)
		    window.location = link[0]
	    });
	});

	addPointsToMap();

	var config = {    
	    sensitivity: 3, 
	    interval: 300, 
	    over: mapHighlightAlert, 
	    timeout: 200, 
	    out: mapClearAlert 
	};

	$("ul.alerts li").hoverIntent(config); 
    });

    -->
    </script>

  </head>
  <body>
    
    <p class="lead">Most recent alerts</p>
    
    <div class="row">

      <%-- ALERTS --%> 

      <div class="span4">
        
	<div style="overflow: auto;">

	  <g:if test="${alerts}">
	    <ul class="alerts clickables">
	      <g:set var="counter" value="${0}" />
	      <g:each var="alert" in="${alerts}">
		<li
		   <g:if test="${alert.latLower}">
		     data-lat_lower="${alert.latLower}"
		   </g:if>
		   <g:if test="${alert.lonLower}">
		     data-lon_lower="${alert.lonLower}"
		   </g:if>
		   <g:if test="${alert.latUpper}">
		     data-lat_upper="${alert.latUpper}"
		   </g:if>
		   <g:if test="${alert.lonUpper}">
		     data-kalle="ok" 
		   </g:if>
		   <g:if test="${alert.lonUpper}">
		     data-lon_upper="${alert.lonUpper}"
		   </g:if>
		   style="<g:if test='${counter % 2 == 0}'>highlight</g:if><g:else> </g:else>">
		  <img src="${resource(dir: 'images/fugue', file: 'exclamation-24.png') }" />
		  <strong>${alert.getTypePretty()}</strong>
		  <span>
		  <g:if test="${alert.latLower && alert.lonLower}">
		    &nbsp;(<i class="icon-map-marker"></i>)
		  </g:if>
		  </span>

		  <br />
		  <g:if test="${alert.getType() == 'ALERT_SERVICE_DOWN'}">
		    Recorded: 
		  </g:if>
		  <g:else>
		    At sensor offering ${alert.offering}, recorded: 
		  </g:else>
		  
		  <g:formatDate format="yyyy-MM-dd kk:mm" date="${alert.dateCreated}"/>
		  (<g:link controller="alert" action="view" id="${alert.id}">details</g:link>)
		</li>
		<g:set var="counter" value="${counter + 1}" />
	      </g:each>
	    </ul>
	    <g:link controller="community">More alerts...</g:link>
	  </g:if>
	  <g:else>
	    <p><i>There are currently no alerts.</i></p>
	  </g:else>
	  
	</div>
	
      </div>

      <%-- MAP --%>

      <div class="span8">
        
	<div id="map" class="largemap"></div>
        
      </div>
    </div>
    
  </body>
</html>


