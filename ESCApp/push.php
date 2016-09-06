 <?
    
 
    header('content-type: text/html; charset=utf-8'); 
 
 
    // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호)
    $connect=mysql_connect( "http://esc.kau.ac.kr/phpmyadmin/index.php", "root", "autoset") or  
        die( "SQL server에 연결할 수 없습니다.");
 
    
    mysql_query("SET NAMES UTF8");
   // 데이터베이스 선택
   mysql_select_db("push",$connect);
 
 
   // 세션 시작
   session_start();
 
 
 
   $id = $_REQUEST[u_id];
 
   $sql = "insert into push(regId) select '$id' from dual where not exists(select * from push where regId='$id')";
 
   $result = mysql_query($sql);
 
   if(!$result)
            die("mysql query error");
 
?>