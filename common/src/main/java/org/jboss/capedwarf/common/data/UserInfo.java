package org.jboss.capedwarf.common.data;

import org.jboss.capedwarf.common.serialization.JSONAware;
import org.jboss.capedwarf.common.serialization.JSONUtils;
import org.jboss.capedwarf.validation.api.Constants;
import org.jboss.capedwarf.validation.api.Email;
import org.jboss.capedwarf.validation.api.MessageTemplateKey;
import org.json.JSONException;
import org.json.JSONObject;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * User info
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class UserInfo implements JSONAware, Serializable {
    private static final long serialVersionUID = 1l;

    private String username;
    private String password;
    private String email;
    private Status status;
    private String recovery;

    @Deprecated
    public UserInfo() {
        // deserialization only
    }

    public UserInfo(String username, String password) {
        if (username == null)
            throw new IllegalArgumentException("Null username");
        if (password == null)
            throw new IllegalArgumentException("Null password");

        this.username = username;
        this.password = password;
    }

    public void writeJSONObject(JSONObject json) throws JSONException {
        json.put("username", username);
        json.put("password", password);
        json.putOpt("email", email);
        JSONUtils.writeEnum(json, "status", status);
        json.putOpt("recovery", recovery);
    }

    public void readJSONObject(JSONObject json) throws JSONException {
        username = json.getString("username");
        password = json.getString("password");
        email = json.optString("email");
        status = JSONUtils.readEnum(json, "status", null, Status.class);
        recovery = json.optString("recovery");
    }

    @Size(min = 3, max = 20)
    @Pattern(regexp = Constants.USERNAME_REGEXP)
    @MessageTemplateKey("{capedwarf.login.username}")
    public String getUsername() {
        return username;
    }

    @Size(min = 6, max = 30)
    @Pattern(regexp = Constants.PASSWORD_REGEXP)
    @MessageTemplateKey("{capedwarf.login.password}")
    public String getPassword() {
        return password;
    }

    @Email
    @MessageTemplateKey("{capedwarf.login.email}")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @MessageTemplateKey("{capedwarf.login.recovery}")
    public String getRecovery() {
        return recovery;
    }

    public void setRecovery(String recovery) {
        this.recovery = recovery;
    }
}