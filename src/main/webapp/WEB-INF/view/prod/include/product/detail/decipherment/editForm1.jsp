<div class="ui modal">
    <div class="ui small header">Редактирование - форма ${formName}</div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <c:if test="${isApproved}">
                <div class="field">
                    <div class="ui compact message small warning visible">
                        <div class="header">Форма утверждена</div>
                        К редактированию доступно только поле "Заказчик"
                    </div>
                </div>
            </c:if>
            <div class="field required inline">
                <label>Заказчик</label>
                <i class="add link blue icon decipherment_edit_form1__btn-add-customer" title="<fmt:message key="label.button.add"/>"></i>
                <i class="pen link blue icon decipherment_edit_form1__btn-edit-customer" title="<fmt:message key="label.button.edit"/>"></i>
                <i class="times link red icon decipherment_edit_form1__btn-remove-customer" title="<fmt:message key="label.button.delete"/>"></i>
                <input class="decipherment_edit_form1__customer-id" type="hidden" name="customerId" value="${customerId}">
                <table class="ui tiny compact definition table decipherment_edit_form1__table-customer">
                    <tbody>
                        <tr>
                            <td class="one wide">Наименование</td>
                            <td class="ten wide">${customerName}</td>
                        </tr>
                        <tr>
                            <td>Местоположение</td>
                            <td>${customerLocation}</td>
                        </tr>
                    </tbody>
                </table>
                <div class="ui compact message small error" data-field="customerId"></div>
            </div>
            <c:if test="${not empty okpdCode}">
                <div class="field inline">
                    <label>Код ОКП/ОКПД2</label>
                    <span class="ui text">${okpdCode}</span>
                </div>
            </c:if>
            <div class="field required">
                <label>Процедура</label>
                <div class="ui fluid search decipherment_edit_form1__procedure">
                    <div class="ui input">
                        <input class="prompt" name="procedure" type="text" value="${procedure}" autocomplete="off">
                    </div>
                    <div class="results"></div>
                </div>
                <div class="ui compact message small error" data-field="procedure"></div>
            </div>
            <div class="field required">
                <label>Метод определения цены</label>
                <div class="ui fluid search decipherment_edit_form1__price-determination">
                    <div class="ui input">
                        <input class="prompt" name="priceDetermination" type="text" value="${priceDetermination}" autocomplete="off">
                    </div>
                    <div class="results"></div>
                </div>
                <div class="ui compact message small error" data-field="priceDetermination"></div>
            </div>
            <div class="field required">
                <label>Примечание</label>
                <div class="ui fluid search decipherment_edit_form1__note">
                    <div class="ui input">
                        <input class="prompt" name="note" type="text" value="${note}" autocomplete="off">
                    </div>
                    <div class="results"></div>
                </div>
                <div class="ui compact message small error" data-field="note"></div>
            </div>
            <c:if test="${not empty techDoc}">
                <div class="field inline">
                    <label>Техническая документация</label>
                    <span class="ui text">${techDoc}</span>
                </div>
            </c:if>
            <div class="field required">
                <label>Начало действия цены</label>
                <div class="ui input fluid">
                    <input name="startDate" type="text" value="${startDate}">
                </div>
                <div class="ui compact message small error" data-field="startDate"></div>
            </div>
            <div class="field required">
                <label>Окончание действия цены</label>
                <div class="ui input fluid">
                    <input name="endDate" type="text" value="${endDate}">
                </div>
                <div class="ui compact message small error" data-field="endDate"></div>
            </div>
            <div class="field required">
                <label>Вид цены, предложенный поставщиком</label>
                <div class="ui fluid search decipherment_edit_form1__price-type">
                    <div class="ui input">
                        <input class="prompt" name="priceType" type="text" value="${priceType}" autocomplete="off">
                    </div>
                    <div class="results"></div>
                </div>
                <div class="ui compact message small error" data-field="priceType"></div>
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
                <label>Директор по экономике</label>
                <select name="directorEcoId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq directorEcoId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="directorEcoId"></div>
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
        const $customerId = $('input.decipherment_edit_form1__customer-id');
        const $tableCustomer = $('table.decipherment_edit_form1__table-customer');
        const $btnAddCustomer = $('i.decipherment_edit_form1__btn-add-customer');
        const $btnEditCustomer = $('i.decipherment_edit_form1__btn-edit-customer');
        const $btnDeleteCustomer = $('i.decipherment_edit_form1__btn-remove-customer');

        $('div.decipherment_edit_form1__procedure').search({
            source: [
                { title: 'определение прогнозной цены единицы продукции потенциального единственного поставщика (подрядчика, исполнителя)' },
                { title: 'определение цены государственного контракта, заключаемого с единственным поставщиком (подрядчиком, исполнителем), на поставку продукции по цене, подлежащей регистрации' },
                { title: 'определение цены государственного контракта (контракта), заключаемого с единственным поставщиком (подрядчиком, исполнителем), на поставку продукции по цене, не подлежащей регистрации' },
                { title: 'перевод в фиксированную цену' }
            ],
            minCharacters: 0,
            duration: 0
        });

        $('div.decipherment_edit_form1__price-determination').search({
            source: [
                { title: 'затратный метод' },
                { title: 'метод индексации базовой цены' },
                { title: 'метод индексации по статьям затрат' }
            ],
            minCharacters: 0,
            duration: 0
        });

        $('div.decipherment_edit_form1__note').search({
            source: [
                { title: 'Базовая цена' }
            ],
            minCharacters: 0,
            duration: 0
        });

        $('div.decipherment_edit_form1__price-type').search({
            source: [
                { title: 'фиксированная цена' },
                { title: '10.2 ориентировочная (уточняемая) цена' }
            ],
            minCharacters: 0,
            duration: 0
        });

        // Выбор заказчика
        $customerId.on({
            'change': () => {
                const val = $customerId.val();
                $btnAddCustomer.toggle(val === '');
                $btnEditCustomer.toggle(val !== '');
                $btnDeleteCustomer.toggle(val !== '');
                $tableCustomer.toggle(val !== '');
            }
        });
        $customerId.trigger('change');

        $btnAddCustomer.add($btnEditCustomer).on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT_FORM1_CUSTOMER
            })
        });
        $btnDeleteCustomer.on({
            'click': () => {
                $customerId.val('');
                $tableCustomer.find('tr:eq(0) > td:eq(1)').text('');
                $tableCustomer.find('tr:eq(1) > td:eq(1)').text('');
                $customerId.trigger('change');
            }
        });
    })
</script>