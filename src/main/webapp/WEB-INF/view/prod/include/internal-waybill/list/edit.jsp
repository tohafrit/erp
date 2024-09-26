<div class="ui modal">
    <div class="ui small header">
        ${empty id ? 'Добавление накладной' : 'Редактирование накладной'}
    </div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <c:if test="${not empty number}">
                <div class="field inline">
                    <label>Номер</label>
                    <span class="ui text">${number}</span>
                </div>
            </c:if>
            <c:if test="${not empty createDate}">
                <div class="field inline">
                    <label>Дата создания</label>
                    <span class="ui text">${createDate}</span>
                </div>
            </c:if>
            <c:if test="${not empty acceptDate}">
                <div class="field inline">
                    <label>Дата принятия</label>
                    <span class="ui text">${acceptDate}</span>
                </div>
            </c:if>
            <c:if test="${not empty giveUser}">
                <div class="field inline">
                    <label>Отпустил</label>
                    <span class="ui text">${giveUser}</span>
                </div>
            </c:if>
            <c:if test="${not empty acceptUser}">
                <div class="field inline">
                    <label>Получил</label>
                    <span class="ui text">${acceptUser}</span>
                </div>
            </c:if>
            <c:if test="${not empty id}">
                <div class="field inline">
                    <label>Место назначения</label>
                    <span class="ui text">${storagePlace}</span>
                </div>
            </c:if>
            <c:if test="${empty id}">
                <div class="field disabled">
                    <label>Место назначения</label>
                    <select name="storagePlaceId" class="ui dropdown search std-select">
                        <c:forEach items="${storagePlaceList}" var="opt">
                            <option value="${opt.id}">${opt.value}</option>
                        </c:forEach>
                    </select>
                    <div class="ui compact message small error" data-field="storagePlaceId"></div>
                </div>
            </c:if>
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