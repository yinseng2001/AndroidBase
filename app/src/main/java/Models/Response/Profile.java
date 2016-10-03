package Models.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by yinseng on 10/2/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {
    @JsonProperty("full_name")
    private String full_name;
    @JsonProperty("user")
    private String user;

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
