<h1 class="b-heading"><fmt:message key="messageHistory.title"/></h1>
<table class="erp-datatable display compact hide-add-btn hide-excel-btn hide-print-btn">
    <thead>
        <tr>
            <th></th>
            <th><fmt:message key="messageHistory.dataTable.column.one"/></th>
            <th><fmt:message key="messageHistory.dataTable.column.two"/></th>
            <th><fmt:message key="messageHistory.dataTable.column.three"/></th>
            <th><fmt:message key="messageHistory.dataTable.column.four"/></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${messageHistoryList}" var="messageHistory" varStatus="status">
            <tr data-id="${messageHistory.id}">
                <td>
                    ${status.count}
                </td>
                <td>
                    ${messageHistory.fields}
                </td>
                <td>
                    <javatime:format value="${messageHistory.departureDate}" pattern="dd.MM.yyyy HH:mm:ss"/>
                </td>
                <td>
                    ${messageHistory.messageTemplate.messageType.name}
                </td>
                <td>
                    ${messageHistory.user.userOfficialName}
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>