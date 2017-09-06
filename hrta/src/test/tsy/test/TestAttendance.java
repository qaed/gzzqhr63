package tsy.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.bs.extsys.plugin.dingtalk.attendance.AttendanceHelper;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.extsys.plugin.dingtalk.workflow.SyncWorkFlow;

import org.junit.Test;

import com.alibaba.fastjson.JSONArray;

public class TestAttendance {

	@Test
	public void test() throws Exception {
		List<String> userIds = new ArrayList<String>();
		userIds.add("115266384435531544");
		JSONArray array = AttendanceHelper.listRecord(AuthHelper.getAccessToken(), userIds, "2017-08-11 00:00:00", "2017-08-17 24:00:00");
		for (int i = 0; i < array.size(); i++) {
			Map map = (Map) array.get(i);
			System.out.print(map.get("checkType") + "  ");
			Date date = new Date((Long) map.get("userCheckTime"));
			System.out.print(date.toLocaleString() + "  ");
			System.out.print(map.get("sourceType") + "  ");
			System.out.println();

		}
		System.out.println(array);
	}

	@Test
	public void testgetCheckinDate() throws Exception {
		Calendar start = Calendar.getInstance();

		start.set(2017, 7, 12, 0, 0, 0);
		Calendar end = Calendar.getInstance();
		end.set(2017, 7, 16, 23, 59, 59);
		System.out.println(start.getTime().toLocaleString());
		System.out.println(end.getTime().toLocaleString());
		JSONArray array =
				AttendanceHelper.listCheckinRecord(AuthHelper.getAccessToken(), "1", start.getTimeInMillis(), end.getTimeInMillis(), null, null, null);
		//		AttendanceHelper.listCheckinRecord(AuthHelper.getAccessToken(), "47696366", start.getTimeInMillis(), end.getTimeInMillis(), 0L, 100, "asc");
		for (int i = 0; i < array.size(); i++) {
			Map map = (Map) array.get(i);
			Date date = new Date((Long) map.get("timestamp"));
			System.out.print(date.toLocaleString() + "  ");
			System.out.print(map.get("name") + "  ");
			System.out.print(map.get("userId") + "  ");
			System.out.print(map.get("place") + "  ");
			System.out.print(map.get("detailPlace") + "  ");
			System.out.print(map.get("remark") + "  ");
			System.out.print(map.get("imageList") + "  ");
			System.out.println();

		}
		System.out.println(array);
	}

	@Test
	public void getWorkFlow() throws Exception {
		SyncWorkFlow sync = new SyncWorkFlow();
		sync.executeTask(null);

	}
}
