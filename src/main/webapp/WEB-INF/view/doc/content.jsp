<jsp:include page="/documentation/navigation"/>
<div class="documentation__breadcrumbs-line">
    <div class="ui breadcrumb">
        <a class="section" href="/documentation">Главная</a>
        <c:forEach items="${breadcrumbsList}" var="item">
            <div class="divider">/</div> <a class="section" href="/documentation/${item.id}">${item.name}</a>
        </c:forEach>
        <c:if test="${not empty documentation.name}">
            <div class="divider">/</div> <div class="active section">${documentation.name}</div>
        </c:if>
    </div>
    <div class="documentation__print">
        <i class="print icon documentation__breadcrumbs_print_i"></i>
    </div>
</div>
<div class="documentation__content">
    <div class="documentation__content_text">
        <c:choose>
            <c:when test="${not empty documentation.content}">
                <h1>${documentation.name}</h1>
                <p>${documentation.content}</p>
            </c:when>
            <c:otherwise>
                <h1>Документация</h1>
            </c:otherwise>
        </c:choose>
    </div>
    <c:if test="${not empty documentation.seeAlsoList}">
        <div class="documentation__content_see-also">
            <hr/>
            <h2>См. также:</h2>
            <div class="ui list">
                <c:forEach items="${documentation.seeAlsoList}" var="seeAlso">
                    <a class="item" href="/documentation/${seeAlso.id}">${seeAlso.name}</a>
                </c:forEach>
            </div>
            <hr />
        </div>
    </c:if>
</div>

<script>
    $(() => {
        const $printI = $('i.documentation__breadcrumbs_print_i');
        const $contentText = $('div.documentation__content_text');

        $printI.on({
            'click': () => $contentText.printArea()
        });
    });
</script>