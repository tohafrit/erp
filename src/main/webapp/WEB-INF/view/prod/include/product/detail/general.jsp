<div class="detail_general__main">
    <i class="ui icon blue pencil link alternate detail_general__btn-edit" title="Редактировать изделие"></i>
    <table class="ui tiny definition table detail_general__table">
        <tbody>
            <tr>
                <td><fmt:message key="product.field.conditionalName"/></td>
                <td>${product.conditionalName}</td>
            </tr>
            <tr>
                <td><fmt:message key="product.field.techSpecName"/></td>
                <td>${product.techSpecName}</td>
            </tr>
            <tr>
                <td>Серийное</td>
                <td>${product.serial ? 'Да' : 'Нет'}</td>
            </tr>
            <tr>
                <td><fmt:message key="product.field.type"/></td>
                <td>${product.type.name}</td>
            </tr>
            <tr>
                <td><fmt:message key="product.field.decimalNumber"/></td>
                <td>${product.decimalNumber}</td>
            </tr>
            <tr>
                <td><fmt:message key="product.field.letter"/></td>
                <td>${product.letter.name}</td>
            </tr>
            <tr>
                <td><fmt:message key="product.field.status"/></td>
                <td>
                    <c:if test="${empty product.archiveDate}">
                        <fmt:message key="product.field.status.active"/>
                    </c:if>
                    <c:if test="${not empty product.archiveDate}">
                        <fmt:message key="product.field.status.archive"/>
                        <javatime:format value="${product.archiveDate}" pattern="dd.MM.yyyy HH:mm:ss"/>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td><fmt:message key="product.field.position"/></td>
                <td>${product.position}</td>
            </tr>
            <tr>
                <td><fmt:message key="product.field.lead"/></td>
                <td>${product.lead.userOfficialName}</td>
            </tr>
            <tr>
                <td><fmt:message key="product.field.classificationGroup"/></td>
                <td>${product.classificationGroup.number} ${product.classificationGroup.characteristic}</td>
            </tr>
            <c:if test="${not empty product.templatePath}">
                <tr>
                    <td><fmt:message key="product.field.templatePath"/></td>
                    <td>${product.templatePath}</td>
                </tr>
            </c:if>
            <tr>
                <td><fmt:message key="product.field.comment"/></td>
                <td>${product.comment}</td>
            </tr>
        </tbody>
    </table>
</div>

<script>
    $(() => {
        const $btnEdit = $('i.detail_general__btn-edit');
        const $generalLink = $('a.detail__menu_general');
        $btnEdit.on({
            'click': () => $.modalWindow({
                loadURL: '/api/view/prod/product/list/edit',
                loadData: { id: '${product.id}' },
                submitURL: '/api/action/prod/product/list/edit/save',
                onSubmitSuccess: () => $generalLink.trigger('click')
            })
        });
    });
</script>