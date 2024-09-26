<h1 class="b-heading"><fmt:message key="purchase.menu.obligation.currentLaunch"/></h1>
<div class="js-snapshot-container">
    <div class="b-common-margin10 b-common-fl-left">
        <span class="b-common-label"><fmt:message key="purchase.snapshot.label"/>&nbsp;</span>
        <span class="js-snapshot-parameter">
            <select class="ui dropdown label">
                <c:forEach items="${snapshotParameterList}" var="snapshotParameter">
                    <option value="${snapshotParameter.id}"><fmt:message key="${snapshotType.property}"/> <javatime:format value="${snapshotParameter.generateOn}" pattern="dd.MM.yyyy HH:mm:ss"/></option>
                </c:forEach>
            </select>
        </span>
        <div class="ui tiny basic icon buttons">
            <button title="<fmt:message key="label.button.refresh"/>" class="ui button js-refresh-snapshot" type="button"><i class="blue sync alternate icon"></i></button>
        </div>
    </div>
    <div class="b-common-margin20">
        <table class="erp-obligation-datatable display compact hide-search-btn hide-add-btn">
            <thead>
                <tr>
                    <th></th>
                    <th><fmt:message key="purchase.currentLaunch.obligation.field.productName"/></th>
                    <th><fmt:message key="purchase.currentLaunch.obligation.field.version"/></th>
                    <th><fmt:message key="purchase.currentLaunch.obligation.field.prefix"/></th>
                    <th><fmt:message key="purchase.currentLaunch.obligation.field.amount"/></th>
                    <th><fmt:message key="purchase.currentLaunch.obligation.field.reserve"/></th>
                    <th><fmt:message key="purchase.currentLaunch.obligation.field.approved"/></th>
                    <th><fmt:message key="purchase.currentLaunch.obligation.field.accepted"/></th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>

<script>
    $(() => {
        const
            $container = $('.js-snapshot-container'),
            $snapshotDataTable = $container.find('.erp-obligation-datatable'),
            $snapshotParameter = $container.find('.js-snapshot-parameter'),
            $refreshBtn = $container.find('.js-refresh-snapshot'),
            $preloader = $('.purchase-information-modal-window-preloader'),
            optionText = '<fmt:message key="${snapshotType.property}"/>',
            snapshotTypeId = '${snapshotType.id}';

        let $datatable = $snapshotDataTable.DataTable({
            processing: true,
            serverSide: true,
            paging: false,
            searching: false,
            keys: {
                blurable: false,
                columns: ':not(:eq(0))'
            },
            select: false,
            scrollY: '70vh',
            columns: [
                { defaultContent: '' },
                { data: 'product.productName', name: 'productName', defaultContent: '' },
                { data: 'bom.version', name: 'version', defaultContent: '' },
                { data: 'sapsanProductPrefix', name: 'sapsanProductPrefix', orderable: false, defaultContent: '' },
                { data: 'count', name: 'count', defaultContent: '' },
                { data: 'reserveAmount', name: 'reserveAmount', defaultContent: '' },
                {
                    name: 'launch',
                    defaultContent: '',
                    orderable: false,
                    render: (data, type, row) => row.approved.launch ? row.approved.launch.numberInYear : 'Нет запусков'
                },
                {
                    data: 'accepted',
                    name: 'accepted',
                    orderable: false,
                    className: 'dt-body-center',
                    defaultContent: '',
                    render: (data, type, row) => booleanToLight(row.accepted, { onTrue: 'Активный', onFalse: 'Архивный' })
                }
            ],
            ajax: {
                type: 'POST',
                data: data => {
                    data.snapshotParameterId = $snapshotParameter.find('option:selected').val();
                    data.snapshotTypeId = snapshotTypeId;
                },
                url: '/ajaxLoadSnapshot/obligationLaunch'
            }
        });

        // Перезагрузка таблицы
        $refreshBtn.on({
            'click': e => {
                e.preventDefault();
                $.post({
                    url: '/refreshSnapshot',
                    data: {
                        purchaseId: '${purchaseId}',
                        snapshotTypeId: snapshotTypeId
                    },
                    beforeSend: () => $preloader.show()
                }).done(data => {
                    let select = '<select class="ui dropdown label">';
                    $.each(data, function(k,v) {
                        select += '<option value="' + v.id + '">' + optionText + ' ' + dateTimeStdToString(v.generateOn) + '</option>';
                    });
                    select += '</select>';
                    $snapshotParameter.html(select).find('select').addClass('search').dropdown({ fullTextSearch: true });
                }).fail(() => {
                    globalMessage({message: 'Ошибка обновления слепка'});
                }).always(() => {
                    $preloader.hide();
                    $datatable.ajax.reload();
                }).then(() => {});
            }
        });

        // Смена слепка
        $snapshotParameter.on({
            'change': () => $datatable.ajax.reload()
        });
    });
</script>