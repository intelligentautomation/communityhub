import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH

modules = {
    application {
        resource url:'js/application.js'
		resource url:'less/style.less',attrs:[rel: "stylesheet/less", type:'css']

		resource url: 'http://www.openlayers.org/api/OpenLayers.js', disposition: 'head'
    }
}
