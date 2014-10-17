package cn.koolcloud.pos.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.koolcloud.pos.AndroidHandler;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.util.UtilForDensity;
import cn.koolcloud.pos.util.UtilForGraghic;

public class CustomAnimDialog extends Dialog{

	private Context context;
	private CustomAnimDialog dialog;
	private Handler mainHandler;
	private Runnable cancelRunnable;
	
	private ProgressBar progressBar;
	
	private ImageView iv_animation;
	private TextView tv_message;
	private ImageButton btn_cancel;
	
	private AnimationDrawable animationDrawable;
	
	private Looper showWhileExecutingLooper;
	
	private int showCalledCount;
	
	public CustomAnimDialog(Context context) {
		super(context, R.style.custom_anim_dialog);
		this.context = context;
		dialog = this;
		this.mainHandler = AndroidHandler.getMainHandler();
		setCanceledOnTouchOutside(false);
		setCancelable(false);
		initContentView();
	}
	
	private void initContentView() {
		setContentView(R.layout.custom_anim_dialog);
		progressBar = (ProgressBar)findViewById(R.id.custom_anim_dialog_progress_bar);
		iv_animation = (ImageView)findViewById(R.id.custom_anim_dialog_iv_animation);
		animationDrawable = (AnimationDrawable)iv_animation.getBackground();
		tv_message = (TextView)findViewById(R.id.custom_anim_dialog_tv_message);
		btn_cancel = (ImageButton)findViewById(R.id.custom_anim_dialog_btn_cancel);
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != cancelRunnable) {
					mainHandler.post(cancelRunnable);
				}
			}
		});
	}

	public void setOnCancel(Runnable cancelRunnable) {
		this.cancelRunnable = cancelRunnable;
	}
	
	@Override
	public void onBackPressed() {
		if (View.VISIBLE == btn_cancel.getVisibility() && null != cancelRunnable) {
			mainHandler.post(cancelRunnable);
		}
	}

	/**
	 * set drawable resource of animation, with defualt size(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
	 * @param id id of drawable resource
	 */
	public void setAnimDrawable(int id) {
		setAnimDrawable(id, 0, 0);
	}
	
	/**
	 * set drawable of animation, with defualt size(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
	 * @param drawable drawable to be set
	 */
	public void setAnimDrawable(Drawable drawable) {
		setAnimDrawable(drawable, 0, 0);
	}
	
	/**
	 * set drawable resource and size of animation
	 * @param id id of drawable resource
	 * @param width width of animation, in dp, if width equals 0, the width is LayoutParams.WRAP_CONTENT
	 * @param height height of animation, in dp, if height equals 0, the height is LayoutParams.WRAP_CONTENT
	 */
	public void setAnimDrawable(int id,int width, int height) {
		try {
			setAnimDrawable(context.getResources().getDrawable(id), width, height);
		} catch (NotFoundException e) {
			setAnimDrawable(null, 0, 0);
		}
	}
	
	/**
	 * set drawable and size of animation
	 * @param drawable drawable to be set
	 * @param width width of animation, in dp, if width equals 0, the width is LayoutParams.WRAP_CONTENT
	 * @param height height of animation, in dp, if height equals 0, the height is LayoutParams.WRAP_CONTENT
	 */
	public void setAnimDrawable(Drawable drawable,int width, int height) {
		if (dialog.isShowing()) {
			stopAnimation();
		}
				
		if (null == drawable) {
			iv_animation.setVisibility(View.GONE);

			setViewLayoutParams(progressBar, width, height);
			progressBar.setVisibility(View.VISIBLE);
			return;
		} else {
			progressBar.setVisibility(View.GONE);

			setViewLayoutParams(iv_animation, width, height);
			UtilForGraghic.setBackground(iv_animation, drawable);
			animationDrawable = (AnimationDrawable)iv_animation.getBackground();
			iv_animation.setVisibility(View.VISIBLE);
		}
		
		if (dialog.isShowing()) {
			startAnimation();
		}
	}
	
	/**
	 * set drawable resource of cancel button, with defualt size(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
	 * @param id id of drawable resource
	 */
	public void setBtnCancel(int id) {
		setBtnCancel(id, 0, 0);
	}
	
	/**
	 * set drawable of cancel button, with defualt size(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
	 * @param drawable drawable to be set
	 */
	public void setBtnCancel(Drawable drawable) {
		setBtnCancel(drawable, 0, 0);
	}
	
	/**
	 * set drawable resource and size of cancel button
	 * @param id id of drawable resource
	 * @param width width of cancel button, in dp, if width equals 0, the width is LayoutParams.WRAP_CONTENT
	 * @param height height of cancel button, in dp, if height equals 0, the height is LayoutParams.WRAP_CONTENT
	 */
	public void setBtnCancel(int id,int width, int height) {
		try {
			setBtnCancel(context.getResources().getDrawable(id), width, height);
		} catch (NotFoundException e) {
			setBtnCancel(null, 0, 0);
		}
	}
	
	/**
	 * set drawable and size of cancel button
	 * @param drawable drawable to be set
	 * @param width width of cancel button, in dp, if width equals 0, the width is LayoutParams.WRAP_CONTENT
	 * @param height height of cancel button, in dp, if height equals 0, the height is LayoutParams.WRAP_CONTENT
	 */
	public void setBtnCancel(Drawable drawable,int width, int height) {
		setViewLayoutParams(btn_cancel, width, height);
		UtilForGraghic.setBackground(btn_cancel, drawable);
		if (null == drawable) {
			btn_cancel.setVisibility(View.GONE);
			return;
		} else {
			btn_cancel.setVisibility(View.VISIBLE);
		}
	}
	
	private void setViewLayoutParams(View view,int width, int height) {
		if (0 == width) {
			view.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
		} else {
			view.getLayoutParams().width = UtilForDensity.dip2px(context, width);
		}
		
		if (0 == height) {
			view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
		} else {
			view.getLayoutParams().height = UtilForDensity.dip2px(context, height);
		}
	}
	
	private void stopAnimation() {
		if(progressBar.getVisibility() == View.VISIBLE){

		}else if (null != iv_animation && iv_animation.getVisibility() == View.VISIBLE) {
			if (null != animationDrawable) {
				if (animationDrawable.isRunning()) {
					animationDrawable.stop();
				}
			}
		}
	}
	
	private void startAnimation() {
		if(progressBar.getVisibility() == View.VISIBLE){
			
		}if (null != iv_animation && iv_animation.getVisibility() == View.VISIBLE) {
			if (null != animationDrawable) {
				if (!animationDrawable.isRunning()) {
					animationDrawable.start();
				}
			}
		}
	}
	
	public void setMessage(String message) {
		tv_message.setText(message);
		if (null == message) {
			tv_message.setVisibility(View.GONE);
		} else {
			tv_message.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			startAnimation();
		} else {
			stopAnimation();
		}
		super.onWindowFocusChanged(hasFocus);
	}

	public void showWhileExecuting(Runnable runnable) {
		showCalledCount ++;
		if (showCalledCount > 1) {
			return;
		}
		//fix SmartPos SMTPS-87 bug --start mod by Teddy on 22th September
		if(!((Activity) context).isFinishing()) {
			show();
		}
		//fix SmartPos SMTPS-87 bug --end mod by Teddy on 22th September
		HandlerThread backgroundThread = new HandlerThread("showWhileExecuting");
		backgroundThread.start();
		showWhileExecutingLooper = backgroundThread.getLooper();
		Handler handler = new Handler(showWhileExecutingLooper);
		handler.post(runnable);
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if (null != dialog && dialog.isShowing()) {
					mainHandler.post(new Runnable() {
						
						@Override
						public void run() {
							stopAnimation();
							dialog.cancel();
						}
					});
				}
			}
		});
	}

	@Override
	protected void onStop() {
		showWhileExecutingLooper.quit();
		showCalledCount = 0;
		super.onStop();
	}
	
}
