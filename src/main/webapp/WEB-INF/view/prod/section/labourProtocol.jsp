<h1 class="b-heading"><fmt:message key="labourProtocol.title"/></h1>
<div class="b-common-margin20">
    <select name="type">
        <c:forEach items="${labourProtocolList}" var="item">
            <option value="${item.id}" <c:if test="${item.id eq labourProtocol.id}">selected="selected"</c:if>>${item.protocolNumber}</option>
        </c:forEach>
    </select>
    <input type="hidden" name="typeValue" value="${labourProtocol.id}"/>
</div>

<table class="erp-datatable erp-contextmenu display compact" data-responsive="true">
    <thead>
        <tr>
            <th></th>
            <th><fmt:message key="labour.number"/></th>
            <th><fmt:message key="labour.pay"/></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${labourPriceList}" var="labourPrice" varStatus="key">
            <tr data-id="${labourPrice.id}">
                <td>${key.count}</td>
                <td>${labourPrice.labour.labourName}</td>
                <td><fmt:formatNumber groupingUsed="false" minFractionDigits="2" value="${labourPrice.hourlyPay}"/></td>
            </tr>
        </c:forEach>
    </tbody>
</table>