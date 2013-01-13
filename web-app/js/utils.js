/**
 * Returns the host name
 *
 */
function getHostName() {
  var host = document.location.hostname;
  var port = document.location.port;
  var protocol = document.location.protocol;
  if (port.length <= 0)
    port = "";
  else
    port = ":" + port;
  return protocol + '//' + host + port + '/'; 
}