<div class="ui modal">
    <div class="header">Фильтр журнала регистрации предъявлений</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Номер предъявления</label>
                <input type="search" name="presentLogRecordNumber"/>
            </div>
            <div class="field">
                <label>Изделие</label>
                <input type="search" name="productName"/>
            </div>
            <div class="field">
                <label>Номер письма</label>
                <input type="search" name="letterNumber"/>
            </div>
            <div class="field">
                <label>Номер договора</label>
                <input type="search" name="contractNumber"/>
            </div>
            <div class="field">
                <label>Заказчик</label>
                <input type="search" name="customer"/>
            </div>
            <div class="field">
                <label>Серийный номер</label>
                <input type="search" name="serialNumber"/>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Создано с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="dateRegisteredFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="dateRegisteredTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Пройден ОТК</label>
                <select class="ui dropdown std-select search" name="otkPassed">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <option value="true"><fmt:message key="text.yes"/></option>
                    <option value="false"><fmt:message key="text.no"/></option>
                </select>
            </div>
            <div class="field">
                <label>Упаковано</label>
                <select class="ui dropdown std-select search" name="packaged">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <option value="true"><fmt:message key="text.yes"/></option>
                    <option value="false"><fmt:message key="text.no"/></option>
                </select>
            </div>
        </form>
    </div>
</div>

<script>
    $(() => {
        const $serialNumber = $('input[name="serialNumber"]');

        $serialNumber.inputmask({
            placeholder: '',
            regex: '[0-9]{0,12}'
        });
    });
</script>