package org.tikim.boot.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.tikim.boot.annotation.ValidationGroups;
import org.tikim.boot.annotation.Xss;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
@Xss
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name="tb_user_info")
public class User extends BaseEntity{

    @Column(nullable=false)
    @ApiModelProperty(notes = "아이디", example = "tikim")
    @NotNull(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class, ValidationGroups.UpdateAdmin.class, ValidationGroups.Login.class}, message = "아이디는 비워둘 수 없습니다.")
    @Pattern(regexp = "^[a-z_0-9]{1,12}$", message = "아이디 형식이 올바르지 않습니다.")
    private String account;

    @Column(nullable=false)
    @ApiModelProperty(notes = "패스워드", example = "72ab994fa2eb426c051ef59cad617750bfe06d7cf6311285ff79c19c32afd236")
    @NotNull(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.UpdateAdmin.class, ValidationGroups.Login.class}, message = "비밀번호는 비워둘 수 없습니다.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ApiModelProperty(notes = "이름", example = "김통일")
    @NotNull(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class}, message = "이름은 비워둘 수 없습니다.")
    @Column(nullable=false)
    private String name;

    @Pattern(regexp = "^[0-9]{1,12}$", message = "학번 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "학번", example = "2015136042")
    @Column(name = "student_number")
    private String studentNumber;

    @Pattern(regexp = "^[a-z_0-9]{1,12}$", message = "메일 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "학교 메일", example = "xhdtn8070")
    @Column(name = "student_email",unique=true,nullable=false)
    private String studentEmail;

    /*
    학부를 따로 할까 고민중중
    */
    @ApiModelProperty(notes = "학부", example = "컴퓨터공학부")
    @Column
    private String major;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "휴대폰 번호", example = "010-0000-0000")
    @Column(name = "phone_number")
    private String phoneNumber;

    @ApiModelProperty(notes = "프로필 이미지", example = "https://static-sample.tikim.org/images.jpg")
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(hidden = true)
    private String salt;

    @Column
    @ApiModelProperty(notes = "gender", example = "null")
    private Boolean gender;

    @Column(name = "graduates")
    @ApiModelProperty(notes = "graduated", example = "false")
    private Boolean graduated;

    @Column(name = "certifies")
    @ApiModelProperty(notes = "certifies", example = "false")
    private Boolean certifies;

    @Override
    public void prePersist() {
        super.prePersist();
        this.graduated = this.graduated == null ? false : this.graduated;
        this.certifies = this.certifies == null ? false : this.certifies;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Boolean getGraduated() {
        return graduated;
    }

    public void setGraduated(Boolean graduated) {
        this.graduated = graduated;
    }

    public Boolean getCertifies() {
        return certifies;
    }

    public void setCertifies(Boolean certifies) {
        this.certifies = certifies;
    }
}
