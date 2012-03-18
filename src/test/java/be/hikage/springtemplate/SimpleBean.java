/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.hikage.springtemplate;

/**
 * Created by IntelliJ IDEA.
 * User: hikage
 * Date: 23 mars 2010
 * Time: 12:32:10
 * To change this template use File | Settings | File Templates.
 */
public class SimpleBean {

    private String propertyValue;
    private String externalizedPropertyValue;
    private String constructorValue;
    private String externalizedConstructorValue;

    public String getConstructorValue() {
        return constructorValue;
    }

    public void setConstructorValue(String constructorValue) {
        this.constructorValue = constructorValue;
    }

    public String getExternalizedConstructorValue() {
        return externalizedConstructorValue;
    }

    public void setExternalizedConstructorValue(String externalizedConstructorValue) {
        this.externalizedConstructorValue = externalizedConstructorValue;
    }

    public SimpleBean(String constructorValue, String externalized) {
        this.constructorValue = constructorValue;
        this.externalizedConstructorValue = externalized;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getExternalizedPropertyValue() {
        return externalizedPropertyValue;
    }

    public void setExternalizedPropertyValue(String externalizedPropertyValue) {
        this.externalizedPropertyValue = externalizedPropertyValue;
    }
}
