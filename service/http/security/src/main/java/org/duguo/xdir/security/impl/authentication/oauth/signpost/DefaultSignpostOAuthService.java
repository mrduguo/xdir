package org.duguo.xdir.security.impl.authentication.oauth.signpost;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * Default signpost oauth service implementation to create consumer by create bean with scope="prototype" and load via spring bean factory.
 * 
 * @author duguo
 *
 */
public class DefaultSignpostOAuthService extends AbstractSignpostOAuthService implements BeanFactoryAware{
	
	private BeanFactory beanFactory;
	private OAuthProvider oauthProvider;
	private String oauthConsumerFactoryBeanName;

	@Override
	protected OAuthConsumer retriveOauthConsumer() {
		return beanFactory.getBean(oauthConsumerFactoryBeanName, OAuthConsumer.class);
	}
	@Override
	protected OAuthProvider retriveOauthProvider() {
		return oauthProvider;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory=beanFactory;		
	}
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
	public OAuthProvider getOauthProvider() {
		return oauthProvider;
	}
	public void setOauthProvider(OAuthProvider oauthProvider) {
		this.oauthProvider = oauthProvider;
	}
	public String getOauthConsumerFactoryBeanName() {
		return oauthConsumerFactoryBeanName;
	}
	public void setOauthConsumerFactoryBeanName(String oauthConsumerFactoryBeanName) {
		this.oauthConsumerFactoryBeanName = oauthConsumerFactoryBeanName;
	}


}
