package com.libqa.web.domain;

import com.libqa.application.enums.FeedActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@ToString
@EqualsAndHashCode(of = "feedActionId")
@Table(indexes = {
        @Index(columnList = "isCanceled"),
        @Index(columnList = "feedActionType"),
        @Index(columnList = "feedActorId,userId")
})
public class FeedAction {
    private static FeedAction NOT_YET = new FeedAction();

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer feedActionId;

    @Column(nullable = false)
    private Integer feedActorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FeedActionType feedActionType;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isCanceled;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date insertDate;

    @Temporal(TemporalType.DATE)
    private Date updateDate;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 40)
    private String userNick;

    @Column(nullable = false)
    private Integer insertUserId;

    @Column
    private Integer updateUserId;

    public boolean isNotCanceled() {
        return !this.isCanceled;
    }

    public void cancel() {
        this.setCanceled(true);
        this.setUpdateDate(new Date());
    }

    public static FeedAction notYet() {
        return NOT_YET;
    }

    public boolean isNotYet() {
        return this == NOT_YET;
    }

    public boolean isActed() {
        return !isNotYet();
    }

}
