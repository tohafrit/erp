<table class="ui padded table">
    <tr>
        <th>Заголовок</th>
        <td colspan="2">${news.title}</td>
        <c:if test="${not empty image}">
            <td rowspan="2">
                <a href="<c:url value="/download-file/${image.urlHash}"/>" data-fancybox="gallery">
                    <img src="/download-file/${image.urlHash}" class="list_detail__image" alt="${news.title}"/>
                </a>
            </td>
        </c:if>
    </tr>
    <tr>
        <th>Дата создания</th>
        <td colspan="2"><javatime:format value="${news.dateCreated}" pattern="dd.MM.yyyy HH:mm:ss"/></td>
    </tr>
    <tr>
        <th>Анонс</th>
        <td colspan="3">${news.previewText}</td>
    </tr>
    <tr>
        <th>Полный текст</th>
        <td colspan="3">${news.detailText}</td>
    </tr>
    <tr>
        <th>Дата события с</th>
        <td><javatime:format value="${news.dateEventFrom}" pattern="dd.MM.yyyy HH:mm:ss"/></td>
        <th>по</th>
        <td><javatime:format value="${news.dateEventTo}" pattern="dd.MM.yyyy HH:mm:ss"/></td>
    </tr>
    <tr>
        <th>Дата активности с</th>
        <td><javatime:format value="${news.dateActiveFrom}" pattern="dd.MM.yyyy HH:mm:ss"/></td>
        <th>по</th>
        <td><javatime:format value="${news.dateActiveTo}" pattern="dd.MM.yyyy HH:mm:ss"/></td>
    </tr>
    <tr>
        <th>Тип</th>
        <td>${news.typeName}</td>
        <th>Закреплённая</th>
        <td>${news.topStatus ? "Да" : "Нет"}</td>
    </tr>
</table>