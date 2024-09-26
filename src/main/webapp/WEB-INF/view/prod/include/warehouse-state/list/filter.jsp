<div class="ui modal list_filter__modal">
    <div class="ui small header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Изделие</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="product"/>
                </div>
            </div>
            <div class="field">
                <label>Серийный номер</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="serialNumber"/>
                </div>
            </div>
            <div class="field">
                <label>Отгружено</label>
                <select name="shipped" class="ui dropdown std-select">
                    <option value="false"><fmt:message key="text.no"/></option>
                    <option value="true"><fmt:message key="text.yes"/></option>
                </select>
            </div>
            <div class="field">
                <label>Местонахождение</label>
                <select name="placeId" class="ui dropdown search std-select">
                    <c:forEach items="${placeList}" var="opt">
                        <option value="${opt.id}">${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата поступления с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="receiptDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="receiptDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата отгрузки с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="shipmentDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="shipmentDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Номер договора</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="contractNumber" data-inputmask-regex="[0-9]{0,8}"/>
                    </div>
                </div>
                <div class="field">
                    <label>Год договора</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-year" name="contractYear"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Заказчик</label>
                <select name="customerId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${companyList}" var="opt">
                        <option value="${opt.id}">${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Письмо</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="letter" data-inputmask-regex="[0-9]{0,8}"/>
                </div>
            </div>
            <div class="field">
                <label>Ячейка</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="cell"/>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_filter__modal');
        const $shipmentSelect = $modal.find('select[name="shipped"]');
        const $shipmentDateFrom = $modal.find('input[name="shipmentDateFrom"]').closest('div.field');
        const $shipmentDateTo = $modal.find('input[name="shipmentDateTo"]').closest('div.field');
        const $receiptDateFrom = $modal.find('input[name="receiptDateFrom"]').closest('div.field');
        const $receiptDateTo = $modal.find('input[name="receiptDateTo"]').closest('div.field');
        const $place = $modal.find('select[name="placeId"]').closest('div.field');
        $shipmentSelect.on({
            'change': () => {
                const state = $shipmentSelect.find('option:selected').val() === 'false';
                $shipmentDateFrom.toggleClass('disabled', state);
                $shipmentDateTo.toggleClass('disabled', state);
                $receiptDateFrom.toggleClass('disabled', !state);
                $receiptDateTo.toggleClass('disabled', !state);
                $place.toggleClass('disabled', !state);
            }
        });
        $shipmentSelect.trigger('change');
    })
</script>