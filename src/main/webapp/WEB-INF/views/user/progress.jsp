<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Progress"/>
<c:set var="pageSub" value="How your training has gone over time."/>
<c:set var="nav" value="progress"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="stats">
    <div class="stat"><div class="num">${totals.workouts}</div><div class="lbl">Total workouts</div></div>
    <div class="stat"><div class="num">${totals.minutes}</div><div class="lbl">Total minutes</div></div>
    <div class="stat"><div class="num">${totals.calories}</div><div class="lbl">Total calories (est.)</div></div>
    <div class="stat accent"><div class="num">${streak}</div><div class="lbl">Current streak (days)</div></div>
</div>

<div class="card">
    <div class="card-head">
        <h2>This week vs last week</h2>
    </div>
    <p style="margin:0;">
        You trained <strong>${thisWeek} minutes</strong> since Monday, compared with <strong>${lastWeek} minutes</strong> last week.
        <c:choose>
            <c:when test="${weekDiff > 0}"><span class="badge green">+${weekDiff} min</span></c:when>
            <c:when test="${weekDiff < 0}"><span class="badge amber">${weekDiff} min</span></c:when>
            <c:otherwise><span class="badge grey">same</span></c:otherwise>
        </c:choose>
    </p>
</div>

<div class="tabs">
    <a href="?days=7"  class="${days == 7  ? 'active' : ''}">7 days</a>
    <a href="?days=14" class="${days == 14 ? 'active' : ''}">14 days</a>
    <a href="?days=30" class="${days == 30 ? 'active' : ''}">30 days</a>
</div>

<c:choose>
    <c:when test="${not hasData}">
        <div class="card"><p class="muted" style="margin:0;">No data to chart yet. Log a workout and the graphs will appear here.</p></div>
    </c:when>
    <c:otherwise>
        <div class="grid-2">
            <div class="card">
                <h2>Minutes per day</h2>
                <div class="chart-box"><canvas id="minChart"></canvas></div>
            </div>
            <div class="card">
                <h2>Calories per day</h2>
                <div class="chart-box"><canvas id="calChart"></canvas></div>
            </div>
        </div>
        <div class="card">
            <h2>Where your time goes</h2>
            <div class="chart-box"><canvas id="typeChart"></canvas></div>
        </div>
    </c:otherwise>
</c:choose>

<div class="split">
    <div class="card">
        <h2>Set a weekly goal</h2>
        <form method="post" action="${pageContext.request.contextPath}/user/goals">
            <input type="hidden" name="action" value="add">
            <div class="field">
                <label for="title">Goal name</label>
                <input type="text" id="title" name="title" maxlength="100" placeholder="e.g. Stay active" required>
            </div>
            <div class="row">
                <div class="field">
                    <label for="metric">Measured in</label>
                    <select id="metric" name="metric">
                        <option value="MINUTES">Minutes</option>
                        <option value="WORKOUTS">Workouts</option>
                        <option value="CALORIES">Calories</option>
                    </select>
                </div>
                <div class="field">
                    <label for="target">Target per week</label>
                    <input type="number" id="target" name="target" min="1" required>
                </div>
            </div>
            <button class="btn" type="submit">Add goal</button>
        </form>
    </div>

    <div class="card">
        <h2>Your goals this week</h2>
        <c:forEach var="g" items="${goals}">
            <div class="item">
                <div class="item-top">
                    <strong><c:out value="${g.title}"/></strong>
                    <span>
                        <c:if test="${g.percent >= 100}"><span class="badge green">Achieved</span></c:if>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/user/goals"
                              data-confirm="Remove this goal?">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="${g.id}">
                            <button class="btn danger sm" type="submit">Remove</button>
                        </form>
                    </span>
                </div>
                <div class="bar ${g.percent >= 100 ? 'done' : ''}"><span style="width:${g.percent}%"></span></div>
                <div class="small muted">${g.currentValue} of ${g.targetValue} ${g.unit} (${g.percent}%)</div>
            </div>
        </c:forEach>
        <c:if test="${empty goals}">
            <p class="muted" style="margin:0;">No goals yet.</p>
        </c:if>
    </div>
</div>

<c:if test="${hasData}">
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
    if (window.Chart) {
        var labels = ${dayLabels};
        var common = {
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, ticks: { precision: 0 } }, x: { grid: { display: false }, ticks: { maxRotation: 60, autoSkip: true } } }
        };

        new Chart(document.getElementById('minChart'), {
            type: 'bar',
            data: { labels: labels, datasets: [{ data: ${minuteValues}, backgroundColor: FT.forest, borderRadius: 3 }] },
            options: common
        });

        new Chart(document.getElementById('calChart'), {
            type: 'line',
            data: { labels: labels, datasets: [{ data: ${calorieValues}, borderColor: FT.clay, backgroundColor: 'rgba(217,98,43,.12)', fill: true, tension: .25, pointRadius: 3 }] },
            options: common
        });

        new Chart(document.getElementById('typeChart'), {
            type: 'doughnut',
            data: { labels: ${typeLabels}, datasets: [{ data: ${typeValues}, backgroundColor: FT.palette }] },
            options: { maintainAspectRatio: false, plugins: { legend: { position: 'right' } } }
        });
    }
</script>
</c:if>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
