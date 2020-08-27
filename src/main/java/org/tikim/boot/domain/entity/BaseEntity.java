package org.tikim.boot.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.tikim.boot.annotation.ValidationGroups;

import java.sql.Date;
import java.sql.Timestamp;

@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class}, message = "키는 입력할 수 없습니다.")
    @NotNull(groups = {ValidationGroups.UpdateAdmin.class, ValidationGroups.Update.class}, message = "키는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "고유 id", example = "1", hidden = true)
    protected Long id;

    @ApiModelProperty(notes = "deletes", example = "false",hidden = true)
    @Column(name = "deletes")
    protected Boolean deletes;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Null(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class}, message = "생성일은 입력할 수 없습니다.")
    @ApiModelProperty(hidden = true)
    @Column(name = "created_at")
    protected Timestamp createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Null(groups = {ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class}, message = "수정일은 입력할 수 없습니다.")
    @ApiModelProperty(hidden = true)
    @Column(name = "updated_at")
    protected Timestamp updatedAt;

    @PrePersist
    public void prePersist(){
        this.deletes = this.deletes == null ? false : this.deletes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDeletes() {
        return deletes;
    }

    public void setDeletes(Boolean deletes) {
        this.deletes = deletes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
