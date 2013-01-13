<g:if test="${properties}">
	<g:each in="${properties}" var="property">
	    <button
	       data-property="${property}" 
	       class="btn btn-full btn-link">
	      <g:niceProperty>${property}</g:niceProperty>
	    </button>
	</g:each>
</g:if>
<g:else>
    <p><i>There are no observed properties available.</i></p>
</g:else>
