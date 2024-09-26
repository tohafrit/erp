<div class="list_distribution__header-container">
    <h1 class="list_distribution__header_title">Распределение изделий по договорам для письма № ${fullNumber} от ${createDate}</h1>
    <div class="list_distribution__header_buttons-expand">
        <i class="icon expand alternate link blue list_distribution__btn-expand" title="Развернуть"></i>
        <i class="icon compress alternate link blue list_distribution__btn-compress" title="Свернуть"></i>
    </div>
</div>
<div class="list_distribution__table-wrap">
    <div class="list_distribution__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const letterId = '${letterId}';
        //
        const $btnExpand = $('i.list_distribution__btn-expand');
        const $btnCompress = $('i.list_distribution__btn-compress');

        const table = new Tabulator('div.list_distribution__table', {
            ajaxURL: ACTION_PATH.LIST_DISTRIBUTION_LOAD,
            ajaxRequesting: (url, params) => {
                params.letterId = letterId;
            },
            selectable: 1,
            headerSort: false,
            maxHeight: '100%',
            ajaxSorting: true,
            groupBy: [ TABR_FIELD.GROUP_MAIN ],
            groupStartOpen: [ true, true ],
            groupToggleElement: 'header',
            layout: 'fitDataFill',
            groupHeader: [
                function(value) {
                    return '<span style="color:#315c83;">' + value + '</span>';
                }
            ],
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Комплектность поставки',
                    field: TABR_FIELD.PRODUCT_NAME,
                    variableHeight: true,
                    minWidth: 150,
                    width: 250,
                    resizable: false,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                },
                {
                    title: 'Пункт ведомости поставки',
                    field: TABR_FIELD.ORDER_INDEX,
                    hozAlign: 'center',
                    width: 200,
                    resizable: false
                },
                {
                    title: 'Кол-во',
                    field: TABR_FIELD.AMOUNT,
                    hozAlign: 'center',
                    width: 100,
                    resizable: false
                },
                {
                    title: 'Тип приемки',
                    field: TABR_FIELD.ACCEPT_TYPE_CODE,
                    hozAlign: 'center',
                    width: 100,
                    resizable: false
                },
                {
                    title: 'Тип спец. проверки',
                    field: TABR_FIELD.SPECIAL_TEST_TYPE_CODE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false
                },
                {
                    title: 'Дата поставки',
                    field: TABR_FIELD.DELIVERY_DATE,
                    hozAlign: 'center',
                    width: 130,
                    resizable: false,
                    formatter: 'stdDate'
                }
            ]
        });

        // Кнопка свернуть группы
        $btnCompress.on({
            'click': () => table.getGroups().forEach(group => group.hide())
        });

        // Кнопка развернуть группы
        $btnExpand.on({
            'click': () => table.getGroups().forEach(group => group.show())
        });
    });
</script>