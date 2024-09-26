<div class="ui modal">
    <div class="header">Фильтр изделий</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Изделие</label>
                <input type="search" name="productName"/>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата поставки с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="deliveryDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="deliveryDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>