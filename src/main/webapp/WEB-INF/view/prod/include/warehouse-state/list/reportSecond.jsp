<div class="ui modal list_report-second__modal">
    <div class="ui small header">Параметры формирования отчета об отгрузке за месяц для ПЗ</div>
    <div class="content">
        <form class="ui form">
            <div class="field required">
                <label>Месяц</label>
                <div class="ui calendar">
                    <div class="ui input left icon std-div-input-search">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-month" name="date" value="${date}"/>
                    </div>
                </div>
                <div class="ui compact message small error"></div>
            </div>
            <div class="field required">
                <label>Представитель заказчика</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="customer" value="${customer}"/>
                </div>
                <div class="ui compact message small error"></div>
            </div>
            <div class="field required">
                <label>Начальник СГП</label>
                <select name="chiefId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="opt">
                        <option value="${opt.id}" <c:if test="${opt.id eq chiefId}">selected</c:if>>${opt.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error"></div>
            </div>
        </form>
    </div>
    <div class="actions">
        <div class="ui small button list_report-second__btn-exec">
            <i class="icon blue check"></i>
            Выполнить
        </div>
        <div class="ui small button list_report-second__btn-exec-close">
            <i class="icon blue check"></i>
            Выполнить и закрыть
        </div>
        <div class="ui small button list_report-second__btn-close">
            <i class="icon blue times"></i>
            Отмена
        </div>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_report-second__modal');
        const $btnExec = $('div.list_report-second__btn-exec');
        const $btnExecClose = $('div.list_report-second__btn-exec-close');
        const $btnClose = $('div.list_report-second__btn-close');
        const $inputDate = $modal.find('input[name="date"]');
        const $inputCustomer = $modal.find('input[name="customer"]');
        const $chiefSelect = $modal.find('select[name="chiefId"]');

        $btnExec.on({
            'click': () => {
                if (validate()) window.open(downloadURLString(), '_blank');
            }
        });

        $btnExecClose.on({
            'click': () => {
                if (validate()) {
                    window.open(downloadURLString(), '_blank');
                    $modal.modal('hide');
                }
            }
        });

        $btnClose.on({
            'click': () => $modal.modal('hide')
        });

        function downloadURLString() {
            const usp = new URLSearchParams();
            usp.append(TABR_FIELD.TYPE, 34);
            usp.append(TABR_FIELD.DATE, '01.' + $inputDate.val());
            usp.append(TABR_FIELD.CUSTOMER, $inputCustomer.val());
            usp.append(TABR_FIELD.CHIEF_ID, $chiefSelect.find('option:selected').val());
            return '/warehouse-documentation-formed/download?' + usp.toString();
        }

        function validate() {
            const $divErrors = $modal.find('div.ui.message.error');
            $divErrors.text('');
            $divErrors.removeClass('visible');
            $modal.find('div.field').removeClass('error');
            const checkFieldRequired = $elem => {
                if (!$elem.val().trim()) {
                    const $field = $elem.closest('div.field');
                    const $errorField = $field.find('div.ui.message.error');
                    $field.addClass('error');
                    $errorField.addClass('visible');
                    $errorField.text('обязательно для заполнения');
                    return false;
                }
                return true;
            };
            return checkFieldRequired($inputDate) & checkFieldRequired($inputCustomer) & checkFieldRequired($chiefSelect.find('option:selected'));
        }
    })
</script>