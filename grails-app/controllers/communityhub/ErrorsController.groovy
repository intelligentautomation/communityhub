/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
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
