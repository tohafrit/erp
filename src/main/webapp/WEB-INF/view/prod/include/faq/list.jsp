<div class="list__header">
    <h1 class="list__header_title">Часто задаваемые вопросы</h1>
    <div class="list__header_buttons">
        <div class="ui icon small buttons">
            <div class="ui button basic list__btn-add" title="Добавить">
                <i class="add icon"></i>
            </div>
        </div>
    </div>
</div>
<div class="ui accordion list__accordion">
    <c:forEach items="${faqList}" var="faq">
        <hr />
        <div class="title list__question">
            <i class="dropdown icon"></i>
            <input type="hidden" value="${faq.id}">
            ${faq.question}
        </div>
        <div class="content list__answer">
            ${faq.answer}
        </div>
    </c:forEach>
</div>

<script>
    $(() => {
        const $addFaq = $('div.list__btn-add');
        const $content = $('div.root__content');
        const $accordion = $('div.list__accordion');

        // Инициализация раскрывающегося списка
        $accordion.accordion({
            exclusive: false
        });

        // Добавление вопроса
        $addFaq.on({
            'click': () => editFaq()
        });

        // Пункты меню
        let menuItems = {
            edit: {
                name: 'Редактировать',
                icon: 'edit',
                callback: (itemKey, opt) => editFaq(opt.$trigger.find('input').val())
            },
            delete: {
                name: 'Удалить',
                icon: 'delete',
                callback: (itemKey, opt) => deleteFaq(opt.$trigger.find('input').val())
            }
        };

        // Инициализация меню
        $.contextMenu({
            selector: 'div.list__question',
            build: () => {
                return { items: menuItems };
            }
        });

        // Функция добавления/редактирования вопроса
        function editFaq(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/faq/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/faq/list/edit/save',
                onSubmitSuccess: () => {
                    $.get({
                        url: '/api/view/prod/faq/list',
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(html => $content.html(html));
                }
            });
        }

        // Функция удаления вопроса
        function deleteFaq(id) {
            confirmDialog({
                title: 'Удаление вопроса',
                message: 'Вы действительно хотите удалить вопрос?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/prod/faq/list/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        $.get({
                            url: '/api/view/prod/faq/list',
                            beforeSend: () => togglePreloader(true),
                            complete: () => togglePreloader(false)
                        }).done(html => $content.html(html));
                    });
                }
            });
        }
    });
</script>