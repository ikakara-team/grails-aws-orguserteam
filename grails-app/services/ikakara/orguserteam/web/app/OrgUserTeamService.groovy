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

import ikakara.orguserteam.dao.dynamo.AIdBase
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdTeam
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdOrgTeam
import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdUserTeam
import ikakara.orguserteam.dao.dynamo.IdEmail
import ikakara.orguserteam.dao.dynamo.IdEmailTeam
import ikakara.orguserteam.dao.dynamo.IdEmailOrg

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
  // should we verify if user exist?
  IdUser user(String userId) {
    def user = new IdUser(id: userId)
    def load = user.load()
    if(!load) {
      // doesn't exist
      log.warn("User Not Found: $userId")
      // return null
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
        newslug = new IdSlug().withSlugId(shortName)
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

    // delete slug
    new IdSlug(id: user.aliasId).delete()

    user.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Org
  /////////////////////////////////////////////////////////////////////////////
  IdOrg org(String orgId) {
    def org = new IdOrg().withId(orgId)
    def load = org.load()
    if(!load) {
      // doesn't exist
      log.error("Org Not Found: $orgId")
      return null
    }

    return org
  }

  IdOrg getOrg(IdTeam team) {
    def list = listOrg(team)
    if(list) {
      return list[0].member // hacky, should only be one org
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

  IdOrg createOrg(IdUser user, String orgName, String orgDescription) {
    // create org
    def org = new IdOrg(description: orgDescription)
    .initId()
    .slugify(orgName)
    .withCreatedUpdated()

    def create = org.create()
    if(!create) {
      // failed
      return null
    }

    // create slug
    def slug = new IdSlug(id: org.aliasId)
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
      def userorg = new IdUserOrg(member_role: IdUserOrg.ROLE_ADMIN)
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
        newslug = new IdSlug().withSlugId(shortName)
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

    // delete slug
    new IdSlug(id: org.aliasId).delete()

    org.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Team
  /////////////////////////////////////////////////////////////////////////////

  IdTeam team(String teamId) {
    def team = new IdTeam().withId(teamId)
    def load = team.load()
    if(!load) {
      // doesn't exist
      log.error("Team Not Found: $teamId")
      return null
    }

    return team
  }

  List<IdTeam> listTeamVisible(IdOrg org, IdUser user) {
    List listTeam = []

    List list = new IdOrgTeam().withMember(org).queryByMemberAndType()
    for(orgobj in list) {
      IdTeam team = orgobj.group

      // check if app is visible to user
      if(!team.orgVisible) {
        // check is member is
        def userteam = team.hasMember(user)
        if(!userteam) {
          continue
        }
      }

      team.load()

      listTeam << team
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

  // this is ridculous ... SQL is so much better at relationships
  List<IdTeam> listOrgTeams(IdUser user, String myOrgName) {
    Map mapApp = [:]
    List listOrg = [new IdOrg(name: myOrgName)]

    // get all the user teams and orgs
    List list = new IdUserTeam().withMember(user).queryByMember()
    // we can optimize this ...
    for(userobj in list) {
      if(userobj instanceof IdUserTeam) {
        IdTeam team = userobj.group
        team.load()
        mapApp[team.id] = team
      } else if(userobj instanceof IdUserOrg) {
        def org = userobj.group
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
      for(IdOrgTeam orgteam in list_orgteam) {
        IdTeam team = orgteam.group

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
      listOrg[0].teamListAdd(team)
    }

    return listOrg
  }

  IdTeam createTeam(IdUser user, String teamName, Integer privacy, String orgId) {
    def org
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
    def team = new IdTeam(privacy: privacy)
    .initId()
    .slugify(teamName)
    .withCreatedUpdated()

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
    def userteam = new IdUserTeam()
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
        newslug = new IdSlug().withSlugId(shortName)
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
      def org
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

    // delete slug
    new IdSlug(id: team.aliasId).delete()

    team.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Email
  /////////////////////////////////////////////////////////////////////////////
  IdEmail email(String emailId) {
    def email = new IdEmail().withId(emailId)
    def load = email.load()
    if(!load) {
      // doesn't exist
      log.error("Email Not Found: $emailId")
      return null
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
    def email = new IdEmail(id: emailId)
    .withCreatedUpdated()

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

  IdEmail updateEmail(IdEmail email, IdUser uesr) {
    def load = email.load()
    if(!load) {
      // not found
      return
    }

    email.withAlias(user)
    email.save()

    return email
  }

  // return true if deleted, returns false if not found
  boolean deleteEmail(IdEmail email) {
    def load = email.load()
    if(!load) {
      // not found
      return false
    }

    email.delete()

    return true
  }

  boolean addUserToOrg(IdUser invitedBy, IdUser user, IdOrg org) {
    def userorg = new IdUserOrg().withInvitedBy(invitedBy).withMember(user).withGroup(org).withCreatedUpdated()
    userorg.save()
  }

  boolean addEmailToOrg(IdUser invitedBy, String invitedName, IdEmail email, IdOrg org) {
    def emailorg = new IdEmailOrg().withInvitedBy(invitedBy).withInvitedName(invitedName).withMember(email).withGroup(org).withCreatedUpdated()
    emailorg.save()
  }

  boolean addUserToTeam(IdUser invitedBy, IdUser user, IdTeam team) {
    def userteam = new IdUserTeam().withInvitedBy(invitedBy).withMember(user).withGroup(team).withCreatedUpdated()
    userteam.save()
  }

  boolean addEmailToTeam(IdUser invitedBy, String invitedName, IdEmail email, IdTeam team) {
    def emailteam = new IdEmailTeam().withInvitedBy(invitedBy).withInvitedName(invitedName).withMember(email).withGroup(team).withCreatedUpdated()
    emailteam.save()
  }

}
