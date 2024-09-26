<div class="list_edit_letter_info__header">
    <h1 class="list_edit_letter_info__header_title">Письмо ${number} от ${creationDate}</h1>
</div>
<div class="list_edit_letter_info__table-wrap">
    <div class="list_edit_letter_info__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const letterId = '${letterId}';

        const $modal = $('div.list_edit_letter__main');
        const $editModal = $('div.list_edit__main');
        const $selectBtn = $('button.list_edit_letter__btn-select');
        const $btnAdd = $editModal.find('i.list_edit__btn-add');
        const $btnAddSerialNumber = $editModal.find('i.list_edit__btn-add-serial-number');
        const $btnSelectSerialNumber = $editModal.find('i.list_edit__btn-add-select-number');
        const $maxSerialNumberQuantityInput = $editModal.find('input[name="maxSerialNumberQuantity"]');
        const $examActInput = $editModal.find('input.list_edit__field-examAct');
        const $familyDecimalNumberInput = $editModal.find('input.list_edit__field-familyDecimalNumber');
        const $suffixInput = $editModal.find('input.list_edit__field-suffix');
        const $allotmentId = $editModal.find('input[name="allotmentId"]');
        const $productId = $editModal.find('input[name="productId"]');
        const $examActDate = $editModal.find('input[name="examActDate"]');
        const listEditTable = Tabulator.prototype.findTable('div.list_edit__table')[0];

        const table = new Tabulator('div.list_edit_letter_info__table', {
            ajaxURL: ACTION_PATH.LIST_EDIT_LETTER_INFO_LOAD,
            ajaxRequesting: (url, params) => {
                params.letterId = letterId;
            },
            selectable: 1,
            ajaxSorting: false,
            maxHeight: '150px',
            layout: 'fitDataFill',
            initialSort: [{ column: TABR_FIELD.CUSTOMER, dir: 'asc' }],
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Заказчик', field: TABR_FIELD.CUSTOMER },
                { title: 'Договор', field: TABR_FIELD.FULL_NUMBER },
                { title: 'Комплектность поставки', field: TABR_FIELD.PRODUCT_NAME },
                { title: 'Пункт', field: TABR_FIELD.ORDER_INDEX },
                { title: 'Кол-во', field: TABR_FIELD.AMOUNT },
                { title: 'Тип приемки', field: TABR_FIELD.ACCEPT_TYPE },
                { title: 'Вид спец. проверки', field: TABR_FIELD.SPECIAL_TEST_TYPE },
                {
                    title: 'Дата поставки',
                    field: TABR_FIELD.DELIVERY_DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                }
            ],
            rowClick: () => $selectBtn.toggleClass('disabled', !table.getSelectedRows().length)
        });

        // Выбор пункта письма
        $selectBtn.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length) {
                    const amountArr = [];
                    data.forEach(el => amountArr.push({
                        maxSerialNumberQuantity: el.maxSerialNumberQuantity
                    }));
                    const arr = [];
                    data.forEach(el => arr.push({
                        id: el.id,
                        groupMain: 'Письмо № ' + el.letterNumber + ', ' + 'Пункт: ' + el.orderIndex + ', ' + 'Договор: ' + el.fullNumber,
                        groupSubMain: 'Изделие: ' + el.productName + ', ' + 'Тип приемки: ' + el.acceptType,
                        serialNumber: '',
                        productId: el.productId
                    }));
                    const productInfoArr = [];
                    data.forEach(el => productInfoArr.push({
                        productId: el. productId,
                        examAct: el.examAct,
                        examActDate: el.examActDate,
                        familyDecimalNumber: el.familyDecimalNumber,
                        suffix: el.suffix
                    }));
                    $examActInput.val(productInfoArr[0].examAct);
                    $examActDate.val(dateStdToString(productInfoArr[0].examActDate));
                    $productId.val(productInfoArr[0].productId);
                    $familyDecimalNumberInput.val(productInfoArr[0].familyDecimalNumber);
                    $suffixInput.val(productInfoArr[0].suffix);
                    $maxSerialNumberQuantityInput.val(amountArr[0].maxSerialNumberQuantity);
                    $allotmentId.val(arr[0].id)
                    listEditTable.addData(arr);
                    $btnAdd.hide();
                    $btnSelectSerialNumber.show();
                    $btnAddSerialNumber.show();
                    $modal.modal('hide');
                }
            }
        });
    });
</script>