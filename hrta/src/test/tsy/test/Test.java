package tsy.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dingtalk.open.client.api.model.corp.CorpUser;
import com.dingtalk.open.client.api.model.corp.CorpUserDetail;
import com.dingtalk.open.client.api.model.corp.CorpUserList;
import com.dingtalk.open.client.api.model.corp.Department;

import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.extsys.plugin.dingtalk.department.DepartmentHelper;
import nc.bs.extsys.plugin.dingtalk.user.UserHelper;
import nc.bs.logging.Logger;
import nc.vo.pub.BusinessException;

public class Test {
	private List<CorpUserDetail> userDetails = new ArrayList<CorpUserDetail>();
	private int getUserDetailErrorTimes = 0;
	private int getUserDetailErrorTimesMax = 20;//1

	@org.junit.Test
	public void deletedept() {
		long[] ids1 =
				new long[] { 47696366L, 47703446L, 47772328L, 47788413L, 47789364L, 49186959L, 49186960L, 49186961L, 49186992L, 49186993L, 49186995L, 49187988L, 49190939L, 49190941L, 49190966L, 49190967L, 49190968L, 49190969L, 49190970L, 49195908L, 49195909L, 49195913L, 49195938L, 49195939L, 49196941L, 49196966L, 49196967L, 49196968L, 49199997L, 49200000L, 49201993L, 49201994L, 49203955L, 49203977L, 49203978L, 49203979L, 49203980L, 49203981L, 49204954L, 49204957L, 49204960L, 49204963L, 49204964L, 49204997L, 49204998L, 49207890L, 49207918L, 49218966L, 49218967L, 49218969L, 49218995L, 49218996L, 49220916L, 49220917L, 49220918L, 49220920L, 49220944L, 49241973L, 49241977L, 49245989L, 49245992L, 49245993L, 49257994L, 49270993L, 49270996L, 49270997L, 49271957L, 49271958L, 49271991L, 49273898L, 49273899L, 49273904L, 49273924L, 49275991L, 49275993L, 49280999L, 49286992L, 49286993L, 49286996L, 49288923L, 49288941L, 49288942L, 49288943L, 49288944L, 49296973L, 49296974L, 49296976L, 49296977L, 49297976L, 49299000L, 49302994L, 49302996L, 49342391L, 49342392L, 49342393L, 49342395L, 49342396L, 49342426L, 49347340L, 49347343L, 49347344L, 49347345L, 49347365L, 49347366L, 49347368L, 49350259L, 49350260L, 49350262L, 49350263L, 49350264L, 49350290L, 49351294L, 49351296L, 49351297L, 49351298L, 49354372L, 49354373L, 49354395L, 49354398L, 49354399L, 49356271L, 49356274L };
		long[] ids2 =
				new long[] { 49356275L, 49356276L, 49356307L, 49357252L, 49357288L, 49357289L, 49357290L, 49358280L, 49358281L, 49358300L, 49361223L, 49361226L, 49361254L, 49361255L, 49361256L, 49363214L, 49363219L, 49363220L, 49363242L, 49363243L, 49363244L, 49363245L, 49370209L, 49370241L, 49370242L, 49370243L, 49400192L, 49400195L, 49400243L, 49400244L, 49401200L, 49401201L, 49401202L, 49401215L, 49401220L, 49401221L, 49402189L, 49402191L, 49402192L, 49402216L, 49402219L, 49402222L, 49402223L, 49403152L, 49403153L, 49403158L, 49403160L, 49403179L, 49403181L, 49403182L, 49404145L, 49404166L, 49404175L, 49404177L, 49405165L, 49405166L, 49406158L, 49406159L, 49406161L, 49406186L, 49406189L, 49406190L, 49407263L, 49407290L, 49407291L, 49408193L, 49409195L, 49409196L, 49409200L, 49409219L, 49410222L, 49410224L, 49411111L, 49411114L, 49411115L, 49411133L, 49411135L, 49412171L, 49412172L, 49412173L, 49412174L, 49412197L, 49412198L, 49412200L, 49412203L, 49413124L, 49413125L, 49413126L, 49413144L, 49413148L, 49413150L, 49413152L, 49414194L, 49414195L, 49414196L, 49414197L, 49414219L, 49414220L, 49414244L, 49417095L, 49418092L, 49418093L, 49418094L, 49419103L, 49419104L, 49419107L, 49419110L, 49419111L, 49419112L, 49419138L, 49420110L, 49420111L, 49420112L, 49420113L, 49420114L, 49420116L, 49420117L, 49420150L, 49420153L, 49421105L, 49421106L, 49421107L, 49421134L, 49421135L, 49421136L, 49421138L, 49422095L, 49422096L, 49422098L, 49422101L, 49423081L, 49423111L, 49423114L, 49424065L, 49424090L, 49424092L, 49425079L, 49425081L, 49425109L, 49425110L, 49426075L, 49426077L, 49426079L, 49426080L, 49426083L, 49426084L, 49426085L, 49426121L, 49426122L, 49426124L, 49426125L, 49426126L, 49427066L, 49427067L, 49427069L, 49427070L, 49427072L, 49427088L, 49427099L, 49427100L, 49427102L, 49428069L, 49429071L, 49429072L, 49429073L, 49429075L, 49429101L, 49429102L, 49430053L, 49430056L, 49430072L, 49430075L, 49430079L, 49430080L, 49431072L, 49431073L, 49431075L, 49431076L, 49432082L, 49432084L, 49432085L, 49432086L, 49432114L, 49432115L, 49432117L, 49433077L, 49433078L, 49433079L, 49433101L, 49434123L, 49434124L, 49435060L, 49435062L, 49435063L, 49435083L, 49435084L, 49436062L, 49436066L, 49436090L, 49436094L, 49437069L, 49437071L, 49437073L, 49438075L, 49438076L, 49438077L, 49438078L, 49438080L, 49438082L, 49438106L, 49438107L, 49439058L, 49439059L, 49439060L, 49439061L, 49439063L, 49439088L, 49439090L, 49440054L, 49440057L, 49440085L, 49442055L, 49442059L, 49442082L, 49443048L, 49443049L };
		long[] ids3 =
				new long[] { 49443051L, 49444070L, 49445025L, 49445026L, 49445053L, 49445054L, 49445055L, 49445056L, 49446040L, 49446056L, 49447032L, 49447039L, 49447062L, 49447064L, 49448045L, 49449022L, 49449023L, 49449024L, 49449053L, 49449055L, 49449057L, 49450022L, 49450023L, 49450024L, 49450025L, 49450028L, 49450054L, 49450055L, 49451031L, 49451064L, 49452024L, 49452025L, 49452048L, 49453023L, 49453026L, 49453027L, 49453057L, 49453059L, 49454024L, 49454025L, 49454026L, 49454027L, 49454083L, 49454084L, 49454085L, 49455018L, 49455021L, 49455037L, 49455041L, 49455051L, 49455053L, 49455056L, 49456049L, 49456051L, 49457020L, 49457039L, 49457042L, 49457044L, 49457045L, 49458008L, 49458009L, 49458014L, 49458016L, 49459009L, 49459012L, 49459036L, 49459037L, 49460010L, 49460012L, 49460014L, 49460015L, 49460016L, 49460017L, 49460018L, 49461003L, 49461005L, 49461028L, 49461032L, 49462001L, 49462030L, 49463001L, 49464001L, 49464025L, 49465037L, 49466024L, 49466025L, 49467025L, 49467026L, 49469025L, 49469026L, 49470008L, 49470009L, 49471007L, 49471010L, 49471011L, 49471012L, 49471013L, 49472011L, 49472012L, 49473003L, 49965333L, 50238431L };
		long[] ids4 =
				new long[] { 49271716L, 49271717L, 49271718L, 49271838L, 49271839L, 49271840L, 49271842L, 49272899L, 49272900L, 49272901L, 49272902L, 49272903L, 49272904L, 49272905L, 49272995L, 49272996L, 49272997L, 49272998L, 49272999L, 49273000L, 49273686L, 49273784L, 49273785L, 49273787L, 49275689L, 49275690L, 49275691L, 49275692L, 49275840L, 49275841L, 49276812L, 49276813L, 49276815L, 49277711L, 49277712L, 49277713L, 49277859L, 49277860L, 49278762L, 49278763L, 49278764L, 49278765L, 49278766L, 49278917L, 49279885L, 49279886L, 49279887L, 49279889L, 49279890L, 49279891L, 49279892L, 49279893L, 49279995L, 49279996L, 49280690L, 49280691L, 49280693L, 49280694L, 49280695L, 49280696L, 49280697L, 49280871L, 49281819L, 49281820L, 49281822L, 49281823L, 49281824L, 49281825L, 49281921L, 49282842L, 49282843L, 49282844L, 49282845L, 49282846L, 49282847L, 49282849L, 49282850L, 49282851L, 49282852L, 49282853L, 49282935L, 49282936L, 49283958L, 49283959L, 49283960L, 49283961L, 49284837L, 49284838L, 49284839L, 49284841L, 49284954L, 49284955L, 49285914L, 49285915L, 49286710L, 49286711L, 49286712L, 49286713L, 49286862L, 49286864L, 49286865L, 49286866L, 49287867L, 49287868L, 49287869L, 49287870L, 49287871L, 49288685L, 49288686L, 49288687L, 49288689L, 49288794L, 49289770L, 49289771L, 49289772L, 49289773L, 49289774L, 49289775L, 49289776L, 49289777L, 49289778L, 49289779L, 49289952L, 49289953L, 49290795L, 49290796L, 49290797L, 49290798L, 49290799L, 49290800L, 49290801L, 49290802L, 49290899L, 49290900L, 49290901L, 49290902L, 49292848L, 49292849L, 49292851L, 49292853L, 49292975L, 49294799L, 49294800L, 49294801L, 49294802L, 49294803L, 49294804L, 49294805L, 49294806L, 49294919L, 49294920L, 49294921L, 49294922L, 49295827L, 49295828L, 49295829L, 49295830L, 49295831L, 49295832L, 49295833L, 49295834L, 49295835L, 49295836L, 49295838L, 49295839L, 49295840L, 49295973L, 49296746L, 49296747L, 49296748L, 49296749L, 49296750L, 49296751L, 49296752L, 49296753L, 49296754L, 49296755L, 49296855L, 49296856L, 49296857L, 49297713L, 49297715L, 49297716L, 49297717L, 49297826L, 49297827L, 49297828L };
		long[] ids5 =
				new long[] { 49298694L, 49298695L, 49298697L, 49298698L, 49298699L, 49299795L, 49299796L, 49299797L, 49299798L, 49299928L, 49299929L, 49300798L, 49300799L, 49300800L, 49300897L, 49300899L, 49301795L, 49301796L, 49301797L, 49301798L, 49301799L, 49301800L, 49301801L, 49301804L, 49301805L, 49301898L, 49301903L, 49302767L, 49302768L, 49302769L, 49302770L, 49302771L, 49302772L, 49302876L, 49302877L, 49302878L, 49303793L, 49303794L, 49303795L, 49303796L, 49303797L, 49303798L, 49303799L, 49303898L, 49303899L, 49303900L, 49303901L, 49303902L, 49304830L, 49304831L, 49304833L, 49304834L, 49304835L, 49304837L, 49304838L, 49304840L, 49304841L, 49304842L, 49304843L, 49304844L, 49304847L, 49304934L, 49304935L, 49305922L, 49305923L, 49305924L, 49305925L, 49305927L, 49305928L, 49305929L, 49305930L, 49305931L, 49305932L, 49305933L, 49307746L, 49307747L, 49307748L, 49307749L, 49307750L, 49307860L, 49307861L, 49307862L, 49307863L, 49307864L, 49308929L, 49308930L, 49308931L, 49308932L, 49308933L, 49308934L, 49308935L, 49308936L, 49309810L, 49309811L, 49309812L, 49309816L, 49309817L, 49309818L, 49309908L, 49309909L, 49309910L, 49309911L, 49312887L, 49312889L, 49312890L, 49312891L, 49313000L, 49314003L, 49342138L, 49342139L, 49342140L, 49342141L, 49342142L, 49342143L, 49342144L, 49342145L, 49342146L, 49342147L, 49342256L, 49342257L, 49342258L, 49347051L, 49347052L, 49347053L, 49347054L, 49347055L, 49347058L, 49347059L, 49347060L, 49347183L, 49347184L, 49347185L, 49347186L, 49350042L, 49350043L, 49350044L, 49350045L, 49350046L, 49350047L, 49350048L, 49350049L, 49350050L, 49350153L, 49350154L, 49350155L, 49350156L, 49350157L, 49350158L, 49350159L, 49351049L, 49351050L, 49351051L, 49351052L, 49351053L, 49351054L, 49351055L, 49351056L, 49351057L, 49351175L, 49351176L, 49354102L, 49354103L, 49354104L, 49354105L, 49354106L, 49354107L, 49354108L, 49354109L, 49354110L, 49354111L, 49354112L, 49354233L, 49354234L, 49354235L, 49356017L, 49356018L, 49356019L, 49356020L, 49356021L, 49356022L, 49356023L, 49356024L, 49356025L, 49356026L, 49356027L, 49356028L, 49356029L, 49356030L, 49356031L, 49356139L, 49357018L, 49357019L, 49357020L, 49357021L, 49357023L, 49357024L, 49357025L, 49357026L, 49357124L, 49357125L, 49357126L, 49357127L, 49357128L, 49358001L, 49358002L, 49358003L, 49358004L, 49358005L, 49358007L, 49358147L, 49358148L, 49358149L, 49361091L, 49363076L, 49363077L, 49363078L, 49363079L, 49366055L, 49366056L, 49370098L, 49370099L, 49370100L, 49370101L, 49400050L, 49400051L, 49400052L, 49401042L, 49401043L, 49401044L, 49401045L, 49402041L, 49402042L, 49402043L, 49402044L, 49403031L, 49403032L, 49404022L, 49404023L, 49404024L, 49404025L, 49405029L, 49405030L, 49405031L, 49406024L, 49406025L, 49407111L, 49408012L, 49408013L, 49409041L, 49409042L, 49409043L, 49410001L, 49411001L };
		for (long id : ids1) {
			try {
				DepartmentHelper.deleteDepartment(AuthHelper.getAccessToken(), id);
			} catch (OApiException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (long id : ids2) {
			try {
				DepartmentHelper.deleteDepartment(AuthHelper.getAccessToken(), id);
			} catch (OApiException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (long id : ids3) {
			try {
				DepartmentHelper.deleteDepartment(AuthHelper.getAccessToken(), id);
			} catch (OApiException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//		for (long id : ids4) {
		//			try {
		//				DepartmentHelper.deleteDepartment(AuthHelper.getAccessToken(), id);
		//			} catch (OApiException e) {
		//				e.printStackTrace();
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		for (long id : ids5) {
		//			try {
		//				DepartmentHelper.deleteDepartment(AuthHelper.getAccessToken(), id);
		//			} catch (OApiException e) {
		//				e.printStackTrace();
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		}
	}

	@org.junit.Test
	public void getUser() throws BusinessException {
		//		List<CorpUserDetail> userList = getAllUser();
		List<String> list = new ArrayList<String>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");

		List<String> list1 = new ArrayList<String>();
		list1.add("ddd");
		list1.add("eee");
		list.addAll(list1);
		list1.clear();

		System.out.println(list);
	}

	private List<CorpUserDetail> getAllUser() throws BusinessException {
		if (this.userDetails != null && this.userDetails.size() > 0) {
			return this.userDetails;
		}
		try {
			List<Department> departments = DepartmentHelper.listDepartments(AuthHelper.getAccessToken(), "1");
			for (int i = 0; i < departments.size(); i++) {
				CorpUserList corpuserlist =
						UserHelper.getDepartmentUser(AuthHelper.getAccessToken(), departments.get(i).getId(), null, null, null);
				List<CorpUser> userlist = corpuserlist.getUserlist();
				for (int j = 0; j < userlist.size(); j++) {
					this.userDetails.add(UserHelper.getUser(AuthHelper.getAccessToken(), userlist.get(j).getUserid()));
				}
			}
		} catch (Exception e) {
			this.getUserDetailErrorTimes++;
			if (this.getUserDetailErrorTimes < this.getUserDetailErrorTimesMax) {
				this.userDetails.clear();
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e1) {
					Logger.error(e1);
				}
				return getAllUser();
			} else {
				throw new BusinessException(e);
			}
		}
		return this.userDetails;
	}
}
