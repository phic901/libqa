package com.libqa.web.controller;

import com.libqa.application.framework.ResponseData;
import com.libqa.application.util.LoggedUserManager;
import com.libqa.web.domain.FeedFile;
import com.libqa.web.domain.FeedReply;
import com.libqa.web.domain.FeedThread;
import com.libqa.web.domain.User;
import com.libqa.web.service.feed.FeedFileService;
import com.libqa.web.service.feed.FeedReplyService;
import com.libqa.web.service.feed.FeedThreadService;
import com.libqa.web.view.feed.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.libqa.application.enums.StatusCode.NEED_LOGIN;
import static com.libqa.application.enums.StatusCode.NOT_MATCH_USER;
import static com.libqa.application.framework.ResponseData.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@RequestMapping("/feed")
public class FeedController {
    @Autowired
    private LoggedUserManager loggedUserManager;
    @Autowired
    private FeedThreadService feedThreadService;
    @Autowired
    private FeedReplyService feedReplyService;
    @Autowired
    private FeedFileService feedFileService;
    @Autowired
    private DisplayFeedBuilder displayFeedBuilder;
    @Autowired
    private DisplayFeedReplyBuilder displayFeedReplyBuilder;
    @Autowired
    private DisplayFeedActionBuilder displayFeedActionBuilder;

    @RequestMapping(method = GET)
    public ModelAndView main(ModelAndView mav) {
        User viewer = loggedUserManager.getUser();
        List<FeedThread> feedThreads = feedThreadService.searchRecentlyFeedThreads();

        mav.addObject("loggedUser", viewer);
        mav.addObject("data", displayFeedBuilder.build(feedThreads, viewer));
        mav.setViewName("feed/main");
        return mav;
    }

    @RequestMapping(value = "recentlyList", method = GET)
    public ResponseData<DisplayFeed> recentlyList() {
        User viewer = loggedUserManager.getUser();
        List<FeedThread> feedThreads = feedThreadService.searchRecentlyFeedThreads();
        return createSuccessResult(displayFeedBuilder.build(feedThreads, viewer));
    }

    @RequestMapping(value = "list", method = GET)
    public ResponseData<DisplayFeed> list(@RequestParam(required = false) Integer lastId) {
        User viewer = loggedUserManager.getUser();
        List<FeedThread> feedThreads = feedThreadService.searchRecentlyFeedThreadsLessThanLastId(lastId);
        return createSuccessResult(displayFeedBuilder.build(feedThreads, viewer));
    }

    @RequestMapping(value = "{feedThreadId}", method = GET)
    public ModelAndView view(@PathVariable Integer feedThreadId, ModelAndView mav) {
        User viewer = loggedUserManager.getUser();
        FeedThread feedThread = feedThreadService.getByFeedThreadId(feedThreadId);

        mav.addObject("displayFeed", displayFeedBuilder.build(feedThread, viewer));
        mav.setViewName("feed/view");
        return mav;
    }

    @RequestMapping(value = "myList", method = GET)
    public ResponseData<DisplayFeed> myList(@RequestParam(required = false) Integer lastId) {
        User viewer = loggedUserManager.getUser();
        List<FeedThread> feedThreads = feedThreadService.searchRecentlyFeedThreadsByUserLessThanLastId(viewer, lastId);
        return createSuccessResult(displayFeedBuilder.build(feedThreads, viewer));
    }

    @RequestMapping(value = "save", method = POST)
    public ResponseData<FeedThread> save(FeedThread feedThread) {
        User viewer = loggedUserManager.getUser();
        if (viewer.isGuest()) {
            return createResult(NEED_LOGIN);
        }

        try {
            feedThreadService.create(feedThread, viewer);
            return createSuccessResult(feedThread);
        } catch (Exception e) {
            log.error("save feedThread error.", e);
            return createFailResult();
        }
    }

    @RequestMapping(value = "/modify", method = POST)
    public ResponseData<FeedThread> modify(FeedThread requestFeedThread) {
        User viewer = loggedUserManager.getUser();
        try {
            log.debug("requestFeedThread : {}", requestFeedThread);
            FeedThread originFeedThread = feedThreadService.getByFeedThreadId(requestFeedThread.getFeedThreadId());
            if (viewer.isNotMatchUser(originFeedThread.getUserId())) {
                return createResult(NOT_MATCH_USER);
            }

            FeedThread savedFeedThread = feedThreadService.modify(originFeedThread, requestFeedThread, viewer);
            return createSuccessResult(savedFeedThread);
        } catch (Exception e) {
            log.error("delete feedThread error.", e);
            return createFailResult();
        }
    }

