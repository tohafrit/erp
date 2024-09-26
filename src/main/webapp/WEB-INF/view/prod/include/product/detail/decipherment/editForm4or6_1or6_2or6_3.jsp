<div class="ui modal decipherment_edit__modal">
    <div class="header"><h4>Редактирование - форма ${formName}</h4></div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <%-- Атрибуты --%>
            <c:forEach items="${attrList}" var="attr">
                <c:set var="attrKey" value="${deciphermentType.name().concat('.').concat(attr)}"/>
                <c:set var="attrValue" value="${requestScope[attrKey]}"/>
                <%-- Версия ЗС --%>
                <c:if test="${attr eq 'PURCHASE_SPECIFICATION_VERSION'}">
                    <div class="field">
                        <label>Версия ЗС</label>
                        <input class="js-version" type="hidden" name="${attrKey}" value="${attrValue.id}">
                        <i class="icon add link blue js-btn-add-version"></i>
                        <table class="ui tiny compact definition table js-version-table" style="width: auto;">
                            <tr>
                                <td class="right aligned" colspan="2">
                                    <i class="icon pencil link blue js-btn-edit"></i>
                                </td>
                            </tr>
                            <tr>
                                <th>Версия</th>
                                <td>${attrValue.version}</td>
                            </tr>
                        </table>
                        <div class="ui compact message small error" data-field="PURCHASE_SPECIFICATION_VERSION"></div>
                    </div>
                </c:if>
                <%-- Состав --%>
                <c:if test="${attr eq 'COMPOSITION'}">
                    <div class="field">
                        <label>Состав</label>
                        <div class="ui compact message small error" data-field="COMPOSITION"></div>
                        <div class="js-composition-tree"></div>
                        <input type="hidden" name="${attrKey}">
                    </div>
                </c:if>
            </c:forEach>
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
                <label>Начальник конструкторского отдела</label>
                <select name="headConstructId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq headConstructId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="headConstructId"></div>
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
                    <form:hidden path="fileStorage.id"/>
                    <input type="file" name="file"/>
                    <span>
                        ${form.fileStorage.name}
                        <a target="_blank" href="<c:url value="/download-file/${form.fileStorage.urlHash}"/>">
                            <fmt:message key="text.downloadFile"/>
                        </a>
                    </span>
                </div>
                <div class="ui compact message small error" data-field="file"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <form:textarea path="comment" rows="3"/>
                </div>
                <div class="ui compact message small error" data-field="comment"></div>
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
        const $modal = $('div.decipherment_edit__modal');
        const productId = '${productId}';
        const deciphermentId = '${form.id}';

        <%-- Расшифровка затрат на покупные комплектующие изделия --%>
        let $versionTable = $modal.find('.js-version-table'),
            $bAddVersion = $modal.find('.js-btn-add-version'),
            $bEditVersion = $versionTable.find('.js-btn-edit'),
            $versionHidden = $modal.find('.js-version'),
            $compositionTree = $modal.find('.js-composition-tree');

        $versionTable.toggle($versionHidden.val().length > 0);
        $bAddVersion.toggle($versionHidden.val().length === 0);

        // Загрузка состава для версии ЗС
        $versionHidden.on({
            'change': () => {
                if ($versionHidden.val()) {
                    $compositionTree.html('');
                    $.get({
                        url: '/decipherment/edit/composition-tree',
                        data: {
                            deciphermentId: deciphermentId,
                            versionId: $versionHidden.val()
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(html => {
                        $compositionTree.html(html);
                    }).fail(() => {
                        globalMessage({ message: 'Ошибка загрузки состава' });
                    });
                }
            }
        });
        $versionHidden.trigger('change');
        // Редактирование версии ЗС
        $bAddVersion.add($bEditVersion).on({
            'click': () => $.modalWindow({
                loadURL: '/decipherment/edit/search-version',
                loadData: {
                    mode: 'version',
                    productId: productId,
                    selectedVersionId: $versionHidden.val()
                },
                onAfterClose: () => $versionHidden.trigger('change')
            })
        });
    });
</script>