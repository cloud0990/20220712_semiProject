package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import common.MyConstant;
import dao.BoardDao;
import util.Paging;
import vo.BoardVo;

@Controller
@RequestMapping("/board/")
public class BoardController {
	
	// ��������ó�� ����Ǿ�������, �ش� �޼ҵ尡 ȣ�� �� ������ DS�� �־���
	@Autowired
	HttpSession        session; 
	@Autowired
	HttpServletRequest request;
	@Autowired
	HttpServletRequest response;
	
	// �������̽�(��뼳��) ��ü�� �����Ѵ�.
	// -> Override�� �������� �����ǵ� �޼ҵ尡 ȣ��Ǳ⶧����, Impl Ŭ���� ������ �����ǵ� �޼ҵ尡 ȣ��ȴ�.
	BoardDao boardDao;
	public void setBoardDao(BoardDao boardDao) {
		this.boardDao = boardDao;
	}

	// ��ü��ȸ
	// 1. /board/list.do
	// 3. /board/list.do?page=1
	@RequestMapping("list.do")       //�Ķ���ʹ� ������ ���ڿ��� ������ ������, �ʱⰪ�� ������ String���̴�.
	public String list(@RequestParam(value="page", required=false, defaultValue="1") int nowPage,
					   @RequestParam(value="search", required=false, defaultValue="all") String search,
					   @RequestParam(value="search_text", required=false) String search_text,
	      			   Model model) {
		
		// ���ǿ� ����Ǿ��ִ� show ���� ����
		session.removeAttribute("show");
		
		// ���� �������� �̿��ؼ�, �Խù��� start / end ���
		int start = (nowPage-1) * MyConstant.Board.BLOCK_LIST + 1;
		int end   = start + MyConstant.Board.BLOCK_LIST - 1;
		
		Map map = new HashMap();
		map.put("start", start);
		map.put("end", end);
		
		//��ü�˻��� �ƴϸ�
		if(!search.equals("all")) {
			if(search.equals("subject_content_name")) {
				map.put("subject", search_text);
				map.put("content", search_text);
				map.put("name", search_text);
			}else if(search.equals("subject")) {
				map.put("subject", search_text);
			}else if(search.equals("content")) {
				map.put("content", search_text);
			}else if(search.equals("name")) {
				map.put("name", search_text);
			}
		}
		
		// ��ü(���ǿ� �´�) �Խù� �� ���ϱ�
		int rowTotal = boardDao.selectRowTotal(map);
		
		// �˻��޴������� ����¡�޴��� ����
		String search_filter = String.format("search=%s&search_text=%s", search, search_text);
		
		// ����¡ �޴� �����               pageUrl ���������� ��ü�Խù�
		String pageMenu = Paging.getPaging("list.do", search_filter, nowPage, rowTotal, 
				                             MyConstant.Board.BLOCK_LIST, //�� ȭ�鿡 ������ �Խù� ��
				                             MyConstant.Board.BLOCK_PAGE  //�� ȭ�鿡 ������ ������ ��
				                             );
		
		List<BoardVo> list = boardDao.selectList(map);
		
		// DS�� request binding �� �� �ֵ���, model�� ���� (��������� request binding)
		model.addAttribute("list", list);
		model.addAttribute("pageMenu", pageMenu);
		model.addAttribute("page", nowPage);
		
		return "board/board_list";
	}
	
	// �Խñ� �󼼺���
	@RequestMapping("view.do")
	public String view(int b_idx, Model model) {
		
		BoardVo vo = boardDao.selectOne(b_idx);
		
		//�ش�Խù��� �� ������ ��ȸ�� ����
		if(session.getAttribute("show")==null) {
			
			//�ش� �������� ȣ��� ������ ��ȸ�� ���� -> ������ : ���� ����ڰ� ��� ���ΰ�ħ�ص� ��ȸ���� ����Ǳ⶧���� session ����
			int res = boardDao.update_readhit(b_idx);
			
			session.setAttribute("show", true);
		}// end : if
		
		model.addAttribute("vo", vo);
		
		return "board/board_view";
	}
	
