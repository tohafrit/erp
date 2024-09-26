<div class="list_account-info__header">
    <h1 class="list_account-info__header_title">${identifier}</h1>
</div>
<div class="list_account-info__table-wrap">
    <div class="list_account-info__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const govContractId = parseInt('${id}');
        //
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        new Tabulator('div.list_account-info__table', {
            ajaxURL: ACTION_PATH.LIST_ACCOUNT_LOAD,
            ajaxRequesting: (url, params) => {
                params.govContractId = govContractId;
                params.filterData = JSON.stringify(filterData);
            },
            ajaxSorting: false,
            maxHeight: '100%',
            layout: 'fitDataStretch',
            columns: [
                { title: 'ОБС', field: TABR_FIELD.SEPARATE_ACCOUNT },
                { title: 'Банк', field: TABR_FIELD.BANK_INFO },
                { title: 'Заказчик', field: TABR_FIELD.COMPANY },
                {
                    title: 'Статус',
                    field: TABR_FIELD.STATUS,
                    resizable: false,
                    headerSort: false,
                    width: 100,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue())
                },
                {
                    title: 'Договор/дополнения',
                    field: TABR_FIELD.SECTION_INFO,
                    variableHeight: true,
                    minWidth: 300
                }
            ]
        });
    });
</script>