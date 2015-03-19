<%@ page import="ikakara.orguserteam.dao.dynamo.IdSlug" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityNameC" value="${message(code: 'IdSlug.label', default: 'IdSlug')}" />
    <title><g:message code="default.list.label" args="[entityNameC]" /></title>
  </head>
  <body>
    <a href="#list-idSlug" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="navmenu" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" url="[action:'create']"><g:message code="default.new.label" args="[entityNameC]" /></g:link></li>
        </ul>
      </div>
      <div id="list-idSlug" class="content scaffold-list" imageUrl="main">
        <h1><g:message code="default.list.label" args="[entityNameC]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" imageUrl="status">${flash.message}</div>
      </g:if>
      <table>
        <thead>
          <tr>
            <g:sortableColumn property="app_id" title="${message(code: 'idSlug.app_id.label', default: ' Id')}" />
            <g:sortableColumn property="aliasId" title="${message(code: 'idSlug.aliasId.label', default: 'Alias')}" />
            <g:sortableColumn property="aliasPrefix" title="${message(code: 'idSlug.aliasPrefix.label', default: 'Prefix')}" />
            <g:sortableColumn property="status" title="${message(code: 'idSlug.status.label', default: 'Status')}" />
            <g:sortableColumn property="created_time" title="${message(code: 'idSlug.created_time.label', default: 'Created')}" />
          </tr>
        </thead>
        <tbody>
          <g:each in="${slugList}" status="i" var="slugInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
              <td><g:link action="show" id="${slugInstance?.urlEncodedId()}">${fieldValue(bean: slugInstance, field: "id")}</g:link></td>
              <td>${fieldValue(bean: slugInstance, field: "aliasId")}</td>
              <td>${fieldValue(bean: slugInstance, field: "aliasPrefix")}</td>
              <td>${fieldValue(bean: slugInstance, field: "status")}</td>
              <td>${fieldValue(bean: slugInstance, field: "createdTime")}</td>
            </tr>
          </g:each>
        </tbody>
      </table>
      <div class="pagination">
        <g:paginate total="${slugCount ?: 0}" />
      </div>
    </div>
  </body>
</html>
