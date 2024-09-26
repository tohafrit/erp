<div class="ui modal">
    <div class="header">Фильтр писем на производство</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Номер письма</label>
                <input type="search" name="number"/>
            </div>
            <div class="field">
                <label>Номер договора</label>
                <input type="search" name="contractNumber"/>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Создано с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="createDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="createDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>