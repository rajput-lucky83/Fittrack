<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="User management"/>
<c:set var="pageSub" value="Create accounts, change roles, deactivate or remove people."/>
<c:set var="nav" value="users"/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>

<div class="split">

    <div class="card">
        <c:choose>
            <c:when test="${not empty editing}">
                <h2>Edit user #${editing.id}</h2>
                <form method="post" action="${pageContext.request.contextPath}/admin/users">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${editing.id}">

                    <div class="field">
                        <label for="name">Name</label>
                        <input type="text" id="name" name="name" maxlength="80" value="<c:out value='${editing.name}'/>" required>
                    </div>
                    <div class="field">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" value="<c:out value='${editing.email}'/>" required>
                    </div>
                    <div class="field">
                        <label for="role">Role</label>
                        <select id="role" name="role">
                            <c:forEach var="r" items="${roles}">
                                <option value="${r}" ${editing.role == r ? 'selected' : ''}>${r}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="field">
                        <label for="password">New password <span class="muted">(leave empty to keep)</span></label>
                        <input type="password" id="password" name="password" minlength="6" autocomplete="new-password">
                    </div>
                    <div class="actions">
                        <button class="btn" type="submit">Save changes</button>
                        <a class="btn ghost" href="${pageContext.request.contextPath}/admin/users">Cancel</a>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <h2>Add a user</h2>
                <form method="post" action="${pageContext.request.contextPath}/admin/users">
                    <input type="hidden" name="action" value="create">

                    <div class="field">
                        <label for="name">Name</label>
                        <input type="text" id="name" name="name" maxlength="80" required>
                    </div>
                    <div class="field">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required>
                    </div>
                    <div class="field">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" minlength="6" required autocomplete="new-password">
                    </div>
                    <div class="field">
                        <label for="role">Role</label>
                        <select id="role" name="role">
                            <c:forEach var="r" items="${roles}">
                                <option value="${r}" ${r == 'USER' ? 'selected' : ''}>${r}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button class="btn" type="submit">Create account</button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="card">
        <div class="card-head">
            <h2>All accounts</h2>
            <span class="muted small">${fn:length(users)} total</span>
        </div>
        <div class="table-wrap">
            <table>
                <thead>
                <tr><th>#</th><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Joined</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td class="muted">${u.id}</td>
                        <td><c:out value="${u.name}"/></td>
                        <td class="small"><c:out value="${u.email}"/></td>
                        <td><span class="badge ${u.admin ? 'blue' : 'grey'}">${u.role}</span></td>
                        <td><span class="badge ${u.active ? 'green' : 'red'}">${u.active ? 'Active' : 'Disabled'}</span></td>
                        <td class="small nowrap">${u.joinedOn}</td>
                        <td class="nowrap right">
                            <a class="btn ghost sm" href="${pageContext.request.contextPath}/admin/users?edit=${u.id}">Edit</a>
                            <c:if test="${u.id != sessionScope.user.id}">
                                <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/users">
                                    <input type="hidden" name="action" value="toggle">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button class="btn ghost sm" type="submit">${u.active ? 'Deactivate' : 'Activate'}</button>
                                </form>
                                <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/users"
                                      data-confirm="Delete this user and all of their workouts, goals and posts? This cannot be undone.">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button class="btn danger sm" type="submit">Delete</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
