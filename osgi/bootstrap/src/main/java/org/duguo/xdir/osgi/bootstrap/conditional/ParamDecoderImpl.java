package org.duguo.xdir.osgi.bootstrap.conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.duguo.xdir.osgi.bootstrap.conf.PropertiesUtils;

/** 
 * http://en.wikipedia.org/wiki/ASCII
 * http://en.wikipedia.org/wiki/Portable_character_set
 	U+0020 	SPACE
! 	U+0021 	EXCLAMATION MARK
" 	U+0022 	QUOTATION MARK
# 	U+0023 	NUMBER SIGN
$ 	U+0024 	DOLLAR SIGN
% 	U+0025 	PERCENT SIGN
& 	U+0026 	AMPERSAND
' 	U+0027 	APOSTROPHE
( 	U+0028 	LEFT PARENTHESIS
) 	U+0029 	RIGHT PARENTHESIS
* 	U+002A 	ASTERISK
+ 	U+002B 	PLUS SIGN
, 	U+002C 	COMMA
- 	U+002D 	HYPHEN-MINUS
/ 	U+002F 	SOLIDUS
: 	U+003A 	COLON
; 	U+003B 	SEMICOLON
< 	U+003C 	LESS-THAN SIGN
= 	U+003D 	EQUALS SIGN
> 	U+003E 	GREATER-THAN SIGN
? 	U+003F 	QUESTION MARK
@ 	U+0040 	COMMERCIAL AT
[ 	U+005B 	LEFT SQUARE BRACKET
\ 	U+005C 	REVERSE SOLIDUS
] 	U+005D 	RIGHT SQUARE BRACKET
^ 	U+005E 	CIRCUMFLEX ACCENT
_ 	U+005F 	LOW LINE
` 	U+0060 	GRAVE ACCENT
{ 	U+007B 	LEFT CURLY BRACKET
| 	U+007C 	VERTICAL LINE
} 	U+007D 	RIGHT CURLY BRACKET
~ 	U+007E 	TILDE
 * 
 * @author mrduguo
 *
 */

public class ParamDecoderImpl implements ParamDecoder {

	public Pattern unicodePattern = Pattern.compile("-u([0-9A-Fa-f]{4})-");
	
	public String decode(String rawString) {
		if(rawString.indexOf('-')<0){
			return rawString;
		}else{
			return decodeWithUnicodePattern(rawString);
		}
	}

	protected String decodeWithUnicodePattern(String rawString) {
		Matcher matcher = unicodePattern.matcher(rawString);
		StringBuffer decodedString=new StringBuffer();
		int startPosition=0;
		while(matcher.find()) {
			decodedString.append(rawString.substring(startPosition, matcher.start()));
			startPosition=matcher.end();
			decodedString.append(Character.toString((char)Integer.parseInt(matcher.group( 1), 16)));
		}
		if(startPosition<rawString.length()){
			decodedString.append(rawString.substring(startPosition));
		}
		if(decodedString.length()!=rawString.length()){
			return resolvePlaceHolders(decodedString.toString());
		}else{
			return decodedString.toString();
		}
	}

	protected String resolvePlaceHolders(String rawString) {
		if(rawString.indexOf('{')<0){
			return rawString;
		}else{
			return PropertiesUtils.applyPlaceHoldersFromSystemProperties(rawString);
			
		}
	}

}
