package org.azavea.otm.data;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Model {
	protected JSONObject data;
	
	protected long getLongOrDefault(String key, Long defaultValue) throws JSONException {
		if (data.isNull(key)){ 
			return defaultValue;
		} else {
			return data.getLong(key);
		}
	}
	public void setData(JSONObject data) {
		this.data = data;
	}
	
	public JSONObject getData() {
		return data;
	}
}
