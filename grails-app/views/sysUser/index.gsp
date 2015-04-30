<%@ page import="ikakara.orguserteam.dao.dynamo.IdUser" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityNameC" value="${message(code: 'IdUser.label', default: 'IdUser')}" />
    <title><g:message code="default.list.label" args="[entityNameC]" /></title>
  </head>
  <body>
    <a href="#list-idUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" url="[action:'create']"><g:message code="default.new.label" args="[entityNameC]" /></g:link></li>
        </ul>
      </div>
      <div id="list-idUser" class="content scaffold-list" imageUrl="main">
        <h1><g:message code="default.list.label" args="[entityNameC]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" imageUrl="status">${flash.message}</div>
      </g:if>
      <table>
        <thead>
          <tr>
            <g:sortableColumn property="app_id" title="${message(code: 'idUser.app_id.label', default: ' Id')}" />
            <g:sortableColumn property="aliasId" title="${message(code: 'idUser.aliasId.label', default: 'Alias')}" />
            <g:sortableColumn property="aliasPrefix" title="${message(code: 'idUser.name.label', default: 'Prefix')}" />
            <g:sortableColumn property="created_time" title="${message(code: 'idUser.created_time.label', default: 'Created')}" />
          </tr>
        </thead>
        <tbody>
          <g:each in="${userList}" status="i" var="userInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
              <td><g:link action="show" id="${userInstance?.id?.encodeAsHTML()}">${fieldValue(bean: userInstance, field: "id")}</g:link></td>
              <td>${fieldValue(bean: userInstance, field: "aliasId")}</td>
              <td>${fieldValue(bean: userInstance, field: "aliasPrefix")}</td>
              <td>${fieldValue(bean: userInstance, field: "createdTime")}</td>
            </tr>
          </g:each>
        </tbody>
      </table>
      <div class="pagination">
        <g:paginate total="${userCount ?: 0}" />
      </div>
    </div>
  </body>
</html>
