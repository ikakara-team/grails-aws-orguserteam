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
package ikakara.orguserteam.dao.dynamo

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import grails.validation.Validateable

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.document.Item

import ikakara.awsinstance.util.StringUtil
import ikakara.simplemarshaller.annotation.SimpleMarshaller

/**
 * @author Allen
 */
@Validateable(nullable = true)
@SimpleMarshaller(includes = ["id", "type", "aliasId", "teamList", "orgList", "status", "name", "imageUrl", "initials", "description", "createdDate", "updatedDate"])
@Slf4j("LOG")
@CompileStatic
class IdUser extends AIdBase {
  public static final String ID_TYPE = "User"
  public static final String ID_PREFIX = "~"

  @DynamoDBAttribute(attributeName = "Status")
  Number status
  @DynamoDBAttribute(attributeName = "Name")
  String name
  @DynamoDBAttribute(attributeName = "ImageUrl")
  String imageUrl
  @DynamoDBAttribute(attributeName = "Initials")
  String initials
  @DynamoDBAttribute(attributeName = "Description")
  String description

  // transient
  List<IdTeam> teamList = []
  List<IdOrg> orgList = []

  @DynamoDBIgnore
  List<IdTeam> getTeamList() {
    return teamList
  }

  void teamListAdd(List<IdTeam> list) {
    teamList.addAll(list)
  }

  void teamListAdd(IdTeam team) {
    teamList << team
  }

  @DynamoDBIgnore
  List<IdOrg> getOrgList() {
    return orgList
  }

  void orgListAdd(List<IdOrg> list) {
    orgList.addAll(list)
  }

  void orgListAdd(IdOrg org) {
    orgList << org
  }

  @Override
  @DynamoDBIgnore
  String getTypePrefix() {
    return ID_PREFIX
  }

  @Override
  @DynamoDBAttribute(attributeName = "IdType")
  String getType() {
    return ID_TYPE
  }

  @Override
  void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map) {
    if (item.isPresent("Status")) {
      status = item.getNumber("Status")
    }
    if (item.isPresent("Name")) {
      name = item.getString("Name")
    }
    if (item.isPresent("ImageUrl")) {
      imageUrl = item.getString("ImageUrl")
    }
    if (item.isPresent("Initials")) {
      initials = item.getString("Initials")
    }
    if (item.isPresent("Description")) {
      description = item.getString("Description")
    }
    //}
  }

  @Override
  Item marshalItemOUT(boolean removeAttributeNull) {
    Item outItem = super.marshalItemOUT(removeAttributeNull) ?: new Item()

    if (status != null) {
      outItem = outItem.withNumber("Status", status)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("Status")
    }
    if (name) {
      outItem = outItem.withString("Name", name)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("Name")
    }
    if (imageUrl) {
      outItem = outItem.withString("ImageUrl", imageUrl)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("ImageUrl")
    }
    if (initials) {
      outItem = outItem.withString("Initials", initials)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("Initials")
    }
    if (description) {
      outItem = outItem.withString("Description", description)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("Description")
    }

    return outItem
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params) {

    try {
      status = (Integer) params.status
      name = (String) params.name
      imageUrl = (String) params.image_url
      initials = (String) params.initials
      description = (String) params.description
    } catch (ignored) {
    }

    //}
  }

  IdUser() {
  }

  IdUser(Map params) {
    initParameters(params)
  }

  IdUser slugify(String str) {
    if (str) {
      name = str
    }

    if (!name) {
      name = "unknown" + StringUtil.getRandomNumbers(10)
    }

    alias = new IdSlug().withSlugId(name).withAlias(this)

    return this
  }

  static IdUser fromSlug(String slugId) {
    IdSlug slug = (IdSlug) new IdSlug().withId(slugId)
    boolean load = slug.load()
    if (!load) {
      return null
    }

    // verify that the slug is for an user
    AIdBase user = slug.alias
    return (user instanceof IdUser) ? (IdUser) user : null
  }
}
