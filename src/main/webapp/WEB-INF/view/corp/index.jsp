<div class="content__main-page">
    <div class="block">
        <div class="block__news">
            <div class="news__header">
                <div class="block__title">Новости</div>
                <a href="<c:url value="/corp/news"/>" class="news__all">Все новости</a>
            </div>
            <div class="news-list"></div>
        </div>
        <div class="block__report">
            <div class="block__title">Отчет о посещении</div>
            <table class="ui celled table">
                <thead>
                    <tr>
                        <th>Дата</th>
                        <th>Время прихода</th>
                        <th>Время ухода</th>
                        <th>Время на работе</th>
                        <th>Разница</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>01.02.2021</td>
                        <td>10:55:48</td>
                        <td>19:48:04</td>
                        <td>08:52:16</td>
                        <td class="negative">- 00:07:44</td>
                    </tr>
                    <tr>
                        <td>02.02.2021</td>
                        <td>10:59:07</td>
                        <td>16:07:19</td>
                        <td>05:08:12</td>
                        <td class="positive">+ 03:51:48</td>
                    </tr>
                    <tr>
                        <td>01.02.2021</td>
                        <td>10:55:48</td>
                        <td>19:48:04</td>
                        <td>08:52:16</td>
                        <td class="negative">- 00:07:44</td>
                    </tr>
                    <tr>
                        <td>02.02.2021</td>
                        <td>10:59:07</td>
                        <td>16:07:19</td>
                        <td>05:08:12</td>
                        <td class="positive">+ 03:51:48</td>
                    </tr>
                    <tr>
                        <td>01.02.2021</td>
                        <td>10:55:48</td>
                        <td>19:48:04</td>
                        <td>08:52:16</td>
                        <td class="negative">- 00:07:44</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="block">
        <div class="block__calendar">
            <div class="block__title">Календарь событий</div>
            <div class="calendar-wrapper">
                <div id="calendar"></div>
            </div>
        </div>
        <div class="block__vacation">
            <div class="block__title">Отпуска</div>
            <table class="ui celled table">
                <thead>
                    <tr>
                        <th>Предполагаемые отпуска</th>
                        <th>Фактические отпуска</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>04.02.2021 - 24.02.2021</td>
                        <td>02.05.2021- 24.05.2021</td>
                    </tr>
                    <tr>
                        <td>04.02.2021 - 24.02.2021</td>
                        <td>02.05.2021- 24.05.2021</td>
                    </tr>
                    <tr>
                        <td>04.02.2021 - 24.02.2021</td>
                        <td>02.05.2021- 24.05.2021</td>
                    </tr>
                    <tr>
                        <td><span class="vacation vacation__text">Количество <br />неотгуленных дней</span></td>
                        <td class="positive"><span class="vacation vacation__number">28</span></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $newsList = $('div.news-list');
        $.get({
            url: '/api/view/corp/index/news'
        }).done(html => $newsList.html(html));
    })
</script>