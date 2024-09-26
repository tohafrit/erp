<div class="ui modal">
    <div class="header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Наименование</label>
                <input type="search" name="name"/>
            </div>
            <div class="field">
                <label>Документ</label>
                <input type="search" name="docName"/>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата утверждения с</label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input class="std-date" name="approvalDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input class="std-date" name="approvalDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>