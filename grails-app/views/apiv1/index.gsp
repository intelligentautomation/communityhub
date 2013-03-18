<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>API v1</title>


    <script type="text/javascript" charset="utf-8">
        <!--

        $(document).ready(function() {
	});

        -->
    </script>

  </head>
  <body>
    
    <article>
      <header>
	<h1>API v1</h1>
	
	<p>
	  This page documents the Community Hub API version 1 (v1). 
	</p>
    
	<p>
	  The API is accessed over HTTP and only
	  support <a href="http://en.wikipedia.org/wiki/GET_(HTTP)#Request_methods"
	  title="GET Request Method">GET</a>. The response to each
	  request is a <a href="http://en.wikipedia.org/wiki/JSON"
	  title="JavaScript Object Notation">JSON</a> object. The
	  structure of the response object is explained by example for
	  each supported request below.
	</p>
      </header>
	
      <section>
	<h2>Ping</h2>
	
	<p>
	  Ping is a simple API call that simply returns the following
	  JSON structure:

	  <pre>{
    status: "OK",
    versions: [
        "1.0"
    ]
}</pre>
	</p>

	<p>The ping request can be used to verify that a Community Hub
	  endpoint is responsive and the response will also contain a
	  list of the supported API versions. Specifially, the value
	  to the "versions" key is an array with the supported API
	  versions.</p>

	<h4>Endpoint</h4>

	<table class="table table-striped">
	  <thead>
	  </thead>
	  <tbody>
	    <tr>
	      <td>http://[communityhub]<strong>/apiv1/ping/</strong></td>
	      <td><g:link class="btn btn-info btn-small btn-block" 
		  controller="apiv1" action="ping">Demo</g:link></td>
	    </tr>
	  </tbody>
	</table>

	<h4>Options</h4>

	<p><em>No options</em></p>

      </section>

      <section>
	<h2>List all Groups</h2>

	<p>
	  The list all groups API call returns all the currently active, and
	  public, community groups that have been created by the users
	  of the given Community Hub. The following is an example of a
	  response.
	</p>

	<pre>{
    status: "OK",
    version: "1.0",
    groups: [
        {
            id: 1,
            name: "IOOS",
            description: "The U.S. Integrated Ocean Observing System (IOOS)", 
            dateCreated: "2013-03-14T15:35:06Z",
            lastUpdated: "2013-03-16T01:12:52Z"
        },
        {
            id: 4,
            name: "NASA",
            description: "National Aeronautics and Space Administration (NASA)",
            dateCreated: "2013-03-16T01:32:42Z",
            lastUpdated: "2013-03-16T01:32:42Z"
        }
    ]
}</pre>

	<p>The value of the "groups" key is an array of maps
	representing each group. In this verion of the API five pieces of information is provided for each group:

	  <ul>
	    <li>The ID (<em>e.g. used to fetch alerts for the group</em>)</li>
	    <li>The name</li>
	    <li>A textual description</li>
	    <li>The date the group was created</li>
	    <li>The date the group was last modified (changed name or
	    description)</li>
	  </ul>

	</p>

	<h4>Endpoint</h4>

	<table class="table table-striped">
	  <thead>
	  </thead>
	  <tbody>
	    <tr>
	      <td>http://[communityhub]<strong>/apiv1/groups/</strong></td>
	      <td><g:link class="btn btn-info btn-small btn-block" 
			  controller="apiv1" action="groups">Demo</g:link></td>
	    </tr>
	  </tbody>
	</table>

	<h4>Options</h4>

	<p><em>No options</em></p>
	
      </section>

      <section>
	<h2>Alerts for a Group</h2>

	<p>The alerts for a group API returns the alerts that have
	been generated for a given community group. The following is
	an example of a response.</p>

	<p>

	</p>

	<p>
