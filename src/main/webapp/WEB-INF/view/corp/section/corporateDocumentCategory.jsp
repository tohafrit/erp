<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/corp/view/corporate-document-category.css"/>">

<div class="root__content"></div>

<script>
    $(() => {
        const $content = $('div.root__content');

        $.get({
            url: '/api/view/corp/corporate-document-category/list',
            beforeSend: () => togglePreloader(true),
            complete: () => togglePreloader(false)
        }).done(html => $content.html(html));
    })
</script>