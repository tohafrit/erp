<div class="ui modal fullscreen decipherment_product_invoice__modal">
    <div class="header"><fmt:message key="decipherment.form.productInvoice.title"/></div>
    <div class="content">
        <div class="b-product-invoice__container">
            <div class="ui tiny basic icon buttons">
                <button title="<fmt:message key="label.button.expand"/>" data-mode="plus" class="ui button js-expand-tree" type="button"><i class="expand icon"></i></button>
                <button title="<fmt:message key="label.button.collapse"/>" data-mode="minus" class="ui button js-compress-tree" type="button"><i class="compress icon"></i></button>
                <button title="<fmt:message key="decipherment.form.productInvoice.btn.autofill"/>" class="ui button js-invoice-autofill" type="button"><i class="blue undo icon"></i></button>
                <button title="Автоопределение файлов накладных" class="ui button js-invoice-file-autofill" type="button"><i class="blue file alternate outline icon"></i></button>
            </div>
            <div class="decipherment_product_invoice__total-block">
                <div>
                    Общая стоимость:
                    <span class="ui text decipherment_product_invoice__total-price">0.00</span>
                </div>
            </div>
            <div class="b-product-invoice__tree">
                <ul>
                    <c:set var="componentCount" value="1"/>
                    <c:forEach items="${groupList}" var="group">
                        <li class="b-product-invoice__group">
                            <div style="width: 30px;"><div class="ui icon mini js-group-toggle button"><i class="icon plus"></i></div></div>
                            <div style="width: 700px;">${group.nazGrp}</div>
                        </li>
                        <ul class="b-product-invoice__block" style="display: none;">
                            <table class="b-table b-full-width">
                                <thead>
                                    <tr>
                                        <th class="b-table__th b-align-mid-center" style="width: 40px;"><fmt:message key="decipherment.form.productInvoice.component.number"/></th>
                                        <th class="b-table__th" style="width: 65px;"><fmt:message key="decipherment.form.productInvoice.component.cell"/></th>
                                        <th class="b-table__th" style="width: 250px;"><fmt:message key="decipherment.form.productInvoice.component.ecoName"/></th>
                                        <th class="b-table__th" style="width: 250px;"><fmt:message key="decipherment.form.productInvoice.component.asuName"/></th>
                                        <th class="b-table__th" style="width: 60px;"><fmt:message key="decipherment.form.productInvoice.component.quantity"/></th>
                                        <th class="b-table__th" style="width: 80px;"><fmt:message key="decipherment.form.productInvoice.component.price"/></th>
                                        <th class="b-table__th" style="width: 80px;"><fmt:message key="decipherment.form.productInvoice.component.cost"/></th>
                                        <th class="b-table__th" style="width: 155px;"><fmt:message key="decipherment.form.productInvoice.component.invoice"/></th>
                                        <th class="b-table__th" style="width: 200px;">Файл</th>
                                        <th class="b-table__th" style="width: 100px;"><fmt:message key="decipherment.form.productInvoice.component.date"/></th>
                                        <th class="b-table__th" style="width: 200px;"><fmt:message key="decipherment.form.productInvoice.component.supplier"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${componentList}" var="component">
                                    <c:if test="${component.groupId eq group.id}">
                                        <tr
                                            data-id="${component.componentId}"
                                            data-cell="${component.cell}"
                                            data-quantity="${component.quantity}"
                                            data-invoice-id
                                        >
                                            <td class="b-table__td b-align-mid-center">${componentCount}</td>
                                            <td class="b-table__td b-align-mid-center">${component.cell}</td>
                                            <td class="b-table__td">${component.ecoName}</td>
                                            <td class="b-table__td">${component.asuName}</td>
                                            <td class="b-table__td b-align-mid-center">
                                                <fmt:formatNumber value ="${component.quantity}" minFractionDigits="0"/>
                                            </td>
                                            <td class="b-table__td b-align-mid-center js-price"></td>
                                            <td class="b-table__td b-align-mid-center js-cost"></td>
                                            <td class="b-table__td b-align-mid-center">
                                                <a class="b-link js-search-invoice"><fmt:message key="text.notSpecified"/></a>
                                                <i class="js-remove-invoice icon red times link" title="Убрать накладную" style="display: none;"></i>
                                            </td>
                                            <td class="b-table__td">
                                                <i class="icon redo alternate blue link js-invoice-file-auto" title="Автоопределение файла" style="display: none;"></i>
                                                <i class="icon red times link js-invoice-file-remove" title="Убрать файл" style="display: none;"></i>
                                                <a class="b-link js-invoice-file-label" style="display: none;"></a>
                                                <input type="file" class="js-invoice-file-input" style="display: none;"/>
                                            </td>
                                            <td class="b-table__td b-align-mid-center js-date"></td>
                                            <td class="b-table__td js-supplier"></td>
                                        </tr>
                                        <c:set var="componentCount" value="${componentCount + 1}"/>
                                    </c:if>
                                </c:forEach>
                                </tbody>
                            </table>
                        </ul>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
    <div class="actions">
        <button class="ui small button js-save-btn" type="button">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>

