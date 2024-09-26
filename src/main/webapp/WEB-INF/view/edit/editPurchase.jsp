<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="purchase-dialog" title="<fmt:message key="purchase.${docType}"/>">
    <form:form method="POST" modelAttribute="form" cssClass="ui form small">
        <form:hidden path="id"/>
        <table class="b-full-width">
            <tr>
                <th class="b-table-edit__th"><fmt:message key="purchase.table.field.name"/></th>
                <td class="b-table-edit__td">
                    <div class="ui fluid input">
                        <form:input path="name"/>
                    </div>
                    <div class="name b-error"></div>
                </td>
            </tr>
            <tr>
                <th class="b-table-edit__th"><fmt:message key="purchase.table.field.planDate"/></th>
                <td class="b-table-edit__td">
                    <div class="ui fluid input" style="width:140px;">
                        <form:input cssClass="erp-date" path="planDate"/>
                    </div>
                    <div class="planDate b-error"></div>
                </td>
            </tr>
            <tr>
                <th class="b-table-edit__th"><fmt:message key="purchase.table.field.launch"/></th>
                <td class="b-table-edit__td">
                    <div class="ui fluid input">
                        <form:select cssClass="js-launch ui dropdown label" path="launch.id" data-dropdown-options='{"clearable":true}'>
                            <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                            <c:forEach items="${launchList}" var="launch">
                                <form:option value="${launch.id}">${launch.numberInYear}</form:option>
                            </c:forEach>
                        </form:select>
                    </div>
                </td>
            </tr>
            <tr>
                <th class="b-table-edit__th"><fmt:message key="purchase.table.field.previousLaunch"/></th>
                <td class="b-table-edit__td">
                    <div class="ui fluid input js-previous-launch">
                        <form:select cssClass="ui dropdown label" path="launchList" multiple="true">
                            <c:if test="${fn:length(previousLaunchList) > 0}">
                                <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                                <c:forEach items="${previousLaunchList}" var="previousLaunch">
                                    <form:option value="${previousLaunch.id}">${previousLaunch.numberInYear}</form:option>
                                </c:forEach>
                            </c:if>
                        </form:select>
                    </div>
                </td>
            </tr>
            <tr>
                <th class="b-table-edit__th"><fmt:message key="purchase.table.field.version"/></th>
                <td class="b-table-edit__td">
                    <div class="ui fluid input">
                        <form:select cssClass="ui dropdown label" path="type">
                            <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                            <c:forEach items="${typeList}" var="type">
                                <form:option value="${type.id}"><fmt:message key="${type.property}"/></form:option>
                            </c:forEach>
                        </form:select>
                    </div>
                </td>
            </tr>
            <tr>
                <th class="b-table-edit__th"><fmt:message key="purchase.table.field.note"/></th>
                <td class="b-table-edit__td"><form:textarea path="note" rows="3"/></td>
            </tr>
            <tr>
                <th>&nbsp;</th><td class="b-table-edit__td b-text-right"><button class="ui small button b-btn b-btn-save" type="submit"><fmt:message key="label.button.save"/></button></td>
            </tr>
        </table>
    </form:form>
</div>

<script>
    $(() => {
        $('.js-launch').on({
            'change' : e => {
                const
                    launchId = $(e.currentTarget).find('option:selected').val(),
                    $previousLaunchDiv = $('.js-previous-launch');

                $.get('/getPreviousLaunchList', { launchId: launchId }).done(data => {
                    let
                        select = '<select class="ui dropdown label" name="launchList" id="launchList" multiple>';

                    select += '<option value=""><fmt:message key="text.notSpecified"/></option>';
                    $.each(data, (key, launch) => {
                        select += '<option value="' + launch.id + '">' + launch.numberInYear + '</option>';
                    });
                    $previousLaunchDiv.empty();
                    $(select).appendTo($previousLaunchDiv).addClass('search').dropdown({ fullTextSearch: true });
                });
            }
        });
    });
</script>