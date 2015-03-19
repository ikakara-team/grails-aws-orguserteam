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
import ikakara.orguserteam.dao.dynamo.IdTeam
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdOrgTeam
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdUserTeam
import ikakara.orguserteam.dao.dynamo.IdUserOrg

public class OrgUserTeamService {
  static transactional = false

  def findIdObjBySlugId(String slugId) {
    def list = new IdSlug().queryByAlias(slugId)
    if(list?.size() == 1) {
      return list[0]
    } else {
      log.error("findIdObjBySlugId - invalid result for ${slugId}")
    }

    return null
  }

  def exist(AIdBase id) {
    return id?.load() ? true : false;
  }

  /////////////////////////////////////////////////////////////////////////////
  // User
  /////////////////////////////////////////////////////////////////////////////
  // should we verify if user exist?
  def user(String userId) {
    def user = new IdUser(id: userId)
    def bload = user.load();
    if(!bload) {
      // doesn't exist
      log.warn("User Not Found: " + userId)
      // return null
    }

    return user;
  }

  def listUser(IdOrg org) {
    List listUser = []

    List list = new IdUserOrg().withGroup(org).queryByGroupAndType()
    for(def userobj in list) {
      IdUser user = userobj.getMember();
      user.load()
      listUser.add(user)
    }

    return listUser
  }

  def listUser(IdTeam team) {
    List listUser = []

    List list = new IdUserTeam().withGroup(team).queryByGroupAndType()
    for(def userobj in list) {
      IdUser user = userobj.getMember();
      user.load()
      listUser.add(user)
    }

    return listUser
  }

  def createUser(IdUser user, String name, String initials, String desc, String shortName) {
    // create org
    user.slugify(shortName)
    .withCreatedUpdated()

    user.name = name
    user.initials = initials
    user.description = desc

    def bcreate = user.create()
    if(!bcreate) {
      // failed
      return null
    }

    // create slug
    def slug = new IdSlug(id: user.getAliasId())
    .withAlias(user)
    .withCreatedUpdated()

    bcreate = slug.create()
    if(!bcreate) {
      // failed
      user.delete()
      return null
    }

    return user
  }

  def updateUser(IdUser user, String name, String initials, String desc, String shortName) {
    def bload = user.load();
    if(!bload) {
      // not found, create a user
      user = createUser(user, name, initials, desc, shortName)
      return user
    }

    def oldslug = null
    def newslug = null

    if(shortName) {
      if(user.aliasId != shortName) {
        // create new slug
        newslug = new IdSlug().withSlugId(shortName)
        .withAlias(user)
        .withCreatedUpdated()

        def bcreate = newslug.create()
        if(!bcreate) {
          // failed
          return null
        }

        oldslug = user.getAlias()
        user.withAlias(newslug)
      }
    }

    if(name) {
      user.name  = name
    }

    user.description = desc
    user.initials = initials

    user.setUpdatedDate(new Date())
    def bsave = user.save();
    if(bsave) {
      // cleanup old slug
      if(oldslug) {
        oldslug.delete()
      }
    } else {
      // cleanup new slug
      if(newslug) {
        newslug.delete();
      }
    }

    return user
  }