<pre>
{
    status: "OK",
    version: "1.0",
    alerts: [
        {
            id: 52,
            type: "ALERT_IRREGULAR_DATA_DELIVERY",
            detail: "Varying or irregular data delivery identified",
            dateCreated: "2013-03-18T01:05:40Z",
            lastUpdated: "2013-03-18T01:05:44Z",
            validFrom: "2013-03-17T01:05:39Z",
            validTo: "2013-03-18T01:05:39Z",
            latLower: -46.67,
            latUpper: -46.67,
            lonLower: 161,
            lonUpper: 161,
            serviceEndpoint: "http://sdf.ndbc.noaa.gov/sos/server.php", 
            sensorOfferingId: "station-55013",
            observedProperty: "http://mmisw.org/ont/cf/parameter/sea_floor_depth_below_sea_surface"
        },
        {
            id: 50,
            type: "ALERT_IRREGULAR_DATA_DELIVERY",
            detail: "Varying or irregular data delivery identified",
            dateCreated: "2013-03-18T01:05:38Z",
            lastUpdated: "2013-03-18T01:05:39Z",
            validFrom: "2013-03-17T01:05:38Z",
            validTo: "2013-03-18T01:05:38Z",
            latLower: -5.36,
            latUpper: -5.36,
            lonLower: 165.05,
            lonUpper: 165.05,
            serviceEndpoint: "http://sdf.ndbc.noaa.gov/sos/server.php", 
            sensorOfferingId: "station-52406",
            observedProperty: "http://mmisw.org/ont/cf/parameter/sea_floor_depth_below_sea_surface"
        }
    ]
}
</pre>

	<p>The value of the "alerts" key is an array of maps
	representing each alert. In this verion of the API several pieces of information is provided for each alert:

	  <ul>
	    <li>The ID (<em>e.g. used to view details about the alert</em>)</li>
	    <li>The alert type</li>
	    <li>Alert details as a regular string</li>
	    <li>The date the alert was created</li>
	    <li>The date the alert was last modified (unlikely to be different from when the alert was created)</li>
	    <li>When the alert is valid from</li>
	    <li>When the alert is valid to</li>
	    <li>The lower latitude point of the alert bounding box (<em>if valid</em>)</li>
	    <li>The upper latitude point of the alert bounding box (<em>if valid</em>)</li>
	    <li>The lower longitude point of the alert bounding box (<em>if valid</em>)</li>
	    <li>The upper longitude point of the alert bounding box (<em>if valid</em>)</li>
	    <li>The sensor offering gml:ID (<em>if valid</em>)</li>
	    <li>The relevant observed property (<em>if valid</em>)</li>
	  </ul>

	</p>

	<h4>Endpoint</h4>

	<table class="table table-striped">
	  <thead>
	  </thead>
	  <tbody>
	    <tr>
	      <td>http://[communityhub]<strong>/apiv1/alerts/[group id]/</strong></td>
	      <td><g:link class="btn btn-info btn-small btn-block" 
			  controller="apiv1" action="alerts" id="1" params="[max: 5, offset: 0]">Demo</g:link></td>
	    </tr>
	  </tbody>
	</table>

	<h4>Options</h4>
	
	The following options can be sent as query string parameters: 

	<table class="table table-striped">
	  <thead>
	    <tr>
	      <th>Param</th>
	      <th>Comment</th>
	    </tr>
	  </thead>
	  <tbody>
	    <tr>
	      <td>max</td>
	      <td>The maximun number of alerts to return</td>
	    </tr>
	    <tr>
	      <td>offset</td>
	      <td>The offset in the list of available alerts to use</td>
	    </tr>
	  </tbody>
	</table>

	<h4>Errors</h4>
	
	<p>The following are examples of errors that may be returned:</p>
	
	<p>
<pre>
{
    status: "KO",
    error: "The group id has to be a positive integer"
}
</pre>
	</p>

	<p>
<pre>
{
    status: "KO",
    error: "A group with the id 112 cannot be found"
}
</pre>
	</p>

      </section>

    </article>
    
</body>
</html>


