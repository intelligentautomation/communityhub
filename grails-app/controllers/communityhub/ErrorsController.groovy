package communityhub

import grails.util.GrailsUtil

class ErrorsController {

	def serverError = {
         def env = GrailsUtil.environment;

         if (env == "production")
             render(view:'/errors/serverError')
         else
             render(view:'/errors/error')
     }

     def notFound = {
         render(view : "/errors/notFound")
     }
}
