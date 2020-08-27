package org.tikim.boot.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.tikim.boot.annotation.ValidationGroups;
import org.tikim.boot.annotation.Xss;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

@Xss
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name="tb_disposable_salt_info")
public class DisposableSalt extends BaseEntity{

    @Column(nullable=false)
    @ApiModelProperty(notes = "고유 id", example = "1", hidden = true)
    protected Long userId;

    @Column(nullable=false)
    @ApiModelProperty(notes = "subject", example = "Identity verification")
    @NotNull(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class, ValidationGroups.UpdateAdmin.class, ValidationGroups.Login.class}, message = "아이디는 비워둘 수 없습니다.")
    @Pattern(regexp = "^[a-z_0-9]{1,12}$", message = "subject 형식이 올바르지 않습니다.")
    private String subject;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(hidden = true)
    private String salt;

    @Override
    public void prePersist() {
        super.prePersist();

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