  // return true if deleted, returns false if not found
  def deleteUser(IdUser user) {
    def bload = user.load();
    if(!bload) {
      // not found
      return false
    }

    // delete slug
    def slug = new IdSlug(id: user.getAliasId())
    slug.delete()

    user.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Org
  /////////////////////////////////////////////////////////////////////////////
  def org(String orgId) {
    def org = new IdOrg().withId(orgId)
    def bload = org.load()
    if(!bload) {
      // doesn't exist
      log.error("Org Not Found: " + orgId)
      return null;
    }

    return org;
  }

  // This is what sucks about NOSQL (dynamo) ...
  def listOrg(IdUser user) {
    List<IdOrg> listOrg = new ArrayList<>();

    List list = new IdUserOrg().withMember(user).queryByMemberAndType()

    // we can optimize this ...
    for(IdUserOrg userorg in list) {
      def org = userorg.getGroup();
      org.load()
      listOrg.add(org)
    }

    return listOrg;
  }

  def getOrg(IdTeam team) {
    def org = null

    def list = listOrg(team)
    if(!list?.isEmpty()) {
      org = list[0] // hacky, should only be one org
    }

    return org
  }

  // NOSQL compromise for 1 to many, using many to many table
  def listOrg(IdTeam team) {
    List<IdOrg> listOrg = new ArrayList<>();

    List list = new IdOrgTeam().withGroup(team).queryByGroupAndType()

    // theoretically, only 1
    for(IdOrgTeam orgteam in list) {
      def org = orgteam.getMember();
      org.load()
      listOrg.add(org)
    }

    return listOrg;
  }

  def createOrg(IdUser user, String orgName, String orgDescription) {
    // create org
    def org = new IdOrg(description: orgDescription)
    .initId()
    .slugify(orgName)
    .withCreatedUpdated()

    def bcreate = org.create()
    if(!bcreate) {
      // failed
      return null
    }

    // create slug
    def slug = new IdSlug(id: org.getAliasId())
    .withAlias(org)
    .withCreatedUpdated()

    bcreate = slug.create()
    if(!bcreate) {
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

      bcreate = userorg.create()
      if(!bcreate) {
        // failed
        org.delete()
        slug.delete()
        return null
      }
    }

    return org
  }

  def updateOrg(IdOrg org, String name, String desc, String web_url, String shortName) {
    def bload = org.load();
    if(!bload) {
      // not found
      return
    }

    def oldslug = null
    def newslug = null

    if(shortName) {
      if(org.aliasId != shortName) {
        // create new slug
        newslug = new IdSlug().withSlugId(shortName)
        .withAlias(org)
        .withCreatedUpdated()

        def bcreate = newslug.create()
        if(!bcreate) {
          // failed
          return null
        }

        oldslug = org.getAlias()
        org.withAlias(newslug)
      }
    }

    if(name) {
      org.name  = name
    }

    org.description = desc
    org.webUrl = web_url

    org.setUpdatedDate(new Date())
    def bsave = org.save();
    if(bsave) {
      // cleanup old slug
      if(oldslug) {
        oldslug.delete()
      }
    } else {
      // cleanup new slug
      if(newslug) {
        newslug.delete();
      }
    }

    return org.getAliasId()
  }

  // return true if deleted, returns false if not found
  def deleteOrg(IdOrg org) {
    def bload = org.load();
    if(!bload) {
      // not found
      return false
    }

    // delete slug
    def slug = new IdSlug(id: org.getAliasId())
    slug.delete()

    org.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Team
  /////////////////////////////////////////////////////////////////////////////

  def team(String teamId) {
    def team = new IdTeam().withId(teamId)
    def bload = team.load()
    if(!bload) {
      // doesn't exist
      log.error("App Not Found: " + teamId)
      return null
    }

    return team;
  }

  def listTeamVisible(IdOrg org, IdUser user) {
    List listTeam = []

    List list = new IdOrgTeam().withMember(org).queryByMemberAndType()
    for(def orgobj in list) {
      IdTeam team = orgobj.getGroup();
      team.load()

      // check if app is visible to user
      if(!team.isOrgVisible()) {
        // check is member is
        def userteam = team.hasMember(user)
        if(!userteam) {
          continue
        }
      }

      listTeam.add(team)
    }

    return listTeam
  }

  def listTeam(IdOrg org) {
    List listTeam = []

    List list = new IdOrgTeam().withMember(org).queryByMemberAndType()
    for(def orgobj in list) {
      IdTeam team = orgobj.getGroup();
      team.load()
      listTeam.add(team)
    }

    return listTeam
  }

  def listTeam(IdUser user) {
    List listTeam = []

    List list = new IdUserTeam().withMember(user).queryByMemberAndType()
    for(def userobj in list) {
      IdTeam team = userobj.getGroup();
      team.load()
      listTeam.add(team)
    }

    return listTeam
  }

  // this is ridculous ... SQL is so much better at relationships
  def listOrgTeams(IdUser user, String myOrgName) {
    Map mapApp = new LinkedHashMap();
    List listOrg = []
    listOrg.add(new IdOrg(name: myOrgName))

    // get all the user teams and orgs
    List list = new IdUserTeam().withMember(user).queryByMember()
    // we can optimize this ...
    for(def userobj in list) {
      if(userobj instanceof IdUserTeam) {
        IdTeam team = userobj.getGroup();
        team.load()
        mapApp.put(team.id, team)
      } else if(userobj instanceof IdUserOrg) {
        def org = userobj.getGroup();
        org.load()
        listOrg.add(org)
      } else {
        // unknown class
      }
    }

    for(int i = 1; i < listOrg.size(); i++) {
      def org = listOrg[i]

      // get all the teams of the orgs that the user belongs to
      // query by member and privacy
      List list_orgteam = new IdOrgTeam().withMember(org).queryByMemberAndType()
      for(IdOrgTeam orgteam in list_orgteam) {
        IdTeam team = orgteam.getGroup();

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
    for(def team : mapApp.values()){
      listOrg[0].teamListAdd(team)
    }

    return listOrg;
  }

  def createTeam(IdUser user, String teamName, Integer privacy, String orgId) {
    def org =  null
    if(orgId) {
      // check if org exist
      org = new IdOrg(id: orgId)
      def bload = org.load()
      if(!bload) {
        // failed
        return null
      }
    }

    // create team
    def team = new IdTeam(privacy: privacy)
    .initId()
    .slugify(teamName)
    .withCreatedUpdated()

    def bcreate = team.create()
    if(!bcreate) {
      // failed
      return null
    }

    // create slug
    def slug = new IdSlug(id: team.getAliasId())
    .withAlias(team)
    .withCreatedUpdated()

    bcreate = slug.create()
    if(!bcreate) {
      // failed
      team.delete()
      return null
    }

    // add user to team
    def userteam = new IdUserTeam()
    .withMember(user)
    .withGroup(team)
    .withCreatedUpdated()

    bcreate = userteam.create()
    if(!bcreate) {
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

      bcreate = orgteam.create()
      if(!bcreate) {
        // failed
        userteam.delete()
        team.delete()
        slug.delete()
        return null
      }
    }

    return team;
  }

  //user, params.name, params.int('privacy'), params.org, params.aliasId
  def updateTeam(IdTeam team, String name, Integer privacy, String description, String shortName) {
    //def bload = team.load();
    //if(!bload) {
    // not found
    // return
    //}

    def oldslug = null
    def newslug = null

    if(shortName) {
      if(team.aliasId != shortName) {
        // create new slug
        newslug = new IdSlug().withSlugId(shortName)
        .withAlias(team)
        .withCreatedUpdated()

        def bcreate = newslug.create()
        if(!bcreate) {
          // failed
          return null
        }

        oldslug = team.getAlias()
        team.withAlias(newslug)
      }
    }

    if(name) {
      team.name  = name
    }

    team.privacy = privacy
    team.description = description

    team.setUpdatedDate(new Date())
    def bsave = team.save();
    if(bsave) {
      // cleanup old slug
      if(oldslug) {
        oldslug.delete()
      }
    } else {
      // cleanup new slug
      if(newslug) {
        newslug.delete();
      }
    }

    return team.getAliasId()
  }


  //user, params.name, params.int('privacy'), params.org, params.aliasId
  def updateTeamOwner(IdTeam team, String orgId) {
    //def bload = team.load();
    //if(!bload) {
    // not found
    // return
    //}

    def curOrg = getOrg(team)

    if((orgId || curOrg) && (curOrg?.id != orgId)) {
      def org = null
      if(orgId) {
        // check if org exist
        org = new IdOrg(id: orgId)
        def bload = org.load()
        if(!bload) {
          // failed
          return null
        }
      }

      if(curOrg) {
        // delete org
        def curorgteam = new IdOrgTeam()
        .withMember(curOrg)
        .withGroup(team)
        def bdel = curorgteam.delete()
        if(!bdel) {
          // failed
          return null
        }
      }

      if(org) {
        // add org to team
        def orgteam = new IdOrgTeam()
        .withMember(org)
        .withGroup(team)
        .withCreatedUpdated()

        def bcreate = orgteam.create()
        if(!bcreate) {
          // failed
          return null
        }
      }
    }

    return true
  }

  // return true if deleted, returns false if not found
  def deleteTeam(IdTeam team) {
    def bload = team.load();
    if(!bload) {
      // not found
      return false
    }

    // delete slug
    def slug = new IdSlug(id: team.getAliasId())
    slug.delete()

    team.delete()

    return true
  }
}
