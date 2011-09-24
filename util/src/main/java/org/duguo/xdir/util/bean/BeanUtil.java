package org.duguo.xdir.util.bean;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.duguo.xdir.util.collection.MapUtil;


public class BeanUtil
{
    private static final Logger logger = LoggerFactory.getLogger( BeanUtil.class );

    public static final String IGNORED_BEAN_NAME = "ignored_bean_name";
    /**
     * sample input:  foo_bar
     * sample output: fooBar
     * 
     * @param underscoreName 
     * @return
     */
    public static String underscoreNameToBeanName(String underscoreName){
        Assert.notNull( underscoreName );
        StringBuilder beanName=null;
        String[] names=underscoreName.split( "_" );
        for(String name:names){
            if(beanName==null){
                beanName=new StringBuilder();
                beanName.append( StringUtils.uncapitalize( name ) );
            }else{
                beanName.append( StringUtils.capitalize( name ) );                
            }
        }
        return beanName.toString();
    }

    /**
     * aaaBBBccc_ddd_Eee return Aaa B B Bccc Ddd Eee
     * 
     * @param fieldRawName
     * @return
     */
    public static String displayFieldName(String fieldRawName){
        StringBuilder fieldDisplayableName=new StringBuilder();;
        boolean isStart=true;
        for(char currentChar:fieldRawName.toCharArray()){
            if(isStart){                
                fieldDisplayableName.append(Character.toUpperCase(currentChar));
                isStart=false;
            }else if(currentChar=='_'){
                fieldDisplayableName.append( ' ' );     
                isStart=true;
            }else if(currentChar>='A' && currentChar<='Z'){
                fieldDisplayableName.append( ' ' );
                fieldDisplayableName.append( currentChar );
            }else{
                fieldDisplayableName.append( currentChar );                 
            }
        }
        return fieldDisplayableName.toString();
    }

    public static boolean bindFieldValueIfHasSetter( Object targetBean, Object fieldValue )throws Exception
    {
        Assert.notNull( fieldValue );
        for(Method currentMethod:targetBean.getClass().getMethods()){
            if(currentMethod.getParameterTypes().length==1 && isSetterName(currentMethod.getName())){
                Class<?> fieldType = currentMethod.getParameterTypes()[0];
                if(fieldType.isAssignableFrom( fieldValue.getClass() )){
                    if(logger.isDebugEnabled())
                        logger.debug("bind bean class [{}] by setter with value class [{}]",targetBean.getClass().getName(),fieldValue.getClass().getName());
                    currentMethod.invoke( targetBean, fieldValue );
                    return true;                    
                }
            }
        }        
        return false;
    }

    private static boolean isSetterName( String methodName )
    {
        return methodName.startsWith( "set" ) && methodName.length()>3 && methodName.charAt( 3 )>='A' && methodName.charAt( 3 )<='Z';
    }

    public static void bindFieldValueIfHasSetter( Object beanInstance, String propertyName, String propertyValue )throws Exception
    {
        String setterName="set"+StringUtils.capitalize( propertyName );
        for(Method currentMethod:beanInstance.getClass().getMethods()){
            if(currentMethod.getName().equals( setterName ) && currentMethod.getParameterTypes().length==1){
                Class<?> fieldType = currentMethod.getParameterTypes()[0];
                SimpleTypeConverter typeConverter = new SimpleTypeConverter();
                Object realValue=null;
                if(fieldType.isArray()){
                    String[] arrayValue=StringUtils.commaDelimitedListToStringArray(propertyValue);
                    realValue=typeConverter.convertIfNecessary(arrayValue,fieldType);
                }else{
                    realValue=typeConverter.convertIfNecessary( propertyValue,fieldType);
                }
                if(logger.isDebugEnabled())
                    logger.debug("bind field [{}] by setter with value [{}]",propertyName,realValue);
                currentMethod.invoke( beanInstance, realValue );
                return;
            }
        }        
    }

    public static Object retriveFieldValue( Object beanInstance, String fieldName )
    {
        return retriveFieldValue( beanInstance, Object.class, fieldName );
    }

