package com.leadon.apigw.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leadon.apigw.model.AbstractModel;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Table(name = "S_ROLE")
@Entity
public class RoleEntity extends AbstractModel {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ROLE_NAME" , unique = true , nullable = false , length = 30)
    private String roleName;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    private List<UserEntity> userEntities = new ArrayList<>();


}
