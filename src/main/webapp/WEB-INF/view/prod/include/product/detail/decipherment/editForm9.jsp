<div class="ui modal">
    <div class="ui small header">Редактирование - форма ${formName}</div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <div class="field inline required">
                <label>Тарифы н/час</label>
                <input type="hidden" class="decipherment_edit_form9__justification-id" name="justificationId" value="${justificationId}">
                <i class="add link blue icon decipherment_edit_form9__btn-add-justification" title="<fmt:message key="label.button.add"/>"></i>
                <i class="pen link blue icon decipherment_edit_form9__btn-edit-justification" title="<fmt:message key="label.button.edit"/>"></i>
                <i class="times link red icon decipherment_edit_form9__btn-remove-justification" title="<fmt:message key="label.button.delete"/>"></i>
                <span class="ui text decipherment_edit_form9__justification-name">${justificationName}</span>
                <div class="ui compact message small error" data-field="justificationId"></div>
            </div>
            <div class="field inline required">
                <label>Расчет трудоемкости</label>
                <input type="hidden" class="decipherment_edit_form9__lab-intensity-id" name="labourIntensityId" value="${labourIntensityId}">
                <i class="add link blue icon decipherment_edit_form9__btn-add-lab-intensity" title="<fmt:message key="label.button.add"/>"></i>
                <i class="pen link blue icon decipherment_edit_form9__btn-edit-lab-intensity" title="<fmt:message key="label.button.edit"/>"></i>
                <i class="times link red icon decipherment_edit_form9__btn-remove-lab-intensity" title="<fmt:message key="label.button.delete"/>"></i>
                <span class="ui text decipherment_edit_form9__lab-intensity-name">${labourIntensityName}</span>
                <div class="ui compact message small error" data-field="labourIntensityId"></div>
            </div>
            <div class="field required">
                <label>Начальник ПЭО</label>
                <select name="headEcoId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq headEcoId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="headEcoId"></div>
            </div>
            <div class="field required">
                <label>Главный технолог</label>
                <select name="chiefTechId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq chiefTechId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="chiefTechId"></div>
            </div>
            <div class="field inline">
                <label>Дата создания</label>
                <span class="ui text">${createDate}</span>
            </div>
            <div class="field inline">
                <label>Создано</label>
                <span class="ui text">${createdBy}</span>
            </div>
            <div class="field inline">
                <label>Файл</label>
                <div class="std-file ui action input fluid">
                    <input type="hidden" name="fileId" value="${fileId}">
                    <input type="file" name="file"/>
                    <span>
                        ${fileName}
                        <a target="_blank" href="<c:url value="/download-file/${fileUrlHash}"/>">
                            <fmt:message key="text.downloadFile"/>
                        </a>
                    </span>
                </div>
                <div class="ui compact message small error" data-field="file"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <textarea name="comment" rows="3">${comment}</textarea>
                </div>
                <div class="ui compact message small error" data-field="comment"></div>
            </div>
        </form>
        <table class="ui tiny compact definition table decipherment_edit_form9__total-table">
            <thead>
                <tr>
                    <th></th>
                    <th>Стоимость, руб.</th>
                    <th>Трудоемкость, н/час</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Без упаковки</td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td>С упаковкой</td>
                    <td></td>
                    <td></td>
                </tr>
            </tbody>
        </table>
        <div class="decipherment_edit_form9__table table-sm table-striped"></div>
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
        const productId = '${productId}';
        const $table = $('div.decipherment_edit_form9__table');
        const $justificationId = $('input.decipherment_edit_form9__justification-id');
        const $justificationName = $('span.decipherment_edit_form9__justification-name');
        const $btnAddJustification = $('i.decipherment_edit_form9__btn-add-justification');
        const $btnEditJustification = $('i.decipherment_edit_form9__btn-edit-justification');
        const $btnDeleteJustification = $('i.decipherment_edit_form9__btn-remove-justification');
        const $labourIntensityId = $('input.decipherment_edit_form9__lab-intensity-id');
        const $labourIntensityName = $('span.decipherment_edit_form9__lab-intensity-name');
        const $btnAddLabIntensity = $('i.decipherment_edit_form9__btn-add-lab-intensity');
        const $btnEditLabIntensity = $('i.decipherment_edit_form9__btn-edit-lab-intensity');
        const $btnDeleteLabIntensity = $('i.decipherment_edit_form9__btn-remove-lab-intensity');
        const $totalTable = $('table.decipherment_edit_form9__total-table');
        const $labourTotal = $totalTable.find('tbody > tr:eq(1) > td:eq(1)');
        const $labourWoPackTotal = $totalTable.find('tbody > tr:eq(0) > td:eq(1)');
        const $labourIntensityTotal = $totalTable.find('tbody > tr:eq(1) > td:eq(2)');
        const $labourIntensityWoPackTotal = $totalTable.find('tbody > tr:eq(0) > td:eq(2)');

        // Таблица расчетов стоимости
        let skipInitLoad = true;
        const table = new Tabulator('div.decipherment_edit_form9__table', {
            height: 'calc(100vh * 0.35)',
            layout: 'fitColumns',
            headerSort: false,
            layoutColumnsOnNewData: true,
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_EDIT_FORM9_WORK_COST,
            ajaxRequesting: (url, params) => {
                params.productId = productId;
                params.justificationId = $justificationId.val();
                params.labourIntensityId = $labourIntensityId.val();
                if (skipInitLoad || $justificationId.val() === '' || $labourIntensityId.val() === '') {
                    skipInitLoad = false;
                    return false;
                }
            },
            ajaxResponse: (url, params, data) => {
                if (data.length) {
                    $labourTotal.text(formatAsCurrency(data[0].totalCost));
                    $labourWoPackTotal.text(formatAsCurrency(data[0].totalCostWoPack));
                    $labourIntensityTotal.text(formatAsCurrency(data[0].totalLabourIntensity));
                    $labourIntensityWoPackTotal.text(formatAsCurrency(data[0].totalLabourIntensityWoPack));
                }
                return data;
            },
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                {
                    title: 'Работа',
                    field: TABR_FIELD.NAME
                },
                {
                    title: 'Норма оплаты, руб.',
                    field: TABR_FIELD.PAYMENT_RATE,
                    formatter: 'money',
                    formatterParams: { decimal: ',', thousand: ' ' }
                },
                {
                    title: 'Трудоемкость, н/час',
                    field: TABR_FIELD.LABOUR_INTENSITY,
                    formatter: 'money',
                    formatterParams: { decimal: ',', thousand: ' ' }
                },
                {
                    title: 'Стоимость, руб.',
                    field: TABR_FIELD.COST,
                    formatter: 'money',
                    formatterParams: { decimal: ',', thousand: ' ' }
                }
            ]
        });

        // Изменение полей трудоемкости и стоимости работ
        $justificationId.add($labourIntensityId).on({
            'change': () => {
                const tableDataVisible = $justificationId.val() !== '' && $labourIntensityId.val() !== '';
                $totalTable.toggle(tableDataVisible);
                $table.toggle(tableDataVisible);
                if (tableDataVisible) table.setData();
                $btnAddJustification.toggle($justificationId.val() === '');
                $btnEditJustification.toggle($justificationId.val() !== '');
                $btnDeleteJustification.toggle($justificationId.val() !== '');
                $btnAddLabIntensity.toggle($labourIntensityId.val() === '');
                $btnEditLabIntensity.toggle($labourIntensityId.val() !== '');
                $btnDeleteLabIntensity.toggle($labourIntensityId.val() !== '');
                $labourTotal.text(formatAsCurrency(0));
                $labourWoPackTotal.text(formatAsCurrency(0));
                $labourIntensityTotal.text(formatAsCurrency(0));
                $labourIntensityWoPackTotal.text(formatAsCurrency(0));
            }
        });
        $justificationId.trigger('change'); // только у одного на 1ом старте

        // Выбор стоимости работ
        $btnAddJustification.add($btnEditJustification).on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT_FORM9_COST_JUSTIFICATION,
                loadData: { id: $justificationId.val() }
            })
        });
        $btnDeleteJustification.on({
            'click': () => {
                $justificationId.val('');
                $justificationName.text('');
                $justificationId.trigger('change');
            }
        });

        // Выбор трудоемкости
        $btnAddLabIntensity.add($btnEditLabIntensity).on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT_FORM9_LABOUR_INTENSITY,
                loadData: { id: $labourIntensityId.val(), productId: productId }
            })
        });
        $btnDeleteLabIntensity.on({
            'click': () => {
                $labourIntensityId.val('');
                $labourIntensityName.text('');
                $labourIntensityId.trigger('change');
            }
        });
    })
</script>