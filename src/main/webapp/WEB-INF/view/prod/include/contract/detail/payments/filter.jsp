<div class="ui modal">
    <div class="header">Фильтр платежей</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Номер п/п</label>
                <input name="paymentNumber" type="search"/>
            </div>
            <div class="field">
                <label>Номер счета</label>
                <input name="invoiceNumber" type="search"/>
            </div>
            <div class="column field">
                <div class="two fields">
                    <div class="field">
                        <label>Дата платежа с</label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <input class="std-date" name="paymentDateFrom" type="search"/>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label>
                            <fmt:message key="label.to"/>
                        </label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <input class="std-date" name="paymentDateTo" type="search"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>