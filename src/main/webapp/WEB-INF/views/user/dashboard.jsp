<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Dashboard"/>
<c:set var="pageSub" value="Welcome back, ${sessionScope.user.name}. Here is where you stand today."/>
<c:set var="nav" value="dashboard"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="stats">
    <div class="stat"><div class="num">${totals.workouts}</div><div class="lbl">Workouts logged</div></div>
    <div class="stat"><div class="num">${totals.minutes}</div><div class="lbl">Minutes trained</div></div>
    <div class="stat"><div class="num">${totals.calories}</div><div class="lbl">Calories burned (est.)</div></div>
    <div class="stat accent"><div class="num">${streak}</div><div class="lbl">Day streak</div></div>
</div>

<div class="grid-2">
    <div class="card">
        <div class="card-head">
            <h2>Last 7 days</h2>
            <span class="muted small">minutes per day</span>
        </div>
        <div class="chart-box short"><canvas id="weekChart"></canvas></div>
    </div>

    <div class="card">
        <div class="card-head">
            <h2>Weekly goals</h2>
            <a class="small" href="${pageContext.request.contextPath}/user/progress">Manage</a>
        </div>
        <c:choose>
            <c:when test="${empty goals}">
                <p class="muted">You haven't set any goals yet. Pick a weekly target on the
                    <a href="${pageContext.request.contextPath}/user/progress">progress page</a>.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="g" items="${goals}">
                    <div class="item">
                        <div class="item-top">
                            <strong><c:out value="${g.title}"/></strong>
                            <span class="small muted">${g.currentValue} / ${g.targetValue} ${g.unit}</span>
                        </div>
                        <div class="bar ${g.percent >= 100 ? 'done' : ''}"><span style="width:${g.percent}%"></span></div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="grid-2">
    <div class="card">
        <h2>Suggestions for you</h2>
        <ul class="tips">
            <c:forEach var="tip" items="${tips}">
                <li><c:out value="${tip}"/></li>
            </c:forEach>
        </ul>
    </div>

    <div class="card">
        <div class="card-head">
            <h2>Challenges in progress</h2>
            <a class="small" href="${pageContext.request.contextPath}/user/challenges">Browse all</a>
        </div>
        <c:choose>
            <c:when test="${empty runningChallenges}">
                <p class="muted">You haven't joined a running challenge. Have a look at what's open.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="ch" items="${runningChallenges}">
                    <div class="item">
                        <div class="item-top">
                            <strong><c:out value="${ch.title}"/></strong>
                            <span class="small muted">ends ${ch.endDate}</span>
                        </div>
                        <div class="bar clay ${ch.percent >= 100 ? 'done' : ''}"><span style="width:${ch.percent}%"></span></div>
                        <div class="small muted">${ch.progress} of ${ch.targetValue} ${ch.unit}</div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="card">
    <div class="card-head">
        <h2>Recent workouts</h2>
        <a class="btn sm" href="${pageContext.request.contextPath}/user/workouts">Log a workout</a>
    </div>
    <div class="table-wrap">
        <table>
            <thead><tr><th>Date</th><th>Type</th><th>Duration</th><th>Intensity</th><th>Calories</th></tr></thead>
            <tbody>
            <c:forEach var="w" items="${recent}">
                <tr>
                    <td>${w.workoutDate}</td>
                    <td><c:out value="${w.type}"/></td>
                    <td>${w.durationMin} min</td>
                    <td>${w.intensity.label}</td>
                    <td>${w.calories} kcal</td>
                </tr>
            </c:forEach>
            <c:if test="${empty recent}">
                <tr><td colspan="5" class="empty">Nothing in the last two weeks.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
    if (window.Chart) {
        new Chart(document.getElementById('weekChart'), {
            type: 'bar',
            data: {
                labels: ${weekLabels},
                datasets: [{ data: ${weekValues}, backgroundColor: FT.forest, borderRadius: 3 }]
            },
            options: {
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true, ticks: { precision: 0 } }, x: { grid: { display: false } } }
            }
        });
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
