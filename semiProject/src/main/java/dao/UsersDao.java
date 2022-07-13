package dao;

import java.util.List;

import vo.UsersVo;

public interface UsersDao {
		
//��ü��ȸ (u_grade=�����ڸ� ����)
	public List<UsersVo> selectList();
	
//ȸ������ ����
	public UsersVo selectOne(int u_idx); //u_idx(PK)�� �ش�Ǵ� ��ü 1�� ���ϱ�

//�α���
	public UsersVo selectOne(String u_id); //u_id(unique)�� �ش�Ǵ� ��ü 1�� ���ϱ�
	
//DML	
	public int insert(UsersVo vo); //����
	public int update(UsersVo vo); //����
	public int delete(int u_idx);  //����
	
}