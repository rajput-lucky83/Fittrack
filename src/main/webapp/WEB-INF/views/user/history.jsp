<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Challenge history"/>
<c:set var="pageSub" value="Challenges you took part in that have finished."/>
<c:set var="nav" value="history"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="stats">
    <div class="stat"><div class="num">${fn:length(past)}</div><div class="lbl">Challenges finished</div></div>
    <div class="stat accent"><div class="num">${completedCount}</div><div class="lbl">Targets reached</div></div>
</div>

<div class="card">
    <div class="table-wrap">
        <table>
            <thead>
            <tr><th>Challenge</th><th>Period</th><th>Target</th><th>Your result</th><th>Outcome</th></tr>
            </thead>
            <tbody>
            <c:forEach var="c" items="${past}">
                <tr>
                    <td><strong><c:out value="${c.title}"/></strong></td>
                    <td class="small nowrap">${c.startDate} to ${c.endDate}</td>
                    <td>${c.targetValue} ${c.unit}</td>
                    <td style="min-width:150px;">
                        <div class="bar ${c.completed ? 'done' : 'clay'}"><span style="width:${c.percent}%"></span></div>
                        <span class="small muted">${c.progress} ${c.unit} (${c.percent}%)</span>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${c.completed}"><span class="badge green">Completed</span></c:when>
                            <c:otherwise><span class="badge grey">Not reached</span></c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty past}">
                <tr><td colspan="5" class="empty">Nothing here yet. Finished challenges will show up in this list.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
