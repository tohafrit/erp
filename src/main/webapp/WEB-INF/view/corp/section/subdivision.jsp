<div class="root__content"></div>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/corp/view/subdivision.css"/>">

<script>
    $(() => {
        const $content = $('div.root__content');

        $.get({
            url: '/api/view/corp/subdivision/list',
            beforeSend: () => togglePreloader(true),
            complete: () => togglePreloader(false)
        }).done(html => $content.html(html));
    })
</script>