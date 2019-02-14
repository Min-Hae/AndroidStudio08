//////////////기상청에서 정보 추출하는 앱////////
package kr.co.company.android;
//여러 import들
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//클래스 정의
@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity {
    TextView textView;
    Document doc = null;
    LinearLayout layout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  //activity_main과 연결
        textView = (TextView) findViewById(R.id.textview);
    }
    //버튼 클릭 이벤트 함수
    public void onClick(View view) {
        GetXMLTask task = new GetXMLTask(this);
        task.execute("http://www.kma.go.kr/wid/queryDFS.jsp?gridx=61&gridy=125"); //기상청으로 들어간다.
    }
    //AsyncTask를 확장해 내부 클래스를 만든다.
    @SuppressLint("NewApi")
    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        private Activity context;

        public GetXMLTask(Activity context) {this.context = context; }
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            //예외처리 함수
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db;

                db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                //에러일때 토스트 메시지 출력
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            String s = "";
            NodeList nodeList = doc.getElementsByTagName("data");
            //날씨 정보 출력
            for (int i = 0; i < nodeList.getLength(); i++) {
                s += "" + i + ": 날씨 정보: ";
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;
                NodeList nameList = fstElmnt.getElementsByTagName("temp");
                //온도 정보 추출
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                //문장으로 만들기
                s += "온도 = " + ((Node) nameList.item(0)).getNodeValue() + " ,";

                NodeList websiteList = fstElmnt.getElementsByTagName("wfKor");
                //날씨 정보 추출
                Element websiteElement = (Element) websiteList.item(0);
                websiteList = websiteElement.getChildNodes();
                //문장으로 만들기
                s += "날씨 = " + ((Node) websiteList.item(0)).getNodeValue() + "\n";
            }
            textView.setText(s);
        }
    }
}