<style>
    .b-product-invoice__container {
        padding: 5px;
        width: 1480px;
        margin-bottom: 20px;
    }
    .b-product-invoice__container ul {
        padding: 0;
        margin: 0;
        list-style-type: none;
    }
    .b-product-invoice__tree {
        margin-top: 20px;
    }
    .b-product-invoice__group {
        border: #dddddd solid 1px;
        border-radius: 2px;
        font-weight: bold;
        color: #315c83;
        padding: 5px;
        background: #fffbf0;
        margin: 2px 0 2px 0;
    }
    .b-product-invoice__group > div {
        padding: 0 5px 0 5px;
        vertical-align: middle;
        word-break: break-all;
        display: inline-block;
    }
    .b-product-invoice__group > div .icon {
        font-size: 7px !important;
    }
    .b-product-invoice__block {
        padding-left: 20px;
    }
    .b-product-invoice__several-price {
        background: #fffec4;
    }
    .b-product-invoice__non-price {
        background: #ffe396;
    }
    .b-table {
        border: 1px solid #e1e1e1;
        background-color: #fff;
        color:#363636;
        word-break: break-all;
    }
    .b-full-width {
        width: 100%;
    }
    .b-table__th {
        border: 1px solid #e1e1e1;
        padding: 5px;
        background-color: #f9f9f9;
        color: #315c83;
        font-weight: bold;
        white-space: nowrap;
        vertical-align: middle;
        font-size:12px;
    }
    .b-table__td {
        border: 1px solid #e1e1e1;
        padding: 5px;
    }
    a.b-link, a.b-link:visited,
    a.b-link:focus, a.b-link:active {
        font-weight: bold;
        color: #287fc3;
        text-decoration: none;
        outline: none;
    }
    a.b-link:hover {
        cursor: pointer;
        text-decoration: underline;
    }
    .b-align-mid-center {
        vertical-align: middle;
        text-align: center;
    }
</style>

