<div class="ui modal">
    <div class="header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Наименование</label>
                <input type="search" name="periodName"/>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата начала с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="search" class="std-date" name="startDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="search" class="std-date" name="startDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Изделие</label>
                <input type="search" name="productName"/>
            </div>
        </form>
    </div>
</div>