<jsp:include page="/documentation/navigation"/>
<div class="documentation__breadcrumbs-line">
    <div class="ui breadcrumb">
        <a class="section" href="/documentation">Главная</a>
        <div class="divider">/</div>
        <div class="active section">Результаты поиска</div>
    </div>
</div>
<div class="documentation__content">
    <div class="documentation__content_text">
        <c:forEach items="${resultList}" var="result">
            <a href="/documentation/${result.id}"><h1>${result.name}</h1></a>
            <p>${result.highlightedContent}</p>
            <hr/>
        </c:forEach>
        <c:if test="${empty resultList}">
            <p>Ничего не найдено.</p>
        </c:if>
    </div>
</div>