<script>
    $(() => {
        const deciphermentId = '${deciphermentId}';
        const $dialog = $('div.decipherment_product_invoice__modal');
        const $totalPrice = $('span.decipherment_product_invoice__total-price');
        const deciphermentTable = Tabulator.prototype.findTable('div.detail_decipherment__table')[0];

        // Список сохраненных накладных
        $.get({
            url: '/decipherment/product-invoice/attached-invoices',
            data: { deciphermentId: deciphermentId },
            beforeSend: () => togglePreloader(true),
            complete: () => togglePreloader(false)
        }).done(invoiceList => {
            invoiceList.forEach(el => {
                $dialog.find('tbody > tr[data-id=' + el.componentId + ']').trigger('setInvoice', [{
                    id: el.invoiceId,
                    name: el.name,
                    price: el.price,
                    supplier: el.supplier,
                    date: dateStdToString(el.date),
                    fileName: el.fileName,
                    filePath: el.filePath,
                    fileHash: el.fileHash
                }])
            });
        }).fail(() => {
            globalMessage({message: 'Ошибка загрузки сохраненых накладных'});
        });

        // Переключатели открыть/скрыть подизделия для каждого изделия (+/-)
        $dialog.find('.js-group-toggle').on({
            'click' : function () {
                const $icon = $(this).children('i'), $ul = $(this).closest('li').next('ul');
                $ul.toggle($icon.hasClass('plus'));
                $icon.toggleClass('plus minus');
            }
        });

        // Поиск накладной для компонента
        $dialog.find('.js-search-invoice').on({
            'click' : function () {
                const $tr = $(this).closest('tr');
                $.modalWindow({
                    loadURL: '/decipherment/product-invoice/search-invoice',
                    loadData: {
                        componentId: $tr.data('id'),
                        cell: $tr.data('cell'),
                        invoiceId: $tr.data('invoiceId'),
                        deciphermentId: deciphermentId
                    }
                });
            }
        });

        // Развернуть/Свернуть древо
        $dialog.find('.js-expand-tree, .js-compress-tree').on({
            'click' : function () {
                const mode = $(this).data('mode');
                $dialog.find('li').each(function () {
                    $(this).find('.js-group-toggle:has(i.' + mode + ')').trigger('click');
                });
            }
        });

        // Установка данных накладной для строки
        $dialog.find('tbody > tr').on({
            'setInvoice' : function (e, {id = '', name = '<fmt:message key="text.notSpecified"/>', price = '', supplier = '', date = '', fileName = '', filePath = '', fileHash = ''} = {}) {
                $(this).attr('data-invoice-id', id);
                $(this).data('invoiceId', id);
                $(this).find('.js-search-invoice').text(name);
                $(this).find('.js-date').text(date);
                $(this).find('.js-supplier').text(supplier);
                $(this).find('.js-remove-invoice').show();
                // Определение цены
                if (price !== '' && !isNaN(price)) {
                    price = Number(parseFloat(price).toFixed(2));
                }
                $(this).find('.js-price').text(price);
                // Посчет стоимости
                let quantity = $(this).data('quantity'), cost = '';
                if (quantity !== '' && price!== '' && !isNaN(price) && !isNaN(quantity)) {
                    cost = parseFloat(price)*parseFloat(quantity);
                    cost = Number(cost.toFixed(2));
                }
                $(this).find('.js-cost').text(cost);
                // Пересчет общего кол-ва
                let totalCost = 0;
                $dialog.find('.js-cost').each((idx, elem) => {
                    const txt = $(elem).text().trim();
                    totalCost += txt ? Number(txt) : 0;
                });
                $totalPrice.text(totalCost.toFixed(2));
                // Файл накладной
                const $fileAutoBtn = $(this).find('.js-invoice-file-auto');
                const $fileRemoveBtn = $(this).find('.js-invoice-file-remove');
                const $fileLabel = $(this).find('.js-invoice-file-label');
                if (id) {
                    $fileAutoBtn.show();
                    $fileRemoveBtn.show();
                    $fileLabel.text(fileName ? fileName : 'Обзор');
                    if (fileName) {
                        $fileLabel.data('fileName', fileName);
                    } else {
                        $fileLabel.removeData('fileName');
                    }
                    //
                    if (filePath) {
                        $fileLabel.data('filePath', filePath);
                    } else {
                        $fileLabel.removeData('filePath');
                    }
                    //
                    if (fileHash) {
                        $fileLabel.data('fileHash', fileHash);
                    }
                    $fileLabel.show();
                } else {
                    $fileAutoBtn.hide();
                    $fileRemoveBtn.hide();
                    $fileLabel.text('');
                    $fileLabel.removeData('fileName');
                    $fileLabel.removeData('filePath');
                    $fileLabel.hide();
                }
            }
        });

        // Ручное определение файла накладной
        $dialog.find('.js-invoice-file-label').on({
            'click' : function () {
                $(this).closest('td').find('.js-invoice-file-input').trigger('click');
            }
        });

        // При изменении файла накладной в ручном выборе
        $dialog.find('.js-invoice-file-input').on({
            'change' : function () {
                const $label = $(this).closest('td').find('.js-invoice-file-label');
                const file = $(this).get(0).files[0];
                if (file && file.name) {
                    $label.text(file.name);
                    $label.data('fileName', file.name);
                    $label.removeData('filePath');
                } else {
                    $(this).closest('td').find('.js-invoice-file-remove').trigger('click');
                }
            }
        });

        // Удаление файла накладной
        $dialog.find('.js-invoice-file-remove').on({
            'click' : function () {
                const $label = $(this).closest('td').find('.js-invoice-file-label');
                $label.text('Обзор');
                $label.removeData('fileName');
                $label.removeData('filePath');
                $label.removeData('fileHash');
                $(this).closest('td').find('.js-invoice-file-input').val('');
            }
        });

        // Автоопределение файла накладной
        $dialog.find('.js-invoice-file-auto').on({
            'click' : function () {
                const $tr = $(this).closest('tr');
                $(this).closest('td').find('.js-invoice-file-remove').trigger('click');
                const $label = $(this).closest('td').find('.js-invoice-file-label');
                const $invoiceNumber = $tr.find('.js-search-invoice');
                //
                const $invoiceSupplier = $tr.find('.js-supplier');
                let supplier = $invoiceSupplier.text();
                supplier = supplier ? supplier.trim() : supplier;
                //
                const $invoiceDate = $tr.find('.js-date');
                let dateStr = $invoiceDate.text();
                dateStr = dateStr ? dateStr.trim() : dateStr;
                if ($invoiceNumber.text()) {
                    $.get({
                        url: '/decipherment/product-invoice/auto-invoice-file',
                        data: {
                            fileData: JSON.stringify({ id: $tr.data('invoiceId'), number: $invoiceNumber.text().trim(), supplier: supplier, dateString: dateStr })
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(data => {
                        if (data) {
                            $label.text(data.fileName);
                            $label.data('fileName', data.fileName);
                            $label.data('filePath', data.filePath);
                        }
                    }).fail(() => globalMessage({message: 'Ошибка автопределения файла'}));
                }
            }
        });

        // Автоопределение файлов накладных
        $dialog.find('.js-invoice-file-autofill').on({
            'click' : function () {
                $dialog.find('.js-invoice-file-remove').trigger('click');
                const dataList = [];
                const $rowNumberList = $dialog.find('tbody .js-search-invoice');
                $rowNumberList.each((inx, elem) => {
                    const $tr = $(elem).closest('tr');
                    if ($tr.data('invoiceId')) {
                        let supplier = $tr.find('.js-supplier').text();
                        supplier = supplier ? supplier.trim() : supplier;
                        //
                        let dateStr = $tr.find('.js-date').text();
                        dateStr = dateStr ? dateStr.trim() : dateStr;
                        //
                        dataList.push({ id: $tr.data('invoiceId'), number: $(elem).text().trim(), supplier: supplier, dateString: dateStr });
                    }
                });
                if (dataList.length) {
                    $.post({
                        url: '/decipherment/product-invoice/auto-invoice-file-list',
                        data: { dataList: JSON.stringify(dataList) },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(fileSet => {
                        if (fileSet) {
                            $rowNumberList.each((inx, elem) => {
                                const $tr = $(elem).closest('tr');
                                const invoiceId = $tr.data('invoiceId');
                                if (invoiceId) {
                                    $.each(fileSet, (inx, elem) => {
                                        if (invoiceId === elem.id) {
                                            const $label = $tr.find('.js-invoice-file-label');
                                            $label.text(elem.fileName);
                                            $label.data('fileName', elem.fileName);
                                            $label.data('filePath', elem.filePath);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

        // Удаление накладной из строки
        $dialog.find('.js-remove-invoice').on({
            'click' : function () {
                $(this).closest('tr').trigger('setInvoice');
                $(this).closest('td').removeClass('b-product-invoice__several-price b-product-invoice__non-price');
                $(this).hide();
            }
        });

        // Автоподбор накладных
        $dialog.find('.js-invoice-autofill').on({
            'click' : () => {
                // Переменные для работы с автозагруженными накладными
                let invoiceList = [];
                let countCell = 0, totalCell = $dialog.find('tbody > tr').length;
                let $progressDialog, $progressBar, $cancelBtn;
                let isCancelLoad = false;
                // Данные для загрузки пусты
                if (totalCell === 0) {
                    return false;
                }

                // Диалог прогресса загрузки накладных
                $.modalWindow({
                    loadURL: '/decipherment/autofill-invoice/progress-bar',
                    onAfterClose: () => {
                        isCancelLoad = true;
                    },
                    onInitComplete: () => {
                        $dialog.find('.b-product-invoice__several-price').removeClass('b-product-invoice__several-price');
                        $dialog.find('.b-product-invoice__non-price').removeClass('b-product-invoice__non-price');
                        $dialog.find('.js-remove-invoice').trigger('click');
                        // Переменные прогресс бара
                        $progressDialog = $('.decipherment_auto_invoice__modal');
                        $progressBar = $progressDialog.find('.js-progress-bar');
                        $cancelBtn = $progressDialog.find('.js-cancel-btn');
                        // Отмена загрузки по нажатию отмены, или закрытию окна диалога
                        $cancelBtn.on({'click': () => $progressDialog.modal('hide')});
                        // Инициализация прогресс бара
                        $progressBar.progress({
                            label: false,
                            total: totalCell,
                            precision: 3,
                            text: {
                                error: 'Ошибка выполнения',
                                success: 'Завершено'
                            },
                            onSuccess: () => {
                                setInvoices();
                                $progressDialog.modal('hide');
                            }
                        });
                        loadComponentData();
                    }
                });

                // Функция загрузки данных по компонентам
                function loadComponentData() {
                    $.get({
                        url: '/decipherment/autofill-invoice/load-component-data',
                        data: { deciphermentId: deciphermentId }
                    }).done(componentData => {
                        loadCellInvoice(componentData);
                    }).fail(() => {
                        if (!isCancelLoad) $progressBar.progress('set error');
                    });
                }

                // Функция загрузки накладной для позиции
                function loadCellInvoice(componentData) {
                    if (isCancelLoad) return;
                    if (countCell < totalCell) {
                        let $cellData = $dialog.find('tbody > tr').eq(countCell);
                        $.get({
                            url: '/decipherment/autofill-invoice/load-invoice',
                            data: {
                                componentId: $cellData.data('id'),
                                cell: $cellData.data('cell'),
                                componentData: JSON.stringify(componentData),
                                deciphermentId: deciphermentId
                            }
                        }).done(invoice => {
                            invoiceList.push(invoice);
                            countCell++;
                            const percent = (Math.floor(countCell/totalCell*100));
                            $progressBar.progress('set percent', percent);
                            $progressBar.progress('set bar label', percent + '%');
                            $progressBar.progress('set label', 'Позиция ' + $cellData.data('cell'));
                            loadCellInvoice(componentData);
                        }).fail(() => {
                            if (!isCancelLoad) $progressBar.progress('set error');
                        });
                    }
                }

                // Функция установки накладных по ячейкам
                function setInvoices() {
                    $.each(invoiceList, function () {
                        if (this == null && this === '' && this.length !== 3) return;
                        let componentId = this[0], invoice = this[1], status = this[2];
                        let $row = $dialog.find('tbody > tr[data-id=' + componentId + ']');
                        if ($row.length === 1) {
                            let $invoiceTd = $row.find('.js-search-invoice').closest('td');
                            // Статус
                            if (status === 1) {
                                $invoiceTd.addClass('b-product-invoice__non-price');
                            } else if (status === 2) {
                                $invoiceTd.addClass('b-product-invoice__several-price');
                            }
                            if (invoice != null) {
                                $row.trigger('setInvoice', [{
                                    id: invoice.id,
                                    name: invoice.name,
                                    price: invoice.price,
                                    supplier: invoice.supplierName,
                                    date: dateStdToString(invoice.date)
                                }]);
                            }
                        }
                    });
                    $dialog.find('.js-expand-tree').trigger('click');
                }
            }
        });

        // Сохранение накладных
        $dialog.find('.js-save-btn').off();
        $dialog.find('.js-save-btn').on({
            'click': function () {
                let data = [];
                //
                let manuallyData = [];
                let manuallyFileData = [];
                $dialog.find('tbody > tr').each(function () {
                    const $label = $(this).find('.js-invoice-file-label');
                    let componentId = $(this).data('id'),
                        invoiceId = $(this).data('invoiceId'),
                        fileName = $label.data('fileName'),
                        filePath = $label.data('filePath'),
                        fileHash = $label.data('fileHash');
                    if (invoiceId) {
                        const elem = {
                            componentId: componentId,
                            invoiceId: invoiceId,
                            fileName: fileName ? fileName : null,
                            filePath: filePath ? filePath : null,
                            fileHash: fileHash ? fileHash : null
                        };
                        const file = $(this).find('.js-invoice-file-input').get(0).files[0];
                        if (file) {
                            manuallyFileData.push(file);
                            manuallyData.push(elem);
                        } else {
                            data.push(elem);
                        }
                    }
                });
                const formData = new FormData();
                formData.append("deciphermentId", deciphermentId);
                formData.append("data", JSON.stringify(data));
                formData.append("manuallyData", JSON.stringify(manuallyData));
                for (let i = 0; i < manuallyFileData.length; i++) {
                    formData.append("manuallyFileData", manuallyFileData[i]);
                }
                $.post({
                    url: '/decipherment/product-invoice/save-invoices',
                    data: formData,
                    contentType: false,
                    processData: false,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => {
                    deciphermentTable.setData();
                    $dialog.modal('hide');
                }).fail(() => {
                    globalMessage({message: 'Ошибка сохранения накладных'});
                });
            }
        });
    });
</script>