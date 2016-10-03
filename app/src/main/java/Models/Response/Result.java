package Models.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by yinseng on 10/2/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {
    @JsonProperty("access_token")
    private String access_token;
    @JsonProperty("_id")
    private String _id;
    @JsonProperty("api_access")
    private String api_access;
    @JsonProperty("email")
    private String email;
    @JsonProperty("profile")
    private Profile profile;


    public String getAccess_token() {
        return access_token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
