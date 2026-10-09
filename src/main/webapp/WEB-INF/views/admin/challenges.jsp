<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Challenges"/>
<c:set var="pageSub" value="Create fitness challenges and see how many members joined."/>
<c:set var="nav" value="challenges"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="split">

    <div class="card">
        <h2>New challenge</h2>
        <form method="post" action="${pageContext.request.contextPath}/admin/challenges">
            <input type="hidden" name="action" value="create">

            <div class="field">
                <label for="title">Title</label>
                <input type="text" id="title" name="title" maxlength="100" placeholder="e.g. October 500 minutes" required>
            </div>
            <div class="field">
                <label for="description">Description</label>
                <textarea id="description" name="description" maxlength="400" style="min-height:70px;"></textarea>
            </div>
            <div class="row">
                <div class="field">
                    <label for="metric">Counts</label>
                    <select id="metric" name="metric">
                        <option value="MINUTES">Total minutes</option>
                        <option value="WORKOUTS">Number of workouts</option>
                    </select>
                </div>
                <div class="field">
                    <label for="target">Target</label>
                    <input type="number" id="target" name="target" min="1" required>
                </div>
            </div>
            <div class="row">
                <div class="field">
                    <label for="startDate">Starts</label>
                    <input type="date" id="startDate" name="startDate" value="${today}" required>
                </div>
                <div class="field">
                    <label for="endDate">Ends</label>
                    <input type="date" id="endDate" name="endDate" required>
                </div>
            </div>
            <button class="btn" type="submit">Create challenge</button>
        </form>
    </div>

    <div class="card">
        <h2>All challenges</h2>
        <div class="table-wrap">
            <table>
                <thead>
                <tr><th>Challenge</th><th>Target</th><th>Dates</th><th>Status</th><th>Joined</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="ch" items="${challenges}">
                    <tr>
                        <td><strong><c:out value="${ch.title}"/></strong></td>
                        <td class="nowrap">${ch.targetValue} ${ch.unit}</td>
                        <td class="small nowrap">${ch.startDate}<br>${ch.endDate}</td>
                        <td><span class="badge ${ch.state == 'Active' ? 'green' : (ch.state == 'Upcoming' ? 'blue' : 'grey')}">${ch.state}</span></td>
                        <td>${ch.participantCount}</td>
                        <td class="right">
                            <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/challenges"
                                  data-confirm="Delete this challenge? Participants will lose it from their history.">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${ch.id}">
                                <button class="btn danger sm" type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty challenges}">
                    <tr><td colspan="6" class="empty">No challenges yet. Create the first one.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
