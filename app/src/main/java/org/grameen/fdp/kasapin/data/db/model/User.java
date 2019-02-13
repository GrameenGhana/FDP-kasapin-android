package org.grameen.fdp.kasapin.data.db.model;


/**
 * Created by AangJnr on 09, December, 2018 @ 11:54 AM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("avatar_type")
    @Expose
    private String avatarType;
    @SerializedName("avatar_location")
    @Expose
    private String avatarLocation;
    @SerializedName("password_changed_at")
    @Expose
    private String passwordChangedAt;
    @SerializedName("active")
    @Expose
    private boolean active;
    @SerializedName("confirmation_code")
    @Expose
    private String confirmationCode;
    @SerializedName("confirmed")
    @Expose
    private boolean confirmed;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("last_login_at")
    @Expose
    private String lastLoginAt;
    @SerializedName("last_login_ip")
    @Expose
    private String lastLoginIp;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("full_name")
    @Expose
    private String fullName;

    /**
     * No args constructor for use in serialization
     *
     */
    public User() {
    }

    /**
     *
     * @param lastName
     * @param lastLoginAt
     * @param passwordChangedAt
     * @param confirmationCode
     * @param updatedAt
     * @param id
     * @param lastLoginIp
     * @param timezone
     * @param confirmed
     * @param avatarType
     * @param email
     * @param createdAt
     * @param deletedAt
     * @param active
     * @param fullName
     * @param uuid
     * @param firstName
     * @param avatarLocation
     */
    public User(int id, String uuid, String firstName, String lastName, String email, String avatarType, String avatarLocation, String passwordChangedAt, boolean active, String confirmationCode, boolean confirmed, String timezone, String lastLoginAt, String lastLoginIp, String createdAt, String updatedAt, String deletedAt, String fullName) {
        super();
        this.id = id;
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarType = avatarType;
        this.avatarLocation = avatarLocation;
        this.passwordChangedAt = passwordChangedAt;
        this.active = active;
        this.confirmationCode = confirmationCode;
        this.confirmed = confirmed;
        this.timezone = timezone;
        this.lastLoginAt = lastLoginAt;
        this.lastLoginIp = lastLoginIp;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.fullName = fullName;
    }


    public User(int id, String uuid, String firstName, String lastName, String email, String passwordChangedAt, boolean active, String confirmationCode, boolean confirmed) {
        super();
        this.id = id;
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordChangedAt = passwordChangedAt;
        this.active = active;
        this.confirmationCode = confirmationCode;
        this.confirmed = confirmed;
        }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User withId(int id) {
        this.id = id;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public User withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(String avatarType) {
        this.avatarType = avatarType;
    }

    public User withAvatarType(String avatarType) {
        this.avatarType = avatarType;
        return this;
    }

    public String getAvatarLocation() {
        return avatarLocation;
    }

    public void setAvatarLocation(String avatarLocation) {
        this.avatarLocation = avatarLocation;
    }

    public User withAvatarLocation(String avatarLocation) {
        this.avatarLocation = avatarLocation;
        return this;
    }

    public String getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(String passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }

    public User withPasswordChangedAt(String passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public User withActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public User withConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
        return this;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public User withConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
        return this;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public User withTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public String getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(String lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public User withLastLoginAt(String lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        return this;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public User withLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public User withDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public User withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

}