<jsp:include page="/pageHead">
    <jsp:param name="type" value="5"/>
</jsp:include>

<table class="erp-datatable display compact hide-excel-btn hide-print-btn hide-add-btn">
    <thead>
        <tr>
            <th></th>
            <th><fmt:message key="laboriousnessCalculation.product.name"/></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${productTechnicalProcessList}" var="productTechnicalProcess">
            <tr data-id="${productTechnicalProcess.id}">
                <td></td>
                <td>${productTechnicalProcess.product.conditionalName}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>