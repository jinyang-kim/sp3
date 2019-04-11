package com.sp.pscore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.common.MyUtil;

@Controller("pscore.scoreController")
public class ScoreController {
	@Autowired
	private ScoreService scoreService;
	@Autowired
	private MyUtil myUtil;
	
	@RequestMapping(value="/pscore/list")
	public String list(@RequestParam(value="page", defaultValue="1") int current_page,
			HttpServletRequest req,
			Model model) throws Exception {
		
		int total_page = 0;
		int dataCount = 0;
		int rows = 10;
		
		dataCount = scoreService.dataCount();
		
		if(dataCount != 0) {
			total_page = myUtil.pageCount(rows, dataCount);
		}
		
		if(current_page > total_page) {
			current_page = total_page;
		}
		
		int start = (current_page - 1) * rows + 1;
		int end = current_page * rows;
		
		Map<String, Object> map = new HashMap<>();
		map.put("start", start);
		map.put("end", end);
		
		List<Score> list = scoreService.listScore(map);
		
		String cp = req.getContextPath();
		String listUrl = cp + "/pscore/list";
		String articleUrl = cp + "/pscore/article?page" + current_page;
		
		String paging = myUtil.paging(current_page, total_page, listUrl);
		
		model.addAttribute("list", list);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);
		model.addAttribute("articleUrl", articleUrl);
		
		return "pscore/list";
	}
	
	@RequestMapping(value="/pscore/write", method = RequestMethod.GET)
	public String writeForm(Model model) throws Exception {
		
		model.addAttribute("mode", "insert");
		
		return "pscore/write";
	}
	
	@RequestMapping(value="/pscore/write", method = RequestMethod.POST)
	public String writeSubmit(Score dto, Model model) throws Exception {
		
		int result = scoreService.insertScore(dto);
		
		if(result == 0) {
			model.addAttribute("mode", "insert");
			model.addAttribute("mode", "데이터 등록을 실패했습니다.");
			return "/pscore/write";
		}
		
		return "redirect:/pscore/list";
	}
	
	@RequestMapping(value="/pscore/update", method = RequestMethod.GET)
	public String updateForm(@RequestParam String hak,
			@RequestParam(value="page", defaultValue="1") int current_page,
			Model model) throws Exception {
		
		Score dto = scoreService.readScore(hak);
		if(dto == null) {
			return "redirect:/pscore/list?page=" + current_page;
		}
		
		model.addAttribute("dto", dto);
		model.addAttribute("page", current_page);
		model.addAttribute("mode", "update");
		
		return "pscore/write";
	}
	
	@RequestMapping(value="/pscore/update", method = RequestMethod.POST)
	public String updateSubmit(Score dto,
			@RequestParam(value="page", defaultValue="1") int current_page) throws Exception {
		
		scoreService.updateScore(dto);
		
		return "redirect:/pscore/list?page=" + current_page;
	}
	
	@RequestMapping(value="/pscore/delete")
	public String deleteSubmit(@RequestParam String hak,
			@RequestParam(value="page", defaultValue="1") int current_page) throws Exception {
		
		scoreService.deleteScore(hak);
		
		return "redirect:/pscore/list?page=" + current_page;
	}
}
