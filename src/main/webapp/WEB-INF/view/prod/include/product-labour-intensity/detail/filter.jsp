<div class="ui modal">
    <div class="header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Изделие</label>
                <div class="ui input std-div-input-search icon">
                    <input type="text" name="productName"/>
                </div>
            </div>
            <div class="field">
                <label>ТУ изделия</label>
                <div class="ui input std-div-input-search icon">
                    <input type="text" name="decimalNumber"/>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата добавления с</label>
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
                    <label>Дата утверждения с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="approvalDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="approvalDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>