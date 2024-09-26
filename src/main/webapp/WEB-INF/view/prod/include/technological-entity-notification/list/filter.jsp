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
                    <label>Причина</label>
                    <select class="ui dropdown search std-select" name="reason">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${reasonTypeList}" var="reasonType">
                            <option value="${reasonType.id}">${reasonType.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="column field">
                    <label>Дата выпуска c</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="releaseOnFrom"/>
                        </div>
                    </div>
                </div>
                <div class="column field">
                    <label>Срок изменения c</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="termChangeOnFrom"/>
                        </div>
                    </div>
                </div>
                <div class="column field">
                    <label>по</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="releaseOnTo"/>
                        </div>
                    </div>
                </div>
                <div class="column field">
                    <label>по</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="termChangeOnTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="ui two column grid">
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
                    <label>Технолог</label>
                    <select class="ui dropdown search std-select" name="techUserId">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${userList}" var="user">
                            <option value="${user.id}">${user.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="column field">
                    <label>Текст</label>
                    <input type="text" name="text"/>
                </div>
                <div class="column field">
                    <label>Номер ТД</label>
                    <input type="text" name="entityNumber"/>
                </div>
            </div>
        </form>
    </div>
</div>