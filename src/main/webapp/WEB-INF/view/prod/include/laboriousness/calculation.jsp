<h1 class="b-heading"><fmt:message key="calculation.title"/></h1>
<div class="b-common-margin20">
    <table class="b-table">
        <thead>
        <tr>
            <th class="b-table__th"><fmt:message key="calculation.product.name"/></th>
            <th class="b-table__th"><fmt:message key="calculation.product.evm"/></th>
            <th class="b-table__th"><fmt:message key="calculation.product.technologicalProcess"/></th>
            <th class="b-table__th"><fmt:message key="calculation.withPackage"/></th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td class="b-table__td">${laboriousnessCalculation.productTechnicalProcess.product.name}</td>
            <td class="b-table__td"><fmt:message key="${laboriousnessCalculation.productTechnicalProcess.product.evm ? 'text.yes' : 'text.no'}"/></td>
            <td class="b-table__td">${laboriousnessCalculation.productTechnicalProcess.fullName}</td>
            <td class="b-table__td b-align-mid-center">
                <div class="ui checkbox">
                    <input type="checkbox" class="js-calculation-package"
                           data-id="${laboriousnessCalculation.id}"
                           <c:if test="${laboriousnessCalculation.withPackage}">checked</c:if>
                    />
                </div>
            </td>
        </tr>
        </tbody>
    </table>
</div>

<div class="b-common-margin10 b-common-fl-left">
    <button class="ui button b-btn b-btn-add js-add-process" type="button" title="<fmt:message key="calculation.tree.action.add"/>"></button>
    <button class="ui button b-btn b-btn-expand-tree js-expand-tree" type="button" title="<fmt:message key="calculation.tree.action.expand"/>"></button>
    <button class="ui button b-btn b-btn-collapse-tree js-collapse-tree" type="button" title="<fmt:message key="calculation.tree.action.collapse"/>"></button>
</div>
<div class="b-common-margin10 b-common-fl-right b-common-display-inline-block">
    <span class="b-common-label"><fmt:message key="text.search"/> </span>
    <div class="ui input">
        <input type="search" class="js-search-process">
    </div>
</div>
<div class="js-calculation-tree-container b-calculation-tree-container">
    <jsp:include page="/calculationTreeList?mainEntityId=${laboriousnessCalculation.id}"/>
</div>

<script>
    $(() => {
        let url = new URL(window.location.href);
        $('.ui.checkbox').checkbox({
            onChange: () => {
                let $this = $('.js-calculation-package'),
                    $preloader = $('.modal-window-preloader');
                $.post({
                    url: '/calculationWithPackage',
                    data: { laboriousnessCalculationId: $this.data('id') },
                    beforeSend: () => $preloader.show(),
                    success: () => $preloader.hide()
                }).then(() => {});
            }
        });
        $('.js-add-process').on({
            'click': () => {
                $.modalDialog({
                    dialogName: 'selectPtp',
                    url: '/selectProductTechnicalProcess',
                    parameters: {
                        laboriousnessCalculationId: ${laboriousnessCalculation.id},
                        justificationId: url.searchParams.get('typeValue')
                    }
                });
            }
        });
    });
</script>