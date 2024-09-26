<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/corp/view/news-edition.css"/>">

<div class="root__content"></div>

<script>
    $(() => {
        const $content = $('div.root__content');

        $.get({
            url: '/api/view/corp/news-edition/list',
            beforeSend: () => togglePreloader(true),
            complete: () => togglePreloader(false)
        }).done(html => $content.html(html));
    })
</script>