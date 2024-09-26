<jsp:include page="/pageHead">
    <jsp:param name="type" value="5"/>
</jsp:include>

<table class="erp-datatable erp-contextmenu display compact add-clone-btn add-common-btn"
       data-order='[[2, "asc"]]'
       data-scroll-y='65vh'
       data-common-button-options='{"title":"Утвердить все", "confirmation":"Вы действительно хотите утвердить все?", "justificationId":${justification.id}}'
       data-clone-button-options='{"title":"Клонировать из обоснований", "justificationId":${justification.id}}'
>
    <thead>
        <tr>
            <th data-default-content=""></th>
            <th
                data-class-name="details-control"
                data-orderable="false"
            ></th>
            <th><fmt:message key="productTechnicalProcess.name"/></th>
            <th><fmt:message key="productTechnicalProcess.source"/></th>
            <th><fmt:message key="productTechnicalProcess.sum"/></th>
            <th><fmt:message key="productTechnicalProcess.approved"/></th>
            <th
                data-data="work"
                data-visible="false"
            ><fmt:message key="laboriousness.workType"/></th>
            <th
                data-data="value"
                data-visible="false"
            ><fmt:message key="laboriousness.value"/></th>
            <th
                data-data="package"
                data-visible="false"
            ><fmt:message key="laboriousness.withPackage"/></th>
            <th
                data-data="number"
                data-visible="false"
            ><fmt:message key="laboriousness.number"/></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${productTechnicalProcessList}" var="productTechnicalProcess">
            <tr data-id="${productTechnicalProcess.id}">
                <td></td>
                <td></td>
                <td>${productTechnicalProcess.fullName}</td>
                <td>${productTechnicalProcess.source}</td>
                <td>
                    <fmt:formatNumber groupingUsed="false" minFractionDigits="2" value="${productTechnicalProcess.sumWithoutPackage}"/>
                    <c:if test="${productTechnicalProcess.packageOperation}">
                        /
                        <fmt:formatNumber groupingUsed="false" minFractionDigits="2" value="${productTechnicalProcess.sumWithPackage}"/>
                    </c:if>
                </td>
                <td>
                    <i class="fas fa-circle ${productTechnicalProcess.approved ? 'b-color-green' : 'b-color-red'}"></i>
                </td>
                <td>${productTechnicalProcess.laboriousnessWorkTypeNames}</td>
                <td>${productTechnicalProcess.laboriousnessValues}</td>
                <td>${productTechnicalProcess.laboriousnessWithPackages}</td>
                <td>${productTechnicalProcess.laboriousnessNumbers}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>