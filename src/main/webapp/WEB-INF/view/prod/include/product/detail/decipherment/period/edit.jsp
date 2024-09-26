<div class="ui modal">
    <div class="header">
        ${empty id ? 'Добавление периода' : 'Редактирование периода'}
    </div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="productId" value="${productId}">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <input type="hidden" class="decipherment_period_edit__plan-period-id" name="planPeriodId" value="${planPeriodId}">
            <c:if test="${empty id}">
                <div class="field inline required">
                    <label>Планируемый период</label>
                    <i class="add link blue icon decipherment_period_edit__btn-add-plan-period" title="<fmt:message key="label.button.add"/>"></i>
                    <i class="pen link blue icon decipherment_period_edit__btn-edit-plan-period" title="<fmt:message key="label.button.edit"/>"></i>
                    <i class="times link red icon decipherment_period_edit__btn-remove-plan-period" title="<fmt:message key="label.button.delete"/>"></i>
                    <span class="ui text decipherment_period_edit__plan-period-name">${planPeriodName}</span>
                    <div class="ui compact message small error" data-field="planPeriodId"></div>
                </div>
            </c:if>
            <c:if test="${not empty id}">
                <div class="field inline">
                    <label>Планируемый период</label>
                    <span class="ui text">${planPeriodName}</span>
                </div>
            </c:if>
            <div class="field inline">
                <label>Отчетный период</label>
                <input type="hidden" class="decipherment_period_edit__report-period-id" name="reportPeriodId" value="${reportPeriodId}">
                <i class="add link blue icon decipherment_period_edit__btn-add-report-period" title="<fmt:message key="label.button.add"/>"></i>
                <i class="pen link blue icon decipherment_period_edit__btn-edit-report-period" title="<fmt:message key="label.button.edit"/>"></i>
                <i class="times link red icon decipherment_period_edit__btn-remove-report-period" title="<fmt:message key="label.button.delete"/>"></i>
                <span class="ui text decipherment_period_edit__report-period-name">${reportPeriodName}</span>
                <div class="ui compact message small error" data-field="reportPeriodId"></div>
            </div>
        </form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const id = '${id}';
        const productId = ${productId};
        const $planPeriodId = $('input.decipherment_period_edit__plan-period-id');
        const $planPeriodName = $('span.decipherment_period_edit__plan-period-name');
        const $btnAddPlanPeriod = $('i.decipherment_period_edit__btn-add-plan-period');
        const $btnEditPlanPeriod = $('i.decipherment_period_edit__btn-edit-plan-period');
        const $btnDeletePlanPeriod = $('i.decipherment_period_edit__btn-remove-plan-period');
        const $reportPeriodId = $('input.decipherment_period_edit__report-period-id');
        const $reportPeriodName = $('span.decipherment_period_edit__report-period-name');
        const $btnAddReportPeriod = $('i.decipherment_period_edit__btn-add-report-period');
        const $btnEditReportPeriod = $('i.decipherment_period_edit__btn-edit-report-period');
        const $btnDeleteReportPeriod = $('i.decipherment_period_edit__btn-remove-report-period');

        // Изменение периода
        $planPeriodId.add($reportPeriodId).on({
            'change': () => {
                $btnAddPlanPeriod.toggle($planPeriodId.val() === '');
                $btnEditPlanPeriod.toggle($planPeriodId.val() !== '');
                $btnDeletePlanPeriod.toggle($planPeriodId.val() !== '');
                $btnAddReportPeriod.toggle($reportPeriodId.val() === '');
                $btnEditReportPeriod.toggle($reportPeriodId.val() !== '');
                $btnDeleteReportPeriod.toggle($reportPeriodId.val() !== '');
            }
        });
        $planPeriodId.trigger('change');

        // Выбор планового периода
        $btnAddPlanPeriod.add($btnEditPlanPeriod).on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_PERIOD_EDIT_PLAN_PERIOD,
                loadData: { productId: productId }
            })
        });
        $btnDeletePlanPeriod.on({
            'click': () => {
                $planPeriodId.val('');
                $planPeriodName.text('');
                $planPeriodId.trigger('change');
            }
        });

        // Выбор отчетного периода
        $btnAddReportPeriod.add($btnEditReportPeriod).on({
            'click': () => {
                const planPeriodId = $planPeriodId.val();
                if (planPeriodId) {
                    $.modalWindow({
                        loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_PERIOD_EDIT_REPORT_PERIOD,
                        loadData: { productId: productId, planPeriodId: planPeriodId, excludePeriodId: id }
                    });
                } else {
                    alertDialog({ message: 'Укажите планируемый период' });
                }
            }
        });
        $btnDeleteReportPeriod.on({
            'click': () => {
                $reportPeriodId.val('');
                $reportPeriodName.text('');
                $reportPeriodId.trigger('change');
            }
        });
    })
</script>