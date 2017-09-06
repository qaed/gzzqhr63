package nc.bs.extsys.plugin.dingtalk.attendance;

import java.io.Serializable;

public class Attendance implements Serializable {

	private static final long serialVersionUID = 8972128571389653347L;
	/**
	 * 计算迟到和早退，基准时间
	 */
	private Long baseCheckTime;
	/**
	 * 考勤类型（OnDuty：上班，OffDuty：下班）
	 */
	private String checkType;
	private String corpId;
	/**
	 * 设备id
	 */
	private String deviceId;
	/**
	 * 创建时间
	 */
	private Long gmtCreate;
	/**
	 * 修改时间
	 */
	private Long gmtModified;
	/**
	 * 考勤组ID
	 */
	private Long groupId;
	/**
	 * 唯一标示ID
	 */
	private Long id;
	/**
	 * 是否合法
	 */
	private String isLegal;
	/**
	 * 定位方法
	 */
	private String locationMethod;
	/**
	 * 位置结果（Normal:范围内；Outside:范围外）
	 */
	private String locationResult;
	/**
	 * 排班打卡时间
	 */
	private Long planCheckTime;
	/**
	 * 排班ID
	 */
	private Long planId;
	/**
	 * 数据来源 （ATM:考勤机;BEACON:IBeacon;DING_ATM:钉钉考勤机;APP_USER:用户打卡;APP_BOSS:老板改签;APP_APPROVE:审批系统;SYSTEM:考勤系统;APP_AUTO_CHECK:自动打卡）
	 */
	private String sourceType;
	/**
	 * 时间结果（Normal:正常;Early:早退; Late:迟到;SeriousLate:严重迟到；NotSigned:未打卡）
	 */
	private String timeResult;
	/**
	 * 用户打卡定位精度
	 */
	private Double userAccuracy;
	/**
	 * 用户打卡地址
	 */
	private String userAddress;
	/**
	 * 实际打卡时间
	 */
	private Long userCheckTime;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 用户打卡纬度
	 */
	private Double userLatitude;
	/**
	 * 用户打卡经度
	 */
	private Double userLongitude;
	/**
	 * 工作日
	 */
	private Long workDate;
	/**
	 * @return baseCheckTime
	 */
	public Long getBaseCheckTime() {
		return baseCheckTime;
	}
	/**
	 * @param baseCheckTime 要设置的 baseCheckTime
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
	 * @param checkType 要设置的 checkType
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
	 * @param corpId 要设置的 corpId
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
	 * @param deviceId 要设置的 deviceId
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
	 * @param gmtCreate 要设置的 gmtCreate
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
	 * @param gmtModified 要设置的 gmtModified
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
	 * @param groupId 要设置的 groupId
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
	 * @param id 要设置的 id
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
	 * @param isLegal 要设置的 isLegal
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
	 * @param locationMethod 要设置的 locationMethod
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
	 * @param locationResult 要设置的 locationResult
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
	 * @param planCheckTime 要设置的 planCheckTime
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
	 * @param planId 要设置的 planId
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
	 * @param sourceType 要设置的 sourceType
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
	 * @param timeResult 要设置的 timeResult
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
	 * @param userAccuracy 要设置的 userAccuracy
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
	 * @param userAddress 要设置的 userAddress
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
	 * @param userCheckTime 要设置的 userCheckTime
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
	 * @param userId 要设置的 userId
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
	 * @param userLatitude 要设置的 userLatitude
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
	 * @param userLongitude 要设置的 userLongitude
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
	 * @param workDate 要设置的 workDate
	 */
	public void setWorkDate(Long workDate) {
		this.workDate = workDate;
	}

}
