<c:if test="${not empty topNews}">
    <div class="block__title">Важная новость</div>
    <div class="important-news">
        <div class="important-news__image">
            <c:if test="${empty topNews.fileUrlHash}">
                <img src="<c:url value="/resources/image/news-1.png"/>" alt="${topNews.title}">
            </c:if>
            <c:if test="${not empty topNews.fileUrlHash}">
                <img src="<c:url value="/download-file/${topNews.fileUrlHash}"/>" alt="${topNews.title}">
            </c:if>
        </div>
        <div class="important-news__content">
            <div class="important-news__date"><javatime:format value="${topNews.date}" pattern="dd.MM.yyyy"/></div>
            <div class="important-news__title">${topNews.title}</div>
            <div class="important-news__text">${topNews.previewText}</div>
            <c:if test="${not empty topNews.detailText}">
                <a href="<c:url value="/corp/news/list/${topNews.id}"/>" class="important-news__detail">Подробнее...</a>
            </c:if>
        </div>
    </div>
    <div class="block__title">Другие новости</div>
</c:if>
<div class="news-list">
    <c:forEach items="${newsList}" var="news">
        <div class="news-item news-list__item news-item_mb30">
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
            <a href="<c:url value="/corp/news/list/${news.id}"/>" class="news-item__title">${news.title}</a>
            <div class="news-item__preview-text">${news.previewText}</div>
            <c:if test="${not empty news.detailText}">
                <a href="<c:url value="/corp/news/list/${news.id}"/>">Подробнее...</a>
            </c:if>
        </div>
    </c:forEach>
</div>
<a class="news-item__more"><i class="fas fa-undo news-item__icon"></i>Ещё новости</a>

<script>
    $(() => {
        const $newsList = $('div.news-list');
        const $more = $('a.news-item__more');
        const pages = ${pages};

        let pageCounter = 1;

        $more.on({
            'click': () => {
                $.get({
                    url: '/api/view/corp/news/list/load',
                    data: { page: pageCounter++ },
                }).done(html => {
                    $more.toggle(pageCounter !== pages);
                    $newsList.append(html);
                });
            }
        });
    })
</script>