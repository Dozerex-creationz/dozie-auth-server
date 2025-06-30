package org.dozie.auth.model.entity;

import org.dozie.common.model.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="tb_user_roles")
public class UserRole extends BaseEntity {

    @ManyToOne(optional = false)
    public User user;

    @ManyToOne(optional = false)
    public Role role;
}