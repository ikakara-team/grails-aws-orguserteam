/* Copyright 2014-2015 Allen Arakaki.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ikakara.orguserteam.web.app

import groovy.transform.CompileStatic

import ikakara.orguserteam.dao.dynamo.AIdBase
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdTeam
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdOrgTeam
import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdUserTeam
import ikakara.orguserteam.dao.dynamo.IdEmail
import ikakara.orguserteam.dao.dynamo.AIdEmailGroup
import ikakara.orguserteam.dao.dynamo.IdEmailTeam
import ikakara.orguserteam.dao.dynamo.IdEmailOrg

@CompileStatic
class OrgUserTeamService {
  static transactional = false

  def findIdObjBySlugId(String slugId) {
    def list = new IdSlug().queryByAlias(slugId)
    if(list?.size() == 1) {
      return list[0]
    }

    log.error("findIdObjBySlugId - invalid result for $slugId")
  }

  boolean exist(AIdBase id) {
    return id?.load()
  }

  /////////////////////////////////////////////////////////////////////////////
  // User
  /////////////////////////////////////////////////////////////////////////////
  IdUser user(String userId, instance=true) {
    def user = new IdUser(id: userId)
    def load = user.load()
    if(!load) {
      // doesn't exist
      log.warn("User Not Found: $userId")
      if(!instance) {
        return null
      }
    }

    return user
  }

  List<IdUserOrg> listUser(IdOrg org) {
    List list = new IdUserOrg().withGroup(org).queryByGroupAndType()
    return list
  }

  List<IdUserTeam> listUser(IdTeam team) {
    List list = new IdUserTeam().withGroup(team).queryByGroupAndType()
    return list
  }

  IdUser createUser(IdUser user, String name, String initials, String desc, String shortName) {
    // create org
    user.slugify(shortName)
    .withCreatedUpdated()

    user.name = name
    user.initials = initials
    user.description = desc

    def create = user.create()
    if(!create) {
      // failed
      return null
    }

    // create slug
    def slug = new IdSlug(id: user.aliasId)
    .withAlias(user)
    .withCreatedUpdated()

    create = slug.create()
    if(!create) {
      // failed
      user.delete()
      return null
    }

    return user
  }

  IdUser updateUser(IdUser user, String name, String initials, String desc, String shortName) {
    def load = user.load()
    if(!load) {
      // not found, create a user
      user = createUser(user, name, initials, desc, shortName)
      return user
    }

    def oldslug
    def newslug

    if(shortName) {
      if(user.aliasId != shortName) {
        // create new slug
        newslug = (IdSlug)new IdSlug().withSlugId(shortName)
        .withAlias(user)
        .withCreatedUpdated()

        def create = newslug.create()
        if(!create) {
          // failed
          return null
        }

        oldslug = user.alias
        user.withAlias(newslug)
      }
    }

    if(name) {
      user.name  = name
    }

    user.description = desc
    user.initials = initials

    user.updatedDate = new Date()
    def save = user.save()
    if(save) {
      // cleanup old slug
      if(oldslug) {
        oldslug.delete()
      }
    } else {
      // cleanup new slug
      if(newslug) {
        newslug.delete()
      }
    }

    return user
  }

  // return true if deleted, returns false if not found
  boolean deleteUser(IdUser user) {
    def load = user.load()
    if(!load) {
      // not found
      return false
    }

    // delete all org references
    def list = listOrg(user)
    list.each{
      it.delete() // delete only connection
    }

    // delete all team references
    list = listTeam(user)
    list.each{
      it.delete() // delete only connection
    }

    // delete slug
    new IdSlug(id: user.aliasId).delete()
    // delete user
    user.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Org
  /////////////////////////////////////////////////////////////////////////////
  IdOrg org(String orgId, instance=true) {
    IdOrg org = (IdOrg)new IdOrg().withId(orgId)
    def load = org.load()
    if(!load) {
      // doesn't exist
      log.warn("Org Not Found: $orgId")
      if(!instance) {
        return null
      }
    }

    return org
  }

  IdOrg getOrg(IdTeam team) {
    List<IdOrgTeam> list = listOrg(team)
    if(list) {
      return (IdOrg)list[0].member // hacky, should only be one org
    }
  }

  // NOSQL compromise for 1 to many, using many to many table
  List<IdOrgTeam> listOrg(IdTeam team) {
    List list = new IdOrgTeam().withGroup(team).queryByGroupAndType()
    return list
  }

  List<IdUserOrg> listOrg(IdUser user) {
    List list = new IdUserOrg().withMember(user).queryByMemberAndType()
    return list
  }

  List<IdEmailOrg> listOrg(IdEmail email) {
    List list = new IdEmailOrg().withMember(email).queryByMemberAndType()
    return list
  }

  IdOrg createOrg(IdUser user, String orgName, String orgDescription) {
    // create org
    IdOrg org = (IdOrg)new IdOrg(description: orgDescription)
    .initId()
    .slugify(orgName)
    .withCreatedUpdated()

    def create = org.create()
    if(!create) {
      // failed
      return null
    }

    // create slug
    def slug = (IdSlug)new IdSlug(id: org.aliasId)
    .withAlias(org)
    .withCreatedUpdated()

    create = slug.create()
    if(!create) {
      // failed
      org.delete()
      return null
    }

    if(user) {
      // add user to org
      IdUserOrg userorg = (IdUserOrg)new IdUserOrg()
      .withMemberRoles(IdUserOrg.MEMBERROLE_OWNER)
      .withMember(user)
      .withGroup(org)
      .withCreatedUpdated()

      create = userorg.create()
      if(!create) {
        // failed
        org.delete()
        slug.delete()
        return null
      }
    }

    return org
  }

  String updateOrg(IdOrg org, String name, String desc, String web_url, String shortName) {
    def load = org.load()
    if(!load) {
      // not found
      return
    }

    def oldslug
    def newslug

    if(shortName) {
      if(org.aliasId != shortName) {
        // create new slug
        newslug = (IdSlug)new IdSlug().withSlugId(shortName)
        .withAlias(org)
        .withCreatedUpdated()

        def create = newslug.create()
        if(!create) {
          // failed
          return null
        }

        oldslug = org.alias
        org.withAlias(newslug)
      }
    }

    if(name) {
      org.name  = name
    }

    org.description = desc
    org.webUrl = web_url

    org.updatedDate = new Date()
    def save = org.save()
    if(save) {
      // cleanup old slug
      if(oldslug) {
        oldslug.delete()
      }
    } else {
      // cleanup new slug
      if(newslug) {
        newslug.delete()
      }
    }

    return org.aliasId
  }

  // return true if deleted, returns false if not found
  boolean deleteOrg(IdOrg org) {
    def load = org.load()
    if(!load) {
      // not found
      return false
    }

    // delete all teams and references
    def list = listTeam(org)
    list.each{
      IdTeam team = (IdTeam)it.group
      deleteTeam(team) // deletes both team and team-connections
    }

    // delete all user references
    list = listUser(org)
    list.each{
      it.delete() // delete only connection
    }

    // delete all email references
    list = listEmail(org)
    list.each{
      it.delete() // delete only connection
    }

    // delete slug
    new IdSlug(id: org.aliasId).delete()
    // delete org
    org.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Team
  /////////////////////////////////////////////////////////////////////////////

  IdTeam team(String teamId, instance=true) {
    IdTeam team = (IdTeam)new IdTeam().withId(teamId)
    def load = team.load()
    if(!load) {
      // doesn't exist
      log.warn("Team Not Found: $teamId")
      if(!instance) {
        return null
      }
    }

    return team
  }

  boolean haveOrgRole(IdOrg org, IdUser user, Set orgRoles) {
    boolean rolevisibility = false

    if(orgRoles) {
      // allow orgusers to have visibility based on roles
      def orguser = org?.hasMember(user)
      if(orguser?.memberRoles) {
        def res = orgRoles.intersect(orguser.memberRoles)
        if(res.size() > 0) {
          rolevisibility = true
        }
      }
    }

    return rolevisibility
  }

  boolean isTeamVisible(IdTeam team, IdUser user) {
    // check if app is visible to user
    if(!team.orgVisible) {
      // check is member is
      def userteam = team.hasMember(user)
      if(!userteam) {
        return false
      }
    }

    return true;
  }

  List<IdOrgTeam> listTeamVisible(IdOrg org, IdUser user, Set orgRoles=null) {
    List listTeam = []

    List list = new IdOrgTeam().withMember(org).queryByMemberAndType()

    if(haveOrgRole(org, user, orgRoles)) {
      listTeam = list
    } else {
      for(orgobj in list) {
        IdTeam team = (IdTeam)orgobj.group
        if(isTeamVisible(team, user)) {
          listTeam << orgobj
        }
      }
    }

    return listTeam
  }

  List<IdOrgTeam> listTeam(IdOrg org) {
    List list = new IdOrgTeam().withMember(org).queryByMemberAndType()
    return list
  }

  List<IdUserTeam> listTeam(IdUser user) {
    List list = new IdUserTeam().withMember(user).queryByMemberAndType()
    return list
  }

  List<IdEmailTeam> listTeam(IdEmail email) {
    List list = new IdEmailTeam().withMember(email).queryByMemberAndType()
    return list
  }

  // this is ridculous ... SQL is so much better at relationships
  List<IdOrg> listOrgTeams(IdUser user, String myOrgName) {
    Map mapApp = [:]
    List<IdOrg> listOrg = [new IdOrg(name: myOrgName)]

    // get all the user teams and orgs
    List list = new IdUserTeam().withMember(user).queryByMember()
    // we can optimize this ...
    for(userobj in list) {
      if(userobj instanceof IdUserTeam) {
        IdTeam team = (IdTeam)userobj.group
        team.load()
        mapApp[team.id] = team
      } else if(userobj instanceof IdUserOrg) {
        IdOrg org = (IdOrg)userobj.group
        org.load()
        listOrg << org
      } else {
        // unknown class
      }
    }

    int size = listOrg.size()
    for(int i = 1; i < size; i++) {
      def org = listOrg[i]

      // get all the teams of the orgs that the user belongs to
      // query by member and privacy
      List list_orgteam = new IdOrgTeam().withMember(org).queryByMemberAndType()
      for(orgteam in list_orgteam) {
        IdTeam team = (IdTeam)orgteam.group

        if(mapApp.containsKey(team.id)) {
          team = mapApp.remove(team.id)
          org.teamListAdd(team)
        } else {
          team.load()
          if(team.isOrgVisible()) {
            org.teamListAdd(team)
          }
        }
      }
    }

    // add the remaining teams to 'my apps'
    for(team in mapApp.values()){
      listOrg[0].teamListAdd((IdTeam)team)
    }

    return listOrg
  }

  IdTeam createTeam(IdUser user, String teamName, Integer privacy, String orgId) {
    IdOrg org
    if(orgId) {
      // check if org exist
      org = new IdOrg(id: orgId)
      def load = org.load()
      if(!load) {
        // failed
        return null
      }
    }

    // create team
    IdTeam team = (IdTeam)new IdTeam()
    .initId()
    .slugify(teamName)
    .withCreatedUpdated()

    team.privacy = privacy

    def create = team.create()
    if(!create) {
      // failed
      return null
    }

    // create slug
    def slug = new IdSlug(id: team.aliasId)
    .withAlias(team)
    .withCreatedUpdated()

    create = slug.create()
    if(!create) {
      // failed
      team.delete()
      return null
    }

    // add user to team
    IdUserTeam userteam = (IdUserTeam)new IdUserTeam()
    .withMemberRoles(IdUserTeam.MEMBERROLE_ADMIN)
    .withMember(user)
    .withGroup(team)
    .withCreatedUpdated()

    create = userteam.create()
    if(!create) {
      // failed
      team.delete()
      slug.delete()
      return null
    }

    if(org) {
      // add org to team
      def orgteam = new IdOrgTeam()
      .withMember(org)
      .withGroup(team)
      .withCreatedUpdated()

      create = orgteam.create()
      if(!create) {
        // failed
        userteam.delete()
        team.delete()
        slug.delete()
        return null
      }
    }

    return team
  }

  //user, params.name, params.int('privacy'), params.org, params.aliasId
  String updateTeam(IdTeam team, String name, Integer privacy, String description, String shortName) {
    //def load = team.load()
    //if(!load) {
    // not found
    // return
    //}

    def oldslug
    def newslug

    if(shortName) {
      if(team.aliasId != shortName) {
        // create new slug
        newslug = (IdSlug)new IdSlug().withSlugId(shortName)
        .withAlias(team)
        .withCreatedUpdated()

        def create = newslug.create()
        if(!create) {
          // failed
          return null
        }

        oldslug = team.alias
        team.withAlias(newslug)
      }
    }

    if(name) {
      team.name  = name
    }

    team.privacy = privacy
    team.description = description

    team.updatedDate = new Date()
    def save = team.save()
    if(save) {
      // cleanup old slug
      if(oldslug) {
        oldslug.delete()
      }
    } else {
      // cleanup new slug
      if(newslug) {
        newslug.delete()
      }
    }

    return team.aliasId
  }

  //user, params.name, params.int('privacy'), params.org, params.aliasId
  boolean updateTeamOwner(IdTeam team, String orgId) {
    //def load = team.load()
    //if(!load) {
    // not found
    // return
    //}

    def curOrg = getOrg(team)

    if((orgId || curOrg) && (curOrg?.id != orgId)) {
      IdOrg org
      if(orgId) {
        // check if org exist
        org = new IdOrg(id: orgId)
        def load = org.load()
        if(!load) {
          // failed
          return false
        }
      }

      if(curOrg) {
        // delete org
        def curorgteam = new IdOrgTeam()
        .withMember(curOrg)
        .withGroup(team)
        def del = curorgteam.delete()
        if(!del) {
          // failed
          return false
        }
      }

      if(org) {
        // add org to team
        def orgteam = new IdOrgTeam()
        .withMember(org)
        .withGroup(team)
        .withCreatedUpdated()

        def create = orgteam.create()
        if(!create) {
          // failed
          return false
        }
      }
    }

    return true
  }

  // return true if deleted, returns false if not found
  boolean deleteTeam(IdTeam team) {
    def load = team.load()
    if(!load) {
      // not found
      return false
    }

    // delete all org references
    def list = listOrg(team)
    list.each{
      it.delete() // delete only connection
    }

    // delete all user references
    list = listUser(team)
    list.each{
      it.delete() // delete only connection
    }

    // delete all email references
    list = listEmail(team)
    list.each{
      it.delete() // delete only connection
    }

    // delete slug
    new IdSlug(id: team.aliasId).delete()
    // delete team
    team.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Email
  /////////////////////////////////////////////////////////////////////////////
  IdEmail email(String emailId, instance=true) {
    IdEmail email = (IdEmail)new IdEmail().withId(emailId)
    def load = email.load()
    if(!load) {
      // doesn't exist
      log.warn("Email Not Found: $emailId")
      if(!instance) {
        return null
      }
    }

    return email
  }

  List<IdEmailOrg> listEmail(IdOrg org) {
    List list = new IdEmailOrg().withGroup(org).queryByGroupAndType()
    return list
  }

  List<IdEmailTeam> listEmail(IdTeam team) {
    List list = new IdEmailTeam().withGroup(team).queryByGroupAndType()
    return list
  }

  IdEmail createEmail(String emailId, IdUser user=null) {
    // create org
    IdEmail email = (IdEmail)new IdEmail(id: emailId).withCreatedUpdated()

    if(user) {
      email.withAlias(user)
    }

    def create = email.create()
    if(!create) {
      // failed
      return null
    }

    return email
  }

  IdEmail updateEmail(IdEmail email, IdUser user) {
    def load = email.load()
    if(!load) {
      // not found
      return
    }

    email.withAlias(user).withUpdated().save()

    return email
  }

  // return true if deleted, returns false if not found
  boolean deleteEmail(IdEmail email) {
    def load = email.load()
    if(!load) {
      // not found
      return false
    }

    // delete all org references
    def list = listOrg(email)
    list.each{
      it.delete() // delete only connection
    }

    // delete all team references
    list = listTeam(email)
    list.each{
      it.delete() // delete only connection
    }

    // delete email
    email.delete()

    return true
  }

  List<AIdEmailGroup> listGroup(IdEmail email) {
    List list = new IdEmailOrg().withMember(email).queryByMember()
    return list
  }

  boolean addUserToOrg(IdUser invitedBy, IdUser user, IdOrg org, String... roles) {
    IdUserOrg userorg = (IdUserOrg)new IdUserOrg().withMember(user).withGroup(org)
    if(userorg.load()) {
      userorg.withUpdated()
    } else {
      userorg.withCreatedUpdated()
    }

    userorg.withInvitedBy(invitedBy).withMemberRoles(roles).save()
  }

  boolean addEmailToOrg(IdUser invitedBy, String invitedName, IdEmail email, IdOrg org) {
    IdEmailOrg emailorg = (IdEmailOrg)new IdEmailOrg().withMember(email).withGroup(org)
    if(emailorg.load()) {
      emailorg.withUpdated()
    } else {
      emailorg.withCreatedUpdated()
    }
    emailorg.withInvitedName(invitedName).withInvitedBy(invitedBy).save()
  }

  boolean addUserToTeam(IdUser invitedBy, IdUser user, IdTeam team, String... roles) {
    IdUserTeam userteam = (IdUserTeam)new IdUserTeam().withMember(user).withGroup(team)
    if(userteam.load()) {
      userteam.withUpdated()
    } else {
      userteam.withCreatedUpdated()
    }
    userteam.withInvitedBy(invitedBy).withMemberRoles(roles).save()
  }

  boolean addEmailToTeam(IdUser invitedBy, String invitedName, IdEmail email, IdTeam team) {
    IdEmailTeam emailteam = (IdEmailTeam)new IdEmailTeam().withMember(email).withGroup(team)
    if(emailteam.load()) {
      emailteam.withUpdated()
    } else {
      emailteam.withCreatedUpdated()
    }
    emailteam.withInvitedName(invitedName).withInvitedBy(invitedBy).save()
  }

}
