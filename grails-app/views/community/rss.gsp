<?xml version="1.0" encoding="UTF-8" ?>
<rss version="2.0">
<channel>
        <title>${group.name} Alerts</title>
        <description>Alerts feed for Community Group ${group.name}</description>
        <link>http://communityhub.i-a-i.com/community/feed/${group.id}/</link>
        <lastBuildDate><g:formatDate date="${new Date()}" format="EEE, dd MMM yyyy HH:mm:ss Z" /></lastBuildDate>
        <pubDate><g:formatDate date="${new Date()}" format="EEE, dd MMM yyyy HH:mm:ss Z" /></pubDate>
        <ttl>1800</ttl>

  <g:each var="alert" in="${alerts}">
    <item>
      <title>${alert.getTypePretty()}<g:ifAlert rule="${alert}" type="service"> (<g:getServiceUrl rule="${alert}" />)</g:ifAlert><g:ifAlert rule="${alert}" type="irregular"> (${alert.offering})</g:ifAlert></title>
      <link>${createLink(absolute: true, controller: 'alert', action: 'id', params: [id: alert.id])}</link>
      <guid>${createLink(absolute: true, controller: 'alert', action: 'id', params: [id: alert.id])}</guid>
      <pubDate><g:formatDate date="${alert.timestamp}" format="yyyy-MM-dd HH:mm:ssZ" /></pubDate>
      <author>
          <name>Community Hub</name>
      </author>
      <description type="html" xml:lang="en"><![CDATA[
	  A ${alert.getTypePretty()} alert was generated for offering ${alert.offering}. 
	  ]]></description>
      <g:if test="${alert.latitude && alert.longitude}">
      <georss:point>${alert.latitude} ${alert.longitude}</georss:point>
      </g:if>
    </item>
  </g:each>

</channel>
</rss>


