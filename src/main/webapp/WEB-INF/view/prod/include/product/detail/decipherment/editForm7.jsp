<div class="ui modal">
    <div class="ui small header">Редактирование - форма ${formName}</div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <div class="field required">
                <label>Начальник производства</label>
                <select name="headProdId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq headProdId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="headProdId"></div>
            </div>
            <div class="field required">
                <label>Начальник планово-экономического отдела</label>
                <select name="headEcoId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq headEcoId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="headEcoId"></div>
            </div>
            <div class="field inline">
                <label>Дата создания</label>
                <span class="ui text">${createDate}</span>
            </div>
            <div class="field inline">
                <label>Создано</label>
                <span class="ui text">${createdBy}</span>
            </div>
            <div class="field inline">
                <label>Файл</label>
                <div class="std-file ui action input fluid">
                    <input type="hidden" name="fileId" value="${fileId}">
                    <input type="file" name="file"/>
                    <span>
                        ${fileName}
                        <a target="_blank" href="<c:url value="/download-file/${fileUrlHash}"/>">
                            <fmt:message key="text.downloadFile"/>
                        </a>
                    </span>
                </div>
                <div class="ui compact message small error" data-field="file"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <textarea name="comment" rows="3">${comment}</textarea>
                </div>
                <div class="ui compact message small error" data-field="comment"></div>
            </div>
        </form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>
