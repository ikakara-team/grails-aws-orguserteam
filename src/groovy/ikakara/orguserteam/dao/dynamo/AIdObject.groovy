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

//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.document.Item

import ikakara.awsinstance.dao.dynamo.ACreatedUpdatedObject
import ikakara.awsinstance.dao.dynamo.ADynamoObject
import ikakara.awsinstance.util.StringUtil

/**
 *
 * @author Allen
 */
@Slf4j("LOG")
@CompileStatic
abstract public class AIdObject extends ACreatedUpdatedObject {

  protected String id

  abstract public String getTypePrefix()

  @Override
  abstract public ADynamoObject newInstance(Item item)

  @Override
  abstract public String tableName()

  @Override
  abstract public Map initTable()

  @Override
  public Object valueHashKey() {
    return getTypePrefix() + id
  }

  @Override
  public String nameHashKey() {
    return "Id"
  }

  @Override
  public Object valueRangeKey() {
    return null
  }

  @Override
  public String nameRangeKey() {
    return null
  }

  @Override
  public void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map != null && !map.isEmpty()) {
    if (item.isPresent("Id")) {
      String str = item.getString("Id")
      id = str.substring(this.getTypePrefix().length())
    }
    //}
  }

  @Override
  public Item marshalItemOUT(boolean bRemoveAttributeNull) {
    Item outItem = super.marshalItemOUT(bRemoveAttributeNull)
    if (outItem == null) {
      outItem = new Item()
    }

    return outItem
  }

  @Override
  public void initParameters(Map params) {
    super.initParameters(params)
    //if (params != null && !params.isEmpty()) {
    id = (String) params.get("id")
    //}
  }

  public String urlEncodedId() {
    return StringUtil.urlEncodeExt(id)
  }

  public void urlDecodeId(String id) {
    this.id = StringUtil.urlDecode(id)
  }

  @DynamoDBHashKey(attributeName = "Id")
  @Override
  public String getId() {
    return id
  }

  @Override
  public void setId(String id) {
    this.id = id
  }

}