	// �� �� ���� �� ����
	@RequestMapping("insert_form.do")
	public String insert_form() {
		
		return "board/board_insert_form";
	}
	
	// �� �� ���� insert
	@RequestMapping("insert.do")
	public String insert(BoardVo vo, Model model) {
		
		//������ ����Ǿ� �α׾ƿ����� ��� �۾��� ����
		if(session.getAttribute("user") == null) {
				
			model.addAttribute("reason", "session_timeout");

			return "redirect:../users/login_form.do";
		}
		
		
		String b_ip = request.getRemoteAddr();	
		
		vo.setB_ip(b_ip);
		
		int res = boardDao.insert(vo);
		
		return "redirect:list.do";
	}
	
	// �Խñ� ���� : /board/delete.do?b_idx=16&page=3&search=name&search_text=�浿
	@RequestMapping("delete.do")
	public String delete(int b_idx,
						 int page,
						 @RequestParam(value="search", required=false, defaultValue="all") String search,
						 @RequestParam(value="search_text", required=false) String search_text,
						 Model model) {
		
		int res = boardDao.delete(b_idx);
		
		model.addAttribute("page", page);
		model.addAttribute("search", search);
		model.addAttribute("search_text", search_text);
		
		return "redirect:list.do"; // list.do?page=3&search=name&search_text=�浿
	}
	
	
	// ��۾��� �� ����
	@RequestMapping("reply_form.do")
	public String reply_form() {
		
		return "board/board_reply_form";
	}
	
	// ��۾���
	@RequestMapping("reply.do")
	public String reply(BoardVo vo) { 	
		
		String b_ip = request.getRemoteAddr();
		vo.setB_ip(b_ip);
		
		//���ر� ���� ������
		BoardVo baseVo = boardDao.selectOne(vo.getB_idx());
		
		//���رۺ��� b_step�� ū �Խù��� b_step + 1
		int res = boardDao.update_step(baseVo);
		
		//���� ����� ���� ���� : �׷�۹�ȣ �״�� �ޱ� (���� �׷쿡 �ֱ� ����)
		vo.setB_ref(baseVo.getB_ref());
		vo.setB_step(baseVo.getB_step()+1);  //������ b_step����  +1 ���� (�������� �ٷ� �ؿ� ��ġ)
		vo.setB_depth(baseVo.getB_depth()+1);//������ b_depth���� +1 ���� (������ �ٷ� �ؿ� ��ġ) 
		
		//��� ���
		res = boardDao.reply(vo);
		
		return "redirect:list.do";
	}
	
	// ������ ���� : /board/modify_form.do?b_idx=16&page=3&search=name&search_text=�浿
	@RequestMapping("modify_form.do") 
	public String modify_form(int b_idx, Model model) {
		
		BoardVo vo = boardDao.selectOne(b_idx);
		
		model.addAttribute("vo", vo);
		
		return "board/board_modify_form";
	}
	
	// �����ϱ�
	@RequestMapping("modify.do")
	public String modify(BoardVo vo,
						 int page,
						 @RequestParam(value="search", required=false, defaultValue="all") String search,
						 @RequestParam(value="search_text", required=false) String search_text,
						 Model model) {
		
		// ���� ����� ���
		if(session.getAttribute("user")==null) {
			model.addAttribute("reason", "session_timeout");
			return "redirect:../users/users_login_form.do";
		}
		
		String b_ip = request.getRemoteAddr();
		vo.setB_ip(b_ip);
		
		int res = boardDao.update(vo);
		
		// redirect : view.do?b_idx=16&page=3&search=name&search_text=�浿
		model.addAttribute("b_idx", vo.getB_idx());
		model.addAttribute("page", page);
		model.addAttribute("search", search);
		model.addAttribute("search_text", search_text);
		
		return "redirect:view.do";
	}
	
	
	
}