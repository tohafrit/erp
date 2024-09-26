<div class="ui modal">
    <div class="header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Предпрятие</label>
                <select class="std-select" name="plantId">
                    <c:forEach items="${plantList}" var="plant">
                        <option value="${plant.id}">${plant.nazPrin}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="dateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label>по</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="dateTo"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>