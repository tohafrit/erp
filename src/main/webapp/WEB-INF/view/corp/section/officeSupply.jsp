<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/corp/view/office-supply.css"/>">

<div class="root__content"></div>

<script>
    $(() => {
        const $content = $('div.root__content');

        $.get({
            url: '/api/view/corp/office-supply/list',
            beforeSend: () => togglePreloader(true),
            complete: () => togglePreloader(false)
        }).done(html => $content.html(html));
    })
</script>
