<div class="ui modal">
    <div class="header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Наименование</label>
                <input type="search" name="name"/>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата добавления с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="search" class="std-date" name="createDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="search" class="std-date" name="createDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>