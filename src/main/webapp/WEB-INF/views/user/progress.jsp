<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Progress"/>
<c:set var="pageSub" value="How your training has gone over time."/>
<c:set var="nav" value="progress"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<%-- Progress-page-only styling. Scoped to body.page-progress / .pg so other pages are untouched. --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/progress.css?v=2">
<script>document.body.classList.add('page-progress');</script>

<div class="pg">

    <div class="stats">
        <div class="stat">
            <span class="stat-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
            </span>
            <div class="num">${totals.workouts}</div><div class="lbl">Total workouts</div>
        </div>
        <div class="stat">
            <span class="stat-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            </span>
            <div class="num">${totals.minutes}</div><div class="lbl">Total minutes</div>
        </div>
        <div class="stat">
            <span class="stat-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2c1 3.5 5 5.5 5 10a5 5 0 0 1-10 0c0-2 1-3 2-4 .5 1.5 1.5 2 2 2 0-3-.5-5 1-8z"/></svg>
            </span>
            <div class="num">${totals.calories}</div><div class="lbl">Total calories (est.)</div>
        </div>
        <div class="stat accent">
            <span class="stat-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
            </span>
            <div class="num">${streak}</div><div class="lbl">Current streak (days)</div>
        </div>
    </div>

    <div class="card pg-week">
        <div class="card-head">
            <h2>This week vs last week</h2>
        </div>
        <p>
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
            <div class="card pg-empty">
                <span class="stat-icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
                </span>
                <p class="muted">No data to chart yet. Log a workout and the graphs will appear here.</p>
                <a class="btn" href="${pageContext.request.contextPath}/user/workouts">Log a workout</a>
            </div>
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

            <div class="pg-row">
                <div class="card pg-consistency">
                    <h2>Workout consistency</h2>
                    <div class="pg-ring-wrap">
                        <div class="pg-ring" id="pgRing">
                            <div class="pg-ring-in"><b id="pgActive">0</b><small>days trained</small></div>
                        </div>
                        <p class="pg-ring-text muted" id="pgRingText"></p>
                    </div>
                    <div class="pg-dots" id="pgDots" role="img"></div>
                    <p class="pg-dots-note small muted" id="pgDotsNote" hidden>Each dot is one day in this range; filled dots are days you trained.</p>
                </div>
                <div class="card">
                    <h2>Where your time goes</h2>
                    <div class="chart-box"><canvas id="typeChart"></canvas></div>
                </div>
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
                <p class="muted">No goals yet.</p>
            </c:if>
        </div>
    </div>

</div>

<c:if test="${hasData}">
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
(function () {
    // Same server-rendered values as before.
    var labels        = ${dayLabels};
    var minuteValues  = ${minuteValues};
    var calorieValues = ${calorieValues};
    var rangeDays     = Number('${days}') || minuteValues.length;

    // Chart colors for the navy/teal theme (kept local so the page does not depend on load order of app.js).
    var PG = {
        navy: '#0b1b33',
        teal: '#14b8a6',
        muted: '#5b6b82',
        grid: '#e8eef5',
        palette: ['#0f766e', '#14b8a6', '#5eead4', '#1e3a8a', '#3b82f6', '#93c5fd', '#f59e0b', '#94a3b8']
    };

    /* ---------- Workout consistency (derived from the minutes-per-day series) ---------- */
    (function consistency() {
        if (!Array.isArray(minuteValues)) { return; }
        var active = 0;
        minuteValues.forEach(function (v) { if (Number(v) > 0) { active++; } });
        var total = rangeDays > 0 ? rangeDays : minuteValues.length;
        var pct = total > 0 ? Math.min(100, Math.round(active / total * 100)) : 0;

        var ring = document.getElementById('pgRing');
        var num  = document.getElementById('pgActive');
        var text = document.getElementById('pgRingText');
        var dots = document.getElementById('pgDots');
        var note = document.getElementById('pgDotsNote');

        if (ring) { ring.style.setProperty('--pct', pct); }
        if (num)  { num.textContent = active; }
        if (text) {
            text.textContent = active === 0
                ? 'No workouts logged in the last ' + total + ' days.'
                : 'You trained on ' + active + ' of the last ' + total + ' days (' + pct + '%).';
        }
        if (dots) {
            if (minuteValues.length === total) {
                minuteValues.forEach(function (v, i) {
                    var d = document.createElement('span');
                    d.className = 'pg-dot' + (Number(v) > 0 ? ' on' : '');
                    d.title = (labels[i] || '') + ': ' + v + ' min';
                    dots.appendChild(d);
                });
                dots.setAttribute('aria-label', 'Trained on ' + active + ' of ' + total + ' days');
                if (note) { note.hidden = false; }
            } else {
                dots.hidden = true;
            }
        }
    })();

    /* ---------- Charts ---------- */
    if (!window.Chart) { return; }

    Chart.defaults.color = PG.muted;
    Chart.defaults.font.family = getComputedStyle(document.body).fontFamily;

    function tooltip(unit) {
        return {
            backgroundColor: PG.navy, padding: 10, cornerRadius: 8, displayColors: false,
            callbacks: { label: function (c) { return c.parsed.y + ' ' + unit; } }
        };
    }

    function axes(unit) {
        return {
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            plugins: { legend: { display: false }, tooltip: tooltip(unit) },
            scales: {
                y: { beginAtZero: true, border: { display: false }, grid: { color: PG.grid }, ticks: { precision: 0 } },
                x: { border: { color: PG.grid }, grid: { display: false }, ticks: { maxRotation: 60, autoSkip: true } }
            }
        };
    }

    new Chart(document.getElementById('minChart'), {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                data: minuteValues,
                borderColor: PG.teal, backgroundColor: 'rgba(20,184,166,.14)',
                fill: true, tension: .35, borderWidth: 2.5,
                pointRadius: 3, pointHoverRadius: 5,
                pointBackgroundColor: '#fff', pointBorderColor: PG.teal, pointBorderWidth: 2
            }]
        },
        options: axes('min')
    });

    new Chart(document.getElementById('calChart'), {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                data: calorieValues,
                backgroundColor: PG.navy, hoverBackgroundColor: PG.teal,
                borderRadius: 6, borderSkipped: false, maxBarThickness: 28
            }]
        },
        options: axes('kcal')
    });

    new Chart(document.getElementById('typeChart'), {
        type: 'doughnut',
        data: {
            labels: ${typeLabels},
            datasets: [{ data: ${typeValues}, backgroundColor: PG.palette, borderColor: '#fff', borderWidth: 2 }]
        },
        options: {
            maintainAspectRatio: false,
            cutout: '68%',
            plugins: {
                legend: {
                    position: window.matchMedia('(max-width: 560px)').matches ? 'bottom' : 'right',
                    labels: { usePointStyle: true, boxWidth: 8, padding: 14 }
                },
                tooltip: { backgroundColor: PG.navy, padding: 10, cornerRadius: 8 }
            }
        }
    });
})();
</script>
</c:if>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