    @RequestMapping(value = "{feedThreadId}/delete", method = POST)
    public ResponseData<Integer> delete(@PathVariable Integer feedThreadId) {
        User viewer = loggedUserManager.getUser();
        try {
            FeedThread originFeedThread = feedThreadService.getByFeedThreadId(feedThreadId);
            if (viewer.isNotMatchUser(originFeedThread.getUserId())) {
                return createResult(NOT_MATCH_USER);
            }

            feedThreadService.delete(originFeedThread);
            return createSuccessResult(feedThreadId);
        } catch (Exception e) {
            log.error("delete feedThread error.", e);
            return createFailResult();
        }
    }

    @RequestMapping(value = "{feedThreadId}/like", method = POST)
    public ResponseData<DisplayFeedAction> likeFeed(@PathVariable Integer feedThreadId) {
        User viewer = loggedUserManager.getUser();
        if (viewer.isGuest()) {
            return createResult(NEED_LOGIN);
        }

        try {
            FeedThread feedThread = feedThreadService.like(feedThreadId, viewer);
            DisplayFeedAction displayFeedAction = displayFeedActionBuilder.buildLike(feedThread, viewer);
            return createSuccessResult(displayFeedAction);
        } catch (Exception e) {
            log.error("like feedThread error.", e);
            return createFailResult();
        }
    }

    @RequestMapping(value = "{feedThreadId}/claim", method = POST)
    public ResponseData<DisplayFeedAction> claimFeed(@PathVariable Integer feedThreadId) {
        User viewer = loggedUserManager.getUser();
        if (viewer.isGuest()) {
            return createResult(NEED_LOGIN);
        }

        try {
            FeedThread feedThread = feedThreadService.claim(feedThreadId, viewer);
            DisplayFeedAction displayFeedAction = displayFeedActionBuilder.buildClaim(feedThread, viewer);
            return createSuccessResult(displayFeedAction);
        } catch (Exception e) {
            log.error("claim feedThread error.", e);
            return createFailResult();
        }
    }

    @RequestMapping(value = "reply/save", method = POST)
    public ResponseData<DisplayFeedReply> saveReply(FeedReply feedReply) {
        User viewer = loggedUserManager.getUser();
        if (viewer.isGuest()) {
            return createResult(NEED_LOGIN);
        }

        try {
            feedReplyService.create(feedReply, viewer);
            return createSuccessResult(displayFeedReplyBuilder.build(feedReply, viewer));
        } catch (Exception e) {
            log.error("save reply error.", e);
            return createFailResult();
        }
    }

    @RequestMapping(value = "reply/{feedReplyId}/delete", method = POST)
    public ResponseData<Integer> deleteReply(@PathVariable Integer feedReplyId) {
        User viewer = loggedUserManager.getUser();

        try {
            FeedReply originFeedReply = feedReplyService.getByFeedReplyId(feedReplyId);
            if (viewer.isNotMatchUser(originFeedReply.getUserId())) {
                return createResult(NOT_MATCH_USER);
            }

            feedReplyService.delete(feedReplyId);
            return createSuccessResult(feedReplyId);
        } catch (Exception e) {
            log.error("delete reply error.", e);
            return createFailResult(feedReplyId);
        }
    }

    @RequestMapping(value = "reply/{feedReplyId}/like", method = POST)
    public ResponseData<DisplayFeedAction> likeReply(@PathVariable Integer feedReplyId) {
        User viewer = loggedUserManager.getUser();
        if (viewer.isGuest()) {
            return createResult(NEED_LOGIN);
        }

        try {
            FeedReply feedReply = feedReplyService.like(feedReplyId, viewer);
            DisplayFeedAction displayFeedAction = displayFeedActionBuilder.buildLike(feedReply, viewer);
            return createSuccessResult(displayFeedAction);
        } catch (Exception e) {
            log.error("like feedReply error.", e);
            return createFailResult();
        }
    }

    @RequestMapping(value = "reply/{feedReplyId}/claim", method = POST)
    public ResponseData<DisplayFeedAction> claimReply(@PathVariable Integer feedReplyId) {
        User viewer = loggedUserManager.getUser();
        if (viewer.isGuest()) {
            return createResult(NEED_LOGIN);
        }

        try {
            FeedReply feedReply = feedReplyService.claim(feedReplyId, viewer);
            DisplayFeedAction displayFeedAction = displayFeedActionBuilder.buildClaim(feedReply, viewer);
            return createSuccessResult(displayFeedAction);
        } catch (Exception e) {
            log.error("claim feedReply error.", e);
            return createFailResult();
        }
    }

    @RequestMapping(value = "/file/{feedFileId}/delete", method = POST)
    public ResponseData<Boolean> deleteFile(@PathVariable Integer feedFileId) {
        User viewer = loggedUserManager.getUser();

        try {
            FeedFile feedFile = feedFileService.getByFeedFileId(feedFileId);
            if (viewer.isNotMatchUser(feedFile.getUserId())) {
                return createResult(NOT_MATCH_USER);
            }

            feedFileService.delete(feedFile);
            return createSuccessResult(true);
        } catch (Exception e) {
            log.error("delete reply error.", e);
            return createFailResult(false);
        }
    }
}
