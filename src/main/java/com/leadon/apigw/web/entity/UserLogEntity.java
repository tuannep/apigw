package com.leadon.apigw.web.entity;

import com.leadon.apigw.model.AbstractModel;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "D_USER_LOG")
public class UserLogEntity extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "D_USER_LOG_SEQ")
    @SequenceGenerator(sequenceName = "D_USER_LOG_SEQUENCE", name = "D_USER_LOG_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "ACTION")
    private String action;

    @Column(name = "ACTION_TIME")
    private Date actionTime;

    @Column(name = "IP")
    private String ip;

}
