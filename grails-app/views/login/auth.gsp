<html>
  <head>
    <meta name="layout" content="main"/>
    <title><g:message code="springSecurity.login.title"/></title>
  </head>

  <body>
    <div id="login">
      <div class="inner">

	<div class="alert alert-info">
	  <i class="icon-info-sign"></i>
	  If you do not have account, you may request one by sending
	  us an <a href="mailto:jhenriksson@i-a-i.com">email</a>.
	</div>

	<p class="lead">Login</p>
	
	<%--
	<div class="fheader"><g:message code="springSecurity.login.header"/></div>

	<g:if test="${flash.message}">
	  <div class="login_message">${flash.message}</div>
	</g:if>
	--%>

	<form id="loginForm" 
	      action="${postUrl}" method="POST" class="form-horizontal">
	  <div class="control-group">
	    <label class="control-label" for="username">
	      <g:message code="springSecurity.login.username.label"/>
	    </label>
	    <div class="controls">
	      <input type="text" id="username" placeholder="Username" 
		     name="j_username" />
	    </div>
	  </div>
	  <div class="control-group">
	    <label class="control-label" for="password">
	      <g:message code="springSecurity.login.password.label"/>
	    </label>
	    <div class="controls">
	      <input type="password" id="password" placeholder="Password"
		     name="j_password" />
	    </div>
	  </div>
	  <div class="control-group">
	    <div class="controls">
	      <label class="checkbox">
		<input type="checkbox" 
		       <g:if test="${hasCookie}">checked="checked"</g:if> 
		       name="${rememberMeParameter}" /> 
		<g:message code="springSecurity.login.remember.me.label"/>
	      </label>
	    </div>
	  </div>

	  <div class="form-actions">
	    <button type="submit" class="btn btn-primary"><i class="icon-user icon-white"></i> Login</button>
	  </div>

	</form>

      </div>
    </div>
    <script type="text/javascript">
      <!--
	  (function() {
	  document.forms["loginForm"].elements["j_username"].focus();
	  })();
	  // -->
    </script>
  </body>
</html>
