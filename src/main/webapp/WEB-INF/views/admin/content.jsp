<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Fitness content"/>
<c:set var="pageSub" value="Review what members have submitted. Approved posts show up in the guidance library."/>
<c:set var="nav" value="content"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="tabs">
    <a href="?status=ALL"      class="${filter == 'ALL' ? 'active' : ''}">All</a>
    <a href="?status=PENDING"  class="${filter == 'PENDING' ? 'active' : ''}">Pending (${pendingCount})</a>
    <a href="?status=APPROVED" class="${filter == 'APPROVED' ? 'active' : ''}">Approved</a>
    <a href="?status=REJECTED" class="${filter == 'REJECTED' ? 'active' : ''}">Rejected</a>
</div>

<div class="card">
    <div class="table-wrap">
        <table>
            <thead>
            <tr><th>Post</th><th>Category</th><th>Submitted by</th><th>Status</th><th>Decision</th></tr>
            </thead>
            <tbody>
            <c:forEach var="it" items="${items}">
                <tr>
                    <td style="max-width:340px;">
                        <strong><c:out value="${it.title}"/></strong>
                        <details>
                            <summary class="small muted">Read content</summary>
                            <p class="small" style="white-space:pre-line; margin:.4rem 0 0;"><c:out value="${it.body}"/></p>
                        </details>
                    </td>
                    <td><c:out value="${it.category}"/></td>
                    <td class="small"><c:out value="${it.submitterName}"/><br><span class="muted">${it.createdText}</span></td>
                    <td>
                        <span class="badge ${it.status == 'APPROVED' ? 'green' : (it.status == 'REJECTED' ? 'red' : 'amber')}">${it.status}</span>
                        <c:if test="${not empty it.adminNote}"><div class="small muted"><c:out value="${it.adminNote}"/></div></c:if>
                    </td>
                    <td style="min-width:260px;">
                        <div class="actions">
                            <c:if test="${it.status != 'APPROVED'}">
                                <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/content">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="id" value="${it.id}">
                                    <input type="hidden" name="filter" value="${filter}">
                                    <button class="btn sm" type="submit">Approve</button>
                                </form>
                            </c:if>
                            <c:if test="${it.status != 'REJECTED'}">
                                <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/content"
                                      style="display:flex; gap:.3rem;">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="id" value="${it.id}">
                                    <input type="hidden" name="filter" value="${filter}">
                                    <input type="text" name="note" placeholder="Reason (optional)" maxlength="255" style="width:130px; padding:.25rem .4rem;">
                                    <button class="btn danger sm" type="submit">Reject</button>
                                </form>
                            </c:if>
                            <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/content"
                                  data-confirm="Delete this post permanently?">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${it.id}">
                                <input type="hidden" name="filter" value="${filter}">
                                <button class="btn ghost sm" type="submit">Delete</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty items}">
                <tr><td colspan="5" class="empty">No posts in this view.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
