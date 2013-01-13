<!doctype html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Report</title>
    
    
    <script type="text/javascript" charset="utf-8">
      <!--

	  $(document).ready(function() {

	  });

	-->
    </script>

  </head>
  <body>

    <g:if test="${group}">
      <p>
	<g:inFuture date="${dateNext}">
	  <a href="#" class="btn btn-small pull-right disabled">Next&nbsp;<i class="icon-arrow-right"></i></a>
 	</g:inFuture>
	<g:notInFuture date="${dateNext}">
	  <a href="?year=<g:formatDate format='yyyy' date='${dateNext}'/>&month=<g:formatDate format='MMM' date='${dateNext}'/>" 
	     class="btn btn-small pull-right">Next&nbsp;<i class="icon-arrow-right"></i></a>
	</g:notInFuture>

	<g:isThisMonth date="${date}">
	  <a href="#" class="btn btn-small pull-right disabled">Today</a>
	</g:isThisMonth>
	<g:isNotThisMonth date="${date}">
	  <a href="?year=<g:formatDate format='yyyy' date='${new Date()}'/>&month=<g:formatDate format='MMM' date='${new Date()}'/>"
	   class="btn btn-small pull-right">Today</a>
	</g:isNotThisMonth>

	<a href="?year=<g:formatDate format='yyyy' date='${datePrev}'/>&month=<g:formatDate format='MMM' date='${datePrev}'/>"
	   class="btn btn-small pull-right"><i class="icon-arrow-left"></i>&nbsp;Prev</a>
      </p>

      <p class="lead">
	Report for ${group.name}, 
	<strong>
	  <g:formatDate format="MMMM yyyy" date="${date}"/>
	</strong>
      </p>

      <g:if test="${usingDefault}">
	<div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	  <strong>Warning!</strong> Did not understand time selection, showing this month's report. 
	</div>
      </g:if>

      <h3>Services at a glance</h3>

      <g:if test="${mapServiceDown}">
	<table class="table table-striped">
	  <thead>
	    <tr>
	      <th style="width: 50%;">Service</th>
	      <th style="width: 50%;">Approximate time down</th>
	    </tr>
	  </thead>
	  <tbody>
	    <g:each var="key" in="${mapServiceDown}">
	      <tr 
		 <g:if test="${key.value <= 10}">class="success"</g:if>
		 <g:elseif test="${key.value > 10 && key.value < 60}">class="warning"</g:elseif>
		 <g:else>class="error"</g:else>
		 >
		<td>
		  <g:if test="${key.value == 0}">
		    <g:img dir="images" file="weather-sunny.png" />
		  </g:if>
		  <g:else>
		    <g:if test="${key.value > 60}">
		      <g:img dir="images" file="weather-clouds.png" />
		    </g:if>
		    <g:else>
		      <g:img dir="images" file="weather-cloudy.png" />
		    </g:else>
		  </g:else>
		  &nbsp;
		  <g:getServiceTitle serviceId="${key.key}" />
		</td>
		<td>
		  <g:each var="timeDown" in="${key.value}">
		    <g:if test="${key.value < 60}">
		      ${timeDown} minutes
		    </g:if>
		    <g:else>
		      ${(int)Math.floor(key.value / 60)} hours ${key.value % 60} minutes
		    </g:else>
		  </g:each>
		</td>
	      </tr>
	    </g:each>
	  </tbody>
	</table>
      </g:if>
      <g:else>
	<i>No service down alerts.</i>
      </g:else>

      <h3>Irregular data deliveries</h3>
    
      <g:if test="${mapCountIrregularOfferings}">
	<table class="table table-striped">
	  <thead>
	    <tr>
	      <th style="width: 50%;">Sensor Offering</th>
	      <th style="width: 50%;">Irregular data delivery alerts</th>
	    </tr>
	  </thead>
	  <tbody>
	    <g:each var="offering" in="${mapCountIrregularOfferings}">
	      <tr 
		 <g:if test="${offering.value <= 1}">class="success"</g:if>
		 <g:elseif test="${offering.value > 1 && offering.value < 8}">class="warning"</g:elseif>
		 <g:else>class="error"</g:else>
		 >
		<td>
		  <g:if test="${offering.value == 0}">
		    <g:img dir="images" file="weather-sunny.png" />
		  </g:if>
		  <g:else>
		    <g:if test="${offering.value > 5}">
		      <g:img dir="images" file="weather-clouds.png" />
		    </g:if>
		    <g:else>
		      <g:img dir="images" file="weather-cloudy.png" />
		    </g:else>
		  </g:else>
		  &nbsp;
		  ${offering.key}
		</td>
		<td>${offering.value}</td>
	      </tr>
	    </g:each>
	  </tbody>
	</table>
      </g:if>
      <g:else>
	<i>No irregular data delivery alerts found.</i>
      </g:else>


    </g:if>
    

  </body>
</html>


