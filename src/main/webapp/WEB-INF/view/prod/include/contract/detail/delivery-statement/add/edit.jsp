<div class="ui modal detail_delivery-statement_products_edit__main">
    <div class="header">${empty form.lotId ? 'Добавление изделия в ведомость поставки' : 'Редактироавние изделия ведомости поставки'}</div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="sectionId"/>
            <form:hidden path="productId"/>
            <form:hidden path="serviceTypeId"/>
            <form:hidden path="lotId"/>
            <div class="field">
                <label>Договор</label>
                    ${contractSectionFullNumber}
            </div>
            <div class="field">
                <label>Изделие</label>
                    ${conditionalName}
            </div>
            <div class="field required">
                <label>Количество</label>
                <form:input path="amount" cssClass="detail_delivery-statement_products_edit__field-amount"/>
                <div class="ui compact message error" data-field="amount"></div>
            </div>
            <div class="field">
                <label>Тип приемки</label>
                <form:select cssClass="ui dropdown label std-select" path="acceptType">
                    <c:forEach items="${acceptTypeList}" var="acceptType">
                        <form:option value="${acceptType}"><fmt:message key="${acceptType.property}"/></form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field">
                <label>Спец. проверка</label>
                <form:select cssClass="ui dropdown label std-select" path="specialTestType">
                    <c:forEach items="${specialTestTypeList}" var="specialTestType">
                        <form:option value="${specialTestType}"><fmt:message key="${specialTestType.property}"/></form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field required">
                <label>Дата поставки</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="deliveryDate"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="deliveryDate"></div>
            </div>
            <div class="field required">
                <label>Цена</label>
                <form:input  path="price" cssClass="detail_delivery-statement_products_edit__price-input"/>
                <div class="ui compact message error" data-field="price"></div>
            </div>
            <div class="field">
                <label>Вид цены</label>
                <form:select cssClass="ui dropdown label std-select detail_delivery-statement_products_edit__price-kind-select" path="priceKind">
                    <c:forEach items="${priceKindTypeList}" var="priceKind">
                        <form:option value="${priceKind}">
                            <fmt:message key="${priceKindExport eq priceKind and isExport ? 'priceKind.fixed' : priceKind.property}"/>
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field detail_delivery-statement_products_edit__protocol-select">
                <label>Протокол</label>
                <form:select cssClass="ui dropdown label std-select detail_delivery-statement_products_edit__protocol-dropdown" path="productChargesProtocol">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${productChargesProtocolList}" var="protocol">
                        <form:option value="${protocol.id}">
                            <c:set var="date"><javatime:format value="${protocol.protocolDate}" pattern="dd.MM.yyyy"/></c:set>
                            <fmt:message key="productChargesProtocol.params">
                                <fmt:param value="${protocol.protocolNumber}"/>
                                <fmt:param value="${date}"/>
                            </fmt:message>
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>
        </form:form>
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
        const priceKindPreliminary = '${priceKindPreliminary}';
        const priceKindExport = '${priceKindExport}';
        const priceKindFinal = '${priceKindFinal}';
        const isProtocolList = '${isProtocolList}' === 'true';
        const contractSectionId = '${contractSectionId}';
        const productId = '${productId}';
        const price = '${price}';
        const isLotId = '${not empty form.lotId}' === 'true';
        //
        const $priceInput = $('input.detail_delivery-statement_products_edit__price-input');
        const $selectPriceKind = $('div.detail_delivery-statement_products_edit__price-kind-select');
        const $selectProtocol = $('div.detail_delivery-statement_products_edit__protocol-select');
        const $protocolDropdown = $('div.detail_delivery-statement_products_edit__protocol-dropdown');
        const $inputAmount = $('input.detail_delivery-statement_products_edit__field-amount');

        // Ивент для загрузки нужной цены при определенном протоколе
        let protocolPriceKind;
        $protocolDropdown.dropdown().on({
            'change': e => $.post({
                url:  ACTION_PATH.DETAIL_DELIVERY_STATEMENT_EDIT_NEEDED_PRICE,
                data: {
                    contractSectionId: contractSectionId,
                    priceKind: protocolPriceKind,
                    protocolId: $(e.currentTarget).find('option:selected').val(),
                    productId: productId
                }
            }).done(data => $priceInput.val(data))
        });

        $selectPriceKind.dropdown().on({
            'change': e => {
                let priceKind = $(e.currentTarget).find('option:selected').val();
                let isProtocol = priceKind === priceKindFinal;
                $selectProtocol.toggle(isProtocol);
                if (isProtocol) {
                    protocolPriceKind = priceKind;
                    $protocolDropdown.trigger('change');
                }
            }
        }).trigger('change');

        $priceInput.inputmask('inputMoney');

        $inputAmount.inputmask({
            placeholder: '',
            regex: '[0-9]{0,6}'
        });
    })
</script>