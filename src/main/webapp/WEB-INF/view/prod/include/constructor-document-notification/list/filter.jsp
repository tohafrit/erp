<div class="ui modal">
    <div class="header">Фильтр извещений</div>
    <div class="content">
        <form class="ui small form">
            <div class="ui two column grid">
                <div class="column field">
                    <label>Номер</label>
                    <input type="text" name="docNumber"/>
                </div>
                <div class="column field">
                    <label>Дата выпуска</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="releaseOn"/>
                        </div>
                    </div>
                </div>
                <div class="column field">
                    <label>Срок изменения</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="termChangeOn"/>
                        </div>
                    </div>
                </div>
                <div class="column field">
                    <label>Причина</label>
                    <input type="text" name="reason"/>
                </div>
                <div class="column field">
                    <label>Указание о заделе</label>
                    <select class="ui dropdown search std-select" name="reserveIndication">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <option value="0">Не использовать</option>
                        <option value="1">Использовать</option>
                    </select>
                </div>
                <div class="column field">
                    <label>Указание о внедрении</label>
                    <input type="text" name="introductionIndication"/>
                </div>
                <div class="column field">
                    <label>Ведущий изделия</label>
                    <select class="ui dropdown search std-select" name="userId">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${userList}" var="user">
                            <option value="${user.id}">${user.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="column field">
                    <label>Название изделия</label>
                    <input type="text" name="conditionalName"/>
                </div>
                <div class="column field">
                    <label>Децимальный номер</label>
                    <input type="text" name="decimalNumber"/>
                </div>
            </div>
        </form>
    </div>
</div>