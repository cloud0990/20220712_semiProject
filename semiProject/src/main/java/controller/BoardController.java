package controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dao.BoardDao;
import vo.BoardVo;

@Controller
@RequestMapping("/board/")
public class BoardController {
	
	//��������ó�� ����Ǿ�������, �ش� �޼ҵ尡 ȣ�� �� ������ DS�� �־���
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
	@RequestMapping("list.do")
	public String list(Model model) {
		
		//���ǿ� ����Ǿ��ִ� show ���� ����
		session.removeAttribute("show");
		
		List<BoardVo> list = boardDao.selectList();
		
		model.addAttribute("list", list);
		
		return "board/board_list";
	}
	
	// �Խñ� �󼼺���
	@RequestMapping("view.do")
	public String view(int b_idx, Model model) {
		
		//b_idx�� �ش��ϴ� �Խù� ���� ������
		BoardVo vo = boardDao.selectOne(b_idx);
		
		//�ش�Խù��� �� ������ ��ȸ�� ����
		if(session.getAttribute("show")==null) {
			
			//�ش� �������� ȣ��� ������ ��ȸ�� ���� -> ������ : ���� ����ڰ� ��� ���ΰ�ħ�ص� ��ȸ���� ����Ǳ⶧���� session ����
			int res = boardDao.update_readhit(b_idx);
			
			//show������ ���ǿ� �ִ´�.
			session.setAttribute("show", true);
			
		}// end : if
		
		//��������� DS�� request binding ����
		model.addAttribute("vo", vo);
		
		return "board/board_view";
	}
	
	// �� �� ���� �� ����
	@RequestMapping("insert_form.do")
	public String insert_form() {
		
		return "board/board_insert_form";
	}
	
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
	
	// �Խñ� ����
	@RequestMapping("delete.do")
	public String delete(int b_idx) {
		
		int res = boardDao.delete(b_idx);
		
		return "redirect:list.do";
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
	
	// ������ ����
	@RequestMapping("modify_form.do") 
	public String modify_form(int b_idx, Model model) {
		
		BoardVo vo = boardDao.selectOne(b_idx);
		
		model.addAttribute("vo", vo);
		
		return "board/board_modify_form";
	}
	
	// �����ϱ�
	@RequestMapping("modify.do")
	public String modify(BoardVo vo) {
		
		int res = boardDao.update(vo);
		
		return "redirect:list.do";
	}
	
	
	
}