    @SuppressWarnings("unchecked")
    public static <T> T retriveFieldValue( Object beanInstance, Class<T> fieldType, String fieldName )
    {
        Object value=null;
        try
        {
            Class sourceClass=beanInstance.getClass();
            Field field = null;
            while(true){
                try{
                    field=sourceClass.getDeclaredField( fieldName );
                }catch(NoSuchFieldException ignore){
                }
                if(field!=null || sourceClass.equals( Object.class )){
                    break;
                }
                sourceClass=sourceClass.getSuperclass();
            }            
            Assert.notNull( field );
            field.setAccessible( true );
            value=field.get( beanInstance );
            return ( T ) value;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex );
        }finally{
            if ( logger.isDebugEnabled() )
                logger.debug( "retrived field [{}] value [{}]", fieldName,value );
        }
    }
    
    @SuppressWarnings("unchecked")
    public static boolean hasField( Object beanInstance, String fieldName )
    {
        try
        {
            Class sourceClass=beanInstance.getClass();
            Field field = null;
            while(true){
                try{
                    field=sourceClass.getDeclaredField( fieldName );
                }catch(NoSuchFieldException ignore){
                }
                if(field!=null || sourceClass.equals( Object.class )){
                    break;
                }
                sourceClass=sourceClass.getSuperclass();
            }            
            return field!=null;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Map retriveAllFields( Object beanInstance)
    {
        Map<String, Object> fields=new HashMap<String, Object>();
        try
        {
            Class sourceClass=beanInstance.getClass();
            while(true){
                if(sourceClass.equals( Object.class )){
                    break;
                }
                for(Field field:sourceClass.getDeclaredFields()){
                    String fieldName=field.getName();
                    if(!fields.containsKey( fieldName )){
                        field.setAccessible( true );
                        Object value=null;
                        if(Modifier.isStatic( field.getModifiers() )){
                            value=field.get( null );
                        }else{
                            value=field.get( beanInstance );
                        }                        
                        if(value==null){
                            fields.put( fieldName, new Object[]{field.getType(),null} );
                        }else{
                            fields.put( fieldName, new Object[]{field.getType(),value} );
                        }
                    }
                }
                sourceClass=sourceClass.getSuperclass();
                if(logger.isDebugEnabled())
                    logger.debug("retriveAllFields on class: [{}]",sourceClass.getName());
            }
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex );
        }
        if(logger.isDebugEnabled())
            logger.debug("retrived [{}] fields",fields.size());
        return MapUtil.sort( fields);
    }


    /**
     * Retrive setter/getter style bean field
     * 
     * @param <T>
     * @param beanInstance
     * @param fieldName
     * @param fieldType
     * @return
     */
    public static <T> GetterSetterCallback<T> getBeanGetterSetterIfAvailable( Object beanInstance, Class<T> fieldType,
        String fieldName )
    {
        Method getter = ClassUtils.getMethodIfAvailable( beanInstance.getClass(), getGetterName( fieldName ) );
        if ( getter != null && fieldType.isAssignableFrom( getter.getReturnType() ) )
        {
            String setterName=getSetterName( fieldName );
            for(Method currentMethod:beanInstance.getClass().getMethods()){
                if(currentMethod.getName().equals( setterName ) && currentMethod.getParameterTypes().length==1 && fieldType.isAssignableFrom(currentMethod.getParameterTypes()[0]) ){
                    return new BeanUtil.GetterSetterCallback<T>( beanInstance, getter, currentMethod );
                }
            }
        }
        return null;
    }


    public static void setValueIfNotAvailable( Object beanInstance, Class<?> fieldType, String fieldName, Object value )
    {
        GetterSetterCallback<?> setterGetterCallback = getBeanGetterSetterIfAvailable( beanInstance, fieldType,
            fieldName );
        if ( setterGetterCallback != null )
        {
            if ( setterGetterCallback.get() == null )
            {
                if ( logger.isDebugEnabled() )
                    logger.debug( "set bean value for field [{}]", fieldName );
                setterGetterCallback.set( value );
            }
        }
    }


    public static String getGetterName( String fieldName )
    {
        return "get" + StringUtils.capitalize( fieldName );
    }


    public static String getSetterName( String fieldName )
    {
        return "set" + StringUtils.capitalize( fieldName );
    }

    public static class GetterSetterCallback<T>
    {
        private Object beanInstance;
        private Method getter;
        private Method setter;


        public GetterSetterCallback( Object beanInstance, Method getter, Method setter )
        {
            this.beanInstance = beanInstance;
            this.getter = getter;
            this.setter = setter;
        }


        @SuppressWarnings(
            { "hiding", "unchecked" })
        public <T> T get()
        {
            try
            {
                return ( T ) getter.invoke( beanInstance );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( "failed to get field value", e );
            }
        }


        public void set( Object value )
        {
            try
            {
                if ( logger.isDebugEnabled() )
                    logger.debug( "set bean field via [{}]", setter.getName() );
                setter.invoke( beanInstance, value );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( "failed to set field ["+setter.getName()+"] value", e );
            }
        }
    }
}
