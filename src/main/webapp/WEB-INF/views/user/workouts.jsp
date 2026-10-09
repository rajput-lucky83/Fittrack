<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Workout log"/>
<c:set var="pageSub" value="Everything you've logged, newest first."/>
<c:set var="nav" value="workouts"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="split">

    <div class="card">
        <h2><c:out value="${empty editing ? 'Log a workout' : 'Edit workout'}"/></h2>

        <form method="post" action="${pageContext.request.contextPath}/user/workouts">
            <input type="hidden" name="action" value="${empty editing ? 'add' : 'update'}">
            <c:if test="${not empty editing}">
                <input type="hidden" name="id" value="${editing.id}">
            </c:if>

            <div class="field">
                <label for="type">Type</label>
                <select id="type" name="type" required>
                    <c:forEach var="t" items="${types}">
                        <option value="${fn:escapeXml(t)}" ${editing.type == t ? 'selected' : ''}><c:out value="${t}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div class="row">
                <div class="field">
                    <label for="duration">Duration (min)</label>
                    <input type="number" id="duration" name="duration" min="1" max="${maxMinutes}"
                           value="${empty editing ? '' : editing.durationMin}" required>
                </div>
                <div class="field">
                    <label for="intensity">Intensity</label>
                    <select id="intensity" name="intensity">
                        <c:forEach var="i" items="${intensities}">
                            <option value="${i}" ${(empty editing and i == 'MEDIUM') or editing.intensity == i ? 'selected' : ''}>${i.label}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="field">
                <label for="date">Date</label>
                <input type="date" id="date" name="date" max="${today}"
                       value="${empty editing ? today : editing.workoutDate}" required>
            </div>

            <div class="field">
                <label for="notes">Notes <span class="muted">(optional)</span></label>
                <input type="text" id="notes" name="notes" maxlength="255"
                       value="<c:out value='${editing.notes}'/>" placeholder="e.g. 5 km, felt strong">
            </div>

            <div class="actions">
                <button class="btn" type="submit"><c:out value="${empty editing ? 'Save workout' : 'Update workout'}"/></button>
                <c:if test="${not empty editing}">
                    <a class="btn ghost" href="${pageContext.request.contextPath}/user/workouts">Cancel</a>
                </c:if>
            </div>
            <p class="hint" style="margin-top:.7rem;">Calories are estimated from the activity, duration, intensity and your weight.</p>
        </form>
    </div>

    <div class="card">
        <div class="card-head">
            <h2>Your workouts</h2>
            <span class="muted small">${fn:length(workouts)} entries</span>
        </div>
        <div class="table-wrap">
            <table>
                <thead>
                <tr><th>Date</th><th>Type</th><th>Time</th><th>Intensity</th><th>Calories</th><th>Notes</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="w" items="${workouts}">
                    <tr>
                        <td class="nowrap">${w.workoutDate}</td>
                        <td><c:out value="${w.type}"/></td>
                        <td class="nowrap">${w.durationMin} min</td>
                        <td>
                            <span class="badge ${w.intensity == 'HIGH' ? 'red' : (w.intensity == 'MEDIUM' ? 'amber' : 'green')}">${w.intensity.label}</span>
                        </td>
                        <td class="nowrap">${w.calories} kcal</td>
                        <td class="small muted"><c:out value="${w.notes}"/></td>
                        <td class="nowrap right">
                            <a class="btn ghost sm" href="${pageContext.request.contextPath}/user/workouts?edit=${w.id}">Edit</a>
                            <form class="inline" method="post" action="${pageContext.request.contextPath}/user/workouts"
                                  data-confirm="Delete this workout?">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${w.id}">
                                <button class="btn danger sm" type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty workouts}">
                    <tr><td colspan="7" class="empty">No workouts yet. Use the form to log your first one.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
