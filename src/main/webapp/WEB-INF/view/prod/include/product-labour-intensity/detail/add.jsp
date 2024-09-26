<div class="ui modal detail_add__modal">
    <div class="header">Добавление трудоемкостей. Выбор excel файла</div>
    <div class="content">
        <div class="detail_add__file-block">
            <div class="ui action input fluid detail_add__file std-file">
                <input type="file" name="file" accept=".xls,.xlsx"/>
            </div>
            <div class="ui small button disabled detail_add__btn-file-import" title="Выполнить импорт файла">
                <i class="file import blue icon"></i>
                Импорт
            </div>
        </div>
        <div class="detail_add__result-block">
            <div class="ui secondary pointing menu small">
                <a class="item detail_add__btn-result">
                    <span class="ui text"></span>
                    <i class="check green icon"></i>
                </a>
                <a class="item detail_add__btn-error">
                    <span class="ui text"></span>
                    <i class="exclamation circle red icon"></i>
                </a>
            </div>
            <div class="detail_add__result-table table-sm table-striped"></div>
            <div class="detail_add__error-table table-sm table-striped"></div>
        </div>
    </div>
    <div class="actions">
        <div class="ui small button detail_add__btn-apply disabled">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const CELL_TYPE_NEW = 'new';
        const CELL_TYPE_OLD = 'old';
        const labourIntensityId = '${id}';
        const $modal = $('div.detail_add__modal');
        const $fileInput = $('div.detail_add__file > input[type="file"]');
        const $btnFileImport = $('div.detail_add__btn-file-import');
        const $btnApply = $('div.detail_add__btn-apply');
        const $resultBlock = $('div.detail_add__result-block');
        const $btnResult = $('a.detail_add__btn-result');
        const $tableResult = $('div.detail_add__result-table');
        const $btnError = $('a.detail_add__btn-error');
        const $tableError = $('div.detail_add__error-table');
        const detailTable = Tabulator.prototype.findTable('div.detail__table')[0];

        $fileInput.on({
            'change': e => $btnFileImport.toggleClass('disabled', !e.target.files.length)
        });

        // Таблица результатов
        const tableResult = new Tabulator('div.detail_add__result-table', {
            height: 'calc(100vh * 0.7)',
            layout: 'fitColumns',
            headerSort: false,
            layoutColumnsOnNewData: true,
            groupBy: TABR_FIELD.PRODUCT_ID,
            groupHeader: (value, count, data) => {
                let result = 'Изделие - ' + data[0].productName;
                const productDecNumber = data[0].productDecNumber;
                if (productDecNumber) result += ' | ТУ - ' + productDecNumber;
                return result;
            },
            groupToggleElement: false,
            columns: [
                //TABR_COL_LOCAL_ROW_NUM, // TODO баг с bottomCalc при наличии frozen колонок (длинна строки отрисовывается некорректно)
                TABR_COL_ID,
                {
                    title: 'Работа',
                    field: TABR_FIELD.OP_NAME
                },
                {
                    title: 'Трудоемкость',
                    headerHozAlign: 'center',
                    columns: [
                        {
                            title: 'Исходная',
                            field: TABR_FIELD.OLD_VAL,
                            width: 150,
                            resizable: false,
                            formatter: cell => cellOldNewFormatter(cell, CELL_TYPE_OLD),
                            bottomCalc: 'sum',
                            bottomCalcFormatter: 'money',
                            bottomCalcFormatterParams: { decimal: ',', thousand: ' ' },
                            cellClick: (e, cell) => labourIntensityCellEvent(e, cell, CELL_TYPE_OLD)
                        },
                        {
                            title: 'Новая',
                            field: TABR_FIELD.NEW_VAL,
                            width: 150,
                            resizable: false,
                            formatter: cell => cellOldNewFormatter(cell, CELL_TYPE_NEW),
                            bottomCalc: 'sum',
                            bottomCalcFormatter: 'money',
                            bottomCalcFormatterParams: { decimal: ',', thousand: ' ' },
                            cellClick: (e, cell) => labourIntensityCellEvent(e, cell, CELL_TYPE_NEW)
                        },
                        {
                            title: 'Итоговая',
                            field: TABR_FIELD.FINAL_VAL,
                            width: 150,
                            resizable: false,
                            formatter: cell => cellFinalFormatter(cell),
                            bottomCalc: 'sum',
                            bottomCalcFormatter: 'money',
                            bottomCalcFormatterParams: { decimal: ',', thousand: ' ' }
                        }
                    ]
                }
            ]
        });

        // Таблица ошибок
        const tableError = new Tabulator('div.detail_add__error-table', {
            height: 'calc(100vh * 0.7)',
            layout: 'fitDataFill',
            headerSort: false,
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: '',
                    field: TABR_FIELD.MSG,
                    variableHeight: true,
                    minWidth: 600,
                    formatter: 'textarea'
                }
            ]
        });

        // Перерисовка цвета ячейки
        // Стиль ячеек - желтый для конфликта, зеленый - норма
        function drawCellBackground($cell, valid) {
            $cell.css({
                'background-color': valid ? '#c2d6b8' : '#fcfceb',
                'color': valid ? '#2c662d' : '#ada93d',
                'font-weight': 'bold'
            });
        }

        // Функция форматирования для ячеек трудоемкости (исходной и новой)
        function cellOldNewFormatter(cell, type) {
            const $cell = $(cell.getElement());
            const row = cell.getRow();
            const data = row.getData();
            const oldVal = data.oldVal;
            const newVal = data.newVal;
            const valid = data.valid;
            drawCellBackground($cell, valid);
            const visible = type === CELL_TYPE_NEW && valid && oldVal !== newVal ? 'inline' : 'none';
            return formatAsCurrency(cell.getValue()) + '<i class="check blue icon detail_add__check-icon" style="display: ' + visible + ';"></i>';
        }

        // Функция форматирования для ячеек итоговой трудоемкости
        function cellFinalFormatter(cell) {
            const $cell = $(cell.getElement());
            const row = cell.getRow();
            const data = row.getData();
            const oldVal = data.oldVal;
            const newVal = data.newVal;
            const finalVal = data.finalVal;
            const valid = data.valid;
            drawCellBackground($cell, valid);
            if (valid && oldVal !== newVal) {
                const $oldCell = $(row.getCells().find(cell => cell.getColumn().getField() === TABR_FIELD.OLD_VAL).getElement());
                const $newCell = $(row.getCells().find(cell => cell.getColumn().getField() === TABR_FIELD.NEW_VAL).getElement());
                drawCellBackground($oldCell, valid);
                drawCellBackground($newCell, valid);

                $oldCell.add($newCell).find('i.detail_add__check-icon').hide();
                let $targetCell;
                if (finalVal === oldVal) $targetCell = $oldCell;
                else if (finalVal === newVal) $targetCell = $newCell;
                if ($targetCell) $targetCell.find('i.detail_add__check-icon').show();
            }
            //setTimeout(() => tableResult.redraw(), 1); // TODO связано с перерисовкой bottomCalc
            return formatAsCurrency(cell.getValue());
        }

        // Функция нажатия на ячейку трудоемкости - заменяет итоговое значение, и разрешает конфликт для строки
        function labourIntensityCellEvent(e, cell, type) {
            e.stopPropagation();
            const row = cell.getRow();
            const data = row.getData();
            const oldVal = data.oldVal;
            const newVal = data.newVal;
            if (oldVal !== newVal) {
                let value = .0;
                if (type === CELL_TYPE_OLD) value = oldVal;
                else if (type === CELL_TYPE_NEW) value = newVal;
                row.update({ finalVal: -1 }); // для срабатывания форматтера (срабатывает только при изменении реального значения)
                row.update({ valid: true, finalVal: value }); // при этом апдейте срабатывает форматтер только для итоговой ячейки
            }
        }

        // Переключение вкладок
        $btnResult.on({
            'click': () => {
                $btnResult.addClass('active');
                $btnError.removeClass('active');
                $tableResult.show();
                $tableError.hide();
            }
        });
        $btnError.on({
            'click': () => {
                $btnError.addClass('active');
                $btnResult.removeClass('active');
                $tableError.show();
                $tableResult.hide();
            }
        });

        // Импорт файла
        $btnFileImport.on({
            'click': () => {
                $resultBlock.hide();
                $tableResult.hide();
                $tableError.hide();
                $btnError.removeClass('active');
                $btnError.show();

                const data = new FormData();
                data.append('labourIntensityId', labourIntensityId);
                data.append('file', $fileInput.get(0).files[0]);
                $.post({
                    url: ACTION_PATH.DETAIL_ADD_IMPORT,
                    enctype: 'multipart/form-data',
                    data: data,
                    contentType: false,
                    processData: false,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(data => {
                    const resultList = data.resultList;
                    const errorList = data.errorList;
                    $btnResult.addClass('active');
                    tableResult.setData(resultList).then(() => setTimeout(() => tableResult.redraw(), 1));
                    $btnResult.find('span').html('Результаты (' + resultList.length + ')');
                    $tableResult.show();
                    tableError.setData(errorList);
                    $btnError.find('span').html('Ошибки (' + errorList.length + ')');
                    if (!errorList.length) $btnError.hide();
                    $resultBlock.show();
                    $btnApply.toggleClass('disabled', !resultList.length);
                });
            }
        });

        // Применение изменений
        $btnApply.on({
            'click': () => {
                const data = tableResult.getData();
                const resultData = [];
                let productId = 0;
                let product;
                data.forEach(row => {
                    if (productId !== row.productId) {
                        productId = row.productId;
                        product = {
                            id: productId,
                            entryId: row.entryId,
                            operationList: []
                        };
                        resultData.push(product);
                    }
                    product.operationList.push({
                        id: row.entryOpId,
                        opId: row.opId,
                        oldVal: row.oldVal,
                        newVal: row.newVal,
                        finalVal: row.finalVal
                    });
                });
                const request = () => {
                    const formData = new FormData();
                    formData.append('labourIntensityId', labourIntensityId);
                    formData.append('data', JSON.stringify(resultData));
                    $.post({
                        url: ACTION_PATH.DETAIL_ADD_APPLY,
                        data: formData,
                        contentType: false,
                        processData: false,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        $modal.modal('hide');
                        detailTable.setSort(TABR_SORT_ID_DESC);
                        detailTable.setPage(1);
                    });
                }
                if (data.some(el => !el.valid)) {
                    confirmDialog({
                        title: '',
                        message: 'Разбор содержит неразрешенные конфликты. Все неразрешенные значения трудоемкости будут автоматически заменены на новые. Вы уверены, что хотите продолжить?',
                        onAccept: () => request()
                    });
                } else request()
            }
        });
    })
</script>