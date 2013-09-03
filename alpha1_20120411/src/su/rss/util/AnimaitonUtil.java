package su.rss.util;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimaitonUtil {

	public static Animation TranslateAnimaiton(float fromXDelta,
			float toXDelta, float fromYDelta, float toYDelta) {
		Animation anima = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				fromXDelta, Animation.RELATIVE_TO_SELF, toXDelta, Animation.RELATIVE_TO_SELF,
				fromYDelta, Animation.RELATIVE_TO_SELF, toYDelta);
		anima.setDuration(500);
		return anima;
	}
}
