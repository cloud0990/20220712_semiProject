<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script type="text/javascript">
	
	function result(){
	  
	  // �Է¹��� ������ ������
	  var text = $("#text").val().trim();	
	
	  // �ʼ� �迭 ����
	  var cs = ["��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��"];
	  
	  // result�� �߰��ϱ� ���� �� ���ڿ� ����
	  var result = "";
	  
	  for(var i=0; i<text.length; i++) {
	    
	    //�ѱ��� ��� ���ڵ�
	    var code = text.charCodeAt(i)-44032;
	    
	    if(code>-1 && code<11172) {
	    	
	    	result += cs[Math.floor(code/588)];
	    
	    //�ѱ��� �����Ѵٸ� ���� ���� �����Ƿ� �״�� ����
	    } else { 
	    	result += text.charAt(i);
	    }// end : if
	  }// end : for
	  
	  //return result;
	  $("#resChosung").val(result);	
	}// emd : result 
	
</script>
</head>
<body>

�ʼ��� ������ �ؽ�Ʈ�� �Է��ϼ��� : <input id="text">
<br>
<input type="button" value="���" onclick="result();">
<input id="resChosung">

</body>
</html>