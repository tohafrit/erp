<div class="ui modal list_edit__modal">
    <div class="header">
        ${empty id ? 'Добавление технологической документации' : 'Редактирование технологической документации'}
    </div>
    <div class="content">
        <form class="ui small form">
            <input type="hidden" name="id" value="${id}"/>
            <input type="hidden" name="productApplicabilityIdList"/>
            <div class="field">
                <label>Наименование</label>
                <select class="ui dropdown std-select <c:if test="${not empty id}">disabled</c:if>" name="entityTypeId">
                    <c:forEach items="${entityTypeList}" var="entityType">
                        <option value="${entityType.id}" data-multi="${entityType.multi}" <c:if test="${entityType.id eq entityTypeId}">selected</c:if>>${entityType.fullName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field required">
                <label>№ технологической документации</label>
                <input type="text" name="entityNumber" value="${entityNumber}"/>
                <div class="ui compact message error" data-field="entityNumber"></div>
            </div>
            <div class="field required">
                <label>№ комплекта</label>
                <input type="text" name="setNumber" value="${setNumber}"/>
                <div class="ui compact message error" data-field="setNumber"></div>
            </div>
            <div class="field inline required list_edit__product-applicability">
                <label>Применяемость</label>
                <i class="add link blue icon list_edit__btn-add-product" title="<fmt:message key="label.button.add"/>"></i>
                <i class="icon trash link blue list_edit__btn-delete-product" title="<fmt:message key="label.button.delete"/>"></i>
                <div class="list_edit__product-table table-sm table-striped"></div>
                <div class="ui compact message small error" data-field="productApplicabilityIdList"></div>
            </div>
            <div class="four fields">
                <div class="field">
                    <label>Литера</label>
                    <select class="ui dropdown std-select" name="productLetterId">
                        <c:forEach items="${productLetterList}" var="productLetter">
                            <option value="${productLetter.id}" <c:if test="${productLetter.id eq productLetterId}">selected</c:if>>${productLetter.value}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <h4 class="ui dividing header">Согласование документации</h4>
            <div class="list-edit__checkbox-block">
                <div class="field">
                    <label>Утвержден</label>
                    <div class="fields">
                        <div class="fourteen wide field">
                            <select class="ui dropdown search std-select" name="approvedBy">
                                <c:forEach items="${userList}" var="user">
                                    <option value="${user.id}" <c:if test="${user.id eq approvedById}">selected</c:if>>${user.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="two wide field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="approved" <c:if test="${approved}">checked</c:if>/><label></label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label>Разработан</label>
                    <div class="fields">
                        <div class="fourteen wide field">
                            <select class="ui dropdown search std-select" name="designedBy">
                                <c:forEach items="${userList}" var="user">
                                    <option value="${user.id}" <c:if test="${user.id eq designedById}">selected</c:if>>${user.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="two wide field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="designed" <c:if test="${designed}">checked</c:if>/><label></label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label>Проверен</label>
                    <div class="fields">
                        <div class="fourteen wide field">
                            <select class="ui dropdown search std-select" name="checkedBy">
                                <c:forEach items="${userList}" var="user">
                                    <option value="${user.id}" <c:if test="${user.id eq checkedById}">selected</c:if>>${user.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="two wide field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="checked" <c:if test="${checked}">checked</c:if>/><label></label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label>Метролог</label>
                    <div class="fields">
                        <div class="fourteen wide field">
                            <select class="ui dropdown search std-select" name="metrologistBy">
                                <c:forEach items="${userList}" var="user">
                                    <option value="${user.id}" <c:if test="${user.id eq metrologistById}">selected</c:if>>${user.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="two wide field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="metrologist" <c:if test="${metrologist}">checked</c:if>/><label></label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label>Нормоконтролер</label>
                    <div class="fields">
                        <div class="fourteen wide field">
                            <select class="ui dropdown search std-select" name="normocontrollerBy">
                                <c:forEach items="${userList}" var="user">
                                    <option value="${user.id}" <c:if test="${user.id eq normocontrollerById}">selected</c:if>>${user.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="two wide field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="normocontroller" <c:if test="${normocontroller}">checked</c:if>/><label></label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label>Согласовано ВП МО РФ</label>
                    <div class="fields">
                        <div class="fourteen wide field">
                            <select class="ui dropdown search std-select" name="militaryBy">
                                <c:forEach items="${userList}" var="user">
                                    <option value="${user.id}" <c:if test="${user.id eq militaryById}">selected</c:if>>${user.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="two wide field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="military" <c:if test="${military}">checked</c:if>/><label></label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label>Начальник технологического отдела</label>
                    <div class="fields">
                        <div class="fourteen wide field">
                            <select class="ui dropdown search std-select" name="technologicalChiefBy">
                                <c:forEach items="${userList}" var="user">
                                    <option value="${user.id}" <c:if test="${user.id eq technologicalChiefById}">selected</c:if>>${user.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="two wide field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="technologicalChief" <c:if test="${technologicalChief}">checked</c:if>/><label></label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field inline list_edit__agreed-block">
                <label>Согласовано</label>
                <i class="add link blue icon list_edit__btn-add-agreed" title="<fmt:message key="label.button.add"/>"></i>
                <c:forEach items="${agreedList}" var="agreed">
                    <div class="fields">
                        <div class="fourteen wide field">
                            <select class="ui dropdown search std-select" name="agreedBy">
                                <c:forEach items="${userList}" var="user">
                                    <option value="${user.id}" <c:if test="${user.id eq agreed.id}">selected</c:if>>${user.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="one wide field">
                            <div class="ui checkbox">
                                <input type="checkbox" name="agreed" <c:if test="${agreed.value}">checked</c:if>/><label></label>
                            </div>
                        </div>
                        <div class="one wide field list_edit__delete-cell">
                            <i class="icon trash link red list_edit__btn-delete-agree" title="<fmt:message key="label.button.delete"/>"></i>
                        </div>
                    </div>
                </c:forEach>
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
        const $btnAdd = $('i.list_edit__btn-add-product');
        const $btnDelete = $('i.list_edit__btn-delete-product');
        const $agreedBlock = $('.list_edit__agreed-block');
        const $btnAgreedAdd = $agreedBlock.find('i.list_edit__btn-add-agreed');
        const agreedDelete = '.list_edit__btn-delete-agree';
        const $entityType = $('select[name="entityTypeId"]');
        const $productApplicability = $('div.list_edit__product-applicability');
        const selector = 'div.ui.checkbox';
        const $checkboxes = $modal.find('.list-edit__checkbox-block input[type="checkbox"]');
        const count = $checkboxes.length;
        const agreeRow = `
            <div class="fields">
                <div class="fourteen wide field">
                    <select class="ui dropdown search std-select" name="agreedBy">
                        <c:forEach items="${userList}" var="user">
                            <option value="${user.id}">${user.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="one wide field">
                    <div class="ui checkbox">
                        <input type="checkbox" name="agreed" /><label></label>
                    </div>
                </div>
                <div class="one wide field list_edit__delete-cell">
                    <i class="icon trash link red list_edit__btn-delete-agree" title="<fmt:message key="label.button.delete"/>"></i>
                </div>
            </div>
        `;

        const table = new Tabulator('div.list_edit__product-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(productApplicabilityList)}'),
            height: 'calc(100vh * 0.15)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.CONDITIONAL_NAME },
                { title: 'Децимальный номер', field: TABR_FIELD.DECIMAL_NUMBER }
            ]
        });

        // Переключение наименований
        $entityType.on({
            'change': () => {
                table.deleteRow(table.getData().map(el => el.id));
                const $multi = $entityType.find('option:selected').data('multi');
                if ($multi) {
                    $productApplicability.removeClass('required');
                } else {
                    $productApplicability.addClass('required');
                }
            }
        });

        // Удаление изделий
        $btnDelete.on({
            'click': () => table.deleteRow(table.getSelectedData().map(el => el.id))
        });

        // Добавление изделий
        $btnAdd.on({
            'click': () => {
                const data = table.getData().map(el => el.id);
                $.modalWindow({
                    loadURL: VIEW_PATH.LIST_EDIT_ADD_PRODUCT,
                    loadData: {
                        productApplicabilityIdList: data.join()
                    }
                });
            }
        });

        // Работа с checkboxes
        $checkboxes.each((key, element) => {
            const $element = $(element);
            $(element).attr('disabled', 'disabled').closest(selector).addClass('disabled');

            const index = $checkboxes.index($element);
            const $next = $checkboxes.eq(index + 1);
            if ($element.is(':checked')) {
                if (!$next.is(':checked')) {
                    $(element).removeAttr('disabled').closest(selector).removeClass('disabled');
                }
            } else if (index === 0) {
                $(element).removeAttr('disabled').closest(selector).removeClass('disabled');
            }
        });
        $checkboxes.on({
            'change': e => {
                const $this = $(e.currentTarget);

                const isChecked = $this.is(':checked');
                const index = $checkboxes.index($this);
                const $prev = $checkboxes.eq(index - 1);
                const $next = $checkboxes.eq(index + 1);

                if (isChecked) {
                    if (index > 0) {
                        $prev.attr('disabled', 'disabled').closest(selector).addClass('disabled');
                    }
                    if (count !== index + 1) {
                        $next.removeAttr('disabled').closest(selector).removeClass('disabled');
                    }
                } else {
                    if (index > 0) {
                        $prev.removeAttr('disabled').closest(selector).removeClass('disabled');
                    }
                    if (count !== index + 1) {
                        $next.attr('disabled', 'disabled').closest(selector).addClass('disabled');
                    }
                }
            }
        });

        $agreedBlock.on({
            'update': e => {
                const $this = $(e.currentTarget);
                $this.find('select').off().on({
                    'change': e => {
                        const $this = $(e.currentTarget);
                        $this.closest('.fields').find('input[type="checkbox"]').attr('name', 'agreed-' + $this.val());
                    }
                }).trigger('change');
                $this.find(agreedDelete).off().on({
                    'click': e => $(e.currentTarget).closest('.fields').remove()
                });
            }
        }).trigger('update');

        $btnAgreedAdd.on({
            'click': () => {
                $agreedBlock.append(agreeRow).find('select').dropdown({
                    forceSelection: false,
                    fullTextSearch: true,
                    message: { noResults: '' }
                });
                $agreedBlock.trigger('update');
            }
        });

        // Добавление списка изделий в сабмит форму
        $modal.on({
            'cb.onInitSubmit' : () => {
                const data = table.getData().map(el => el.id);
                $modal.find('input[name="productApplicabilityIdList"]').val(data.join());
            }
        });
    });
</script>