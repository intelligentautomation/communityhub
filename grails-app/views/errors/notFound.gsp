<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Page not found</title>
    <%-- Bootstrap --%>
    <r:require modules="bootstrap" />
    <g:javascript library="jquery" plugin="jquery" />
    <r:layoutResources />    
  </head>
  <body>

    <div class="alert alert-block alert-error fade in">
      <h4 class="alert-heading">
        Page not found
      </h4>
      <p>The requested page could not be found, please check the URL</p>
      <p><g:link controller="alert">Go home</g:link></p>
    </div>

  </body>
</html>
