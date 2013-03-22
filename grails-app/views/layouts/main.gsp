<!DOCTYPE html>
<html lang="en">
  <head>
    <title><g:layoutTitle default="Community Hub"/></title>
    
    <%-- Bootstrap --%>
    <r:require modules="bootstrap" />
    <r:require modules="bootstrap-responsive-css" />

    <style>
      body {
      /* 60px to make the container go all the way to the bottom of the topbar */
      padding-top: 60px; 
      }
    </style>

    <%-- jQuery --%>
    <g:javascript library="jquery" plugin="jquery" />
    <script src="${resource(dir: 'js/', file: 'utils.js')}" 
	    type="text/javascript"></script>

    <r:layoutResources />

    <link rel="shortcut icon" type="image/png" 
	  href="${resource(dir: 'images', file: 'favicon.ico')}">
    <link rel="stylesheet" media="screen" 
	  href="${resource(dir: 'less/', file: 'style.less' )}">
    
    <g:layoutHead/>

  </head>
  <body>

    <!-- Navbar ================================================== -->
    <div class="navbar navbar-fixed-top"> 
      <div class="navbar-inner">
	<div class="container-fluid">

	  <!-- .btn-navbar is used as the toggle for collapsed navbar
	       content -->
	  <a class="btn btn-navbar" 
	     data-toggle="collapse" data-target=".nav-collapse">
	    <span class="icon-bar"></span>
	    <span class="icon-bar"></span>
	    <span class="icon-bar"></span>
	  </a> 

	  <a href="${createLink(controller: 'alert')}"
	     class="brand"><g:img dir="images" 
				  file="iai-logo-black.png" /></a>

	  <div class="nav-collapse collapse">
	    <ul class="nav">
	      <%--
	      <li class="${controllerName == null ? 'active' : '' }">
		<g:link controller="alert">Home</g:link>
	      </li>
	      --%>
	      <li class="${controllerName.equals('alert') ? 'active' : ''}">
		<g:link controller="alert">Alerts</g:link>
	      </li>
	      <li class="${controllerName.equals('community') ? 'active' : ''}">
		<g:link controller="community">Community</g:link>
	      </li>

	      <%-- Show service link if the user is an admin --%> 
	      <sec:access expression="hasRole('ROLE_ADMIN')">
	      <li class="${controllerName.equals('service') ? 'active' : ''}">
		<g:link controller="service">Services</g:link>
	      </li>
	      </sec:access>

	      <%-- Show admin link if the user is an admin --%> 
	      <sec:access expression="hasRole('ROLE_ADMIN')">
	      <li class="${controllerName.equals('admin') ? 'active' : ''}">
		<g:link controller="admin">Admin</g:link>
	      </li>
	      </sec:access>

	      <li class="divider-vertical"></li>
	      <sec:ifLoggedIn>
		<li class="dropdown" id="user-dropdown">
		  <a href="#user-dropdown" class="dropdown-toggle js-dropdown-toggle js-hover" id="user-dropdown-toggle" data-toggle="dropdown"><i class="icon-user"></i><sec:ifLoggedIn> <sec:username /></sec:ifLoggedIn></a>
		  <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu">
		    <li>
		      <g:link controller="logout">Logout</g:link>
		    </li>
		  </ul>
		</li>
	      </sec:ifLoggedIn>
	      <sec:ifNotLoggedIn>
		<li>
		  <g:link controller="login" 
			  action="auth" params="[url: "${request.forwardURI}"]">Login</g:link>
		</li>
	      </sec:ifNotLoggedIn>


	    </ul>
      
	  </div> <!--/.nav-collapse -->

	</div>
      </div>
    </div>

    <div class="container-fluid">

      <%-- messages --%>

      <g:if test="${flash.error}">
	<div class="alert alert-error fade in">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	  <strong>Error!</strong> 
	  <g:message code="${flash.error}" 
		     default="Sorry, an unknown error occured" />
	</div>
      </g:if>

      <g:if test="${flash.message}">
	<div class="alert alert-info fade in">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	  <strong>Info!</strong> 
	  <g:if test="${flash.message}">
	    <g:message code="${flash.message}" 
		       default="Sorry, an unknown message was delivered" />
	  </g:if>
	</div>
      </g:if>

      <%-- body --%>

      <g:layoutBody/>

    </div>

    <div id="footer">
      <p>
	<a class="secret" 
	   href="http://www.i-a-i.com">Intelligent Automation Inc.</a> 
	&copy; 
	2013
      </p>
      <p class="disabled">Version <g:meta name="app.version"/></p>
      <%--
      <p>&copy; Intelligent Automation Inc. 2013 &mdash;
        15400 Calhoun Drive, Suite 400, Rockville, MD 20855 &mid; Phone: 301 294 5200 &mid; Fax: 301 294 5201</p>
      --%>
    </div>

    <%-- bootstrap --%>
    <r:layoutResources/>

    <!-- Placed at the end of the document so the pages load faster -->
    <%--<script src="${resource(dir: 'bootstrap/js/', file: 'bootstrap.min.js')}"></script>--%>

  </body>
</html>
