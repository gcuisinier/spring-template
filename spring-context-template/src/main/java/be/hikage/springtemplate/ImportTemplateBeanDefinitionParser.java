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

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * {@link org.springframework.beans.factory.xml.BeanDefinitionParser} that parses an {code import-template} element
 * and import beans defined in the {@code location} attribute resource, by replacing the pattern defined by nested
 * {@code replacement} elements
 *
 * @author Gildas Cuisinier
 * @author Stefan Isele
 */
public class ImportTemplateBeanDefinitionParser implements BeanDefinitionParser {

    /**
     * Attribute that contains the template resource
     */
    private static final String LOCATION_ATTRIBUTE = "location";

    /**
     * Attribute that contains the pattern to replace
     */
    private static final String PATTERN_ATTRIBUTE = "name";

    /**
     * Attribute that contains the substitution
     */
    private static final String SUBSTITUTION_ATTRIBUTE = "value";

    /**
     * Element name for the nested replacement element
     */
    private static final String REPLACEMENT_TAG = "variable";


    public BeanDefinition parse(Element element, ParserContext parserContext) {

        Map<String, String> patternMap = prepareReplacement(element);

        MappingBeanDefinitionVisitor visitor = new MappingBeanDefinitionVisitor(patternMap);
        Map<String, BeanDefinition> beansDefinition = loadTemplateBeans(element, visitor);

        CompositeComponentDefinition def = new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));

        parserContext.pushContainingComponent(def);

        registerBeans(parserContext, beansDefinition);

        parserContext.popAndRegisterContainingComponent();


        return null;


    }


    private void registerBeans(ParserContext parserContext, Map<String, BeanDefinition> beansDefinition) {
        for (Map.Entry<String, BeanDefinition> entry : beansDefinition.entrySet()) {
            parserContext.getRegistry().registerBeanDefinition(entry.getKey(), entry.getValue());
            parserContext.registerComponent(new BeanComponentDefinition(entry.getValue(), entry.getKey()));
        }
    }

    private Map<String, BeanDefinition> loadTemplateBeans(Element element, MappingBeanDefinitionVisitor visitor) {
        String resource = element.getAttribute(LOCATION_ATTRIBUTE);

        BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(registry);
        reader.loadBeanDefinitions(resource);

        Map<String, BeanDefinition> beans = new HashMap<String, BeanDefinition>();

        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            visitor.visitBeanDefinition(beanDef);
            String resolvedBeanName = visitor.resolveStringValue(beanName);
            if (resolvedBeanName.equals(beanName))
                throw new BeanCreationException("The bean name in template must contain pattern content");
            beans.put(visitor.resolveStringValue(beanName), beanDef);

        }

        return beans;


    }

    private Map<String, String> prepareReplacement(Element element) {

        List<Element> replacementElements = DomUtils.getChildElementsByTagName(element, REPLACEMENT_TAG);
        if (replacementElements.size() == 0) return Collections.emptyMap();

        Map<String, String> replacementMap = new HashMap();
        for (Element replacement : replacementElements) {
            replacementMap.put(replacement.getAttribute(PATTERN_ATTRIBUTE), replacement.getAttribute(SUBSTITUTION_ATTRIBUTE));

        }
        return replacementMap;

    }


    /**
     * {@link org.springframework.beans.factory.config.BeanDefinitionVisitor} based on a Map to store the replacement
     */
    private static class MappingBeanDefinitionVisitor extends BeanDefinitionVisitor {
        private Map<String, String> replacements;

        private String prefix = "${";
        private String suffix = "}";

        private MappingBeanDefinitionVisitor(Map<String, String> replacements) {
            this.replacements = replacements;
        }

        @Override
        protected String resolveStringValue(String s) {

            for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                s = s.replace(prefix + replacement.getKey() + suffix, replacement.getValue());
            }
            return s;

        }

        @Override
        public void visitBeanDefinition(BeanDefinition beanDefinition) {

            if (beanDefinition instanceof AbstractBeanDefinition) {
                visitDependsOn((AbstractBeanDefinition) beanDefinition);

            }
            super.visitBeanDefinition(beanDefinition);
        }


        /**
         * Replaces bean names in depends-on
         *
         * @param beanDefinition
         */
        private void visitDependsOn(AbstractBeanDefinition beanDefinition) {
            String[] allDependsOn = beanDefinition.getDependsOn();
            if (allDependsOn == null || allDependsOn.length == 0) {
                return;
            }
            String[] allResolved = new String[allDependsOn.length];
            for (int i = 0; i < allDependsOn.length; i++) {
                allResolved[i] = resolveStringValue(allDependsOn[i]);
            }
            beanDefinition.setDependsOn(allResolved);
        }
    }


}
