<div class="ui modal">
    <div class="ui small header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="five fields">
                <div class="six wide field">
                    <label>Накладная</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="number" data-inputmask-regex="[0-9]{0,8}"/>
                    </div>
                </div>
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
                <div class="field">
                    <label>Дата отгрузки с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="shipmentDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="shipmentDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Изделие</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="productName"/>
                    </div>
                </div>
                <div class="field">
                    <label>Серийный номер изделия</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="serialNumber"/>
                    </div>
                </div>
            </div>
            <div class="three fields">
                <div class="field">
                    <label>Договор</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="contractNumber" data-inputmask-regex="[0-9]{0,8}"/>
                    </div>
                </div>
                <div class="field">
                    <label>Год создания договора</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-year" name="contractYear"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label>Внешний номер договора</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="contractExternalNumber"/>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Расчетный счет</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="account"/>
                </div>
            </div>
            <div class="field">
                <label>Плательщик</label>
                <select name="payerId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${payerList}" var="opt">
                        <option value="${opt.id}">${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Грузополучатель</label>
                <select name="consigneeId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${consigneeList}" var="opt">
                        <option value="${opt.id}">${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
        </form>
    </div>
</div>