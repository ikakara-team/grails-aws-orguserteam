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

import java.util.Map
import java.util.Set
import java.util.ArrayList
import java.util.List

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import grails.validation.Validateable

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.document.Item

import ikakara.simplemarshaller.annotation.SimpleMarshaller
import ikakara.awsinstance.util.StringUtil

/**
 *
 * @author Allen
 */
@Validateable(nullable = true)
@SimpleMarshaller(includes = ["id", "type", "aliasId", "teamList", "orgList", "status", "name", "imageUrl", "initials", "description", "createdDate", "updatedDate"])
@Slf4j("LOG")
@CompileStatic
public class IdUser extends AIdBase {
  static public final String ID_TYPE = "User"
  static public final String ID_PREFIX = "~"

  Number status
  String name
  String image_url
  String initials
  String description

  // transient
  List<IdTeam> teamList = new ArrayList<>()
  List<IdOrg> orgList = new ArrayList<>()

  @DynamoDBIgnore
  public List<IdTeam> getTeamList() {
    return teamList
  }

  public void teamListAdd(List<IdTeam> list) {
    teamList.addAll(list)
  }

  public void teamListAdd(IdTeam team) {
    teamList.add(team)
  }

  @DynamoDBIgnore
  public List<IdOrg> getOrgList() {
    return orgList
  }

  public void orgListAdd(List<IdOrg> list) {
    orgList.addAll(list)
  }

  public void orgListAdd(IdOrg org) {
    orgList.add(org)
  }

  @Override
  @DynamoDBIgnore
  public String getTypePrefix() {
    return ID_PREFIX
  }

  @Override
  @DynamoDBAttribute(attributeName = "IdType")
  public String getType() {
    return ID_TYPE
  }

  @Override
  public void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map != null && !map.isEmpty()) {
    if (item.isPresent("Status")) {
      status = item.getNumber("Status")
    }
    if (item.isPresent("Name")) {
      name = item.getString("Name")
    }
    if (item.isPresent("ImageUrl")) {
      image_url = item.getString("ImageUrl")
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
  public Item marshalItemOUT(boolean bRemoveAttributeNull) {
    Item outItem = super.marshalItemOUT(bRemoveAttributeNull)
    if (outItem == null) {
      outItem = new Item()
    }

    if (status != null) {
      outItem = outItem.withNumber("Status", status)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("Status")
    }
    if (name != null && !"".equals(name)) {
      outItem = outItem.withString("Name", name)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("Name")
    }
    if (image_url != null && !"".equals(image_url)) {
      outItem = outItem.withString("ImageUrl", image_url)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("ImageUrl")
    }
    if (initials != null && !"".equals(initials)) {
      outItem = outItem.withString("Initials", initials)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("Initials")
    }
    if (description != null && !"".equals(description)) {
      outItem = outItem.withString("Description", description)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("Description")
    }

    return outItem
  }

  @Override
  public void initParameters(Map params) {
    super.initParameters(params)
    //if (params != null && !params.isEmpty()) {

    try {
      status = (Integer) params.get("status")
      name = (String) params.get("name")
      image_url = (String) params.get("image_url")
      initials = (String) params.get("initials")
      description = (String) params.get("description")
    } catch (Exception e) {

    }

    //}
  }

  public IdUser() {
    super()
  }

  public IdUser(Map params) {
    super()
    initParameters(params)
  }

  public IdUser slugify(String str) {
    if (str != null && !"".equals(str)) {
      name = str
    }

    if (name == null || "".equals(name)) {
      name = "unknown" + StringUtil.getRandomNumbers(10)
    }

    alias = new IdSlug().withSlugId(name).withAlias(this)

    return this
  }

  static public IdUser fromSlug(String slugId) {
    IdSlug slug = (IdSlug) new IdSlug().withId(slugId)
    boolean bload = slug.load()
    if (!bload) {
      return null
    }

    // verify that the slug is for an user
    AIdBase user = slug.getAlias()
    if (user instanceof IdUser) {
      return (IdUser) user
    }

    return null
  }

  @DynamoDBAttribute(attributeName = "Status")
  public Number getStatus() {
    return status
  }

  public void setStatus(Number d) {
    status = d
  }

  @DynamoDBAttribute(attributeName = "Name")
  public String getName() {
    return name
  }

  public void setName(String d) {
    name = d
  }

  @DynamoDBAttribute(attributeName = "ImageUrl")
  public String getImageUrl() {
    return image_url
  }

  public void setImageUrl(String d) {
    image_url = d
  }

  @DynamoDBAttribute(attributeName = "Initials")
  public String getInitials() {
    return initials
  }

  public void setInitials(String d) {
    initials = d
  }

  @DynamoDBAttribute(attributeName = "Description")
  public String getDescription() {
    return description
  }

  public void setDescription(String d) {
    description = d
  }

}
