<c:forEach items="${newsList}" var="news">
    <div class="news-item news-list__item">
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
        <a href="" class="news-item__title">${news.title}</a>
        <div class="news-item__preview-text">${news.previewText}</div>
    </div>
</c:forEach>