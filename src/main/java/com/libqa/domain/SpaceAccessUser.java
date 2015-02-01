package com.libqa.domain;

import com.libqa.application.enums.SpaceLayoutType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yion on 2015. 2. 1..
 */

@Data
@Entity
@Table(name = "space_access_user")
@EqualsAndHashCode
public class SpaceAccessUser {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer spaceAccessUserId;

    @ManyToOne
    @JoinColumn(name="spaceId", referencedColumnName="spaceId", nullable=false)
    private Space space;

    @Column(nullable = false)
    private Integer userId;

    @Column(columnDefinition="TINYINT(1) DEFAULT 0")
    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    private Date insertDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;


}