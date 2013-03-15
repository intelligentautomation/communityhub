<?xml version="1.0" encoding="UTF-8" ?>
<feed xmlns="http://www.w3.org/2005/Atom"
	  xmlns:georss="http://www.georss.org/georss">
  <title>${group.name} Alerts</title>
  <link href="http://communityhub.i-a-i.com/community/feed/${group.id}/" rel="self"/>
  <link href="http://communityhub.i-a-i.com" />
  <id>http://communityhub.i-a-i.com/community/feed/${group.id}/</id>
  <updated><g:formatDate date="${new Date()}" format="yyyy-MM-dd HH:mm:ssZ" /></updated>

  <g:each var="alert" in="${alerts}">
    <entry>
      <title>${alert.getTypePretty()}<g:ifAlert rule="${alert}" type="service"> (${alert.service.endpoint})</g:ifAlert><g:ifAlert rule="${alert}" type="irregular"> (${alert.offering})</g:ifAlert></title>
      <link href="${createLink(absolute: true, controller: 'alert', action: 'id', params: [id: alert.id])}" />
      <id>${createLink(absolute: true, controller: 'alert', action: 'id', params: [id: alert.id])}</id>
      <published><g:formatDate date="${alert.dateCreated}" format="yyyy-MM-dd HH:mm:ssZ" /></published>
      <%-- RSS format for javaxt-rss library --%>
      <pubDate><g:formatDate date="${alert.dateCreated}" format="yyyy-MM-dd HH:mm:ssZ" /></pubDate>
      <summary>${alert.detail}</summary>
      <author>
          <name>Community Hub</name>
      </author>
      <content type="html" xml:lang="en"><![CDATA[
	  A ${alert.getTypePretty()} alert was generated for offering ${alert.offering}. 
	  ]]></content>
      <g:if test="${alert.latLower && alert.lonLower && alert.latUpper && alert.lonUpper}">
	<g:if test="${alert.latLower == alert.latUpper && alert.lonLower == alert.lonUpper}">
	  <georss:point>${alert.latLower} ${alert.lonLower}</georss:point>
	</g:if>
	<g:else>
	  <georss:box>${alert.latLower} ${alert.lonLower} ${alert.latUpper} ${alert.lonUpper}</georss:box>
	</g:else>
      </g:if>
    </entry>
  </g:each>

</feed>


