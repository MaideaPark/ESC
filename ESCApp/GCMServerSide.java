package project.esc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GCMServerSide {

	public void sendMessage(ArrayList<String> aList) throws IOException {
		String msg = "새 글이 등록되었습니다.";
  		String encoder = URLEncoder.encode(msg,"euc-kr");

		Sender sender = new Sender("954082168178");

		Message message = new Message.Builder().addData("msg", "encoder")
				.build();
		List<String> list = aList;

		MulticastResult multiResult = sender.send(message, list, 5);

		if (multiResult != null) {

			List<Result> resultList = multiResult.getResults();

			for (Result result : resultList) {

				System.out.println(result.getMessageId());

			}

		}

	}

	public static void main(String[] args) throws Exception {

		GCMServerSide s = new GCMServerSide();
		connmysql a = new connmysql();

		ArrayList<String> list = a.testMySql();
		s.sendMessage(list);

	}

}