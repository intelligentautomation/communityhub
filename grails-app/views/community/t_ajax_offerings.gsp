<g:if test="${offerings}">
	<g:each var="offering" in="${offerings}">
	    <button data-offering="${offering}"  
	       class="btn btn-full btn-link">${offering}</button>
	</g:each>
</g:if>
<g:else>
    <p><i>There are no offerings for this service</i></p>
</g:else>
