/** 
 * <p>功能描述:</p>
 * @version v63
 * <p>文件名： AlarmFormateMsg.java</p>
 * <p>创建人及时间： supplier3 2016-6-18下午4:42:12</p>
 *
 * <p>修改人：</p>
 * <p>修改时间：</p>
 * <p>修改描述：</p>
 **/
package nc.bs.extsys.plugin.dingtalk;

import nc.bs.pub.pa.html.IAlertMessage;

/** 
 * <p>功能说明:</p>
 * <p>创建人及时间： supplier3 2016-6-18下午4:42:12</p>
 *
 * <p>修改人：</p>
 * <p>修改时间：</p>
 * <p>修改描述：</p>
 **/
public class AlarmFormateMsg implements IAlertMessage {

	/**
	 * serialVersionUID的类型long
	 */
	private static final long serialVersionUID = 1415821549616134140L;
	public AlarmFormateMsg()
    {
    }

    public String[] getBodyFields()
    {
        return m_fields;
    }

    public Object[][] getBodyValue()
    {
        return m_values;
    }

    public float[] getBodyWidths()
    {
        return m_widths;
    }

    public String[] getBottom()
    {
        return m_bottom;
    }

    public String getTitle()
    {
        return m_title;
    }

    public String[] getTop()
    {
        return m_top;
    }

    public void setBodyFields(String fields[])
    {
        m_fields = fields;
    }

    public void setBodyValue(String values[][])
    {
        m_values = values;
    }

    public void setBodyWidths(float widths[])
    {
        m_widths = widths;
    }

    public void setBottom(String bottom[])
    {
        m_bottom = bottom;
    }

    public void setTitle(String title)
    {
        m_title = title;
    }

    public void setTop(String top[])
    {
        m_top = top;
    }

    private String m_bottom[];
    private String m_fields[];
    private String m_title;
    private String m_top[];
    private String m_values[][];
    private float m_widths[];
	
}
