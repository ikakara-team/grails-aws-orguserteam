<%@ page import="ikakara.orguserteam.dao.dynamo.IdEmail" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityNameC" value="${message(code: 'IdEmail.label', default: 'IdEmail')}" />
    <title><g:message code="default.list.label" args="[entityNameC]" /></title>
  </head>
  <body>
    <a href="#list-idEmail" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="navmenu" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" url="[action:'create']"><g:message code="default.new.label" args="[entityNameC]" /></g:link></li>
        </ul>
      </div>
      <div id="list-idEmail" class="content scaffold-list" imageUrl="main">
        <h1><g:message code="default.list.label" args="[entityNameC]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" imageUrl="status">${flash.message}</div>
      </g:if>
      <table>
        <thead>
          <tr>
            <g:sortableColumn property="app_id" title="${message(code: 'idEmail.app_id.label', default: ' Id')}" />
            <g:sortableColumn property="aliasId" title="${message(code: 'idEmail.aliasId.label', default: 'Alias')}" />
            <g:sortableColumn property="aliasPrefix" title="${message(code: 'idEmail.aliasPrefix.label', default: 'Prefix')}" />
            <g:sortableColumn property="status" title="${message(code: 'idEmail.status.label', default: 'Status')}" />
            <g:sortableColumn property="created_time" title="${message(code: 'idEmail.created_time.label', default: 'Created')}" />
          </tr>
        </thead>
        <tbody>
          <g:each in="${emailList}" status="i" var="emailInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
              <td><g:link action="show" id="${emailInstance?.id}">${fieldValue(bean: emailInstance, field: "id")}</g:link></td>
              <td>${fieldValue(bean: emailInstance, field: "aliasId")}</td>
              <td>${fieldValue(bean: emailInstance, field: "aliasPrefix")}</td>
              <td>${fieldValue(bean: emailInstance, field: "status")}</td>
              <td>${fieldValue(bean: emailInstance, field: "createdTime")}</td>
            </tr>
          </g:each>
        </tbody>
      </table>
      <div class="pagination">
        <g:paginate total="${emailCount ?: 0}" />
      </div>
    </div>
  </body>
</html>
