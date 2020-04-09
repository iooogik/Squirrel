package iooojik.app.klass.models.userData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("oauth_uid")
    @Expose
    private Object oauthUid;
    @SerializedName("oauth_provider")
    @Expose
    private Object oauthProvider;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("banned")
    @Expose
    private String banned;
    @SerializedName("last_login")
    @Expose
    private String lastLogin;
    @SerializedName("last_activity")
    @Expose
    private String lastActivity;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    @SerializedName("forgot_exp")
    @Expose
    private Object forgotExp;
    @SerializedName("remember_time")
    @Expose
    private Object rememberTime;
    @SerializedName("remember_exp")
    @Expose
    private Object rememberExp;
    @SerializedName("verification_code")
    @Expose
    private Object verificationCode;
    @SerializedName("top_secret")
    @Expose
    private Object topSecret;
    @SerializedName("ip_address")
    @Expose
    private String ipAddress;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getOauthUid() {
        return oauthUid;
    }

    public void setOauthUid(Object oauthUid) {
        this.oauthUid = oauthUid;
    }

    public Object getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(Object oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBanned() {
        return banned;
    }

    public void setBanned(String banned) {
        this.banned = banned;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Object getForgotExp() {
        return forgotExp;
    }

    public void setForgotExp(Object forgotExp) {
        this.forgotExp = forgotExp;
    }

    public Object getRememberTime() {
        return rememberTime;
    }

    public void setRememberTime(Object rememberTime) {
        this.rememberTime = rememberTime;
    }

    public Object getRememberExp() {
        return rememberExp;
    }

    public void setRememberExp(Object rememberExp) {
        this.rememberExp = rememberExp;
    }

    public Object getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(Object verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Object getTopSecret() {
        return topSecret;
    }

    public void setTopSecret(Object topSecret) {
        this.topSecret = topSecret;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
