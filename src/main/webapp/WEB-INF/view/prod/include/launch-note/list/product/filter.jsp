<div class="ui modal">
    <div class="header">Фильтр изделий</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Наименование изделия</label>
                <input type="search" name="productName"/>
            </div>
            <div class="field">
                <label>Запуск</label>
                <select class="std-tree-select" name="launchId" multiple>
                    <std:treeChosen hierarchyList="${launchList}"/>
                </select>
            </div>
        </form>
    </div>
</div>