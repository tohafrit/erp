<div class="ui modal">
    <div class="header">Фильтр писем на производство</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Номер письма</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="number" data-inputmask-regex="[0-9]{0,6}"/>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Номер договора</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="contractNumber" data-inputmask-regex="[0-9]{0,8}"/>
                    </div>
                </div>
                <div class="field">
                    <label>Год договора</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-year" name="contractYear"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Изделие</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="productName"/>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Создано с</label>
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
            <div class="two fields">
                <div class="field">
                    <label>Отправлено в ОТК с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="sendToProductionDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="sendToProductionDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Отправлено на склад с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="sendToWarehouseDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="sendToWarehouseDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>