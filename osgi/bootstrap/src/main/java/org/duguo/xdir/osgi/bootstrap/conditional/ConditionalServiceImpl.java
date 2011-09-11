package org.duguo.xdir.osgi.bootstrap.conditional;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.duguo.xdir.osgi.spi.conditional.ConditionalService;
import org.duguo.xdir.osgi.spi.conditional.ConditionalTerm;
import org.duguo.xdir.osgi.spi.util.ClassUtil;
import org.duguo.xdir.osgi.bootstrap.conditional.term.AndTerm;
import org.duguo.xdir.osgi.bootstrap.conditional.term.NotTerm;
import org.duguo.xdir.osgi.bootstrap.conditional.term.OrTerm;
import org.duguo.xdir.osgi.bootstrap.log.Logger;

public class ConditionalServiceImpl implements ConditionalService {
	
	public static final String KEY_XDIR_OSGI_CONDITIONAL_IMPL = "xdir.osgi.conditional.impl";
	public static final String KEY_XDIR_OSGI_CONDITIONAL_PARAM_DECODER = "xdir.osgi.conditional.param.decoder";
	public static final String KEY_XDIR_OSGI_CONDITIONAL_TERM_SPLITER = "xdir.osgi.conditional.term.spliter";
	public static final String KEY_XDIR_OSGI_CONDITIONAL_PREFIX = "xdir.osgi.conditional.prefix";
	
	public String termSpliter;
	public String conditionalPrefix;
	public Map<String, ConditionalTerm> terms=new HashMap<String, ConditionalTerm>();
	public Pattern termPattern=Pattern.compile("^[a-z]([a-z0-9])+$");
	public ParamDecoder paramDecoder;
	
	public ConditionalServiceImpl(){
		termSpliter=System.getProperty(KEY_XDIR_OSGI_CONDITIONAL_TERM_SPLITER,"__");
		conditionalPrefix=System.getProperty(KEY_XDIR_OSGI_CONDITIONAL_PREFIX,"if"+termSpliter);
		paramDecoder=ClassUtil.loadRequiredInstanceFromSystemProperty(ParamDecoder.class, ParamDecoderImpl.class, KEY_XDIR_OSGI_CONDITIONAL_PARAM_DECODER);
	}
	
	public boolean isConditionalString(String source){
		return source.startsWith(conditionalPrefix);
	}
	
	public boolean eval(String source) {
		String[] params=source.split(termSpliter);
		boolean evalResult=false;
		if(params.length>1){
			int processedLocation=1;
			boolean hasNot=false;
			boolean hasAnd=false;
			boolean hasOr=false;
			String currentParam=null;
			while(processedLocation<params.length){
				currentParam=params[processedLocation];
				if(isTerm(currentParam)){
					ConditionalTerm term=terms.get(currentParam);
					if(term.numberOfParams()<0){ // operator term
						if(term instanceof NotTerm){
							hasNot=true;
						}else if(term instanceof AndTerm){
							hasAnd=true;
						}else if(term instanceof OrTerm){
							hasOr=true;
						}else{
							throw new RuntimeException("Unsupported opertor: "+term.getClass().getName());
						}
					}else{ // logic term
						if(hasOr){
							if(evalResult==true){
								break;
							}else{
								hasOr=false;
							}
						}
						if(hasAnd){
							if(evalResult==false){
								break;
							}else{
								hasAnd=false;
							}
						}
						if(term.numberOfParams()==0){
							evalResult=term.eval();
						}else if(term.numberOfParams()==1){
							processedLocation++;
							currentParam=paramDecoder.decode(params[processedLocation]);
							evalResult=term.eval(currentParam);
						}else{
							String[] termParams=new String[term.numberOfParams()];
							if(hasNot){
								currentParam=params[processedLocation-2];
							}else{
								currentParam=params[processedLocation-1];
							}
							termParams[0]=paramDecoder.decode(currentParam);
							for(int i=1;i<term.numberOfParams();i++){
								processedLocation++;
								if(processedLocation<params.length){
									termParams[i]=paramDecoder.decode(params[processedLocation]);
								}
							}
							evalResult=term.eval(termParams);
						}
						if(hasNot){
							evalResult=!evalResult;
							hasNot=false;
						}
					}
				}
				processedLocation++;
			}
		}
		return evalResult;
	}

	public void registTerm(String key, ConditionalTerm term) {
		terms.put(key, term);
	}

	protected boolean isTerm(String param) {		
		if(terms.containsKey(param)){
			return true;
		}else if(termPattern.matcher(param).find()){
			tryToLoadTerm(param);
			if(terms.containsKey(param)){
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected void tryToLoadTerm(String term) {
		String termClassName=term.substring(0, 1).toUpperCase(Locale.ENGLISH)+term.substring(1);
		termClassName=getClass().getPackage().getName()+".term."+termClassName+"Term";
		try {
			Class termClass=Class.forName(termClassName);
			ConditionalTerm termInstance;
			try {
				termInstance = (ConditionalTerm)termClass.newInstance();
				registTerm(term, termInstance);
				Logger.debug("loaded term: "+term);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("failed to init term instance:"+termClassName,e);
			} catch (InstantiationException e) {
				throw new RuntimeException("failed to init term instance:"+termClassName,e);
			}
		} catch (ClassNotFoundException ignore) {
			Logger.debug("not a term: "+term);
		}
	}

}
