package nc.bs.extsys.plugin.dingtalk.attendance;

import java.io.Serializable;

public class Attendance implements Serializable {

	private static final long serialVersionUID = 8972128571389653347L;
	/**
	 * ����ٵ������ˣ���׼ʱ��
	 */
	private Long baseCheckTime;
	/**
	 * �������ͣ�OnDuty���ϰ࣬OffDuty���°ࣩ
	 */
	private String checkType;
	private String corpId;
	/**
	 * �豸id
	 */
	private String deviceId;
	/**
	 * ����ʱ��
	 */
	private Long gmtCreate;
	/**
	 * �޸�ʱ��
	 */
	private Long gmtModified;
	/**
	 * ������ID
	 */
	private Long groupId;
	/**
	 * Ψһ��ʾID
	 */
	private Long id;
	/**
	 * �Ƿ�Ϸ�
	 */
	private String isLegal;
	/**
	 * ��λ����
	 */
	private String locationMethod;
	/**
	 * λ�ý����Normal:��Χ�ڣ�Outside:��Χ�⣩
	 */
	private String locationResult;
	/**
	 * �Ű��ʱ��
	 */
	private Long planCheckTime;
	/**
	 * �Ű�ID
	 */
	private Long planId;
	/**
	 * ������Դ ��ATM:���ڻ�;BEACON:IBeacon;DING_ATM:�������ڻ�;APP_USER:�û���;APP_BOSS:�ϰ��ǩ;APP_APPROVE:����ϵͳ;SYSTEM:����ϵͳ;APP_AUTO_CHECK:�Զ��򿨣�
	 */
	private String sourceType;
	/**
	 * ʱ������Normal:����;Early:����; Late:�ٵ�;SeriousLate:���سٵ���NotSigned:δ�򿨣�
	 */
	private String timeResult;
	/**
	 * �û��򿨶�λ����
	 */
	private Double userAccuracy;
	/**
	 * �û��򿨵�ַ
	 */
	private String userAddress;
	/**
	 * ʵ�ʴ�ʱ��
	 */
	private Long userCheckTime;
	/**
	 * �û�ID
	 */
	private String userId;
	/**
	 * �û���γ��
	 */
	private Double userLatitude;
	/**
	 * �û��򿨾���
	 */
	private Double userLongitude;
	/**
	 * ������
	 */
	private Long workDate;
	/**
	 * @return baseCheckTime
	 */
	public Long getBaseCheckTime() {
		return baseCheckTime;
	}
	/**
	 * @param baseCheckTime Ҫ���õ� baseCheckTime
	 */
	public void setBaseCheckTime(Long baseCheckTime) {
		this.baseCheckTime = baseCheckTime;
	}
	/**
	 * @return checkType
	 */
	public String getCheckType() {
		return checkType;
	}
	/**
	 * @param checkType Ҫ���õ� checkType
	 */
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
	/**
	 * @return corpId
	 */
	public String getCorpId() {
		return corpId;
	}
	/**
	 * @param corpId Ҫ���õ� corpId
	 */
	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}
	/**
	 * @return deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId Ҫ���õ� deviceId
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return gmtCreate
	 */
	public Long getGmtCreate() {
		return gmtCreate;
	}
	/**
	 * @param gmtCreate Ҫ���õ� gmtCreate
	 */
	public void setGmtCreate(Long gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	/**
	 * @return gmtModified
	 */
	public Long getGmtModified() {
		return gmtModified;
	}
	/**
	 * @param gmtModified Ҫ���õ� gmtModified
	 */
	public void setGmtModified(Long gmtModified) {
		this.gmtModified = gmtModified;
	}
	/**
	 * @return groupId
	 */
	public Long getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId Ҫ���õ� groupId
	 */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id Ҫ���õ� id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return isLegal
	 */
	public String getIsLegal() {
		return isLegal;
	}
	/**
	 * @param isLegal Ҫ���õ� isLegal
	 */
	public void setIsLegal(String isLegal) {
		this.isLegal = isLegal;
	}
	/**
	 * @return locationMethod
	 */
	public String getLocationMethod() {
		return locationMethod;
	}
	/**
	 * @param locationMethod Ҫ���õ� locationMethod
	 */
	public void setLocationMethod(String locationMethod) {
		this.locationMethod = locationMethod;
	}
	/**
	 * @return locationResult
	 */
	public String getLocationResult() {
		return locationResult;
	}
	/**
	 * @param locationResult Ҫ���õ� locationResult
	 */
	public void setLocationResult(String locationResult) {
		this.locationResult = locationResult;
	}
	/**
	 * @return planCheckTime
	 */
	public Long getPlanCheckTime() {
		return planCheckTime;
	}
	/**
	 * @param planCheckTime Ҫ���õ� planCheckTime
	 */
	public void setPlanCheckTime(Long planCheckTime) {
		this.planCheckTime = planCheckTime;
	}
	/**
	 * @return planId
	 */
	public Long getPlanId() {
		return planId;
	}
	/**
	 * @param planId Ҫ���õ� planId
	 */
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	/**
	 * @return sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}
	/**
	 * @param sourceType Ҫ���õ� sourceType
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	/**
	 * @return timeResult
	 */
	public String getTimeResult() {
		return timeResult;
	}
	/**
	 * @param timeResult Ҫ���õ� timeResult
	 */
	public void setTimeResult(String timeResult) {
		this.timeResult = timeResult;
	}
	/**
	 * @return userAccuracy
	 */
	public Double getUserAccuracy() {
		return userAccuracy;
	}
	/**
	 * @param userAccuracy Ҫ���õ� userAccuracy
	 */
	public void setUserAccuracy(Double userAccuracy) {
		this.userAccuracy = userAccuracy;
	}
	/**
	 * @return userAddress
	 */
	public String getUserAddress() {
		return userAddress;
	}
	/**
	 * @param userAddress Ҫ���õ� userAddress
	 */
	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
	/**
	 * @return userCheckTime
	 */
	public Long getUserCheckTime() {
		return userCheckTime;
	}
	/**
	 * @param userCheckTime Ҫ���õ� userCheckTime
	 */
	public void setUserCheckTime(Long userCheckTime) {
		this.userCheckTime = userCheckTime;
	}
	/**
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId Ҫ���õ� userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return userLatitude
	 */
	public Double getUserLatitude() {
		return userLatitude;
	}
	/**
	 * @param userLatitude Ҫ���õ� userLatitude
	 */
	public void setUserLatitude(Double userLatitude) {
		this.userLatitude = userLatitude;
	}
	/**
	 * @return userLongitude
	 */
	public Double getUserLongitude() {
		return userLongitude;
	}
	/**
	 * @param userLongitude Ҫ���õ� userLongitude
	 */
	public void setUserLongitude(Double userLongitude) {
		this.userLongitude = userLongitude;
	}
	/**
	 * @return workDate
	 */
	public Long getWorkDate() {
		return workDate;
	}
	/**
	 * @param workDate Ҫ���õ� workDate
	 */
	public void setWorkDate(Long workDate) {
		this.workDate = workDate;
	}

}
