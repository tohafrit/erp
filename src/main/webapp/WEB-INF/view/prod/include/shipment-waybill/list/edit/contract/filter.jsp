<div class="ui modal">
    <div class="ui small header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Номер</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="number" data-inputmask-regex="[0-9]{0,8}"/>
                </div>
            </div>
            <div class="field">
                <label>Год</label>
                <div class="ui calendar">
                    <div class="ui input left icon std-div-input-search">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-year" name="year"/>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Внешний номер</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="externalNumber"/>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата создания с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="createDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="createDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Готов к отгрузке</label>
                <select name="ready" class="ui dropdown std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <option value="false"><fmt:message key="text.no"/></option>
                    <option value="true"><fmt:message key="text.yes"/></option>
                </select>
            </div>
            <div class="field">
                <label>Заказчик</label>
                <select name="customerId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${companyList}" var="opt">
                        <option value="${opt.id}">${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
        </form>
    </div>
</div>