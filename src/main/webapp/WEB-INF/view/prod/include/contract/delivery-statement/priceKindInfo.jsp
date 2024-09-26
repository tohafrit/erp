<div class="priceKindInfo-dialog" title="<fmt:message key="priceKind.select"/>">
    <div class="b-common-margin20">
        <table class="b-full-width js-add-invoice">
            <c:forEach items="${priceKindTypeList}" var="priceKindType" varStatus="status">
                <tr>
                    <th class="b-table-edit__th">
                        <div class="ui radio checkbox">
                            <input type="radio" name="priceKind" id="priceKind-${priceKindType.id}"
                                   value="${priceKindType.id}" <c:if test="${priceKindId eq priceKindType.id}">checked="checked"</c:if>/>
                            <label for="priceKind-${priceKindType.id}">
                                <fmt:message key="${priceKindExportId eq priceKindType.id and contractSection.contract.contractType ne contractTypeSupplyOfExported  ? 'priceKind.fixed' : priceKindType.property}"/>
                            </label>
                        </div>
                    </th>
                    <td class="b-table-edit__td">
                        <c:if test="${status.last}">
                            <select class="ui dropdown js-protocols">
                                <c:forEach items="${product.productChargesProtocolList}" var="productChargesProtocol">
                                    <option value="${productChargesProtocol.id}"
                                            <c:if test="${productChargesProtocol.id eq protocolId}">
                                                selected="selected"
                                            </c:if>
                                    >
                                        <c:set var="date"><javatime:format value="${productChargesProtocol.protocolDate}" pattern="dd.MM.yyyy"/></c:set>
                                        <fmt:message key="productChargesProtocol.params">
                                            <fmt:param value="${productChargesProtocol.protocolNumber}"/>
                                            <fmt:param value="${date}"/>
                                        </fmt:message>
                                    </option>
                                </c:forEach>
                            </select>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
    <div class="b-common-margin20 b-common-fl-right">
        <button class="ui small button b-btn b-btn-select js-select-btn" type="button"><fmt:message key="label.button.select"/></button>
    </div>
</div>

<script>
    $(() => {
        const
            $dialog = $('.priceKindInfo-dialog'),
            $protocolDropdown = $dialog.find('.js-protocols'),
            $priceKindRadio = $dialog.find('[name="priceKind"]'),
            $btnSelect = $dialog.find('.js-select-btn'),
            priceKindFinalId = '${priceKindFinalId}',

            $parentDialog = $('.editDeliveryStatement-dialog'),
            sectionId = '${contractSection.id}',
            parentPriceKindId = '${priceKindId}',
            parentProtocolId = '${protocolId}',
            $parentRow = $parentDialog.find('.js-table-statement-container tbody tr:eq(${index})');

        $priceKindRadio.on({
            'change' : e => $protocolDropdown.toggleClass('disabled', $(e.currentTarget).val() !== priceKindFinalId)
        });
        $dialog.find('[name="priceKind"]:checked').trigger('change');

        // Выбор вида цены и/или протокола
        $btnSelect.on({
           'click' : () => {
               let
                   $checkedPriceKind = $dialog.find('[name="priceKind"]:checked'),
                   $checkedPriceKindId = $checkedPriceKind.val(),
                   $priceKindText = $checkedPriceKind.next().text(),
                   protocolId = null;

               if ($checkedPriceKindId === priceKindFinalId) {
                   let $protocolOption = $protocolDropdown.find('option:selected');
                   $priceKindText += ' ' + $protocolOption.text();
                   protocolId = $protocolOption.val();
               }

               if ($checkedPriceKindId !== parentPriceKindId || ($checkedPriceKindId === parentPriceKindId && parentProtocolId !== protocolId)) {
                   $.get({
                       url: '/ajaxLoadNeededPrice',
                       data: {
                           sectionId: sectionId,
                           priceKindId: $checkedPriceKindId,
                           protocolId: protocolId,
                           productId: '${product.id}'
                       }
                   }).done(data => {
                       $parentRow.find('.js-price-kind').text($priceKindText);
                       $parentRow.find('.js-price-kind-id').val($checkedPriceKindId);
                       $parentRow.find('.js-protocol-id').val(protocolId);
                       $parentRow.find('.js-price').val(data);
                       $dialog.dialog('close');
                   }).fail(() => {
                       globalMessage({message: 'Ошибка загрузки цены'});
                   }).then(() => {});
               }
           }
        });
    });
</script>

