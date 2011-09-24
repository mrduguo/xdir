package org.duguo.xdir.security.api.authentication;

import org.duguo.xdir.http.json.Json;

public interface UserInfoRetriver {
	public Json retriveUserInfo(Object... params);
}
