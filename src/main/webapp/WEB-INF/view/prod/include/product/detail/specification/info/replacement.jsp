<div class="detail_specification_info_replacement__header">
    <h1 class="detail_specification_info_replacement__header_title">Допустимые замены</h1>
    <div class="detail_specification_info_replacement__table_buttons">
        <i class="icon add link blue detail_specification_info_replacement__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="detail_specification_info_replacement__table-wrap">
    <div class="detail_specification_info_replacement__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const bomItemId = '${bomItemId}';
        const scroll = '${scroll}' === 'true';
        const $btnAdd = $('i.detail_specification_info_replacement__btn-add');
        const $datatable = $('div.detail_specification_info_replacement__table');
        const specTabulator = Tabulator.prototype.findTable('div.detail_specification_info__spec-table')[0];

        const datatable = new Tabulator('div.detail_specification_info_replacement__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/product/detail/specification/info/replacement/list-load',
            ajaxRequesting: (url, params) => {
                params.bomItemId = bomItemId;
                params.mainComponentId = '${mainComponentId}';
            },
            ajaxResponse: (url, params, response) => {
                if (scroll) {
                    setTimeout(() => specTabulator.scrollToRow(bomItemId, 'top', false), 100);
                }
                return response;
            },
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Позиция', field: 'position', headerSort: false },
                { title: 'Наименование', field: 'name', headerSort: false },
                { title: 'Производитель', field: 'producer', headerSort: false },
                {
                    title: 'Описание',
                    field: 'description',
                    variableHeight: true,
                    headerSort: false,
                    width: 300,
                    minWidth: 300,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                },
                {
                    title: 'КД',
                    field: 'kd',
                    headerSort: false,
                    hozAlign: 'center',
                    vertAlign: 'middle',
                    formatter: cell => booleanToLight(cell.getValue())
                },
                {
                    title: 'Закупать в изделии',
                    field: 'purchaseInProduct',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    vertAlign: 'middle',
                    formatter: cell => booleanToLight(cell.getValue())
                },
                {
                    title: 'Статус',
                    field: 'status',
                    headerSort: false,
                    hozAlign: 'center',
                    vertAlign: 'middle'
                },
                {
                    title: 'Закупка',
                    field: 'purchase',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    vertAlign: 'middle',
                    formatter: cell => booleanToLight(cell.getValue())
                },
                {
                    title: 'Дата замены',
                    field: 'replacementDate',
                    hozAlign: 'center',
                    vertAlign: 'middle',
                    headerSort: false,
                    formatter: cell => dateStdToString(cell.getValue())
                },
                {
                    title: 'Дата изменения статуса',
                    field: 'statusDate',
                    hozAlign: 'center',
                    vertAlign: 'middle',
                    headerSort: false,
                    formatter: cell => dateStdToString(cell.getValue())
                }
            ],
            rowContextMenu: row => {
                datatable.deselectRow();
                row.select();
                const menu = [];
                const data = row.getData();
                //
                const id = data.id;
                const purchaseInProduct = data.purchaseInProduct;
                menu.push({
                    label: `<i class="dollar sign icon blue"></i>` + (purchaseInProduct ? 'Убрать из закупки в изделии' : 'Закупать в изделии'),
                    action: () => purchaseReplacement(id, purchaseInProduct)
                });
                menu.push({
                    label: `<i class="edit icon blue"></i>Редактировать статус`,
                    action: () => editStatus(id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteReplacement(id)
                });
                return menu;
            }
        });

        // Функция редактирования статуса
        function editStatus(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/product/detail/specification/info/replacement/edit-status',
                loadData: { id: id }
            });
        }

        // Действие замены к закупке
        function purchaseReplacement(id, purchaseInProduct) {
            $.post({
                url: '/api/action/prod/product/detail/specification/info/replacement/purchase-replacement',
                data: {
                    id: id,
                    bomItemId: bomItemId,
                    purchaseInProduct: purchaseInProduct
                },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(() => datatable.setData());
        }

        // Удаление замены
        function deleteReplacement(id) {
            confirmDialog({
                title: 'Удаление замены',
                message: 'Вы действительно хотите удалить замену?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/product/detail/specification/info/replacement/delete-replacement/' + id + '/' + bomItemId,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => datatable.setData())
            });
        }

        // Добавление замены
        $btnAdd.on({
            'click': () => $.modalWindow({
                loadURL: '/api/view/prod/product/detail/specification/info/replacement/add-component',
                loadData: { bomItemId: bomItemId }
            })
        });

        // Ивенты для взаимодействия с табулятором
        $datatable.on({
            'reload': () => datatable.setData()
        });
    });
</script>