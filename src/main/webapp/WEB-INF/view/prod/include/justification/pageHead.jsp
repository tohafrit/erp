<h1 class="b-heading"><fmt:message key="${justificationType.property}"/></h1>
<div class="b-common-margin20">
    <select class="ui search dropdown" name="type" data-justification-type-value="${justificationType.type}">
        <c:forEach items="${justificationList}" var="item">
            <option value="${item.id}" <c:if test="${item.id eq justification.id}">selected="selected"</c:if>>
                ${item.name}
                <c:if test="${not empty item.date}">
                    <fmt:message key="justification.from"/> <javatime:format value="${item.date}" pattern="dd.MM.yyyy"/>
                </c:if>
            </option>
        </c:forEach>
    </select>
    <button class="js-justification compact ui button"><i class="fas fa-list-ul"></i></button>
    <input type="hidden" name="typeValue" value="${justification.id}"/>
</div>

<c:if test="${not empty justification}">
    <div class="b-common-margin20">
        <table class="b-table">
            <tr>
                <th class="b-table__th"><fmt:message key="justification.date"/></th>
                <td class="b-table__td"><javatime:format value="${justification.date}" pattern="dd.MM.yyyy"/></td>
            </tr>
            <c:if test="${fn:length(justification.note) > 0}">
                <tr>
                    <th class="b-table__th"><fmt:message key="justification.note"/></th>
                    <td class="b-table__td">${justification.note}</td>
                </tr>
            </c:if>
            <c:if test="${not empty justification.file}">
                <tr>
                    <th class="b-table__th"><fmt:message key="justification.document"/></th>
                    <td class="b-table__td"><a target="_blank" href="<c:url value="/download-file/${justification.file.storeHash}"/>"><fmt:message key="text.downloadFile"/></a></td>
                </tr>
            </c:if>
        </table>
    </div>
</c:if>

<script>
    $(() => {
        $('.js-justification').on({
            'click': () => {
                $.modalDialog({
                    dialogName: 'justification',
                    url: '/justification',
                    parameters: {
                        type: '${justificationType.type}'
                    }
                });
            }
        });
    });
</script>