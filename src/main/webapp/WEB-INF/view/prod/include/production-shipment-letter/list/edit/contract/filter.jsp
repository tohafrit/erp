<div class="ui modal">
    <div class="header">Фильтр договоров</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Номер</label>
                <input type="search" name="contractNumber"/>
            </div>
            <div class="field">
                <label>Заказчик</label>
                <input type="search" name="customer"/>
            </div>
            <div class="field">
                <label>Передано в ПЗ</label>
                <select class="ui dropdown std-select search" name="pzCopy">
                    <option value="true"><fmt:message key="text.yes"/></option>
                    <option value="false"><fmt:message key="text.no"/></option>
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                </select>
            </div>
            <div class="field">
                <label>Доступные</label>
                <select class="ui dropdown std-select search" name="available">
                    <option value="true"><fmt:message key="text.yes"/></option>
                    <option value="false"><fmt:message key="text.no"/></option>
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                </select>
            </div>
        </form>
    </div>
</div>