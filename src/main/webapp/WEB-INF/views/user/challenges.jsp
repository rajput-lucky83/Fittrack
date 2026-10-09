<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Challenges"/>
<c:set var="pageSub" value="Join a challenge. Your logged workouts inside its dates count automatically."/>
<c:set var="nav" value="challenges"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="card">
    <h2>My active challenges</h2>
    <c:choose>
        <c:when test="${empty joined}">
            <p class="muted" style="margin:0;">You haven't joined any challenge that is still open.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="c" items="${joined}">
                <div class="item">
                    <div class="item-top">
                        <div>
                            <strong><c:out value="${c.title}"/></strong>
                            <span class="badge ${c.state == 'Active' ? 'green' : 'blue'}">${c.state}</span>
                        </div>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/user/challenges"
                              data-confirm="Leave this challenge?">
                            <input type="hidden" name="action" value="leave">
                            <input type="hidden" name="challengeId" value="${c.id}">
                            <button class="btn ghost sm" type="submit">Leave</button>
                        </form>
                    </div>
                    <div class="bar clay ${c.percent >= 100 ? 'done' : ''}"><span style="width:${c.percent}%"></span></div>
                    <div class="small muted">
                        ${c.progress} of ${c.targetValue} ${c.unit} &middot; ${c.startDate} to ${c.endDate}
                        <c:if test="${c.completed}"> &middot; <strong>Target reached</strong></c:if>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<h2>Open for joining</h2>
<c:if test="${empty available}">
    <div class="card"><p class="muted" style="margin:0;">There are no open challenges right now. Check back soon.</p></div>
</c:if>

<div class="grid-2">
    <c:forEach var="c" items="${available}">
        <div class="card">
            <div class="item-top">
                <h3><c:out value="${c.title}"/></h3>
                <span class="badge ${c.state == 'Active' ? 'green' : 'blue'}">${c.state}</span>
            </div>
            <p class="muted"><c:out value="${c.description}"/></p>
            <p class="small">
                Goal: <strong>${c.targetValue} ${c.unit}</strong><br>
                ${c.startDate} to ${c.endDate} &middot; ${c.participantCount} joined
            </p>
            <c:choose>
                <c:when test="${c.joinedByCurrentUser}">
                    <span class="badge green">You're in</span>
                </c:when>
                <c:otherwise>
                    <form method="post" action="${pageContext.request.contextPath}/user/challenges">
                        <input type="hidden" name="action" value="join">
                        <input type="hidden" name="challengeId" value="${c.id}">
                        <button class="btn clay sm" type="submit">Join challenge</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </c:forEach>
</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
