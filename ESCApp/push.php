 <?
    
 
    header('content-type: text/html; charset=utf-8'); 
 
 
    // �����ͺ��̽� ���� ���ڿ�. (db��ġ, ���� �̸�, ��й�ȣ)
    $connect=mysql_connect( "http://esc.kau.ac.kr/phpmyadmin/index.php", "root", "autoset") or  
        die( "SQL server�� ������ �� �����ϴ�.");
 
    
    mysql_query("SET NAMES UTF8");
   // �����ͺ��̽� ����
   mysql_select_db("push",$connect);
 
 
   // ���� ����
   session_start();
 
 
 
   $id = $_REQUEST[u_id];
 
   $sql = "insert into push(regId) select '$id' from dual where not exists(select * from push where regId='$id')";
 
   $result = mysql_query($sql);
 
   if(!$result)
            die("mysql query error");
 
?>