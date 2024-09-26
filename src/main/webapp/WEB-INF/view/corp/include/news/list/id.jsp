<div class="news-item__breadcrumbs">
    <a href="<c:url value="/corp/news"/>">Новости</a> > ${news.title}
</div>
<div class="news-item__image">
    <c:if test="${empty news.fileUrlHash}">
        <img src="<c:url value="/resources/image/news-1.png"/>" alt="${news.title}">
    </c:if>
    <c:if test="${not empty news.fileUrlHash}">
        <img src="<c:url value="/download-file/${news.fileUrlHash}"/>" alt="${news.title}">
    </c:if>
</div>
<div class="news-item__date">
    <javatime:format value="${news.date}" pattern="dd.MM.yyyy"/>
</div>
<span class="news-item__title">${news.title}</span>
<div class="news-item__preview-text">${news.detailText}</div>