/**
 * 
 */
package com.yktx.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.yktx.group.R;

/**
 * 
 * @项目名称：Group
 * @类名称：MemberDialog.java
 * @类描述：用户详细信息
 * @创建人：chenyongxian
 * @创建时间：2014年10月31日下午5:26:01
 * @修改人：
 * @修改时间：2014年10月31日下午5:26:01
 * @修改备注：
 * @version
 *
 */
public class MemberDialog extends AlertDialog{

	public TextView tv_group_member_name;
	public ImageView iv_group_member_head,iv_group_member_aite,iv_group_member_jubao;
	private Activity activity;
	
	public MemberDialog(Activity activity, int theme) {
	    super(activity, theme);
	    this.activity = activity;
	}
	
	public MemberDialog(Activity activity) {
	    super(activity);
	    this.activity = activity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.member_dialog);
	    Log.v("TAG", "MemberDialog      MemONCREATE");
	    setInverseBackgroundForced(true);
	    tv_group_member_name = (TextView) activity.findViewById(R.id.tv_group_member_name);
	    iv_group_member_head = (ImageView) activity.findViewById(R.id.iv_group_member_head);
	    iv_group_member_aite = (ImageView) activity.findViewById(R.id.iv_group_member_aite);
	    iv_group_member_jubao = (ImageView) activity.findViewById(R.id.iv_group_member_jubao);
	}
}
