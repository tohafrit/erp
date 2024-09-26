<h1 class="b-heading">
    <fmt:message key="routeMap.title">
        <fmt:param>
            <fmt:message key="${routeMapType.property}"/>
        </fmt:param>
    </fmt:message>
</h1>
<c:set var="className">
    <c:choose>
        <c:when test="${searchRouteMapForm.routeType eq 0}">
            erp-route-datatable erp-contextmenu
        </c:when>
        <c:otherwise>
            erp-product-route-datatable
        </c:otherwise>
    </c:choose>
</c:set>
<table class="${className} stripe compact cell-border"></table>