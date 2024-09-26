<div class="ui modal">
    <div class="header">Фильтр периодов поставок компонентов</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Номер</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="number"/>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата создания с</label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="createDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="createDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата начала периода с</label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="firstDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="firstDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата окончания периода с</label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="lastDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="lastDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>