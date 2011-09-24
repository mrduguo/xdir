package org.duguo.xdir.security.api.authentication.facebook;

import org.duguo.xdir.http.json.Json;

public interface FacebookService {
	public Json signAndRequest(String authToken, String method, String... params);
}
