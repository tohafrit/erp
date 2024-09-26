<div class="ui modal list_edit__modal">
    <div class="header">
        ${empty id ? 'Добавление операции' : 'Редактирование операции'}
    </div>
    <div class="content">
        <form class="ui small form">
            <input type="hidden" name="id" value="${id}"/>
            <input type="hidden" name="entityId" value="${entityId}"/>
            <input type="hidden" name="parentId" value="${parentId}"/>
            <input type="hidden" name="productionAreaIdList"/>
            <input type="hidden" name="laborIdList"/>
            <input type="hidden" name="equipmentIdList"/>
            <input type="hidden" name="toolIdList"/>
            <input type="hidden" name="materialIdList"/>
            <input type="hidden" name="symbol" value="${symbol}"/>
            <c:if test="${empty id}">
                <div class="field">
                    <label>Функциональность перехода</label>
                    <select class="ui dropdown std-select" name="serviceSymbol">
                        <c:forEach items="${serviceSymbolTypeList}" var="symbol">
                            <option value="${symbol.code}">${symbol.property}</option>
                        </c:forEach>
                    </select>
                </div>
            </c:if>
            <div class="a-functionality-fields">
                <div class="field inline required">
                    <label>Участки</label>
                    <i class="add link blue icon list_edit__btn-add-area" title="<fmt:message key="label.button.add"/>"></i>
                    <i class="icon trash link blue list_edit__btn-delete-area" title="<fmt:message key="label.button.delete"/>"></i>
                    <div class="list_edit__area-table table-sm table-striped"></div>
                    <div class="ui compact message small error" data-field="productionAreaIdList"></div>
                </div>
                <div class="field required">
                    <label>Номер операции</label>
                    <input type="text" name="number" value="${number}"/>
                    <div class="ui compact message error" data-field="number"></div>
                </div>
                <div class="field required">
                    <label>Вид работы</label>
                    <select class="ui dropdown std-select" name="workTypeId">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${workTypeList}" var="workType">
                            <option value="${workType.id}" <c:if test="${workType.id eq workTypeId}">selected</c:if>>${workType.value}</option>
                        </c:forEach>
                    </select>
                    <div class="ui compact message error" data-field="workTypeId"></div>
                </div>
                <div class="field">
                    <label>Комментарий к виду работ</label>
                    <div class="ui textarea">
                        <textarea name="nameComment">${nameComment}</textarea>
                    </div>
                    <div class="ui compact message error" data-field="nameComment"></div>
                </div>
                <div class="field inline required">
                    <label>ИОТ</label>
                    <i class="add link blue icon list_edit__btn-add-labor" title="<fmt:message key="label.button.add"/>"></i>
                    <i class="icon trash link blue list_edit__btn-delete-labor" title="<fmt:message key="label.button.delete"/>"></i>
                    <div class="list_edit__labor-table table-sm table-striped"></div>
                    <div class="ui compact message small error" data-field="laborIdList"></div>
                </div>
                <div class="field">
                    <label>Описание операции</label>
                    <div class="ui textarea">
                        <textarea name="description">${description}</textarea>
                    </div>
                    <div class="ui compact message error" data-field="description"></div>
                </div>
            </div>
            <div class="b-functionality-fields">
                <div class="field inline required">
                    <label>Оборудование</label>
                    <i class="add link blue icon list_edit__btn-add-equipment" title="<fmt:message key="label.button.add"/>"></i>
                    <i class="icon trash link blue list_edit__btn-delete-equipment" title="<fmt:message key="label.button.delete"/>"></i>
                    <div class="list_edit__equipment-table table-sm table-striped"></div>
                    <div class="ui compact message small error" data-field="equipmentIdList"></div>
                </div>
                <div class="field required">
                    <label>КОИД</label>
                    <input type="text" name="koid" value="${koid}"/>
                    <div class="ui compact message error" data-field="koid"></div>
                </div>
                <div class="field required">
                    <label>Кшт</label>
                    <input type="text" name="ksht" value="${ksht}"/>
                    <div class="ui compact message error" data-field="ksht"></div>
                </div>
                <div class="field required">
                    <label>Тпз</label>
                    <input type="text" name="tpz" value="${tpz}"/>
                    <div class="ui compact message error" data-field="tpz"></div>
                </div>
                <div class="field required">
                    <label>Тшт</label>
                    <input type="text" name="tsht" value="${tsht}"/>
                    <div class="ui compact message error" data-field="tsht"></div>
                </div>
            </div>
            <div class="o-functionality-fields">
                <div class="inline field required">
                    <label>Содержание операции</label>
                    <i class="add link blue icon list_edit__btn-add-content" title="<fmt:message key="label.button.add"/>"></i>
                    <table class="list_edit__add-content-table">
                        <tbody>
                            <tr>
                                <td>
                                    <div class="ui textarea">
                                        <textarea rows="3" name="content">${content}</textarea>
                                    </div>
                                </td>
                                <td>
                                    <i class="icon trash link blue list_edit__btn-delete-content" title="Удалить"></i>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="ui compact message error" data-field="content"></div>
                </div>
            </div>
            <div class="r-functionality-fields">
                <div class="inline field">
                    <label>Режим проведения операции</label>
                    <i class="add link blue icon list_edit__btn-add-mode" title="<fmt:message key="label.button.add"/>"></i>
                    <table class="list_edit__add-mode-table">
                        <tbody>
                            <c:if test="${empty id}">
                                <tr>
                                    <td>
                                        <div class="ui textarea">
                                            <textarea rows="3" name="mode"></textarea>
                                        </div>
                                    </td>
                                    <td>
                                        <i class="icon trash link blue list_edit__btn-delete-mode" title="Удалить"></i>
                                    </td>
                                </tr>
                            </c:if>
                            <c:forEach items="${modeList}" var="mode">
                                <tr>
                                    <td>
                                        <div class="ui textarea">
                                            <textarea rows="3" name="mode">${mode}</textarea>
                                        </div>
                                    </td>
                                    <td>
                                        <i class="icon trash link blue list_edit__btn-delete-mode" title="Удалить"></i>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="t-functionality-fields">
                <div class="field inline required">
                    <label>Технологическая оснастка и инструмент</label>
                    <i class="add link blue icon list_edit__btn-add-tool" title="<fmt:message key="label.button.add"/>"></i>
                    <i class="icon trash link blue list_edit__btn-delete-tool" title="<fmt:message key="label.button.delete"/>"></i>
                    <div class="list_edit__tool-table table-sm table-striped"></div>
                    <div class="ui compact message small error" data-field="toolIdList"></div>
                </div>
            </div>
            <div class="m-functionality-fields">
                <div class="field inline required">
                    <label>Материалы</label>
                    <i class="add link blue icon list_edit__btn-add-material" title="<fmt:message key="label.button.add"/>"></i>
                    <i class="icon trash link blue list_edit__btn-delete-material" title="<fmt:message key="label.button.delete"/>"></i>
                    <div class="list_edit__material-table table-sm table-striped"></div>
                    <div class="ui compact message small error" data-field="materialIdList"></div>
                </div>
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
        const $modal = $('div.list_edit__modal');
        const $btnAddArea = $('i.list_edit__btn-add-area');
        const $btnDeleteArea = $('i.list_edit__btn-delete-area');
        const $btnAddLabor = $('i.list_edit__btn-add-labor');
        const $btnDeleteLabor = $('i.list_edit__btn-delete-labor');
        const $btnAddEquipment = $('i.list_edit__btn-add-equipment');
        const $btnDeleteEquipment = $('i.list_edit__btn-delete-equipment');
        const $btnAddTool = $('i.list_edit__btn-add-tool');
        const $btnDeleteTool = $('i.list_edit__btn-delete-tool');
        const $btnAddMaterial = $('i.list_edit__btn-add-material');
        const $btnDeleteMaterial = $('i.list_edit__btn-delete-material');
        const $serviceSymbol = $('select[name="serviceSymbol"]');
        const $btnAddContent = $('i.list_edit__btn-add-content');
        const $btnAddMode = $('i.list_edit__btn-add-mode');
        const $contentTable = $('.list_edit__add-content-table');
        const $modeTable = $('.list_edit__add-mode-table');
        const trashContentSelector = '.list_edit__btn-delete-content';
        const trashModeSelector = '.list_edit__btn-delete-mode';
        const rowContent = `
            <tr>
                <td>
                    <div class="ui textarea">
                        <textarea rows="3" name="content"></textarea>
                    </div>
                </td>
                <td>
                    <i class="icon trash link blue list_edit__btn-delete-content" title="Удалить"></i>
                </td>
            </tr>
        `;
        const rowMode = `
            <tr>
                <td>
                    <div class="ui textarea">
                        <textarea rows="3" name="mode"></textarea>
                    </div>
                </td>
                <td>
                    <i class="icon trash link blue list_edit__btn-delete-mode" title="Удалить"></i>
                </td>
            </tr>
        `;
        const id = '${id}';

        initSortableTable($contentTable);
        initSortableTable($modeTable);

        const areaTable = new Tabulator('div.list_edit__area-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(productionAreaList)}'),
            height: 'calc(100vh * 0.15)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Код', field: TABR_FIELD.CODE },
                { title: 'Наименование', field: TABR_FIELD.NAME }
            ]
        });

        $btnDeleteArea.on({
            'click': () => areaTable.deleteRow(areaTable.getSelectedData().map(el => el.id))
        });

        $btnAddArea.on({
            'click': () => {
                const data = areaTable.getData().map(el => el.id);
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_EDIT_ADD_AREA,
                    loadData: {
                        productionAreaIdList: data.join()
                    }
                });
            }
        });

        const laborTable = new Tabulator('div.list_edit__labor-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(laborList)}'),
            height: 'calc(100vh * 0.15)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME }
            ]
        });

        $btnDeleteLabor.on({
            'click': () => laborTable.deleteRow(laborTable.getSelectedData().map(el => el.id))
        });

        $btnAddLabor.on({
            'click': () => {
                const data = laborTable.getData().map(el => el.id);
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_EDIT_ADD_LABOR,
                    loadData: {
                        laborIdList: data.join()
                    }
                });
            }
        });

        const equipmentTable = new Tabulator('div.list_edit__equipment-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(equipmentList)}'),
            height: 'calc(100vh * 0.15)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                { title: 'Модель', field: TABR_FIELD.MODEL }
            ]
        });

        $btnDeleteEquipment.on({
            'click': () => equipmentTable.deleteRow(equipmentTable.getSelectedData().map(el => el.id))
        });

        $btnAddEquipment.on({
            'click': () => {
                const data = equipmentTable.getData().map(el => el.id);
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_EDIT_ADD_EQUIPMENT,
                    loadData: {
                        equipmentIdList: data.join()
                    }
                });
            }
        });

        const toolTable = new Tabulator('div.list_edit__tool-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(technologicalToolList)}'),
            height: 'calc(100vh * 0.15)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Обозначение', field: TABR_FIELD.SIGN },
                { title: 'Наименование', field: TABR_FIELD.NAME }
            ]
        });

        $btnDeleteTool.on({
            'click': () => toolTable.deleteRow(toolTable.getSelectedData().map(el => el.id))
        });

        $btnAddTool.on({
            'click': () => {
                const data = toolTable.getData().map(el => el.id);
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_EDIT_ADD_TOOL,
                    loadData: {
                        toolIdList: data.join()
                    }
                });
            }
        });

        const materialTable = new Tabulator('div.list_edit__material-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(operationMaterialList)}'),
            height: 'calc(100vh * 0.15)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME }
            ]
        });

        $btnDeleteMaterial.on({
            'click': () => materialTable.deleteRow(materialTable.getSelectedData().map(el => el.id))
        });

        $btnAddMaterial.on({
            'click': () => {
                const data = materialTable.getData().map(el => el.id);
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_EDIT_ADD_MATERIAL,
                    loadData: {
                        materialIdList: data.join(),
                        parentId: '${parentId}'
                    }
                });
            }
        });

        $modal.on({
            'cb.onInitSubmit' : () => {
                const areaData = areaTable.getData().map(el => el.id);
                $modal.find('input[name="productionAreaIdList"]').val(areaData.join());
                const laborData = laborTable.getData().map(el => el.id);
                $modal.find('input[name="laborIdList"]').val(laborData.join());
                const equipmentData = equipmentTable.getData().map(el => el.id);
                $modal.find('input[name="equipmentIdList"]').val(equipmentData.join());
                const toolData = toolTable.getData().map(el => el.id);
                $modal.find('input[name="toolIdList"]').val(toolData.join());
                const materialData = materialTable.getData().map(el => el.id);
                $modal.find('input[name="materialIdList"]').val(materialData.join());
                if (!id) $modal.find('input[name="symbol"]').val($serviceSymbol.val());
            }
        });

        $modal.find('input[name="koid"]').inputmask({alias: 'numeric', rightAlign: false});
        $modal.find('input[name="ksht"], input[name="tpz"], input[name="tsht"]').inputmask({alias: 'decimal', rightAlign: false});

        // Отображение полей
        const fieldsVisibility = symbol => {
            const selector = symbol.toLowerCase() + '-functionality-fields';
            $("div[class$='-functionality-fields']").hide();
            $('.' + selector).show();
        }
        if (id) {
            fieldsVisibility($modal.find('input[name="symbol"]').val());
        } else {
            $serviceSymbol.on({
                'change': e => fieldsVisibility($(e.currentTarget).val())
            }).trigger('change');
        }

        $btnAddContent.on({
            'click': e => {
                $(e.currentTarget).next().find('tbody').append(rowContent);
                $contentTable.trigger('content.recalc');
            }
        });

        $btnAddMode.on({
            'click': e => {
                $(e.currentTarget).next().find('tbody').append(rowMode);
                $modeTable.trigger('mode.recalc');
            }
        });

        $contentTable.on({
            'content.recalc': e => {
                const $this = $(e.currentTarget);
                $this.find(trashContentSelector).off().on({
                    'click': e => $(e.currentTarget).closest('tr').remove()
                });
            }
        }).trigger('content.recalc');

        $modeTable.on({
            'mode.recalc': e => {
                const $this = $(e.currentTarget);
                $this.find(trashModeSelector).off().on({
                    'click': e => $(e.currentTarget).closest('tr').remove()
                });
            }
        }).trigger('mode.recalc');
    });
</script>