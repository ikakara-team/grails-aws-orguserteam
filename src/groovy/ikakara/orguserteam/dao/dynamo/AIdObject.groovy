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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.document.Item

import ikakara.awsinstance.dao.dynamo.ACreatedUpdatedObject
import ikakara.awsinstance.dao.dynamo.ADynamoObject
import ikakara.awsinstance.util.StringUtil

/**
 * @author Allen
 */
@ToString(includePackage=false, includeNames=true, ignoreNulls=true, includeSuperProperties=true)
@Slf4j("LOG")
@CompileStatic
abstract class AIdObject extends ACreatedUpdatedObject {

  @DynamoDBHashKey(attributeName = "Id")
  String id

  abstract String getTypePrefix()

  @Override
  abstract ADynamoObject newInstance(Item item)

  @Override
  abstract String tableName()

  @Override
  abstract Map initTable()

  @Override
  def valueHashKey() {
    return typePrefix + id
  }

  @Override
  String nameHashKey() {
    return "Id"
  }

  @Override
  def valueRangeKey() {}

  @Override
  String nameRangeKey() {}

  @Override
  void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map) {
    if (item.isPresent("Id")) {
      String str = item.getString("Id")
      id = str.substring(typePrefix.length())
    }
    //}
  }

  @Override
  Item marshalItemOUT(boolean removeAttributeNull) {
    super.marshalItemOUT(removeAttributeNull) ?: new Item()
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params) {
    id = (String) params.id
    //}
  }

  boolean equals(AIdObject obj) {
    return valueHashKey() == obj.valueHashKey()
  }

  String urlEncodedId() {
    return StringUtil.urlEncodeExt(id)
  }

  void urlDecodeId(String id) {
    this.id = StringUtil.urlDecode(id)
  }
}
