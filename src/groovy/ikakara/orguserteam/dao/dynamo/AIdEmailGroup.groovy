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

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import grails.validation.Validateable

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.document.Item

/**
 *
 * @author Allen
 */
@Validateable(nullable = true)
@Slf4j("LOG")
@CompileStatic
abstract class AIdEmailGroup extends AMemberGroupBase {

  @DynamoDBAttribute(attributeName = "InvitedName")
  String invitedName;

  @Override
  void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map != null && !map.isEmpty()) {
    if (item.isPresent("InvitedName")) {
      invitedName = item.getString("InvitedName");
    }
    //}
  }

  @Override
  Item marshalItemOUT(boolean removeAttributeNull) {
    Item outItem = super.marshalItemOUT(removeAttributeNull) ?: new Item()

    if (invitedName) {
      outItem = outItem.withString("InvitedName", invitedName);
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("InvitedName");
    }

    return outItem
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params != null && !params.isEmpty()) {

    try {
      invitedName = params.invitedName
    } catch (Exception e) {

    }

    //}
  }

  AIdEmailGroup withInvitedName(String name) {
    invitedName = name
    return this
  }

  AIdEmailGroup() {
  }

  AIdEmailGroup(Map params) {
    initParameters(params)
  }

}
