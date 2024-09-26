<div class="ui modal list_report-first__modal">
    <div class="ui small header">Параметры формирования отчета о поступлении и отгрузке изделия за период</div>
    <div class="content">
        <form class="ui form">
            <div class="field required inline">
                <label>Изделие</label>
                <i class="add link blue icon list_report-first__btn-add-product" title="<fmt:message key="label.button.add"/>"></i>
                <i class="pen link blue icon list_report-first__btn-edit-product" title="<fmt:message key="label.button.edit"/>"></i>
                <i class="times link red icon list_report-first__btn-remove-product" title="<fmt:message key="label.button.delete"/>"></i>
                <input type="hidden" class="list_report-first__product-id" name="productId">
                <span class="ui text list_report-first__product-name"></span>
                <div class="ui compact message small error"></div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата периода с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="periodDateFrom" value="${periodDateFrom}"/>
                        </div>
                    </div>
                    <div class="ui compact message small error"></div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon std-div-input-search">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="periodDateTo" value="${periodDateTo}"/>
                        </div>
                    </div>
                    <div class="ui compact message small error"></div>
                </div>
            </div>
        </form>
    </div>
    <div class="actions">
        <div class="ui small button list_report-first__btn-exec">
            <i class="icon blue check"></i>
            Выполнить
        </div>
        <div class="ui small button list_report-first__btn-exec-close">
            <i class="icon blue check"></i>
            Выполнить и закрыть
        </div>
        <div class="ui small button list_report-first__btn-close">
            <i class="icon blue times"></i>
            Отмена
        </div>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_report-first__modal');
        const $btnExec = $('div.list_report-first__btn-exec');
        const $btnExecClose = $('div.list_report-first__btn-exec-close');
        const $btnClose = $('div.list_report-first__btn-close');
        const $productId = $('input.list_report-first__product-id');
        const $productName = $('span.list_report-first__product-name');
        const $btnAddProduct = $('i.list_report-first__btn-add-product');
        const $btnEditProduct = $('i.list_report-first__btn-edit-product');
        const $btnDeleteProduct = $('i.list_report-first__btn-remove-product');
        const $inputDateFrom = $modal.find('input[name="periodDateFrom"]');
        const $inputDateTo = $modal.find('input[name="periodDateTo"]');

        $productId.on({
            'change': () => {
                const val = $productId.val();
                $btnAddProduct.toggle(val === '');
                $btnEditProduct.toggle(val !== '');
                $btnDeleteProduct.toggle(val !== '');
            }
        });
        $productId.trigger('change');

        $btnAddProduct.add($btnEditProduct).on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.LIST_REPORT_FIRST_PRODUCT
            })
        });
        $btnDeleteProduct.on({
            'click': () => {
                $productId.val('');
                $productName.text('');
                $productId.trigger('change');
            }
        });

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
            usp.append(TABR_FIELD.TYPE, 33);
            usp.append(TABR_FIELD.ID, $productId.val());
            usp.append(TABR_FIELD.DATE_FROM, $inputDateFrom.val());
            usp.append(TABR_FIELD.DATE_TO, $inputDateTo.val());
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
            return checkFieldRequired($productId) & checkFieldRequired($inputDateFrom) & checkFieldRequired($inputDateTo);
        }
    })
</script>