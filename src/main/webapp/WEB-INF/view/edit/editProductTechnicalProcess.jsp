<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="productTechnicalProcess-dialog" title="<fmt:message key="productTechnicalProcess.${docType eq 'edit' ? 'editing' : 'adding'}"/>">
    <form:form method="POST" modelAttribute="editProductTechnicalProcessForm">
        <form:hidden path="id"/>
        <form:hidden path="justification.id"/>
        <form:hidden path="deleteLaboriousnessList"/>
        <input type="hidden" name="buffer" id="buffer"/>
        <table class="b-full-width">
            <tr>
                <td class="b-table-edit__td">
                    <table class="b-full-width">
                        <tr>
                            <th class="b-table-edit__th" style="width:120px;"><fmt:message key="productTechnicalProcess.name"/></th>
                            <td class="b-table-edit__td">
                                <div class="ui fluid input">
                                    <c:set var="name">
                                        <fmt:message key="productTechnicalProcess.defaultName"/>
                                    </c:set>
                                    <c:if test="${fn:length(editProductTechnicalProcessForm.name) > 0}">
                                        <c:set var="name" value="${editProductTechnicalProcessForm.name}"/>
                                    </c:if>
                                    <form:input path="name" value="${name}"/>
                                </div>
                                <div class="name b-error"></div>
                            </td>
                        </tr>
                        <tr>
                            <th class="b-table-edit__th"><fmt:message key="productTechnicalProcess.source"/></th>
                            <td class="b-table-edit__td">
                                <div class="ui fluid input">
                                    <form:input path="source" value=""/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <th class="b-table-edit__th"><fmt:message key="productTechnicalProcess.approved"/></th>
                            <td class="b-table-edit__td" style="vertical-align: middle;text-align: left;">
                                <div class="ui checkbox">
                                    <form:checkbox path="approved"/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <th style="vertical-align: middle;" class="b-table-edit__th"><fmt:message key="productTechnicalProcess.product"/></th>
                            <td class="b-table-edit__td">
                                <button class="js-btn-add compact ui button b-btn b-btn-add" type="button"></button>
                                <table class="b-table js-product-info">
                                    <form:hidden path="product.id" cssClass="js-product-id"/>
                                    <tr>
                                        <th class="b-table__th b-text-right" colspan="2">
                                            <button class="js-btn-edit ui small button b-btn b-btn-edit" type="button"></button>
                                            <i class="js-remove-product b-icon-remove"></i>
                                        </th>
                                    </tr>
                                    <tr>
                                        <th class="b-table__th"><fmt:message key="productTechnicalProcess.product.name"/></th>
                                        <td class="b-table__td">${editProductTechnicalProcessForm.product.name}</td>
                                    </tr>
                                    <tr>
                                        <th class="b-table__th"><fmt:message key="productTechnicalProcess.product.decimalNumber"/></th>
                                        <td class="b-table__td">${editProductTechnicalProcessForm.product.decimalNumber}</td>
                                    </tr>
                                </table>
                                <div class="product b-error"></div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="b-table-edit__td">
                    <p class="justification b-error"></p>
                    <table class="b-table b-full-width dialog-list-form-container js-work-type">
                        <thead>
                            <tr>
                                <th class="b-table__th"></th>
                                <th class="b-table__th"><fmt:message key="laboriousness.number"/></th>
                                <th class="b-table__th"><fmt:message key="laboriousness.workType"/></th>
                                <th class="b-table__th"><fmt:message key="laboriousness.value"/></th>
                                <th class="b-table__th"><fmt:message key="laboriousness.package"/></th>
                                <th class="b-table__th b-align-mid-center">
                                    <button class="js-add-option compact ui button b-btn b-btn-add" type="button" title='<fmt:message key="productTechnicalProcess.operation.add"/>'></button>
                                    <button class="js-buffer-option compact ui button b-btn b-btn-buffer" type="button" title='<fmt:message key="productTechnicalProcess.insert.array"/>'></button>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="js-buffer-origin-event b-display_none">
                                <td colspan="6" class="b-table__td">
                                    <div class="ui fluid input">
                                        <c:set var="placeholder">
                                            <fmt:message key="productTechnicalProcess.buffer.placeholder"/>
                                        </c:set>
                                        <input type="text" placeholder="${placeholder}" value=""/>
                                    </div>
                                </td>
                            </tr>
                            <c:if test="${docType eq 'add'}">
                                <tr>
                                    <td class="b-table__td nfile" style="vertical-align:middle"></td>
                                    <td class="b-table__td">
                                        <div class="ui fluid input">
                                            <input class="js-number" type="text" data-list-form-attribute="laboriousnessFormList.number" value="005"/>
                                        </div>
                                    </td>
                                    <td class="b-table__td">
                                        <input type="hidden" data-list-form-attribute="laboriousnessFormList.id" value=""/>
                                        <select class="ui dropdown" data-list-form-attribute="laboriousnessFormList.workTypeId">
                                            <c:forEach items="${workTypeList}" var="workType">
                                                <option value="${workType.id}">${workType.name}</option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                    <td class="b-table__td b-align-mid-center">
                                        <div class="ui fluid input">
                                            <input class="js-value" type="text" data-list-form-attribute="laboriousnessFormList.value" value=""/>
                                        </div>
                                    </td>
                                    <td class="b-table__td b-align-mid-center">
                                        <div class="ui checkbox">
                                            <input type="checkbox" data-list-form-attribute="laboriousnessFormList.withPackage"/>
                                        </div>
                                    </td>
                                    <td class="b-table__td b-align-mid-center">
                                        <i class="js-remove-option b-icon-remove" title='<fmt:message key="productTechnicalProcess.operation.delete"/>'></i>
                                    </td>
                                </tr>
                            </c:if>
                            <c:forEach items="${editProductTechnicalProcessForm.laboriousnessFormList}" var="laboriousnessForm" varStatus="status">
                                <tr>
                                    <td class="b-table__td nfile" style="vertical-align:middle">${status.count}</td>
                                    <td class="b-table__td b-align-mid-center">
                                        <div class="ui fluid input">
                                            <input class="js-number" type="text" data-list-form-attribute="laboriousnessFormList.number" value="${laboriousnessForm.number}"/>
                                        </div>
                                    </td>
                                    <td class="b-table__td">
                                        <input type="hidden" data-list-form-attribute="laboriousnessFormList.id" value="${laboriousnessForm.id}"/>
                                        <select class="ui dropdown" data-list-form-attribute="laboriousnessFormList.workTypeId">
                                            <c:forEach items="${workTypeList}" var="workType">
                                                <option value="${workType.id}"
                                                        <c:if test="${laboriousnessForm.workTypeId eq workType.id}">selected="selected"</c:if>
                                                >${workType.name}</option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                    <td class="b-table__td b-align-mid-center">
                                        <div class="ui fluid input">
                                            <input class="js-value" type="text" data-list-form-attribute="laboriousnessFormList.value" value="${laboriousnessForm.value}"/>
                                        </div>
                                    </td>
                                    <td class="b-table__td b-align-mid-center">
                                        <div class="ui checkbox">
                                            <input type="checkbox" data-list-form-attribute="laboriousnessFormList.withPackage"
                                                   <c:if test="${laboriousnessForm.withPackage eq 'true'}">checked</c:if>/>
                                        </div>
                                    </td>
                                    <td class="b-table__td b-align-mid-center">
                                        <i class="js-remove-option b-icon-remove" title='<fmt:message key="productTechnicalProcess.operation.delete"/>'></i>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="b-table-edit__td b-text-right"><button class="ui small button b-btn b-btn-save" type="submit"><fmt:message key="label.button.save"/></button></td>
            </tr>
        </table>
    </form:form>
    <script>
        $(() => {
            let url = new URL(window.location.href);
            $('#justification\\.id').val(url.searchParams.get('typeValue'));

            const
                $productTechnicalProcessDialog = $('.productTechnicalProcess-dialog'),
                $productTable = $productTechnicalProcessDialog.find('.js-product-info'),
                $workTypeTable = $productTechnicalProcessDialog.find('table.js-work-type'),
                $buttonAdd = $productTechnicalProcessDialog.find('.js-btn-add'),
                $buttonBuffer = $productTechnicalProcessDialog.find('.js-buffer-option'),
                $buffer = $productTechnicalProcessDialog.find('.js-buffer-origin-event'),
                $buttonEdit = $productTechnicalProcessDialog.find('.js-btn-edit'),
                removeOptionSel = '.js-remove-option',
                addOptionSel = '.js-add-option';

            let productExists = ${editProductTechnicalProcessForm.product.id ne null},
                row =
                    `<tr>
                        <td class="b-table__td nfile" style="vertical-align:middle"></td>
                        <td class="b-table__td">
                            <div class="ui fluid input">
                                <input class="js-number" type="text" data-list-form-attribute="laboriousnessFormList.number" value=""/>
                            </div>
                        </td>
                        <td class="b-table__td">
                            <input type="hidden" data-list-form-attribute="laboriousnessFormList.id" value=""/>
                            <select class="ui dropdown" data-list-form-attribute="laboriousnessFormList.workTypeId">
                                <c:forEach items="${workTypeList}" var="workType">
                                    <option value="${workType.id}">${workType.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td class="b-table__td b-align-mid-center">
                            <div class="ui fluid input">
                                <input class="js-value" type="text" data-list-form-attribute="laboriousnessFormList.value" value=""/>
                            </div>
                        </td>
                        <td class="b-table__td b-align-mid-center">
                            <div class="ui checkbox">
                                <input type="checkbox" data-list-form-attribute="laboriousnessFormList.withPackage"/>
                            </div>
                        </td>
                        <td class="b-table__td b-align-mid-center">
                            <i class="js-remove-option b-icon-remove" title='<fmt:message key="productTechnicalProcess.operation.delete"/>'></i>
                        </td>
                    </tr>`;

            $productTable.toggle(productExists);
            $buttonAdd.toggle(!productExists);

            initSortableTable($workTypeTable);
            // Функция пересчета таблицы
            $workTypeTable.on({
                'table.recalculate': () => {
                    $workTypeTable.find(removeOptionSel).off();
                    $workTypeTable.find('.nfile').each(function(index) {
                        $(this).text(index + 1);
                    });
                    $workTypeTable.find(removeOptionSel).on({
                        'click': e => {
                            let $deleteLaboriousnessList = $('#deleteLaboriousnessList'),
                                deleteLaboriousnessList = $deleteLaboriousnessList.val().length > 0 ? $deleteLaboriousnessList.val().split(',') : [],
                                $this = $(e.currentTarget);
                            deleteLaboriousnessList.push($this.closest('tr').find('input:hidden').val());
                            $deleteLaboriousnessList.val(deleteLaboriousnessList);
                            $this.closest('tr').remove();
                            $workTypeTable.trigger('table.recalculate');
                        }
                    });
                }
            });
            $workTypeTable.trigger('table.recalculate');
            // Добавление опции
            $(addOptionSel).on({
                'click': () => {
                    let number = $workTypeTable.find('.js-number:last').val(),
                        $row = $(row);
                    $row.find('.js-number').val(
                        number !== undefined && !isNaN(number) ? (5*Math.floor((+number + 5)/5)).toString().padStart(3, '0') : '005'
                    );
                    $row.find('.js-value').inputmask('9{1,2}.99');
                    $row.find('select').addClass('search').dropdown({fullTextSearch: true});
                    $row.find('.ui.checkbox').checkbox();
                    $row.appendTo($workTypeTable.find('tbody'));
                    $workTypeTable.trigger('table.recalculate');
                }
            });

            // Убрать изделие
            $productTable.find('.js-remove-product').on({
                'click': () => {
                    $productTable.find('.js-product-id').val('');
                    $buttonAdd.show();
                    $productTable.hide();
                }
            });

            // Редактирование изделия
            $buttonAdd.add($buttonEdit).on({
                'click': () => {
                    $.modalDialog({
                        dialogName : 'selectedProduct',
                        url : '/product/selectedProduct',
                        parameters : {
                            selectedId: $productTechnicalProcessDialog.find('.js-product-id').val()
                        }
                    });
                }
            });

            // Работа с буфером
            $buffer.on({
                'paste': e => {
                    let content = e.originalEvent.clipboardData.getData('text/plain'),
                        arrRow = content.split('\n');
                    arrRow.forEach(function(element) {
                        if (element.length > 0) {
                            let $row = $(row);
                            $row.appendTo($workTypeTable.find('tbody'));
                            let arr = element.split('\t');
                            $row.find('td:eq(2)').find('input').val(arr[0].padStart(3, '0'));
                            $row.find('td:eq(3)').find('select option:contains(' + arr[1] + ')').prop('selected', true);
                            $row.find('td:eq(4)').find('input').val(arr[2].replace(',','.'));
                            $row.find('select').addClass('search').dropdown({fullTextSearch: true});
                            $row.find('.ui.checkbox').checkbox();
                        }
                    });
                    $workTypeTable.trigger('table.recalculate');
                }
            });
            $buttonBuffer.on({
                'click': () => {
                    $buffer.toggle();
                }
            });
        });
    </script>
</div>