package com.leadon.apigw.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leadon.apigw.model.AbstractModel;
import lombok.*;
import lombok.experimental.Accessors;


import javax.persistence.*;
import java.time.LocalDateTime;

@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "D_USER")
public class UserEntity extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "D_USER_SEQ")
    @SequenceGenerator(sequenceName = "D_USER_SEQUENCE", name = "D_USER_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "USERNAME", length = 50, nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    @Column(name = "EXP_DT")
    private LocalDateTime expDt;

    @Column(name = "STATUS", length = 1)
    private String status;

    @Column(name = "IS_RESET_PASSWORD")
    private boolean isResetPassword;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private RoleEntity role ;

}