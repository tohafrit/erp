<div class="ui modal fullscreen decipherment_price_analysis__modal">
    <div class="ui small header">Показатели для ${productName}</div>
    <div class="content">
        <form class="ui small form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="period" value="${periodId}">
            <div class="five fields">
                <div class="field">
                    <label>Дополнительная заработная плата ${planYear}</label>
                    <div class="ui fluid">
                        <input name="additionalSalary" type="text" value="${additionalSalary}">
                    </div>
                </div>
                <div class="field">
                    <label>Отчисление на социальное страхование ${planYear}</label>
                    <div class="ui fluid">
                        <input name="socialSecurityContribution" type="text" value="${socialSecurityContribution}">
                    </div>
                </div>
                <div class="field">
                    <label>Общепроизводственные затраты ${planYear}</label>
                    <div class="ui fluid">
                        <input name="generalProductionCost" type="text" value="${generalProductionCost}">
                    </div>
                </div>
                <div class="field">
                    <label>Общехозяйственные затраты ${planYear}</label>
                    <div class="ui fluid">
                        <input name="generalOperationCost" type="text" value="${generalOperationCost}">
                    </div>
                </div>
                <div class="field">
                    <label>Коэф.дефлятор (ИЦП) ${planYear}</label>
                    <div class="ui fluid">
                        <input name="deflatorCoefficient" type="text" value="${deflatorCoefficient}">
                    </div>
                </div>
            </div>
            <div class="ui small button decipherment_price_analysis__btn-reload">
                <i class="icon blue redo"></i>
                <fmt:message key="label.button.calculate"/>
            </div>
            <button class="ui small button" type="submit">
                <i class="icon blue save"></i>
                <fmt:message key="label.button.save"/>
            </button>
        </form>
        <div class="decipherment_price_analysis__table table-sm table-striped"></div>
    </div>
</div>
<script>
    $(() => {
        const $modal = $('div.decipherment_price_analysis__modal');
        const $inputs = $modal.find('input[type="text"]').not('input[name^="deflator"]');
        $inputs.inputmask('decimal', {
            min: 0,
            max: 100,
            rightAlign: false,
            allowMinus: false
        });

        const $coef = $modal.find('input[name="deflatorCoefficient"]');
        $coef.inputmask('decimal', {
            rightAlign: false,
            allowMinus: true
        });

        const $reloadBtn = $('div.decipherment_price_analysis__btn-reload');
        const $addSalaryPlanInput = $modal.find('input[name="additionalSalary"]');
        const $socialSecurityPlanInput = $modal.find('input[name="socialSecurityContribution"]');
        const $generalProductionPlanInput = $modal.find('input[name="generalProductionCost"]');
        const $generalOperationPlanInput = $modal.find('input[name="generalOperationCost"]');
        const $deflatorCoefficientPlanInput = $modal.find('input[name="deflatorCoefficient"]');

        $reloadBtn.on({
            'click': e => table.setData()
        });

        var table = new Tabulator('div.decipherment_price_analysis__table', {
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_ANALYSIS_LOAD,
            layout: 'fitColumns',
            ajaxLoader: true,
            ajaxRequesting: (url, params) => {
                params.productId = '${productId}';
                params.periodId = '${periodId}';
                params.additionalSalary = $addSalaryPlanInput.val();
                params.socialSecurityContribution = $socialSecurityPlanInput.val();
                params.generalProductionCost = $generalProductionPlanInput.val();
                params.generalOperationCost = $generalOperationPlanInput.val();
                params.deflatorCoefficient = $deflatorCoefficientPlanInput.val();
            },
            height: 'calc(100vh * 0.6)',
            columnHeaderVertAlign: 'bottom',
            columns: [
                { title: 'Наименование статей затрат', field: TABR_FIELD.NAME },
                {
                    title: 'СТАЛО',
                    columns:[
                        {
                            title: 'нормативы ${planYear} г.',
                            field: TABR_FIELD.BECAME_STANDART,
                            formatter: cell => {
                                const name = cell.getRow().getData().name;
                                if (name.indexOf('Прибыль') > -1) {
                                    const profit = cell.getRow().getData().secondProfit;
                                    return formatAsCurrency(cell.getValue()) + '%    ' + formatAsCurrency(profit) + '%'
                                } else {
                                    if (cell.getValue() != null) return formatAsCurrency(cell.getValue()) + '%'
                                }
                            }
                        },
                        {
                            title: 'цена с уп.',
                            field: TABR_FIELD.BECAME_PRICE,
                            formatter: cell => {
                                const name = cell.getRow().getData().name;
                                return formatAsCurrency(cell.getValue()) + (name.indexOf('%') > -1 ? '%' : '');
                            }
                        }
                    ],
                },
                {
                    title: 'БЫЛО',
                    columns: [
                        {
                            title: 'нормативы ${reportYear} г.',
                            field: TABR_FIELD.WAS_STANDART,
                            formatter: cell => {
                                const name = cell.getRow().getData().name;
                                if (name.indexOf('Прибыль') > -1) {
                                    const profit = cell.getRow().getData().secondProfitPrev;
                                    return formatAsCurrency(cell.getValue()) + '%    ' + formatAsCurrency(profit) + '%'
                                } else {
                                    if (cell.getValue() != null) return formatAsCurrency(cell.getValue()) + '%'
                                }
                            }
                        },
                        {
                            title: 'цена с уп.',
                            field: TABR_FIELD.WAS_PRICE,
                            formatter: cell => {
                                const name = cell.getRow().getData().name;
                                return formatAsCurrency(cell.getValue()) + (name.indexOf('%') > -1 ? '%' : '');
                            }
                        }
                    ]
                },
                {
                    title: 'АБСОЛЮТНОЕ отклонение, руб.',
                    columns: [
                        {
                            title: 'с уп.',
                            field: TABR_FIELD.ABSOLUTE_WITH_PACK,
                            formatter: cell => {
                                const name = cell.getRow().getData().name;
                                return formatAsCurrency(cell.getValue()) + (name.indexOf('%') > -1 ? '%' : '');
                            }
                        }
                    ]
                },
                {
                    title: 'ОТНОСИТЕЛЬНОЕ отклонение, %',
                    columns: [
                        {
                            title: 'с уп.',
                            field: TABR_FIELD.RELATIVE_WITH_PACK,
                            formatter: cell => cell.getValue() != null ? formatAsCurrency(cell.getValue()) + '%' : ''
                        }
                    ]
                }
            ]
        });
    });
</script>