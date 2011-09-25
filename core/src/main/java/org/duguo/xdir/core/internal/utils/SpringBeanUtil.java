package org.duguo.xdir.core.internal.utils;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SpringBeanUtil
{

    public static String generateXmlFromBeanDefination( String beanName, AbstractBeanDefinition beanDefinition )
    {
        StringBuilder beanXml=new StringBuilder();
        beanXml.append( "  <!--Generated from bean defination-->\n  <bean name=\""+beanName);
        beanXml.append( "\" class=\"" +beanDefinition.getBeanClassName()+ "\"" );
        if(beanDefinition.isPrototype()){
            beanXml.append( " scope=\"prototype\"" );            
        }
        if(beanDefinition.getParentName()!=null){
            beanXml.append( " parent=\""+ beanDefinition.getParentName()+"\"" );            
         }
        if(beanDefinition.isAbstract()){
            beanXml.append( " abstract=\"true\"" );        
         }
        if(beanDefinition.getDependsOn()!=null){
            beanXml.append( " depends-on=\"" );
            boolean first=true;
            for(String depend:beanDefinition.getDependsOn()){
                if(first){
                    first=false;
                }else{
                    beanXml.append( "," ); 
                }
                beanXml.append( depend );   
            }          
            beanXml.append( "\"" );            
         }
        if(beanDefinition.getFactoryBeanName()!=null){
            beanXml.append( " factory-bean=\""+beanDefinition.getFactoryBeanName() + "\"" );            
         }
        if(beanDefinition.getFactoryMethodName()!=null){
            beanXml.append( " factory-method=\""+beanDefinition.getFactoryMethodName()+"\"" );            
         }
        if(beanDefinition.getInitMethodName()!=null){
            beanXml.append( " init-method=\""+ beanDefinition.getInitMethodName() + "\"" );            
         }
        if(beanDefinition.getDestroyMethodName()!=null){
            beanXml.append( " destroy-method=\""+ beanDefinition.getDestroyMethodName() +"\"" );            
         }
        boolean hasChildElement=false;
        ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
        if(constructorArgumentValues!=null && constructorArgumentValues.getArgumentCount()>0){
            for(Entry<Integer, ValueHolder> entry:constructorArgumentValues.getIndexedArgumentValues().entrySet()){
                if(hasChildElement==false){
                    beanXml.append( ">\n" ); 
                }
                hasChildElement=true;
                beanXml.append( "    <constructor-arg index=\""+entry.getKey()+"\"" );
                if(entry.getValue().getName()!=null){
                    beanXml.append( " name=\""+entry.getValue().getName() +"\"");                    
                }
                appendRealValue( beanXml, entry.getValue().getValue() );
                if(entry.getValue().getType()!=null){
                    beanXml.append( "\" type=\""+entry.getValue().getType() +"\"");                    
                }
                beanXml.append( "/>\n" ); 
            }
        }
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        if(propertyValues!=null && propertyValues.size()>0){
            for(PropertyValue propertyValue:propertyValues.getPropertyValues()){
                if(hasChildElement==false){
                    beanXml.append( ">\n" );
                }
                hasChildElement=true;
                beanXml.append( "    <property name=\""+ propertyValue.getName()+"\"");
                appendRealValue( beanXml, propertyValue.getOriginalPropertyValue().getValue());
            }
        }
        
        
        if(hasChildElement){
            beanXml.append( "  </bean>" );    
        }else{
            beanXml.append( "/>" );              
        }
        return beanXml.toString();
    }
    

    public static String loadBeanDefinationFromXml( String beanName, InputStream inputStream)
        throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = null;
        try
        {
            doc = builder.parse( inputStream );
            NodeList rootbeans = doc.getChildNodes();
            for(int j=0;j<rootbeans.getLength();j++){
                NodeList beans=rootbeans.item( j ).getChildNodes();
                if(beans!=null){
                    for ( int i = 0; i < beans.getLength(); i++ )
                    {
                        Node bean = beans.item( i );
                        String currentBeanName = retriveBeanName( bean );                   
                        if (beanName.equals(currentBeanName ) )
                        {
                            return transformBeanToXml( bean );  
                        }
                    }  
                    return null;
                } 
            }
        }
        finally
        {
            inputStream.close();
        }
        return null;
    }

    private static String transformBeanToXml( Node bean ) throws TransformerConfigurationException,
        TransformerFactoryConfigurationError, TransformerException
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty( OutputKeys.INDENT, "no" );
        transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
        StreamResult result = new StreamResult( new StringWriter() );
        DOMSource source = new DOMSource( bean );
        transformer.transform( source, result );
        String beanXml = result.getWriter().toString();
        return beanXml;
    }

    private static String retriveBeanName( Node bean )
    {
        String currentBeanName=null;
        NamedNodeMap attrs = bean.getAttributes();
        if(attrs!=null){
            Node beanNameAttr = attrs.getNamedItem( "id" );
            if ( beanNameAttr == null )
            {
                beanNameAttr = attrs.getNamedItem( "name" );
                if ( beanNameAttr == null )
                {
                    beanNameAttr = attrs.getNamedItem( "class" );
                }
            }
            if(beanNameAttr != null){
                currentBeanName=beanNameAttr.getNodeValue();
            }   
        }
        return currentBeanName;
    }

    @SuppressWarnings("unchecked")
    private static void appendRealValue( StringBuilder beanXml, Object realValue )
    {
        if(renderSimpleValue( beanXml, realValue," value=\"" )){
            beanXml.append("\"/>\n");
        }else if(realValue instanceof RuntimeBeanReference){
            beanXml.append( " ref=\""+((RuntimeBeanReference)realValue).getBeanName() +"\"/>\n");
        }else if(realValue instanceof ManagedMap){
            beanXml.append( ">\n      <map>\n");
            ManagedMap managedMap=(ManagedMap)realValue;
            for(Object entryObject:managedMap.entrySet()){
                Entry entry=(Entry)entryObject;
                beanXml.append( "        <entry key=\"");
                renderSimpleValue(beanXml,entry.getKey(),null);
                beanXml.append("\" value=\"");
                renderSimpleValue(beanXml,entry.getValue(),null);
                beanXml.append("\"/>\n");                
            }
            beanXml.append( "      </map>\n    </property>\n");
        }else if(realValue instanceof ManagedList){
            beanXml.append( ">\n      <list>\n");
            ManagedList managedList=(ManagedList)realValue;
            for(Object value:managedList){
                beanXml.append( "        <value>");
                renderSimpleValue(beanXml,value,null);
                beanXml.append( "</value>\n");                
            }
            beanXml.append( "      </list>\n    </property>\n");
        }else{
            beanXml.append( " UNSUPPORTED_PROPERTY_VALUE=\""+realValue.getClass().getName()+":"+realValue +"\"/>\n");
        }
    }
    
    private static boolean renderSimpleValue(StringBuilder beanXml,Object realValue,String prefix){
        if(realValue instanceof String || realValue instanceof Integer){
            if(prefix!=null){
                beanXml.append(prefix);            
            }
            beanXml.append(realValue);
            return true;
        }else if(realValue instanceof TypedStringValue){
            if(prefix!=null){
                beanXml.append(prefix);            
            }
            beanXml.append(((TypedStringValue)realValue).getValue());
            return true;
        }else if(prefix==null){
            beanXml.append(realValue);            
        }
        return false;
    }


}
