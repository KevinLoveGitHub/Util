package org.lovedev.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.lovedev.util.LogUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.i("{\"eventType\":10044,\"roomid\":\"1533178646194\",\"action\":\"call\",\"clientmac\":\"E064172810243\"," +
                "\"data\":{\"devicemac\":\"E064172810243\",\"devicename\":\"8床\",\"nursename\":\"王璐璐\",\"departname\":\"内一科护士站\"," +
                "\"incall\":\"0\",\"patientgroup\":\"\",\"levelid\":\"1\",\"infusion\":\"-1\",\"age\":\"79岁\",\"roomid\":\"7822\"," +
                "\"starttime\":0,\"departid\":\"\",\"patienttime\":\"2018-01-11\",\"patientid\":\"0000122508\",\"online\":1," +
                "\"type\":\"18\",\"roomname\":\"3病房\",\"bednum\":\"8床\",\"department\":\"\",\"nurseid\":\"24084\"," +
                "\"mac\":\"E064172810243\",\"levelname\":\"一级护理\",\"levelbgcolor\":\"#d7181f\",\"deviceid\":3670,\"doctorid\":\"\"," +
                "\"servicetype\":3,\"doctorname\":\"李敬奇\",\"username\":\"许德福\",\"levelcolor\":\"#ffffff\",\"ip\":\"10.0.1.214_3\"," +
                "\"sex\":\"1\",\"deviceip\":\"10.0.1.214_3\"},\"clientip\":\"10.0.1.214_3\",\"answered\":0,\"stationip\":\"10.0.1.101\"," +
                "\"clienttype\":\"18\",\"stationmac\":\"3CD16E380785\",\"sender\":\"station\",\"holdingtime\":0,\"clientname\":\"8床\"} ");
    }
}
