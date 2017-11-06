 package nc.vo.pub.change;
 
 import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;
 
 
 
 
 
 public class PublicHeadVO
   extends ValueObject
 {
   private static String[] ATTR_PARAM_CHINA_NAMES = { NCLangRes4VoTransl.getNCLangRes().getStrByID("busitype", "busitypehint-000041"), NCLangRes4VoTransl.getNCLangRes().getStrByID("busitype", "busitypehint-000042"), NCLangRes4VoTransl.getNCLangRes().getStrByID("busitype", "busitypehint-000043"), NCLangRes4VoTransl.getNCLangRes().getStrByID("busitype", "busitypehint-000044"), NCLangRes4VoTransl.getNCLangRes().getStrByID("busitype", "busitypehint-000045"), NCLangRes4VoTransl.getNCLangRes().getStrByID("busitype", "busitypehint-000046"), NCLangRes4VoTransl.getNCLangRes().getStrByID("busitype", "busitypehint-000047"), NCLangRes4VoTransl.getNCLangRes().getStrByID("busitype", "busitypehint-000048"), NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow61_0", "0pfworkflow61-0086") };
   
 
   public static String[] ATTR_PARAM_NAMES = { "pkBillId", "pkOrg", "billType", "transType", "billNo", "businessType", "operatorId", "approveId", "pkBillVersion" };
   
 
   private static String[] ATTR_PARAM_TYPES = { "String", "String", "String", "String", "String", "String", "String", "String", "String" };
   
 
 
 
 
   public String approveId = null;
   
 
 
 
   public String billNo = null;
   
 
 
 
   public String billType = null;
   
 
 
 
   public String transType = null;
   
 
 
 
   public String businessType = null;
   
 
 
 
   public String pkOrg = null;
   
 
 
 
   public String operatorId = null;
   
 
 
 
   public String pkBillId = null;
   
   public String pkBillVersion = null;
   
 
 
 
   public PublicHeadVO() {}
   
 
 
   public static String[] getAttributeChinaNames()
   {
     return ATTR_PARAM_CHINA_NAMES;
   }
   
 
 
 
   public static String[] getAttributeNames()
   {
     return ATTR_PARAM_NAMES;
   }
   
 
 
 
   public static String[] getAttributeType()
   {
     return ATTR_PARAM_TYPES;
   }
   
 
 
 
 
 
   public String getAttributeValue(String attributeName)
   {
     if (attributeName.equals("pkBillId"))
       return this.pkBillId;
     if (attributeName.equals("pkOrg"))
       return this.pkOrg;
     if (attributeName.equals("billType"))
       return this.billType;
     if (attributeName.equals("transType"))
       return this.transType;
     if (attributeName.equals("billNo"))
       return this.billNo;
     if (attributeName.equals("businessType"))
       return this.businessType;
     if (attributeName.equals("operatorId"))
       return this.operatorId;
     if (attributeName.equals("approveId"))
       return this.approveId;
     if (attributeName.equals("pkBillVersion"))
       return this.pkBillVersion;
     return null;
   }
   
   public String getEntityName() {
     return "HEADVO";
   }
   
   public void validate()
     throws ValidationException
   {}
 }
