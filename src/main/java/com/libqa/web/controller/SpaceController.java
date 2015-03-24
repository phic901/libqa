package com.libqa.web.controller;

import com.libqa.application.enums.KeywordTypeEnum;
import com.libqa.application.enums.SpaceViewEnum;
import com.libqa.application.framework.ResponseData;
import com.libqa.application.util.StringUtil;
import com.libqa.web.domain.Space;
import com.libqa.web.service.KeywordService;
import com.libqa.web.service.SpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

/**
 * Created by yion on 2015. 2. 8..
 */
@Slf4j
@Controller
public class SpaceController {

	@Value("${howling.hello.message}")
	private String message;

	@Autowired
	private SpaceService spaceService;

	@Autowired
	private KeywordService keywordService;


	@RequestMapping("/space/fileUpload")
	public ModelAndView fileUpload(Model model) {
		ModelAndView mav = new ModelAndView("/space/ajaxUpload");
		return mav;
	}

	@RequestMapping("/space")
	public String space() {
		return "redirect:/space/main";
	}

	@RequestMapping("/space/main")
	public ModelAndView spaceMain(Model model) {
		log.info("## /main");
		boolean isDeleted = false;    // 삭제 하지 않은 것
		List<Space> spaceList = spaceService.findAllByCondition(isDeleted);
		log.info("# spaceList.size = {}", spaceList.size());
		ModelAndView mav = new ModelAndView("/space/main");
		mav.addObject("spaceList", spaceList);
		return mav;
	}

	@RequestMapping("/space/form")
	public ModelAndView form(Model model) {
		log.info("# message : {}", message);

		ModelAndView mav = new ModelAndView("/space/form");
		mav.addObject("message", message);

		return mav;
	}


	@RequestMapping(value = "/space/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData<Space> saveSpace(@ModelAttribute Space space) {
		// 여기서 request 에 대한 사용자 정보 조회함 (권한관리에 이미 필요)
		space.setInsertDate(new Date());
		space.setInsertUserId(1);
		Space result = spaceService.saveWithKeyword(space);
		log.debug("#result : [{}]", result);
		return ResponseData.createSuccessResult(result);
	}

	@RequestMapping(value = "/space/{spaceId}", method = RequestMethod.GET)
	public ModelAndView spaceDetail(@PathVariable Integer spaceId) {
		Space space = spaceService.findOne(spaceId);


		String view = "/space/" + StringUtil.lowerCase(space.getLayoutType().name());

		log.info("# view : {}", view);
		ModelAndView mav = new ModelAndView(view);

		mav.addObject("space", space);
		return mav;
	}

}


