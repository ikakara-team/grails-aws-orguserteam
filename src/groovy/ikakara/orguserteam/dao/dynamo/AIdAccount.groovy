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

import groovy.transform.ToString
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.document.Item

/**
 * @author Allen
 */
@ToString(includePackage=false, includeNames=true, ignoreNulls=true, includeSuperProperties=true)
@Slf4j("LOG")
@CompileStatic
abstract class AIdAccount extends AIdBase {

  @DynamoDBAttribute(attributeName = "Status")
  Number status

  // transient
  List<IdFolder> folderList = []

  @DynamoDBIgnore
  List<IdFolder> getFolderList() {
    return folderList
  }

  void folderListAdd(List<IdFolder> list) {
    folderList.addAll(list)
  }

  void folderListAdd(IdFolder folder) {
    folderList << folder
  }

  @Override
  void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map) {
    if (item.isPresent("Status")) {
      status = item.getNumber("Status")
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

    return outItem
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params) {

    try {
      status = (Integer) params.status
    } catch (ignored) {
    }

    //}
  }

  AIdAccount() {
  }

  AIdAccount(Map params) {
    initParameters(params)
  }

  AIdAccount withStatus(int s) {
    status = s
    return this
  }

  static AIdAccount toIdAccount(String id_str) {
    AIdAccount obj = (AIdAccount)new IdUser().isId(id_str)
    if (obj) {
      return obj
    }

    obj = (AIdAccount)new IdOrg().isId(id_str)
    if (obj) {
      return obj
    }
  }

}
