<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Admin overview"/>
<c:set var="pageSub" value="Engagement, workouts and challenge activity across the platform."/>
<c:set var="nav" value="dashboard"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="stats">
    <div class="stat"><div class="num">${members}</div><div class="lbl">Members (${activeMembers} active)</div></div>
    <div class="stat"><div class="num">${activeThisWeek}</div><div class="lbl">Trained this week</div></div>
    <div class="stat"><div class="num">${totalWorkouts}</div><div class="lbl">Workouts logged</div></div>
    <div class="stat"><div class="num">${totalMinutes}</div><div class="lbl">Minutes trained</div></div>
    <div class="stat accent"><div class="num">${runningChallenges}</div><div class="lbl">Challenges running</div></div>
    <div class="stat accent">
        <div class="num">${pendingContent}</div>
        <div class="lbl"><a href="${pageContext.request.contextPath}/admin/content?status=PENDING">Posts to review</a></div>
    </div>
</div>

<div class="grid-2">
    <div class="card">
        <h2>Workouts logged per day</h2>
        <div class="chart-box"><canvas id="dayChart"></canvas></div>
    </div>
    <div class="card">
        <h2>Most popular activities</h2>
        <div class="chart-box"><canvas id="typeChart"></canvas></div>
    </div>
</div>

<div class="grid-2">
    <div class="card">
        <h2>Challenge participation</h2>
        <c:choose>
            <c:when test="${empty participation}"><p class="muted" style="margin:0;">No challenges yet.</p></c:when>
            <c:otherwise><div class="chart-box short"><canvas id="challengeChart"></canvas></div></c:otherwise>
        </c:choose>
    </div>
    <div class="card">
        <h2>Most active members (30 days)</h2>
        <table>
            <thead><tr><th>Member</th><th class="right">Minutes</th></tr></thead>
            <tbody>
            <c:forEach var="m" items="${topMembers}">
                <tr><td><c:out value="${m.key}"/></td><td class="right">${m.value}</td></tr>
            </c:forEach>
            <c:if test="${empty topMembers}"><tr><td colspan="2" class="empty">No workouts logged in the last 30 days.</td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>

<div class="card">
    <div class="card-head">
        <h2><span class="live-dot"></span>System activity</h2>
        <span class="muted small">refreshes every 5 seconds</span>
    </div>
    <ul class="feed" id="feed">
        <c:forEach var="a" items="${activity}">
            <li>
                <span class="t">${a.timeText}</span>
                <span class="act">${a.action}</span>
                <strong><c:out value="${a.actorName}"/></strong>
                <span class="muted"><c:out value="${a.details}"/></span>
            </li>
        </c:forEach>
        <c:if test="${empty activity}"><li class="muted">Nothing has happened yet.</li></c:if>
    </ul>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
    if (window.Chart) {
        new Chart(document.getElementById('dayChart'), {
            type: 'bar',
            data: { labels: ${dayLabels}, datasets: [{ data: ${dayValues}, backgroundColor: FT.forest, borderRadius: 3 }] },
            options: {
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true, ticks: { precision: 0 } }, x: { grid: { display: false } } }
            }
        });

        new Chart(document.getElementById('typeChart'), {
            type: 'doughnut',
            data: { labels: ${typeLabels}, datasets: [{ data: ${typeValues}, backgroundColor: FT.palette }] },
            options: { maintainAspectRatio: false, plugins: { legend: { position: 'right' } } }
        });

        var cc = document.getElementById('challengeChart');
        if (cc) {
            new Chart(cc, {
                type: 'bar',
                data: { labels: ${challengeLabels}, datasets: [{ data: ${challengeValues}, backgroundColor: FT.clay, borderRadius: 3 }] },
                options: {
                    indexAxis: 'y',
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: { x: { beginAtZero: true, ticks: { precision: 0 } }, y: { grid: { display: false } } }
                }
            });
        }
    }

    // live activity feed: ask the server for the latest entries every 5 seconds
    (function () {
        var feed = document.getElementById('feed');
        var url = '${pageContext.request.contextPath}/admin/activity';

        function line(entry) {
            var li = document.createElement('li');

            var t = document.createElement('span');
            t.className = 't';
            t.textContent = entry.time;

            var act = document.createElement('span');
            act.className = 'act';
            act.textContent = entry.action + ' ';

            var who = document.createElement('strong');
            who.textContent = entry.actor + ' ';

            var det = document.createElement('span');
            det.className = 'muted';
            det.textContent = entry.details || '';

            li.appendChild(t);
            li.appendChild(act);
            li.appendChild(who);
            li.appendChild(det);
            return li;
        }

        function refresh() {
            fetch(url, { credentials: 'same-origin', cache: 'no-store' })
                .then(function (r) { return r.ok ? r.json() : []; })
                .then(function (list) {
                    if (!list.length) { return; }
                    feed.innerHTML = '';
                    list.forEach(function (entry) { feed.appendChild(line(entry)); });
                })
                .catch(function () { /* network hiccup, try again next time */ });
        }

        setInterval(refresh, 5000);
    })();
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
