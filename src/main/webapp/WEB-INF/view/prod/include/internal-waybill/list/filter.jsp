<div class="ui modal">
    <div class="ui small header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="three fields">
                <div class="field">
                    <label>МСН</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="number" data-inputmask-regex="[0-9]{0,3}"/>
                    </div>
                </div>
                <div class="field">
                    <label>Статус МСН</label>
                    <select name="status" class="ui dropdown search std-select">
                        <option value="1">Все</option>
                        <option value="2">Непринятые</option>
                    </select>
                </div>
                <div class="two fields">
                    <div class="field">
                        <label>Дата принятия с</label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="acceptDateFrom"/>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label><fmt:message key="label.to"/></label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="acceptDateTo"/>
                            </div>
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
                    <label>Письмо ПЭО</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="letter" data-inputmask-regex="[0-9]{0,8}"/>
                    </div>
                </div>
                <div class="field">
                    <label>Извещение</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="notice"/>
                    </div>
                </div>
                <div class="two fields">
                    <div class="field">
                        <label>Дата извещения с</label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="noticeDateFrom"/>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label><fmt:message key="label.to"/></label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="noticeDateTo"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="fields">
                <div class="three wide field">
                    <label>Номер договора</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="contractNumber" data-inputmask-regex="[0-9]{0,8}"/>
                    </div>
                </div>
                <div class="four wide field">
                    <label>Год создания договора</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-year" name="contractYear"/>
                        </div>
                    </div>
                </div>
                <div class="twelve wide field">
                    <label>Заказчик</label>
                    <select name="customerId" class="ui dropdown search std-select">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${companyList}" var="opt">
                            <option value="${opt.id}">${opt.value}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </form>
    </div>
</div>