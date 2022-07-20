package dao;

import java.util.List;
import java.util.Map;

import vo.BoardVo;

public interface BoardDao {
	
	// ��ü ��ȸ
	List<BoardVo> selectList();
	// �˻� ���� �� ��ȸ
	List<BoardVo> selectList(Map map);
	// b_idx�� �ش��ϴ� Vo 1�� ���ϱ�
	BoardVo selectOne(int b_idx);
	// ��ü �� �� ���ϱ�(����¡ ó��)
	int selectRowTotal();
	// �˻� ���Ǻ� ����¡ ó���ϱ�
	int selectRowTotal(Map map);
	
	int insert(BoardVo vo); // ���� ����
	int reply(BoardVo vo);  // ��� ����
	int delete(int b_idx);
	int update(BoardVo vo); 
	int update_step(BoardVo vo);
	int update_readhit(int b_idx); // ��ȸ�� ����
}