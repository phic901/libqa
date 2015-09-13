package com.libqa.web.service;

import com.libqa.application.enums.ActivityTypeEnum;
import com.libqa.web.domain.Activity;
import com.libqa.web.domain.Space;
import com.libqa.web.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author : yion
 * @Date : 2015. 6. 28.
 * @Description :
 */
@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private WikiRepository wikiRepository;

    @Autowired
    private WikiReplyRepository replyRepository;

    @Autowired
    private QaContentRepository qaContentRepository;

    @Autowired
    private QaReplyRepository qaReplyRepository;


    @Override
    public Activity saveActivity(Activity activity, String title) {
        String desc = bindActivityType(activity, title);
        activity.setActivityDesc(desc);

        return activityRepository.save(activity);
    }

    public String bindActivityType(Activity activity, String title) {
        String desc = "";
        switch (activity.getActivityType()) {
            case CREATE_SPACE:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.CREATE_SPACE.name();
                break;

            case UPDATE_SPACE:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.UPDATE_SPACE.name();
                break;

            case ADD_SPACE_FAVORITE:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.ADD_SPACE_FAVORITE.name();
                break;

            case INSERT_WIKI:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.INSERT_WIKI.name();
                break;

            case UPDATE_WIKI:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.UPDATE_WIKI.name();
                break;

            case INSERT_REPLY_WIKI:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.INSERT_REPLY_WIKI.name();
                break;

            case ADD_WIKI_FAVORITE:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.ADD_WIKI_FAVORITE.name();
                break;

            case INSERT_REPLY_QA:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.INSERT_REPLY_QA.name();
                break;

            case ADD_VOTE_YES:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.ADD_VOTE_YES.name();
                break;

            case ADD_VOTE_NO:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.ADD_VOTE_NO.name();
                break;


            case ADD_REPLY_RECOMMEND:
                desc = activity.getUserNick() + "님이 " + title + ActivityTypeEnum.ADD_REPLY_RECOMMEND.name();
                break;
        }
        return desc;
    }

    @Override
    public List<Activity> findBySpaceId(Integer spaceId) {
        return activityRepository.findBySpaceId(spaceId);
    }

    @Override
    public List<Activity> findByWikiId(Integer wikiId) {
        return activityRepository.findByWikiId(wikiId);
    }


}
