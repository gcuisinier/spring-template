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

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

public class TemplateTest {

    @Test
    public void testGood() {

        ApplicationContext context = new ClassPathXmlApplicationContext("test-config-good.xml", this.getClass());
        assertTrue("Bean simple-dev must be defined", context.containsBean("simple-dev"));
        assertEquals(SimpleBean.class, context.getBean("simple-dev").getClass());
        SimpleBean simpleBean = (SimpleBean) context.getBean("simple-dev");

        assertEquals("constructorData.dev", simpleBean.getConstructorValue());
        assertEquals("ExternalizedConstructor", simpleBean.getExternalizedConstructorValue());

        assertEquals("propertyData.dev", simpleBean.getPropertyValue());
        assertEquals("ExternalizedProperty", simpleBean.getExternalizedPropertyValue());


        assertTrue("Bean container-dev must be defined", context.containsBean("container-dev"));
        assertEquals(ContainerBean.class, context.getBean("container-dev").getClass());

        ConfigurableApplicationContext listableBeanFactory = (ConfigurableApplicationContext) context;
        RootBeanDefinition beanDefinition = (RootBeanDefinition) listableBeanFactory.getBeanFactory().getBeanDefinition("container-dev");
        assertArrayEquals(new String[]{"simple-dev"}, beanDefinition.getDependsOn());


    }

    @Test
    public void testExternal() {

        ApplicationContext context = new ClassPathXmlApplicationContext("test-config-good-external.xml", this.getClass());
        assertTrue("Bean simple-dev must be defined", context.containsBean("simple-dev"));
        assertEquals(SimpleBean.class, context.getBean("simple-dev").getClass());
        SimpleBean simpleBean = (SimpleBean) context.getBean("simple-dev");

        assertEquals("constructorData.dev", simpleBean.getConstructorValue());
        assertEquals("ExternalizedConstructor", simpleBean.getExternalizedConstructorValue());

        assertEquals("propertyData.dev", simpleBean.getPropertyValue());
        assertEquals("ExternalizedProperty", simpleBean.getExternalizedPropertyValue());

        assertNotNull(simpleBean.getObjectProperty());
        assertEquals("java.lang.Object", simpleBean.getObjectProperty().getClass().getName());


        assertTrue("Bean container-dev must be defined", context.containsBean("container-dev"));
        assertEquals(ContainerBean.class, context.getBean("container-dev").getClass());

        ConfigurableApplicationContext listableBeanFactory = (ConfigurableApplicationContext) context;
        RootBeanDefinition beanDefinition = (RootBeanDefinition) listableBeanFactory.getBeanFactory().getBeanDefinition("container-dev");
        assertArrayEquals(new String[]{"simple-dev"}, beanDefinition.getDependsOn());


    }



    @Test
    public void testBad() {

        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("test-config-bad.xml", this.getClass());
            fail("Exception must be thrown");
        } catch (BeansException e) {
            assertTrue(e.getCause() instanceof BeanCreationException);
        }


    }
}
